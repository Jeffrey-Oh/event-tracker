from locust import FastHttpUser, task, between
import random

class ClickEventUser(FastHttpUser):
    wait_time = between(1.0, 2.0)

    @task
    def click_event(self):
        self.client.post(
            "/api/users/events",
            json={
                "eventType": "CLICK",
                "userId": random.randint(1, 10000),
                "sessionId": f"session-{random.randint(1, 10000)}",
                "metadata": {
                    "componentId": 1002,
                    "elementId": "element-1002"
                }
            },
            name="POST /api/users/events (CLICK)"
        )