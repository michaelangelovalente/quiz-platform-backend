package com.quizplatform.quiz.business.service;

import com.quizplatform.common.business.service.AbstractBasePublicService;
import com.quizplatform.quiz.business.domain.entity.QuizEntity;
import com.quizplatform.quiz.business.repository.QuizRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService extends AbstractBasePublicService<QuizEntity, Long> {

    @Getter private final QuizRepository repository;

    /**
     * Retrieves a paginated list of all QuizEntity objects, including their associated questions.
     *
     * @param pageable the pagination information, including page number, size, and sorting details
     * @return a {@link Page} containing a subset of QuizEntity objects based on the specified pagination criteria
     */
    @Override
    @Transactional(readOnly = true)
    public Page<QuizEntity> findAll(Pageable pageable) {
        List<QuizEntity> allQuizzes = repository.findAllWithQuestions();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allQuizzes.size());
        List<QuizEntity> pageContent = allQuizzes.subList(start, end);
        return new PageImpl<>(pageContent, pageable, allQuizzes.size());
    }

    /**
     * Archives a quiz (changes status to ARCHIVED)
     * @param publicId the public ID of the quiz to archive
     * @return the archived quiz
     */
    @Transactional
    public QuizEntity archiveQuiz(UUID publicId) {
        log.info("Archiving quiz with public ID: {}", publicId);
        return updateByPublicId(publicId, quiz -> {
            quiz.setStatus("ARCHIVED");
            log.info("Quiz '{}' has been archived", quiz.getTitle());
        });
    }
}