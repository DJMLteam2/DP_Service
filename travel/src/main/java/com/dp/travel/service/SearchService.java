package com.dp.travel.service;
import java.util.List;

import com.dp.travel.data.dto.FastAPIAnswerDTO;
import com.dp.travel.data.dto.QuestionForm;


public interface SearchService {

    List<FastAPIAnswerDTO> searchViewController(QuestionForm questionForm);
    

}