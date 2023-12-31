package com.dp.travel.service;
import java.util.List;

import com.dp.travel.data.dto.FastAPIAnswerDTO;
import com.dp.travel.data.dto.QuestionForm;
import com.dp.travel.data.dto.TagDTO;
import com.dp.travel.data.dto.TravelDTO;



public interface SearchService {

    List<FastAPIAnswerDTO> searchViewController(QuestionForm questionForm, String TagName);

    TravelDTO searchInfo(Long id);
    List<TagDTO> randomTag();
    List<TravelDTO> queryByTop10();

}