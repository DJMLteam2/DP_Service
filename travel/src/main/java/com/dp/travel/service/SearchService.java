package com.dp.travel.service;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dp.travel.data.dto.FastAPIAnswerDTO;
import com.dp.travel.data.dto.QuestionForm;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SearchService {

    private final String FASTAPI_MODEL_URL = "http://localhost:3000/getAnswer"; // FastAPI 모델의 엔드포인트 URL

    private final RestTemplate restTemplate;

    @Autowired
    public SearchService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public QuestionForm search(QuestionForm questionForm) {
        // RestTemplate를 사용하여 FastAPI 모델에 검색어를 전달하고 결과를 받아옴
        log.info("service Quest: " + questionForm.getQuestion());
        log.info("service Area: " + questionForm.getArea());
        QuestionForm result = restTemplate.postForObject(FASTAPI_MODEL_URL, questionForm, QuestionForm.class);

        // 받아온 결과를 처리하거나 가공할 수 있음

        return result;
    }

    public List<FastAPIAnswerDTO> SearchViewController(QuestionForm questionForm) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // QuestionForm을 JSON으로 변환
        String requestBody = "{\"question\": \"" + questionForm.getQuestion() + "\", \"area\": \""
                + questionForm.getArea() + "\"}";

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.postForObject(FASTAPI_MODEL_URL, request, String.class);

        List<FastAPIAnswerDTO> fastAPIAnswerDTOs = new ArrayList<>();

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
            JSONArray destinationInfoArray = (JSONArray) jsonObject.get("similar_tags");
            for (int i = 0; i < destinationInfoArray.size(); i++) {
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

    public List<String> processFastAPIResult(List<FastAPIAnswerDTO> fastAPIAnswerDTOs) {
        List<String> titles = new ArrayList<>();

        int itemsToProcess = Math.min(fastAPIAnswerDTOs.size(), 5);

        for (int i = 0; i < itemsToProcess; i++) {
            FastAPIAnswerDTO article = fastAPIAnswerDTOs.get(i);
            titles.add(article.getTitle());
        }

        return titles;
    }
}