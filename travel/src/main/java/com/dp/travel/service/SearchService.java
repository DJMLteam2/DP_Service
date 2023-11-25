package com.dp.travel.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.dp.travel.data.dto.TravelDTO;
import com.dp.travel.data.entity.Travel;
import com.dp.travel.data.dto.QuestionForm;
import com.dp.travel.data.repository.TravelRepository;
import com.dp.travel.data.repository.TravelTitleRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SearchService {

    private final String FASTAPI_MODEL_URL = "http://localhost:3000/getAnswer"; // FastAPI 모델의 엔드포인트 URL
    private TravelRepository travelRepository;
    private TravelTitleRepository travelTitleRepository;

    @Autowired
    public SearchService(TravelRepository travelRepository, TravelTitleRepository travelTitleRepository){
        this.travelRepository = travelRepository;
        this.travelTitleRepository = travelTitleRepository;
    }

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

    public List<TravelDTO> SearchInfoController(QuestionForm questionForm, String TagName){
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
        
        List<Travel> travels = new ArrayList<>();
        try{
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
            JSONArray destinationInfoArray = (JSONArray) jsonObject.get("similar_tags");
            for(int i = 0; i < destinationInfoArray.size(); i++){
                JSONObject destinationInfo = (JSONObject) destinationInfoArray.get(i);
                Travel travel = new Travel();
                // 이부분에 장소 ID 시
                // Long SpotId = Long.parseLong(destinationInfo.get("id").toString());
                // travel = travelRepository.queryBySpotId(SpotId);

                // 장소 리턴 시
                String SpotTitle = destinationInfo.get("title").toString();
                travel = travelTitleRepository.queryBySpotTitle(SpotTitle);

                travels.add(travel);
            }
        }catch (ParseException e){
            log.info(e.getMessage());
            log.info("에러");
        }
        
        return travels.stream().map(this::convertDTO).collect(Collectors.toList());
    }

    // 서비스 내에서만 사용하는 함수
    private TravelDTO convertDTO(Travel travel){
        TravelDTO dto = new TravelDTO();
        dto.setSPOT_ID(travel.getSPOT_ID());
        dto.setSPOT_CITY(travel.getSPOT_CITY());
        dto.setSPOT_CITY_CODE(travel.getSPOT_CITY_CODE());
        dto.setSPOT_CITY_CONTENT_TYPE(travel.getSPOT_CITY_CONTENT_TYPE());
        dto.setSPOT_TITLE(travel.getSPOT_TITLE());
        dto.setSPOT_CATCHTITLE(travel.getSPOT_CATCHTITLE());
        dto.setSPOT_OVERVIEW(travel.getSPOT_OVERVIEW());
        dto.setSPOT_TREATMENU(travel.getSPOT_TREATMENU());
        dto.setSPOT_CONLIKE(travel.getSPOT_CONLIKE());
        dto.setSPOT_CONREAD(travel.getSPOT_CONREAD());
        dto.setSPOT_CONSHARE(travel.getSPOT_CONSHARE());
        dto.setSPOT_IMGPATH(travel.getSPOT_IMGPATH());
        dto.setSPOT_ADDR(travel.getSPOT_ADDR());
        dto.setSPOT_INFOCENTER(travel.getSPOT_INFOCENTER());
        dto.setSPOT_PARKING(travel.getSPOT_PARKING());
        dto.setSPOT_USETIME(travel.getSPOT_USETIME());
        dto.setSPOT_TAGNAME(travel.getSPOT_TAGNAME());
        dto.setSPOT_DETAIL(travel.getSPOT_DETAIL());
        dto.setSPOT_LON(travel.getSPOT_LON());
        dto.setSPOT_LAT(travel.getSPOT_LAT());
        return dto;
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