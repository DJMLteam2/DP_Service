package main.java.com.dp.travel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.stereotype.Controller;

import main.java.com.dp.travel.data.entity.Travel;
import main.java.com.dp.travel.data.repository.TravelRepository;
import main.java.com.dp.travel.service.TravelService;
import main.java.com.dp.travel.data.dto.TravelDto;

@Controller
public class TravelController {
    @Autowired
    private final TravelService travelService;
    private final TravelRepository travelRepository;
    
    @Autowired
    public TravelController(TravelService travelService, TravelRepository travelRepository){
        this.travelService = travelService;
        this.travelRepository = travelRepository;
    }

    @GetMapping("/travels")
    public String index(Model model) {
        List<Travel> travelEntityList = (List<Travel>)travelRepository.findAll();
        model.addAttribute("travelList", travelEntityList);
        return "travels/index";
    }

    @GetMapping("/travels/{every}")
    public String show(@PathVariable String every, Model model) {
        List<TravelDto> travelEntity = travelService.searchTravelByEvery(every);
        model.addAttribute("travelList", travelEntity);
        return "stores/index";
    }
    
}
