package com.dp.travel.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.domain.Sort;

import com.dp.travel.data.dto.FastAPIAnswerDTO;
import com.dp.travel.data.dto.QuestionForm;
import com.dp.travel.data.dto.TravelDTO;
import com.dp.travel.data.entity.Travel;
import com.dp.travel.data.repository.TravelRepository;
import com.dp.travel.service.SearchService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    private final TravelRepository travelRepository;

    @Autowired
    public SearchServiceImpl(TravelRepository travelRepository) {
        this.travelRepository = travelRepository;
    }

    // 로컬용
    // private static final String FASTAPI_MODEL_URL =
    // "http://localhost:4000/getAnswer";
    // 도커 컴포즈용
    // private static final String FASTAPI_MODEL_URL =
    // "http://fast_api_app:4000/getAnswer";
    // 도커 AWS용
    private static final String FASTAPI_MODEL_URL = "http://3.35.47.48:4000/getAnswer";

    @Override
    public TravelDTO searchInfo(Long id) {
        Optional<Travel> optionalTravel = Optional.ofNullable(travelRepository.queryBySpotId(id));
        Travel travel = optionalTravel.orElseThrow(() -> new RuntimeException("Travel not found for ID: " + id));
        TravelDTO travelDto = travel.toDTO(travel);

        return travelDto;
    }

    @Override
    public List<TravelDTO> findtop5_Info(String tag) {
        // 상위 5개의 정보를 가져오기 위한 Pageable 객체 생성
        PageRequest pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "SPOT_CONLIKE"));

        // 태그에 해당하는 여행 정보를 데이터베이스에서 검색
        List<Travel> travels = travelRepository.findtop5(tag, pageable);

        // 검색 결과를 TravelDTO 객체 리스트로 변환
        List<TravelDTO> travelDTOs = travels.stream()
                .map(travel -> (TravelDTO) travel.toDTO())
                .collect(Collectors.toList());

        return travelDTOs;
    }

    @Override
    public List<FastAPIAnswerDTO> searchViewController(QuestionForm questionForm, String TagName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // QuestionForm을 JSON으로 변환
        String requestBody;

        if (TagName == null) {
            requestBody = "{\"question\": \"" + questionForm.getQuestion() + "\", \"area\": \"" + questionForm.getArea()
                    + "\"}";
            log.info("requestBody = {}", requestBody);
        } else {
            requestBody = "{\"question\": \"" + questionForm.getQuestion() + ", " + TagName + "\", \"area\": \""
                    + questionForm.getArea() + "\"}";
            log.info("requestBody = {}", requestBody);
        }

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();

        String response = restTemplate.postForObject(FASTAPI_MODEL_URL, request, String.class);

        if (response != null && !response.isEmpty()) {
            log.info("fastapi responsed !");
        } else {
            log.error("null or empty...");
        }

        List<FastAPIAnswerDTO> fastAPIAnswerDTOs = parseFastAPIResponse(response);
        log.info("response Parsed !");

        return fastAPIAnswerDTOs;

    }

    private List<FastAPIAnswerDTO> parseFastAPIResponse(String response) {
        List<FastAPIAnswerDTO> fastAPIAnswerDTOs = new ArrayList<>();

        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
            JSONArray recommendArray = (JSONArray) jsonObject.get("recommend");

            for (int i = 0; i < recommendArray.size(); i++) {
                JSONArray innerArray = (JSONArray) recommendArray.get(i);

                if (innerArray.isEmpty()) {
                    log.warn("Inner array is empty at index {}", i);
                    continue;
                }
                for (int j = 0; j < innerArray.size(); j++) {
                    JSONObject recommendObject = (JSONObject) innerArray.get(j);

                    if (recommendObject.isEmpty()) {
                        log.warn("recommendObject is empty at index {}", j);
                        continue;
                    }
                    FastAPIAnswerDTO fastAPIAnswerDTO = new FastAPIAnswerDTO();

                    fastAPIAnswerDTO.setId(Long.parseLong(recommendObject.get("id").toString()));
                    fastAPIAnswerDTO.setArea(recommendObject.get("area").toString());
                    fastAPIAnswerDTO.setTitle(recommendObject.get("title").toString());
                    fastAPIAnswerDTO.setSimilarity((recommendObject.get("similarity").toString()));
                    fastAPIAnswerDTO.setCatchtitle(recommendObject.get("catchtitle").toString());
                    fastAPIAnswerDTO.setOverView(recommendObject.get("overView").toString());
                    fastAPIAnswerDTO.setTreatMenu(recommendObject.get("treatMenu").toString());
                    fastAPIAnswerDTO.setTagName(recommendObject.get("tagName").toString());
                    fastAPIAnswerDTO.setAddr(recommendObject.get("addr").toString());
                    fastAPIAnswerDTO.setInfo(recommendObject.get("info").toString());
                    fastAPIAnswerDTO.setUseTime(recommendObject.get("useTime").toString());
                    fastAPIAnswerDTO.setConLike(recommendObject.get("conLike").toString());
                    fastAPIAnswerDTO.setConRead(recommendObject.get("conRead").toString());
                    fastAPIAnswerDTO.setConShare(recommendObject.get("conShare").toString());
                    fastAPIAnswerDTO.setImgPath(recommendObject.get("imgPath").toString());
                    fastAPIAnswerDTO.setDetail(recommendObject.get("detail").toString());
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
