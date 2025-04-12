CREATE TABLE statistics (
    statistics_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    metadata JSONB NOT NULL,
    count BIGINT DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_event_type_metadata UNIQUE (event_type, metadata)
);

CREATE TABLE post (
    post_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    image_urls VARCHAR(100),
    hashtags TEXT[],
    visibility VARCHAR(50) NOT null default 'PUBLIC',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE post_like (
    post_like_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    liked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_post_like UNIQUE (user_id, post_id)
);

-- 초기 데이터 생성

DO $$
DECLARE
    i INT := 1;
    hashtags_pool TEXT[] := ARRAY[
        '고양이', '강아지', '여행', '맛집', '일상',
        '운동', '독서', '음악', '코딩', '스터디'
    ];
    selected_hashtags TEXT[];
    count INT;
    max_attempts CONSTANT INT := 5;
    attempt_count INT;

BEGIN
    RAISE NOTICE '데이터 생성 시작';

    WHILE i <= 100 LOOP
    BEGIN
        selected_hashtags := '{}';
        count := (random() * 3 + 1)::INT;
        attempt_count := 0;

        WHILE array_length(selected_hashtags, 1) IS DISTINCT FROM count
            AND attempt_count < max_attempts
        LOOP
            attempt_count := attempt_count + 1;

            IF attempt_count = max_attempts THEN
                RAISE WARNING '태그 선택 시도 횟수 초과: % attempts', attempt_count;
            END IF;

            -- 수정된 쿼리: LIMIT을 SELECT절에 이동
            selected_hashtags := (
                SELECT ARRAY_AGG(tag)
                FROM (
                    SELECT tag
                    FROM unnest(hashtags_pool) AS tag
                    ORDER BY random()
                    LIMIT count
                ) AS subquery
            );
        END LOOP;

        INSERT INTO post (
            user_id,
            content,
            image_urls,
            hashtags
        ) VALUES (
             (random() * 100 + 1)::BIGINT,
             format('이것은 자동 생성된 게시물입니다 %s', i),
             format('http://img.com/%s.jpg', i),
             selected_hashtags
         );

        i := i + 1;
        RAISE NOTICE '생성된 게시물 수: %', i;

    EXCEPTION
        WHEN OTHERS THEN
            RAISE WARNING '게시물 생성 실패 (i=%): %', i, SQLERRM;
            CONTINUE;
    END;
END LOOP;

RAISE NOTICE '데이터 생성 완료';
END $$;