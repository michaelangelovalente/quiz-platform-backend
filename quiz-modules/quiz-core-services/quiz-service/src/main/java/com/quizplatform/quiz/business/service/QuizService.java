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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService extends AbstractBasePublicService<QuizEntity, Long> {

    @Getter private final QuizRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Page<QuizEntity> findAll(Pageable pageable) {
        List<QuizEntity> allQuizzes = repository.findAllWithQuestions();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allQuizzes.size());
        List<QuizEntity> pageContent = allQuizzes.subList(start, end);
        return new PageImpl<>(pageContent, pageable, allQuizzes.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuizEntity> findByPublicId(UUID publicId) {
        log.debug("Finding quiz by public ID: {}", publicId);
        return repository.findByPublicId(publicId);
    }

//    /**
//     * Finds a quiz by its public ID with questions loaded
//     * Custom implementation that overrides the base method to include questions
//     * @param publicId the public ID of the quiz
//     * @return the quiz if found
//     */
//    @Override
//    public Optional<QuizEntity> findByPublicId(UUID publicId) {
//        log.debug("Finding quiz by public ID: {}", publicId);
//        return repository.findByPublicId(publicId);
//    }

//    /**
//     * Finds all quizzes
//     * @return list of all quizzes
//     */
//    @Transactional(readOnly = true)
//    public List<QuizEntity> findAll() {
//        log.debug("Finding all quizzes");
//        return quizRepository.findAll();
//    }

//    /**
//     * Updates an existing quiz
//     * @param publicId the public ID of the quiz to update
//     * @param updatedQuiz the updated quiz data
//     * @return the updated quiz
//     * @throws RuntimeException if quiz not found
//     */
//    public QuizEntity update(UUID publicId, QuizEntity updatedQuiz) {
//        log.debug("Updating quiz with public ID: {}", publicId);
//
//        var existingQuiz = quizRepository.findByPublicId(publicId)
//                .orElseThrow(() -> new RuntimeException("Quiz not found with public ID: " + publicId));
//
//        // Update fields (preserve ID and timestamps)
//        existingQuiz.setTitle(updatedQuiz.getTitle());
//        existingQuiz.setDescription(updatedQuiz.getDescription());
//        existingQuiz.setDifficulty(updatedQuiz.getDifficulty());
//        existingQuiz.setTimeLimit(updatedQuiz.getTimeLimit());
//        existingQuiz.setPassingScore(updatedQuiz.getPassingScore());
//        existingQuiz.setStatus(updatedQuiz.getStatus());
//        existingQuiz.setCategory(updatedQuiz.getCategory());
//
//        var savedQuiz = quizRepository.save(existingQuiz);
//        log.debug("Updated quiz with ID: {}", savedQuiz.getId());
//
//        return savedQuiz;
//    }
//
//    /**
//     * Deletes a quiz by its public ID
//     * @param publicId the public ID of the quiz to delete
//     * @throws RuntimeException if quiz not found
//     */
//    public void delete(UUID publicId) {
//        log.debug("Deleting quiz with public ID: {}", publicId);
//
//        var quiz = quizRepository.findByPublicId(publicId)
//                .orElseThrow(() -> new RuntimeException("Quiz not found with public ID: " + publicId));
//
//        quizRepository.delete(quiz);
//        log.debug("Deleted quiz with ID: {}", quiz.getId());
//    }
//
//    /**
//     * Finds quizzes by difficulty level
//     * @param difficulty the difficulty level
//     * @return list of quizzes with the specified difficulty
//     */
//    @Transactional(readOnly = true)
//    public List<QuizEntity> findByDifficulty(QuizDifficultyEnum difficulty) {
//        log.debug("Finding quizzes by difficulty: {}", difficulty);
//        return quizRepository.findByDifficulty(difficulty);
//    }
//
//    /**
//     * Finds all published quizzes ordered by creation date
//     * @return list of published quizzes
//     */
//    @Transactional(readOnly = true)
//    public List<QuizEntity> findPublishedQuizzes() {
//        log.debug("Finding all published quizzes");
//        return quizRepository.findPublishedQuizzesOrderByCreatedAt();
//    }
//
//    /**
//     * Finds quizzes by status
//     * @param status the quiz status
//     * @return list of quizzes with the specified status
//     */
//    @Transactional(readOnly = true)
//    public List<QuizEntity> findByStatus(String status) {
//        log.debug("Finding quizzes by status: {}", status);
//        return quizRepository.findByStatus(status);
//    }
//
//    /**
//     * Finds quizzes by category
//     * @param category the quiz category
//     * @return list of quizzes in the specified category
//     */
//    @Transactional(readOnly = true)
//    public List<QuizEntity> findByCategory(String category) {
//        log.debug("Finding quizzes by category: {}", category);
//        return quizRepository.findByCategoryIgnoreCase(category);
//    }
//
//    /**
//     * Checks if a quiz exists by its public ID
//     * @param publicId the public ID to check
//     * @return true if quiz exists, false otherwise
//     */
//    @Transactional(readOnly = true)
//    public boolean existsByPublicId(UUID publicId) {
//        log.debug("Checking if quiz exists by public ID: {}", publicId);
//        return quizRepository.existsByPublicId(publicId);
//    }
//
//    /**
//     * Counts total number of quizzes
//     * @return total count of quizzes
//     */
//    @Transactional(readOnly = true)
//    public long count() {
//        log.debug("Counting total quizzes");
//        return quizRepository.count();
//    }
//
//    /**
//     * Counts quizzes by status
//     * @param status the status to count
//     * @return count of quizzes with the specified status
//     */
//    @Transactional(readOnly = true)
//    public long countByStatus(String status) {
//        log.debug("Counting quizzes by status: {}", status);
//        return quizRepository.countByStatus(status);
//    }
//    @Override
//    public Page<QuizEntity> findAll(Pageable pageable) {
//        return null;
//    }
//
//    @Override
//    public void deleteById(Long aLong) {
//
//    }
//
//    @Override
//    public <R> Optional<R> findAndTransform(Long aLong, Function<QuizEntity, R> transformer) {
//        return Optional.empty();
//    }
}