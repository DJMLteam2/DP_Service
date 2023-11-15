package com.dp.travel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.stereotype.Controller;

import com.dp.travel.data.entity.Travel;
import com.dp.travel.data.repository.TravelRepository;
import com.dp.travel.service.TravelService;
// import com.dp.travel.data.dto.TravelDto;

// import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
// import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class TravelController {    
    private final TravelService travelService;
    private final TravelRepository travelRepository;
    
    @Autowired
    public TravelController(TravelService travelService, TravelRepository travelRepository){
        this.travelService = travelService;
        this.travelRepository = travelRepository;
    }

    // @GetMapping("/travel")
    // public String index(Model model) {        
    //     List<Travel> travelEntityList = (List<Travel>) travelRepository.findAll();
    //     model.addAttribute("travelList", travelEntityList);
    //     return "travel/index";
    // }

    @GetMapping("/travel")
    public String index(Model model) {
        List<Travel> travelEntityList = (List<Travel>) travelRepository.findAll();
        model.addAttribute("travelList", travelEntityList);

        // 라디오 버튼 항목을 모델에 추가
        List<String> radioOptions = Arrays.asList("Option1", "Option2", "Option3");
        model.addAttribute("radioOptions", radioOptions);

        return "travel/radio";
    }  

}
