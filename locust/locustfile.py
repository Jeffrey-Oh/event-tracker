from locust import HttpUser, task, between
import random
import uuid

class EventUser(HttpUser):
    wait_time = between(0.5, 1.0)

    @task(4)
    def send_event(self):
        event_type = random.choice(["CLICK", "PAGE_VIEW", "SEARCH", "LIKE"])
        session_id = f"session-{uuid.uuid4()}"
        user_id = random.randint(1, 100)

        metadata = {
            "componentId": random.randint(1000, 2000),
            "elementId": f"element-{random.randint(1, 50)}",
            "targetUrl": "https://jeffrey-oh.click"
        }

        if event_type == "LIKE":
            metadata["postId"] = random.randint(1, 10)

        self.client.post("/api/events", json={
            "eventType": event_type,
            "sessionId": session_id,
            "userId": user_id,
            "metadata": metadata
        })