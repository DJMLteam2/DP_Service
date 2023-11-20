package com.dp.travel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.dp.travel.data.dto.FastAPIAnswerDTO;
import com.dp.travel.data.dto.QuestionForm;
import com.dp.travel.service.SearchService;

@Controller
public class ViewController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/view")
    public String index() {
        return "index"; // index.html
    }

    @PostMapping("/create")
    public String answer(QuestionForm questionForm, Model model) {
        List<FastAPIAnswerDTO> fastAPIAnswerDTOs = searchService.SearchViewController(questionForm);
        return "redirect:/index";  // 적절한 리다이렉션 처리
    }
}