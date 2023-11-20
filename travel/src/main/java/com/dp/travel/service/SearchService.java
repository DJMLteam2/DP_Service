package com.dp.travel.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.dp.travel.data.dto.FastAPIAnswerDTO;
import com.dp.travel.data.dto.QuestionForm;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SearchService {

    private final String FASTAPI_MODEL_URL = "http://your-fastapi-model-url"; // FastAPI 모델의 엔드포인트 URL

    private final RestTemplate restTemplate;

    public SearchService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }
    public QuestionForm search(QuestionForm questionForm) {
        // RestTemplate를 사용하여 FastAPI 모델에 검색어를 전달하고 결과를 받아옴
        log.info("service Quest: "+questionForm.getQuestion());
        log.info("service Area: "+questionForm.getArea());
        QuestionForm result = restTemplate.postForObject(FASTAPI_MODEL_URL, questionForm, QuestionForm.class);

        // 받아온 결과를 처리하거나 가공할 수 있음

        return result;
    }

    public List<FastAPIAnswerDTO> SearchViewController(QuestionForm questionForm){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // QuestionForm을 JSON으로 변환
        String requestBody = "{\"question\": \"" + questionForm.getQuestion() + "\"}";

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        // String response = restTemplate.postForObject(FASTAPI_MODEL_URL, request, String.class);
        
        List<FastAPIAnswerDTO> fastAPIAnswerDTOs = new ArrayList<>();
        String response = "{\"question\":\"ㄴㅇ\",\"similar_tags\":[{\"title\":\"국립제주박물관\",\"area\":\"지역박물관\",\"overView\":\"2001년 6월 15일 개관한 국립제주박물관은 제주의 역사와 문화유산을 체계적으로 전시·보존·연구하는 고고·역사박물관이다. 국립제주박물관에서는 제주의 여러 유적에서 출토된 유물과 역사적 문물들을 중심으로 선사시대부터 조선시대까지 시대별 각 유적과 유물이 갖는 역사·문화적인 의의를 전시물에 담았다. 탐라문화의 전시공간을 특성화하여 독특하고 고유한 탐라문화를 체계적으로 선보이는 한편, 해마다 다양한 주제의 특별전을 개최하고 있다. 그 외에도 다양한 사회교육 프로그램을 마련하는 등 제주를 찾는 국내·외 관광객에게 제주 토착문화의 전개 과정을 체계적으로 보여주는 문화공간이 되도록 노력하고 있다.\",\"detail\":\"소개문 설명\",\"lat\":\"111.123456\",\"lon\":\"22.123456\",\"similarity\":0.0},{\"title\":\"파주팜랜드\",\"area\":\"지역박물관\",\"overView\":\"스위스 목장을 모티브로 하여 디자인된 파주팜랜드는 농업회사법인주식회사에서 운영하는 캠핑장이다. 주요 이용 시설은 동물농장 및 항아리 바비큐, 캠핑장과 베이커리 카페가 있다. 카페 3층 옥상에서는 멀리 강 건너 휴전선 너머 북한 전망도 가능하다. 각 시설에 따라 이용 시간에 따른 가격이 다르므로 홈페이지에서 확인하고 이용하는 것이 편리하다. 바비큐에 필요한 용품을 대여하고 캠프파이어 이벤트도 있다. 산책길에는 동물농장이 있어서 어린이들이 즐겁게 이용할 수 있다.\",\"detail\":\"소개문 설명2\",\"lat\":\"112.123456\",\"lon\":\"21.123456\",\"similarity\":0.0},{\"title\":\"새암공원\",\"area\":\"지역박물관\",\"overView\":\"운정신도시 한빛공원과 와석순환로 사이에 있다. 공원 내에 한용운, 이상, 김소월, 정지용 등 시인들의 시비 26개가 세워져 있는 문학 공원으로, 약 90,000여m² 규모에 원추리와 비비추 등의 군락지가 형성된 역사공원 3호이다. 남쪽으로는 소로가 있는 미니 숲이 있어 마치 산에 있는 느낌을 준다.\",\"detail\":\"소개문 설명3\",\"lat\":\"114.123456\",\"lon\":\"27.123456\",\"similarity\":0.0},{\"title\":\"스파플러스\",\"area\":\"지역박물관\",\"overView\":\" 이천 지역 최대의 랜드마크로 알려진 이천 스파플러스는 총 1만 평 규모를 자랑하며, 리조트형 호텔 워터파크를 테마로 아이의 눈높이에 맞는 인테리어와 물놀이 시설, 그리고 어른들이 편안하게 즐길 수 있는 스파 시설을 구성해 힐링과 여가, 레저를 한꺼번에 즐길 수 있는 복합 레저 휴양지이다. 미란다 호텔 스파플러스는 지질자원 연구원에서 공인한 중탄산나트륨형 온천수로 지하 1000m에서 용출되는 평균 33.9C 이상의 7.76PH 약알카리성 천연 온천수를 모든 객실 및 실내풀과 야외탕에 제공하고 있다.\",\"lat\":\"115.123456\",\"lon\":\"34.123456\",\"detail\":\"소개문 설명4\",\"similarity\":0.0},{\"title\":\"별내체육공원\",\"area\":\"지역박물관\",\"overView\":\"별내체육공원은 별내동에 자리하고 있다. 부속 시설들이 여러 개 있어 이용객들이 많이 찾는 곳이다. 부속 시설로는 별내체육공원 물놀이장, 축구장, 테니스장, 산책로, 농구장, 배구장, 배드민턴장, 인공암장, 휴게 쉼터, 광장 등이 있어 주민 생활에 편리함을 주고 있다. 운동 시설이 많고 덕송천이 바로 옆에 있어 산책하기도 좋은 곳이다.\",\"detail\":\"소개문 설명5\",\"lat\":\"119.123456\",\"lon\":\"18.123456\",\"similarity\":0.0}]}";

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
            JSONArray destinationInfoArray = (JSONArray) jsonObject.get("similar_tags");
            for(int i = 0; i < destinationInfoArray.size(); i++){
                JSONObject destinationInfo = (JSONObject) destinationInfoArray.get(i);
                FastAPIAnswerDTO fastAPIAnswerDTO = new FastAPIAnswerDTO();
                fastAPIAnswerDTO.setTitle(destinationInfo.get("title").toString());
                log.info(fastAPIAnswerDTO.getTitle());
                fastAPIAnswerDTO.setArea(destinationInfo.get("area").toString());
                log.info(fastAPIAnswerDTO.getArea());
                fastAPIAnswerDTO.setOverView(destinationInfo.get("overView").toString());
                log.info(fastAPIAnswerDTO.getOverView());
                fastAPIAnswerDTO.setDetail(destinationInfo.get("detail").toString());
                log.info(fastAPIAnswerDTO.getDetail());
                fastAPIAnswerDTO.setLat(destinationInfo.get("lat").toString());
                log.info(fastAPIAnswerDTO.getLat());
                fastAPIAnswerDTO.setLon(destinationInfo.get("lon").toString());
                log.info(fastAPIAnswerDTO.getLon());
                fastAPIAnswerDTO.setSimilarity(destinationInfo.get("similarity").toString());
                log.info(fastAPIAnswerDTO.getSimilarity());

                fastAPIAnswerDTOs.add(fastAPIAnswerDTO);

            }
        } catch (ParseException e) {
            log.info(e.getMessage());
            log.info("에러");
        }

        return fastAPIAnswerDTOs;
    }

    // public String search(String Name) {
    //     // RestTemplate를 사용하여 FastAPI 모델에 검색어를 전달하고 결과를 받아옴
    //     String result = restTemplate.postForObject(FASTAPI_MODEL_URL, Name, String.class);

    //     // 받아온 결과를 처리하거나 가공할 수 있음

    //     return result;
    // }



    // public String search(String searchTerms) {
    //     // FastAPI 모델에 검색어를 전달하고 결과를 받아옴
    //     RestTemplate restTemplate = new RestTemplate();
    //     String result = restTemplate.postForObject(FASTAPI_MODEL_URL, searchTerms, String.class);

    //     // 받아온 결과를 처리하거나 가공할 수 있음

    //     return result;
    // }
}