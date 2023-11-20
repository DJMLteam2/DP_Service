package com.dp.travel.controller;

import com.dp.travel.service.SearchService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.dp.travel.data.dto.QuestionForm;
import com.dp.travel.data.dto.SearchInfoDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
@Slf4j
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping
    public ResponseEntity<QuestionForm> performSearch(@RequestBody QuestionForm questionForm) {
        log.info("controller Quest: "+questionForm.getQuestion());
        log.info("controller Area: "+questionForm.getArea());
        
        QuestionForm searchResult = searchService.search(questionForm);

        // 검색 결과를 프론트엔드로 반환
        return ResponseEntity.ok(searchResult);
    }

    // @PostMapping
    // public ResponseEntity<String> performSearch(@RequestBody String Name) {
    //     log.info(Name);
    //     // JSON 데이터를 반환하는 로직. 검색어를 받아와서 검색 서비스로 전달
    //     // String searchResult = searchService.search(Name);

    //     // 검색 결과를 프론트엔드로 반환
    //     return ResponseEntity.ok(Name);
    // }

    // @PostMapping
    // public ResponseEntity<String> performSearch(@RequestBody SearchInfoDTO searchInfoDTO) {
    //     // JSON 데이터를 반환하는 로직. 검색어를 받아와서 검색 서비스로 전달
    //     String searchResult = searchService.search(searchInfoDTO.getSearchTerms());

    //     // 검색 결과를 프론트엔드로 반환
    //     return ResponseEntity.ok(searchResult);
    // }
}