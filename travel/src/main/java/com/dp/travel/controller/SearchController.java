package com.dp.travel.controller;

import org.springframework.beans.factory.annotation.Autowired;

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