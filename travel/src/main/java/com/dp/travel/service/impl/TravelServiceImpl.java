package main.java.com.dp.travel.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import main.java.com.dp.travel.data.dto.TravelDto;
import main.java.com.dp.travel.data.entity.Travel;
import main.java.com.dp.travel.data.repository.TravelRepository;
import main.java.com.dp.travel.service.TravelService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TravelServiceImpl implements TravelService{
    private TravelRepository travelRepository;

    @Autowired
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
        dto.setLocation(store.getLocation());
        dto.setLatitude(store.getLatitude());
        dto.setLongitude(store.getLongitude());
        return dto;
    }
}
