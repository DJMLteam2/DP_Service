package com.dp.travel.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dp.travel.data.dto.FastAPIAnswerDTO;
import com.dp.travel.data.dto.QuestionForm;
import com.dp.travel.service.SearchService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService{
    
    private static final String FASTAPI_MODEL_URL = "http://localhost:3000/getAnswer";

    public List<FastAPIAnswerDTO> searchViewController(QuestionForm questionForm) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // QuestionForm을 JSON으로 변환
        JSONObject requestBodyJson = new JSONObject();
        requestBodyJson.put("question", questionForm.getQuestion());
        requestBodyJson.put("area", questionForm.getArea());
        log.info("response: {}", requestBodyJson.toJSONString());


        HttpEntity<String> request = new HttpEntity<>(requestBodyJson.toJSONString(), headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.postForObject(FASTAPI_MODEL_URL, request, String.class);
            log.info("response: {}", response);

            List<FastAPIAnswerDTO> fastAPIAnswerDTOs = parseFastAPIResponse(response);
            log.info("Parsed response: {}", fastAPIAnswerDTOs);

            return fastAPIAnswerDTOs;
        } catch (Exception e) {
            log.error("Error while calling FastAPI: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<FastAPIAnswerDTO> parseFastAPIResponse(String response) {
        List<FastAPIAnswerDTO> fastAPIAnswerDTOs = new ArrayList<>();

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
            JSONArray recommendArray = (JSONArray) jsonObject.get("recommend");

            for (int i = 0; i < recommendArray.size(); i++) {
                JSONArray innerArray = (JSONArray) recommendArray.get(i);

                for (int j = 0; j < innerArray.size(); j++) {
                    JSONObject recommendObject = (JSONObject) innerArray.get(j);

                    FastAPIAnswerDTO fastAPIAnswerDTO = new FastAPIAnswerDTO();

                    fastAPIAnswerDTO.setId(Long.parseLong(recommendObject.get("id").toString()));
                    fastAPIAnswerDTO.setArea(recommendObject.get("area").toString());
                    fastAPIAnswerDTO.setTitle(recommendObject.get("title").toString());
                    fastAPIAnswerDTO.setSimilarity(Float.parseFloat(recommendObject.get("similarity").toString()));
                    fastAPIAnswerDTO.setCatchtitle(recommendObject.get("catchtitle").toString());
                    fastAPIAnswerDTO.setTreatMenu(recommendObject.get("treatMenu").toString());
                    fastAPIAnswerDTO.setTagName(recommendObject.get("tagName").toString());
                    fastAPIAnswerDTO.setAddr(recommendObject.get("addr").toString());
                    fastAPIAnswerDTO.setInfo(recommendObject.get("info").toString());
                    fastAPIAnswerDTO.setUseTime(recommendObject.get("useTime").toString());
                    fastAPIAnswerDTO.setConLike(recommendObject.get("conLike").toString());
                    fastAPIAnswerDTO.setConRead(recommendObject.get("conRead").toString());
                    fastAPIAnswerDTO.setConShare(recommendObject.get("conShare").toString());
                    fastAPIAnswerDTO.setLat(recommendObject.get("lat").toString());
                    fastAPIAnswerDTO.setLon(recommendObject.get("lon").toString());

                    fastAPIAnswerDTOs.add(fastAPIAnswerDTO);
                }
            }
        } catch (ParseException e) {
            log.error("Error parsing FastAPI response: {}", e.getMessage());
        }

        return fastAPIAnswerDTOs;
    }
}
