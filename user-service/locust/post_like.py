from locust import FastHttpUser, task, between
import random

class LikeUser(FastHttpUser):
    wait_time = between(1.0, 2.0)

    @task
    def like_or_unlike_post(self):
        user_id = random.randint(1, 10000)
        post_id = random.randint(1, 1000000)
        self.client.post(
            "/api/posts/like",
            json={"userId": user_id, "postId": post_id},
            name="POST /api/posts/like"
        )