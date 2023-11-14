package com.dp.travel.service;

import com.dp.travel.data.dto.TravelDto;


import java.util.List;

public interface TravelService {
    List<TravelDto> searchTravelByEvery(String every);
}
