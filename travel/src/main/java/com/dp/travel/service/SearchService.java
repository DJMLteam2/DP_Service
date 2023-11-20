package com.dp.travel.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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