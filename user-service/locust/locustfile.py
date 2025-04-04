from locust import HttpUser, task, between
import random

class UserBehavior(HttpUser):
    wait_time = between(0.5, 1.5)  # 요청 간 간격 (초)

    @task(1)
    def create_post_and_like(self):
        user_id = random.randint(1, 100)
        post_id = random.randint(1, 10000)

        # 좋아요 요청
        self.client.post(
            "/posts/like",
            json={
                "userId": user_id,
                "postId": post_id
            }
        )
