import heapq
import haversine
import numpy as np

def dijkstra(similar_tags, start=0):
    """
    힙 자료구조를 이용한 '개선된 다익스트라' 알고리즘
    ==============================================
    Variables
     - similar_tags: Dictionary type.
                     메인 스크립트에 해당하는 app.py에서 미리 코사인 유사도를 기준으로 정렬하여 입력받습니다.
     - start: Integer.
              여행 중 처음으로 방문할 장소의 인덱스입니다. 기본값은 0입니다.
    ==============================================
    Return
    
    """
    # 다익스트라 알고리즘은 시작하기 전 모든 노드 간의 거리(또는 가중치)를 무한대로 설정하고 시작합니다.
    INF = np.inf

    # 노드의 개수, 간선의 개수(테스트 용으로 노드 5개, 간선은 5C2=10개로 설정)
    n, m = 5, 10
    
    # 각 노드에 연결되어 있는 노드에 대한 정보를 담는 리스트를 만들기
    graph = [[] for i in range(n + 1)]
    # 방문한 적이 있는지 체크하는 목적의 리스트 만들기
    visited = [False] * (n + 1)
    # 최단 거리 테이블을 모두 무한으로 초기화
    distances = INF * np.ones((n, m))

    # 모든 간선 정보 입력받기 - 거리를 입력
    # for _ in range(m):
    #     a, b, c = map(int, input().split())
    #     # a 노드에서 b 노드로 가는 비용이 c라는 의미
    #     graph[a].append((b, c))
    for i in range(n):
        for j in range(n):
            if i==j:
                distances[i, j] = 0
            else:
                # 여행 장소 위도, 경도(출발지)
                start_location = (float(similar_tags[i]['lat']), float(similar_tags[i]['lon']))
                # 여행 장소 위도, 경도(다른 노드)
                other_location = (float(similar_tags[j]['lat']), float(similar_tags[j]['lon']))
                distance = haversine(start_location, other_location, unit='km')
                distances[i, j] = distance
    
    q = []
    # 시작 노드로 가기 위한 최단 경로는 0으로 설정하여 큐에 삽입
    heapq.heappush(q, (0, start))
    distance[start] = 0
    while q: # 큐가 비어있지 않다면
        # 가장 최단 거리가 짧은 노드에 대한 정보 꺼내기
        dist, now = heapq.heappop(q)
        # 현재 노드가 이미 처리된 적이 있는 노드라면 무시
        if distance[now] < dist:
            continue
        # 현재 노드와 연결된 다른 인접한 노드들을 확인
        for i in graph[now]:
            cost = dist + i[1]
            # 현재 노드를 거쳐서, 다른 노드로 이동하는 거리가 더 짧은 경우
            if cost < distance[i[0]]:
                distance[i[0]] = cost
                heapq.heappush(q, (cost, i[0]))
    return q

def print_course(q):
    # 다익스트라 알고리즘은 시작하기 전 모든 노드 간의 거리(또는 가중치)를 무한대로 설정하고 시작합니다.
    INF = np.inf
    for i in range(1, n + 1):
        # 도달할 수 없는 경우, 무한(INFINITY)라고 출력
        if distance[i] == INF:
            print("INFINITY")
        # 도달할 수 있는 경우, 거리를 출력
        else:
            print(distance[i])