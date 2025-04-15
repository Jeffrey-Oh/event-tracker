from locust import FastHttpUser, task, between
import random

KEYWORDS = ["고양이", "강아지", "운동", "스터디", "맛집"]

class SearchUser(FastHttpUser):
    wait_time = between(1.0, 2.0)

    @task
    def search_post(self):
        user_id = random.randint(1, 10000)
        keyword = random.choice(KEYWORDS)
        self.client.get(
            "/api/search",
            params={"userId": user_id, "keyword": keyword},
            name="GET /api/search"
        )
