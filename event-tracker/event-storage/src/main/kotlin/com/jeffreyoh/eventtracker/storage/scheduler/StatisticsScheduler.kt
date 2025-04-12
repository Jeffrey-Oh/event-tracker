package com.jeffreyoh.eventtracker.storage.scheduler

import com.fasterxml.jackson.databind.ObjectMapper
import com.jeffreyoh.eventtracker.core.domain.event.EventType
import com.jeffreyoh.eventtracker.core.domain.statistics.Statistics
import com.jeffreyoh.eventtracker.port.output.StatisticsPostgrePort
import com.jeffreyoh.eventtracker.port.output.StatisticsRedisPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

@Component
class StatisticsScheduler(
    private val objectMapper: ObjectMapper,
    private val statisticsRedisPort: StatisticsRedisPort,
    private val statisticsPostgrePort: StatisticsPostgrePort
) {

    @Scheduled(fixedRate = 60_000)
    fun fromRedisToPostgre() {
        log.info { "⏰ 통계 동기화 시작" }

        statisticsRedisPort.scan()
            .flatMap { key ->
                statisticsRedisPort.saveCountSnapshot(key)
                    .flatMap { count ->
                        if (count == 0L) {
                            return@flatMap Mono.empty()
                        } else {
                            val parts = key.split(":") // statistics:like:componentId:1000:postId:5
                            if (parts.size < 2) return@flatMap Mono.empty()

                            val eventType = parts[1].uppercase()
                            val metadataMap = mutableMapOf<String, String>()
                            for (i in 2 until parts.size step 2) {
                                val k = parts.getOrNull(i)
                                val v = parts.getOrNull(i + 1)
                                if (k != null && v != null) metadataMap[k] = v
                            }

                            val metadataJson = objectMapper.writeValueAsString(metadataMap)

                            statisticsPostgrePort.save(
                                Statistics(
                                    eventType = EventType.valueOf(eventType),
                                    metadata = metadataJson,
                                    count = count,
                                )
                            ).thenReturn("$eventType:$metadataJson:$count")
                        }
                    }
                    .onErrorResume {
                        log.error(it) { "❌ Redis에서 Postgre로 통계 저장 실패" }
                        Mono.empty()
                    }
            }
            .collectList()
            .doOnSuccess { log.info { "✅ 총 ${it.size}건 저장 완료" } }
            .onErrorResume { ex ->
                log.error(ex) { "❌ 전체 동기화 실패" }
                Mono.empty()
            }
            .subscribe()
    }

}