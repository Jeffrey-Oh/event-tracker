import asyncio
import httpx
import random

semaphore = asyncio.Semaphore(200)

hashtags_pool = ["고양이", "강아지", "여행", "맛집", "일상", "운동", "독서", "음악", "코딩", "스터디"]

async def create_post(session, post_id):
    user_id = random.randint(1, 100)
    hashtags = random.sample(hashtags_pool, random.randint(1, 3))  # 해시태그 1~3개 랜덤 선택

    async with semaphore:
        try:
            await session.post(
                "http://host.docker.internal:8081/api/posts",
                json={
                    "userId": user_id,
                    "content": f"test {post_id}",
                    "imageUrl": f"http://img.com/{post_id}.jpg",
                    "hashtags": hashtags
                },
                headers={"Content-Type": "application/json"}
            )

            if post_id % 5 == 0:
                print(f"✅ {post_id} posts created")
        except Exception as e:
            print(f"❌ Error on {post_id}: {e}")

async def main():
    async with httpx.AsyncClient() as session:
        print("🟢 작업 시작")
        await asyncio.gather(*[create_post(session, i) for i in range(1, 101)])
        print("✅ 작업 끝")

asyncio.run(main())
