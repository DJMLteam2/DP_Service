#!/bin/bash

# 현재 시간을 얻어온다
current_hour=$(date +%H)

# 새벽 2시에 실행
if [ "$current_hour" -eq "2" ]; then
    # Docker Compose 빌드 (no-cache)
    docker-compose build --no-cache

    # Docker Compose 시작
    docker-compose up -d
fi

