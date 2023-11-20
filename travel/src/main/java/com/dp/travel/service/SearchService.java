package com.dp.travel.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SearchService {

    private final String FASTAPI_MODEL_URL = "http://your-fastapi-model-url"; // FastAPI 모델의 엔드포인트 URL

    private final RestTemplate restTemplate;

    public SearchService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String search(String searchTerms) {
        // RestTemplate를 사용하여 FastAPI 모델에 검색어를 전달하고 결과를 받아옴
        String result = restTemplate.postForObject(FASTAPI_MODEL_URL, searchTerms, String.class);

        // 받아온 결과를 처리하거나 가공할 수 있음

        return result;
    }

    // public String search(String searchTerms) {
    //     // FastAPI 모델에 검색어를 전달하고 결과를 받아옴
    //     RestTemplate restTemplate = new RestTemplate();
    //     String result = restTemplate.postForObject(FASTAPI_MODEL_URL, searchTerms, String.class);

    //     // 받아온 결과를 처리하거나 가공할 수 있음

    //     return result;
    // }
}