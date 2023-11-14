package com.dp.travel.service.impl;


import org.springframework.stereotype.Service;

import com.dp.travel.data.dto.TravelDto;
import com.dp.travel.data.entity.Travel;
import com.dp.travel.data.repository.TravelRepository;
import com.dp.travel.service.TravelService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TravelServiceImpl implements TravelService{
    private TravelRepository travelRepository;

    
    public TravelServiceImpl(TravelRepository travelRepository){
        this.travelRepository = travelRepository;
    }

    @Override
    public List<TravelDto> searchTravelByEvery(String every) {
        List<Travel> travels = travelRepository.queryByEvery(every);
        return travels.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<Travel> index() {
        return travelRepository.findAll();
    }

    private TravelDto convertToDTO(Travel travel){
        TravelDto dto = new TravelDto();
        dto.setLocation(travel.getLocation());
        dto.setLatitude(travel.getLatitude());
        dto.setLongitude(travel.getLongitude());
        return dto;
    }
}
