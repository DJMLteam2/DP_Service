package com.dp.travel.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/index")
    public String index() {
        return "travel/index";
    }

    @GetMapping("/detail")
    public String detail() {
        return "travel/detail";
    }

    @GetMapping("/dash")
    public String dash() {
        return "travel/dashboard";
    }


    // fastapi 연동하여 모델값 받아오기
    @PostMapping("/create")
    public String answer(QuestionForm questionForm, RedirectAttributes redirectAttributes) {
        List<FastAPIAnswerDTO> fastAPIAnswerDTOs = searchService.SearchViewController(questionForm);
        
        // Flash 속성 추가
        redirectAttributes.addFlashAttribute("articles", fastAPIAnswerDTOs);

        // 리디렉션
        return "redirect:/index";
    }
}
