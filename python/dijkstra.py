import heapq
from haversine import haversine
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
    - visited_nodes: List. 방문할 여행지의 순서.
    - distances: List. 한 여행지로부터 다른 여행지까지의 거리. 단위는 km이며, 한 여행지에서 같은 여행지까지의 거리는 0으로 저장됩니다.
    """
    # 다익스트라 알고리즘은 시작하기 전 모든 노드 간의 거리(또는 가중치)를 무한대로 설정하고 시작합니다.
    # INF = np.inf

    # 노드의 개수, 간선의 개수(노드 n개, 간선은 nC2 = (n * (n-1) / 2)개로 설정)
    n = len(similar_tags)
    m = n * (n - 1) / 2
    
    # 각 노드에 연결되어 있는 노드에 대한 정보를 담는 리스트를 만들기
    graph = [[] for i in range(n + 1)]
    # 최단 거리 테이블을 모두 무한으로 초기화
    distances = np.zeros((n, n))

    # 모든 간선 정보 입력받기 - 거리를 입력
    # 각 노드로부터 다른 노드까지의 거리 입력(자기 자신에 해당하는 거리는 0으로 설정)
    for i in range(n):
        for j in range(n):
            if i == j: # 자기 자신
                pass
            else: # 그 외의 다른 노드들
                # 여행 장소 위도, 경도(출발지)
                start_location = (float(similar_tags[i]['lat']), float(similar_tags[i]['lon']))
                # 여행 장소 위도, 경도(다른 노드)
                other_location = (float(similar_tags[j]['lat']), float(similar_tags[j]['lon']))
                distance = haversine(start_location, other_location, unit='km')
                distances[i, j] = distance
    
    visited_nodes = greedy(distances, start_node=0)
    return visited_nodes, distances

def greedy(distances, start_node):
    num_nodes = distances.shape[0]      # 노드의 수( = n)
    visited_nodes = [start_node]        # 방문한 노드 목록
    current_node = start_node           # 현재 위치한 노드
    
    INF = np.inf

    while len(visited_nodes) < num_nodes:
        min_distance = INF
        for node in range(num_nodes):
            if node not in visited_nodes and distances[current_node, node] < min_distance:
                min_distance = distances[current_node, node]
                next_node = node
        visited_nodes.append(next_node)
        current_node = next_node

    return visited_nodes

if __name__ == "__main__":
    example_list = [{"title":"위봉폭포","overView":"위봉산(높이 524m)에서 발원한 물줄기가 60m 높이의 바위 절벽으로 떨어지며 형성된 2단 폭포이다. 2단으로 쏟아지는 물줄기가 마치 명주실을 늘어놓은 것과 같은 풍경을 자아내며, 폭포 주변으로 참나무, 서어나무, 단풍나무 등의 식생 군락들이 폭포와 어우러져 경관을 만들고 있다. 위봉폭포는 예로부터 비경으로서 명성이 높아 완산 8경의 하나로 불리었으며 조선 후기 판소리 명창 중의 한 사람인 권삼득이 득음을 했던 곳으로 유명하다. 위봉폭포 일원은 주변의 산천과 기암절벽 등이 어우러져 사계절 내내 아름다운 자연경관을 자아낼 뿐만 아니라, 이를 즐겨온 많은 사람의 기록이 확인되고 있어, 역사적 가치 또한 뛰어나다고 평가받아 2021년 6월 9일 명승으로 지정되었다.","similarity":0.9518505365777218},{"title":"오성한옥마을","overView":"종남산과 위봉산이 병풍처럼 둘러쌓고 있는 천혜의 자연경관에 한옥고택 등 전통한옥 20여 채가 자리 잡고 있는 한옥마을로 실제 주민들의 거주공간이자 품격 있는 카페, 갤러리, 숲속 체험길 등 체험공간이 공존하는 전통과 문화, 자연과 더불어 휴식하는 힐링 장소로 전국적인 명성과 인지도를 높여가며 많은 관광객이 찾고있는 완주군의 가장 핫한 관광명소이다. 마을 내 예술인들이 직접 운영하는 한옥고택 등을 활용한 작은 갤러리와 감성카페, 독립서점 등이 위치해 있어 자연경관과 함께 즐기는 소소한 감성여행지이다. 2019년 BTS(방탄소년단)이 1주일간 머무르며 ‘BTS 2019 SUMMER PACKAGE’ 뮤직비디오 및 화보집을 촬영한 장소로 입소문이 나면서 BTS 팬클럽 아미(ARMY)는 물론 일반관광객의 방문이 지속적으로 이어지고 있다.","similarity":0.8767818982717643},{"title":"서후리숲","overView":"2014년 개장한 10만 평 규모의 수목원이다. 자연 그대로의 숲을 모토로 하면서도 자연스러움을 해치지 않는 조경이 섬세하게 느껴지는 아름다운 공간이다. 숲 내부는 나무들의 수종에 따라 잣나무숲, 비밀의 숲, 단풍나무숲, 메타세쿼이아숲, 은행나무숲, 층층나무숲, 자작나무숲, 철쭉나무 전망대로 구성되어 있다. 삼림욕 산책 코스는 길이에 따라 1시간 길이의 A코스와 30분 길이의 B코스로 나뉜다.","similarity":0.8329176477939366},{"title":"맹방해변","overView":"삼척 시내에서 7km 가량의 거리에 있는 시범해수욕장이며 맹방 관광지 내에 속한 지역으로서 기반 도로 시설이 잘 정비되고 관광지 조성 공사가 진행 중인 곳으로 이미 청정해변의 이미지가 전국에 잘 알려져 있다. 개장 기간 중에는 명사십리 달리기대회를 비롯하여 맨손 송어 잡기 등 다양한 해변 이벤트가 개최되어 피서객의 호응도를 높이는 한편, 인접지역의 골프연습장이 스포츠 산실 역할을 하고 있다. 최근에는 BTS 싱글 앨범 버터(Butter)의 앨범 재킷 촬영지로 알려져 더 많은 관심을 모으고 있다. 촬영 당시 사용했던 소품들을 그대로 재현해 포토존으로 운영 중이다.","similarity":0.7828477948134986},{"title":"주문진읍 BTS 앨범사진 촬영지 (버스정류장)","overView":"BTS 버스 정류장은 강릉 주문진 해변에 위치한 BTS 앨범재킷 촬영장소로 많은 국내외 관광객들이 찾고 있는 핫 플레이스다.K-POP 최초로 미국 빌보드 음반차트 1위를 기록한 방탄소년단의 앨범자켓 사진 속에서 등장한 바닷가 버스 정류장이다. 촬영 당시 임시로 만들었다가 철거된 것을 관광객들을 위한 포토존으로 재현해 놓았다. 이미 방탄소년단의 국내외 팬들 사이에선 폭발적인 반응을 얻고 있으며, 주문진해변의 이색 볼거리로 떠오르고 있다.[출처:강릉시청]","similarity":0.7322688974697387}]
    dijkstra(example_list)