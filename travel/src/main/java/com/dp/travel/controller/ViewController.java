package com.dp.travel.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dp.travel.data.dto.FastAPIAnswerDTO;
import com.dp.travel.data.dto.QuestionForm;
import com.dp.travel.data.dto.TravelDTO;
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
    public String main(Model model) {
        model.addAttribute("questionForm", new QuestionForm());
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

    @PostMapping("/create")
    public String answer(
        @RequestParam(value = "imageValue", required = false) String imageText,
        @ModelAttribute("questionForm") QuestionForm questionForm,
        RedirectAttributes redirectAttributes) {

        if (imageText != null) {
            // "/mainImg" 엔드포인트의 경우 이미지 처리 로직 수행
            questionForm = new QuestionForm(imageText, "전체");
            log.info(questionForm.getArea());
            log.info(questionForm.getQuestion());
        }

        if (questionForm.getQuestion().isEmpty()){
            redirectAttributes.addFlashAttribute("errorMessage", "빈칸입니다! 하고싶은 여행을 작성해주세요!");
            return "redirect:/";
        }

        List<FastAPIAnswerDTO> fastAPIAnswerDTOs = searchService.searchViewController(questionForm, null);

        // Flash 속성 추가
        redirectAttributes.addFlashAttribute("searchResults", fastAPIAnswerDTOs);
        redirectAttributes.addFlashAttribute("questionForm", questionForm);

        for (int i = 0; i < fastAPIAnswerDTOs.size(); i++) {
            FastAPIAnswerDTO searchResult = fastAPIAnswerDTOs.get(i);
            redirectAttributes.addFlashAttribute("searchResult_" + (i + 1), searchResult);
        }

        System.out.println("서비스로 돌아왔다");

        // 리디렉션
        return "redirect:/search";
    }

    // fastapi 연동하여 모델값 받아오기
    // @PostMapping("/create")
    // public String answer(QuestionForm questionForm, RedirectAttributes redirectAttributes) {

    //     if (questionForm.getQuestion().isEmpty()){
    //         redirectAttributes.addFlashAttribute("errorMessage", "빈칸입니다! 하고싶은 여행을 작성해주세요!");

    //         return "redirect:/";
    //     }
        
    //     List<FastAPIAnswerDTO> fastAPIAnswerDTOs = searchService.searchViewController(questionForm, null);        
    //     // Flash 속성 추가
    //     redirectAttributes.addFlashAttribute("searchResults", fastAPIAnswerDTOs);
    //     redirectAttributes.addFlashAttribute("questionForm", questionForm);
    //     // System.out.println(fastAPIAnswerDTOs.size());

    //     for (int i =0; i < fastAPIAnswerDTOs.size(); i++){
    //         FastAPIAnswerDTO searchResult = fastAPIAnswerDTOs.get(i);
    //         redirectAttributes.addFlashAttribute("searchResult_"+(i+1), searchResult);
            
                
    //     }

    //     System.out.println("서비스로 돌아왔다");

    //     // 리디렉션
    //     return "redirect:/search";
    // }

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

    // @PostMapping("/mainImg")
    // public String imgAnswer(@RequestParam("imageValue") String imageText, RedirectAttributes redirectAttributes) {

    //     QuestionForm questionForm = new QuestionForm(imageText, "전체");
    //     log.info(questionForm.getArea());
    //     log.info(questionForm.getQuestion());

    //     List<FastAPIAnswerDTO> fastAPIAnswerDTOs = searchService.searchViewController(questionForm, null);

    //     // Flash 속성 추가
    //     redirectAttributes.addFlashAttribute("searchResults", fastAPIAnswerDTOs);
    //     redirectAttributes.addFlashAttribute("questionForm", questionForm);
    //     // 리디렉션
    //     return "redirect:/search";
    // }
}