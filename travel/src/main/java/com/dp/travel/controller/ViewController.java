package com.dp.travel.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import com.dp.travel.data.dto.QuestionForm;

@Controller
public class ViewController {

    @GetMapping("/view")
    public String index() {
        return "index"; // index.html
    }

    @PostMapping("/create")
    public String answer(QuestionForm form, Model model) {
        // Spring에서 FastAPI로 데이터를 전송하는 부분
        String apiUrl = "http://localhost:3000/getAnswer";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // QuestionForm을 JSON으로 변환
        String requestBody = "{\"question\": \"" + form.getQuestion() + "\", \"area\": \"" + form.getArea() + "\"}";
        System.out.println(requestBody);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.postForObject(apiUrl, request, String.class);

        // FastAPI로부터의 응답 처리
        System.out.println("Response from FastAPI: " + response);

        model.addAttribute("response", response);

        return "redirect:/index";  // 적절한 리다이렉션 처리
    }
}