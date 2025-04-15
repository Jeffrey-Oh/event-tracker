from locust import FastHttpUser, task, between
import random

class PageViewEventUser(FastHttpUser):
    wait_time = between(1.0, 2.0)

    @task
    def page_view_event(self):
        self.client.post(
            "/api/users/events",
            json={
                "eventType": "PAGE_VIEW",
                "userId": random.randint(1, 10000),
                "sessionId": f"session-{random.randint(1, 10000)}",
                "metadata": {
                    "componentId": 1000,
                    "elementId": "element-1000"
                }
            },
            name="POST /api/users/events (PAGE_VIEW)"
        )