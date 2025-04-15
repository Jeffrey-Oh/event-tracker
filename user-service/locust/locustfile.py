from locust import FastHttpUser, task, constant_throughput
import random

KEYWORDS = [
    "고양이", "강아지", "여행", "맛집", "일상",
    "운동", "독서", "음악", "코딩", "스터디"
]

class UserBehavior(FastHttpUser):
    # 사용자당 초당 1회 요청 (200명 → 200 RPS)
    wait_time = constant_throughput(1)

    @task(4)
    def like_or_unlike_post(self):
        user_id = random.randint(1, 100)
        post_id = random.randint(1, 1000)
        self.client.post(
            "/api/posts/like",
            json={"userId": user_id, "postId": post_id},
            name="POST /api/posts/like"
        )

    @task(2)
    def search_post(self):
        user_id = random.randint(1, 100)
        keyword = KEYWORDS[(user_id % len(KEYWORDS))]
        self.client.get(
            "/api/search",
            params={"userId": user_id, "keyword": keyword},
            name="GET /search"
        )

    @task(2)
    def click_event(self):
        self.send_user_event("CLICK", 1002)

    @task(2)
    def page_view_event(self):
        self.send_user_event("PAGE_VIEW", 1000)

    def send_user_event(self, event_type, component_id):
        self.client.post(
            "/api/users/events",
            json={
                "eventType": event_type,
                "userId": random.randint(1, 100),
                "sessionId": f"session-{random.randint(1, 100)}",
                "metadata": {
                    "componentId": component_id,
                    "elementId": f"element-{component_id}"
                }
            },
            name=f"POST /api/users/events ({event_type})"
        )