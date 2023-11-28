package com.dp.travel.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.dp.travel.data.dto.FastAPIAnswerDTO;
import com.dp.travel.data.dto.QuestionForm;
import com.dp.travel.data.dto.TravelDTO;
import com.dp.travel.data.entity.Travel;
import com.dp.travel.service.SearchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ViewController {

    private final SearchService searchService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ViewController(SearchService searchService, ObjectMapper objectMapper){
        this.searchService = searchService;
        this.objectMapper = objectMapper;
    }

    // main 페이지
    @GetMapping("/")
    public String main() {
        return "travel/main";
    }
    // second 페이지
    @GetMapping("/search")
    public String search() {
        return "travel/mid";
    }
    // 상세 페이지
    @GetMapping("/search/{id}")
    public String detail(@PathVariable Long id, Model model){
        TravelDTO travelDto = searchService.searchInfo(id);
        model.addAttribute("travelDto", travelDto);

        return "travel/detail";
    }   


    // fastapi 연동하여 모델값 받아오기
    @PostMapping("/create")
    public String answer(QuestionForm questionForm, RedirectAttributes redirectAttributes) {

        if (questionForm.getQuestion().isEmpty()){
            redirectAttributes.addFlashAttribute("errorMessage", "빈칸입니다! 하고싶은 여행을 작성해주세요!");

            return "redirect:/";
        }
        
        List<FastAPIAnswerDTO> fastAPIAnswerDTOs = searchService.searchViewController(questionForm, null);        
        // Flash 속성 추가
        redirectAttributes.addFlashAttribute("searchResults", fastAPIAnswerDTOs);
        redirectAttributes.addFlashAttribute("questionForm", questionForm);
        // System.out.println(fastAPIAnswerDTOs.size());

        for (int i =0; i < fastAPIAnswerDTOs.size(); i++){
            FastAPIAnswerDTO searchResult = fastAPIAnswerDTOs.get(i);
            redirectAttributes.addFlashAttribute("searchResult_"+(i+1), searchResult);                
        }

        try {
            // 위도와 경도 정보만 담은 새로운 리스트 생성
            List<HashMap<String, Object>> locations = new ArrayList<>();
            for (FastAPIAnswerDTO dto : fastAPIAnswerDTOs) {
                HashMap<String, Object> location = new HashMap<>();
                location.put("lat", dto.getLat());
                location.put("lon", dto.getLon());
                locations.add(location);
            }

            // 위도와 경도 정보만 있는 JSON 문자열 생성
            ObjectMapper objectMapper = new ObjectMapper();
            String locationJson = objectMapper.writeValueAsString(locations);

            redirectAttributes.addFlashAttribute("locations", locationJson);
            System.out.println("jsonRecommendations created.");
        } catch (JsonProcessingException e) {
            log.error("JSON writing error", e);
        }
        
        System.out.println("서비스로 돌아왔다");

        // 리디렉션
        return "redirect:/search";
    }

    @PostMapping("/tagAdd")
    public String tagAnswer(QuestionForm questionForm, String tagName, RedirectAttributes redirectAttributes){

        List<FastAPIAnswerDTO> fastAPIAnswerDTOs = searchService.searchViewController(questionForm, tagName);
        log.info(tagName);

        questionForm.setQuestion(String.format("%s %s", questionForm.getQuestion(), tagName));
        log.info(questionForm.getQuestion());
        log.info(questionForm.getArea());

        // Flash 속성 추가
        redirectAttributes.addFlashAttribute("searchResults", fastAPIAnswerDTOs);
        redirectAttributes.addFlashAttribute("questionForm", questionForm);
        redirectAttributes.addFlashAttribute("tagName", tagName);

        for (int i =0; i < fastAPIAnswerDTOs.size(); i++){
            FastAPIAnswerDTO searchResult = fastAPIAnswerDTOs.get(i);
            redirectAttributes.addFlashAttribute("searchResult_"+(i+1), searchResult);
        }

        return "redirect:/search";
    }
}