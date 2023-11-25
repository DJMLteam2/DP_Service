package com.dp.travel.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dp.travel.data.dto.FastAPIAnswerDTO;
import com.dp.travel.data.dto.QuestionForm;
import com.dp.travel.service.SearchService;

@Controller
public class ViewController {

    private SearchService searchService;

    @Autowired
    public ViewController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "travel/index";
    }

    @PostMapping("/create")
    public String answer(QuestionForm questionForm, RedirectAttributes redirectAttributes) {
        List<FastAPIAnswerDTO> fastAPIAnswerDTOs = searchService.SearchViewController(questionForm);

        // Flash 속성 추가
        for (int i = 0; i < Math.min(fastAPIAnswerDTOs.size(), 5); i++) {
            FastAPIAnswerDTO article = fastAPIAnswerDTOs.get(i);
            redirectAttributes.addFlashAttribute("article_" + (i + 1), article);
        }

        // 리디렉션
        return "redirect:/";
    }

}
