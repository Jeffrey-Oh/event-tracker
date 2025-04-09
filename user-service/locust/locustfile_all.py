from locust import FastHttpUser, task, between
import random

KEYWORDS = ["고양이", "강아지", "음식", "여행", "공부"]

class EventTrackerUser(FastHttpUser):
    wait_time = between(0.5, 1.5)
    host = "http://host.docker.internal:8080"

    @task(1)
    def click_event(self):
        component_id = random.randint(1, 5)
        self.client.post("/api/events", json={
            "eventType": "CLICK",
            "userId": random.randint(1, 100),
            "sessionId": f"session-{random.randint(1, 1000)}",
            "metadata": {
                "componentId": component_id,
                "elementId": f"element-{component_id}"
            }
        })

    @task(1)
    def page_view_event(self):
        component_id = random.randint(1, 5)
        self.client.post("/api/events", json={
            "eventType": "PAGE_VIEW",
            "userId": random.randint(1, 100),
            "sessionId": f"session-{random.randint(1, 1000)}",
            "metadata": {
                "componentId": component_id,
                "elementId": f"element-{component_id}"
            }
        })

class UserServiceUser(FastHttpUser):
    wait_time = between(0.5, 1.5)
    host = "http://host.docker.internal:8081"

    @task(2)
    def like_or_unlike_post(self):
        user_id = random.randint(1, 100)
        post_id = random.randint(1, 100)

        self.client.post("/api/posts/like", json={
            "userId": user_id,
            "postId": post_id
        })

    @task(1)
    def search_post(self):
        user_id = random.randint(1, 100)
        keyword = random.choice(KEYWORDS)

        self.client.get("/api/search", params={"userId": user_id, "keyword": keyword})