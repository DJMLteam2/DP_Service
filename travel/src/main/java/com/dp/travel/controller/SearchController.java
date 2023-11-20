package com.dp.travel.controller;

import com.dp.travel.service.SearchService;
import com.dp.travel.data.dto.SearchInfoDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping
    public ResponseEntity<String> performSearch(@RequestBody SearchInfoDTO searchInfoDTO) {
        // JSON 데이터를 반환하는 로직. 검색어를 받아와서 검색 서비스로 전달
        String searchResult = searchService.search(searchInfoDTO.getSearchTerms());

        // 검색 결과를 프론트엔드로 반환
        return ResponseEntity.ok(searchResult);
    }
}