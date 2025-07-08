package com.quizplatform.results.business.service;

import com.quizplatform.results.business.domain.dto.ResultRequestDto;
import com.quizplatform.results.business.domain.dto.ResultResponseDto;
import com.quizplatform.results.business.domain.entity.ResultEntity;
import com.quizplatform.results.business.domain.mapper.ResultMapper;
import com.quizplatform.results.business.repository.ResultRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResultService {
    private final ResultRepository repository;
    private final ResultMapper mapper;

    public ResultService(ResultRepository repository, ResultMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    public List<ResultResponseDto> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ResultResponseDto> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO);
    }

    public Optional<ResultResponseDto> findBySessionId(Long sessionId) {
        return repository.findBySessionId(sessionId)
                .map(mapper::toDTO);
    }

    public ResultResponseDto save(ResultRequestDto requestDTO) {
        ResultEntity result = mapper.toEntity(requestDTO);
        result.setCreatedAt(LocalDateTime.now());
        ResultEntity saved = repository.save(result);
        return mapper.toDTO(saved);
    }
    public void delete (Long id){
        if(Objects.nonNull(id)) {
            repository.deleteById(id);
        }
    }
}
