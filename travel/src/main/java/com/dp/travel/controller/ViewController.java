package com.dp.travel.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dp.travel.data.dto.FastAPIAnswerDTO;
import com.dp.travel.data.dto.QuestionForm;
import com.dp.travel.service.SearchService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ViewController {

    private final SearchService searchService;

    @Autowired
    public ViewController(SearchService searchService){
        this.searchService = searchService;
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

        return "redirect:/search";
    }
}