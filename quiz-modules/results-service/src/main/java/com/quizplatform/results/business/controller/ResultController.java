package com.quizplatform.results.business.controller;

import com.quizplatform.results.business.domain.entity.ResultEntity;
import com.quizplatform.results.business.service.ResultService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/result")
@CrossOrigin(origins = "*")
public class ResultController {
    private final ResultService resultService;

    public ResultController(ResultService service) {
        this.resultService = service;
    }

}
