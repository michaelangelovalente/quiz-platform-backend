package  com.quizplatform.quiz.business.repository;

import com.quizplatform.common.business.repository.BaseRepository;
import com.quizplatform.quiz.business.domain.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends BaseRepository<QuestionEntity, Long> {}