package com.jeffreyoh.eventtracker.storage.scheduler

import com.fasterxml.jackson.databind.ObjectMapper
import com.jeffreyoh.enums.EventType
import com.jeffreyoh.eventtracker.application.port.out.StatisticsPostgrePort
import com.jeffreyoh.eventtracker.application.port.out.StatisticsRedisPort
import com.jeffreyoh.eventtracker.core.domain.statistics.Statistics
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val log = KotlinLogging.logger {}

@Component
class StatisticsScheduler(
    private val objectMapper: ObjectMapper,
    private val statisticsRedisPort: StatisticsRedisPort,
    private val statisticsPostgrePort: StatisticsPostgrePort
) {

    @Scheduled(fixedRate = 60_000)
    fun fromRedisToPostgre() {
        log.info { "⏰ ZSET 통계 동기화 시작" }

        val now = LocalDateTime.now().minusMinutes(1) // 이전 분 기준
        val time = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))

        Flux.fromIterable(EventType.entries)
            .flatMap { eventType ->
                statisticsRedisPort.getEventCountsForHour(eventType, time)
                    .flatMap { (member, count) ->
                        if (count <= 0L) return@flatMap Mono.empty()

                        val metadataMap = parseZSetMember(member)  // 🔥 중요
                        val metadataJson = objectMapper.writeValueAsString(metadataMap)

                        statisticsPostgrePort.save(
                            Statistics(
                                eventType = eventType,
                                metadata = metadataJson,
                                count = count
                            )
                        ).thenReturn("${eventType.name}:$metadataJson:$count")
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

    private fun parseZSetMember(member: String): Map<String, String> {
        val parts = member.split(":")
        val map = mutableMapOf<String, String>()
        var i = 0
        while (i < parts.size - 1) {
            map[parts[i]] = parts[i + 1]
            i += 2
        }
        return map
    }

}