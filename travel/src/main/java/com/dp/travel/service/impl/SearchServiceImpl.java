package com.dp.travel.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dp.travel.data.dto.FastAPIAnswerDTO;
import com.dp.travel.data.dto.QuestionForm;
import com.dp.travel.data.dto.TagDTO;
import com.dp.travel.data.dto.TravelDTO;
import com.dp.travel.data.entity.Tag;
import com.dp.travel.data.entity.Travel;
import com.dp.travel.data.repository.TagRepository;
import com.dp.travel.data.repository.TravelRepository;
import com.dp.travel.service.SearchService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService{
    
    @Autowired
    private TravelRepository travelRepository;
    private TagRepository tagRepository;
    private ConversionService conversionService;

    // 로컬용
    // private static final String FASTAPI_MODEL_URL = "http://localhost:4000/getAnswer";
    // 도커 컴포즈용
    // private static final String FASTAPI_MODEL_URL = "http://fast_api_app:4000/getAnswer";
    // 도커 AWS용
    private static final String FASTAPI_MODEL_URL = "http://3.35.47.48:4000/getAnswer";

    @Autowired
    public SearchServiceImpl(TravelRepository travelRepository, TagRepository tagRepository, ConversionService conversionService){
        this.travelRepository = travelRepository;
        this.tagRepository = tagRepository;
        this.conversionService = conversionService;
    }

    @Override
    public TravelDTO searchInfo(Long id){
        Optional<Travel> optionalTravel = Optional.ofNullable(travelRepository.queryBySpotId(id));
        Travel travel = optionalTravel.orElseThrow(() -> new RuntimeException("Travel not found for ID: " + id));
        TravelDTO travelDto = travel.toDTO(travel);

        return travelDto;
    }

    @Override
    public List<FastAPIAnswerDTO> searchViewController(QuestionForm questionForm, String TagName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); 

        // QuestionForm을 JSON으로 변환
        String requestBody;

        if(TagName == null){
            requestBody = "{\"question\": \"" + questionForm.getQuestion() + "\", \"area\": \"" + questionForm.getArea() + "\"}";
            log.info("requestBody = {}", requestBody);
        }
        else{
            requestBody = "{\"question\": \"" + questionForm.getQuestion() + ", " + TagName + "\", \"area\": \"" + questionForm.getArea() + "\"}";
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


    // 태그 랜덤으로 주는 함수 생성: randomTag, convertTagDTO, RandomTagIdList
    @Override
    public List<TagDTO> randomTag(){
        List<Tag> tags = new ArrayList<>();
        Set<Long> TagIdList = RandomTagIdList(10);
        for(Long TagId : TagIdList){
            Tag tag = tagRepository.ququeryByTag(TagId);
            tags.add(tag);
        }
        return tags.stream().map(this::convertTagDTO).collect(Collectors.toList());
    }
    private TagDTO convertTagDTO(Tag tag){
        TagDTO tagDTO = new TagDTO();
        tagDTO.setTagID(tag.getTagID());
        tagDTO.setTagName(tag.getTagName());
        tagDTO.setTagTheme(tag.getTagTheme());
        return tagDTO;
    }
    private Set<Long> RandomTagIdList(int count){
        Set<Long> uniqueNumbers = new HashSet<>();
        Random random = new Random();
        int partition = (count / 2);
        long minfirst = 1L;
        long maxfirst = 20L;
        long minsecond = 21L;
        long maxsecond = 60L;
        while(uniqueNumbers.size() < count){
            if(uniqueNumbers.size() < partition){
                long randomNumber = minfirst + (long) (random.nextDouble() * (maxfirst - minfirst + 1));
                uniqueNumbers.add(randomNumber);
            }
            else{
                long randomNumber = minsecond + (long) (random.nextDouble() * (maxsecond - minsecond + 1));
                uniqueNumbers.add(randomNumber);
            }
        }
        return uniqueNumbers;
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

    // top 10
    @Override
    public List<TravelDTO> queryByTop10() {
        List<Travel> travels = travelRepository.queryByTop10();
        List<TravelDTO> travelDTOs = new ArrayList<>();

        for (Travel travel : travels) {
            TravelDTO travelDTO = new TravelDTO();
            travelDTO.setSpotId(travel.getSPOT_ID());
            travelDTO.setSpotCity(travel.getSPOT_CITY());
            travelDTO.setSpotCityCode(travel.getSPOT_CITY_CODE());
            travelDTO.setSpotCityContentType(travel.getSPOT_CITY_CONTENT_TYPE());
            travelDTO.setSpotTitle(travel.getSPOT_TITLE());
            travelDTO.setSpotCatchTitle(travel.getSPOT_CATCHTITLE());
            travelDTO.setSpotOverview(travel.getSPOT_OVERVIEW());
            travelDTO.setSpotTreatMenu(travel.getSPOT_TREATMENU());
            travelDTO.setSpotConLike(travel.getSPOT_CONLIKE());
            travelDTO.setSpotConRead(travel.getSPOT_CONREAD());
            travelDTO.setSpotConShare(travel.getSPOT_CONSHARE());
            travelDTO.setSpotImgPath(travel.getSPOT_IMGPATH());
            travelDTO.setSpotAddr(travel.getSPOT_ADDR());
            travelDTO.setSpotInfoCenter(travel.getSPOT_INFOCENTER());
            travelDTO.setSpotParking(travel.getSPOT_PARKING());
            travelDTO.setSpotUseTime(travel.getSPOT_USETIME());
            travelDTO.setSpotTagName(travel.getSPOT_TAGNAME());
            travelDTO.setSpotDetail(travel.getSPOT_DETAIL());

            travelDTOs.add(travelDTO);
        }
        return travelDTOs;
    }
}
