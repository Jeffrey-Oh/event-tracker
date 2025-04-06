import asyncio
import httpx
import random

semaphore = asyncio.Semaphore(200)  # 동시 200개 제한

async def create_post(session, post_id):
    user_id = random.randint(1, 100)
    async with semaphore:
        try:
            res = await session.post(
                "http://127.0.0.1:8081/api/posts",
                json={
                    "userId": user_id,
                    "content": f"test {post_id}",
                    "imageUrl": f"http://img.com/{post_id}.jpg",
                    "hashtags": ["test"]
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
