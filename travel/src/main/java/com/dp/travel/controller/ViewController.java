package com.dp.travel.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    private SearchService searchService;

    @Autowired
    public ViewController(SearchService searchService){
        this.searchService = searchService;
    }

    // main 페이지
    @GetMapping("/")
    public String main() {
        return "travel/main";
    }

    // fastapi 연동하여 모델값 받아오기
    @GetMapping("/index")
    public String Search(){
        return "travel/index";
    }

    // fastapi 연동하여 모델값 받아오기
    @PostMapping("/create")
    public String answer(QuestionForm questionForm, RedirectAttributes redirectAttributes) {
        List<FastAPIAnswerDTO> fastAPIAnswerDTOs = searchService.SearchViewController(questionForm, null);

        log.info(questionForm.getQuestion());
        log.info(questionForm.getArea());
        // Flash 속성 추가
        redirectAttributes.addFlashAttribute("articles", fastAPIAnswerDTOs);
        redirectAttributes.addFlashAttribute("questionForm", questionForm);

        // 리디렉션
        return "redirect:/index";
    }

    @PostMapping("/tagadd")
    public String tagAnswer(@ModelAttribute("questionForm") QuestionForm questionForm,
                            @RequestParam("tagName") String tagName, RedirectAttributes redirectAttributes){
        List<FastAPIAnswerDTO> fastAPIAnswerDTOs = searchService.SearchViewController(questionForm, tagName);
        log.info(questionForm.getQuestion());
        log.info(questionForm.getArea());
        log.info(tagName);

        // Flash 속성 추가
        redirectAttributes.addFlashAttribute("articles", fastAPIAnswerDTOs);
        redirectAttributes.addFlashAttribute("questionForm", questionForm);
        redirectAttributes.addFlashAttribute("tagName", tagName);

        return "redirect:/index";
    }
}
