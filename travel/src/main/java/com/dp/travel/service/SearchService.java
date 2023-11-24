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

    private final String FASTAPI_MODEL_URL = "http://localhost:3000/getAnswer"; // FastAPI 모델의 엔드포인트 URL

    public List<FastAPIAnswerDTO> SearchViewController(QuestionForm questionForm, String TagName){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestBody = "";

        if(TagName == null){
            // QuestionForm을 JSON으로 변환
            requestBody = "{\"question\": \"" + questionForm.getQuestion() + "\", \"area\": \"" + questionForm.getArea() + "\"}";
        }
        else{
            requestBody = "{\"question\": \"" + questionForm.getQuestion() + ", " + TagName + "\", \"area\": \"" + questionForm.getArea() + "\"}";
        }
        log.info(requestBody);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.postForObject(FASTAPI_MODEL_URL, request, String.class);
        
        List<FastAPIAnswerDTO> fastAPIAnswerDTOs = new ArrayList<>();
        
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