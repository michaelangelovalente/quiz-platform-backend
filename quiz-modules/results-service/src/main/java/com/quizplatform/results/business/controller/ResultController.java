package com.quizplatform.results.business.controller;

import com.quizplatform.results.business.domain.dto.ResultRequestDto;
import com.quizplatform.results.business.domain.dto.ResultResponseDto;
import com.quizplatform.results.business.domain.entity.ResultEntity;
import com.quizplatform.results.business.service.ResultService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/result")
@CrossOrigin(origins = "*")
public class ResultController {
    private final ResultService resultService;

    public ResultController(ResultService service) {
        this.resultService = service;
    }

    @GetMapping
    public List<ResultResponseDto> getAllResults() {
        return resultService.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResultResponseDto> getResultById(@PathVariable Long id) {
        return resultService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ResultResponseDto> getResultBySessionId(@PathVariable Long sessionId) {
        return resultService.findBySessionId(sessionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ResultResponseDto> createResult(@RequestBody @Validated ResultRequestDto requestDTO) {
        ResultResponseDto saved = resultService.save(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        resultService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
