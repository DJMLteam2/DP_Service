package com.dp.travel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dp.travel.data.entity.TravelSpotEntity;
import com.dp.travel.data.repository.TravelSpotRepository;

@Service
public class TravelSpotService {

    private final TravelSpotRepository travelSpotRepository;

    @Autowired
    public TravelSpotService(TravelSpotRepository travelSpotRepository) {
        this.travelSpotRepository = travelSpotRepository;
    }

    public List<TravelSpotEntity> findTitle(String spotTitle) {
        return travelSpotRepository.findTitle(spotTitle);
    }
}