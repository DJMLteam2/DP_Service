package main.java.com.dp.travel.service;

import main.java.com.dp.travel.data.dto.TravelDto;
import main.java.com.dp.travel.data.entity.Travel;

import java.util.List;

public interface TravelService {
    List<TravelDto> searchTravelByEvery(String every);
}
