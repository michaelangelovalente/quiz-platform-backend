package  com.quizplatform.quiz.business.repository;

import com.quizplatform.common.business.repository.BaseRepository;
import com.quizplatform.quiz.business.domain.entity.QuestionEntity;
import com.quizplatform.quiz.business.domain.enums.QuestionTypeEnum;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends BaseRepository<QuestionEntity, Long> {
    
    @Query("SELECT q FROM QuestionEntity q WHERE q.quiz.id = :quizId ORDER BY q.id")
    List<QuestionEntity> findByQuizId(@Param("quizId") Long quizId);
    
    @Query("SELECT q FROM QuestionEntity q WHERE q.type = :type ORDER BY q.createdAt DESC")
    List<QuestionEntity> findByType(@Param("type") QuestionTypeEnum type);
    
    @Query("SELECT COUNT(q) FROM QuestionEntity q WHERE q.quiz.id = :quizId")
    Long countByQuizId(@Param("quizId") Long quizId);
    
    @Query("SELECT q FROM QuestionEntity q WHERE q.points >= :minPoints ORDER BY q.points DESC")
    List<QuestionEntity> findByMinimumPoints(@Param("minPoints") Integer minPoints);
    
    @Query("SELECT q FROM QuestionEntity q WHERE q.quiz.id = :quizId AND q.type = :type ORDER BY q.id")
    List<QuestionEntity> findByQuizIdAndType(@Param("quizId") Long quizId, @Param("type") QuestionTypeEnum type);
    
    @Query("SELECT SUM(q.points) FROM QuestionEntity q WHERE q.quiz.id = :quizId")
    Integer getTotalPointsByQuizId(@Param("quizId") Long quizId);
}