FROM python:3.10

WORKDIR /mnt/locust

COPY requirements.txt .
RUN pip install -r requirements.txt

COPY ../locust1 .