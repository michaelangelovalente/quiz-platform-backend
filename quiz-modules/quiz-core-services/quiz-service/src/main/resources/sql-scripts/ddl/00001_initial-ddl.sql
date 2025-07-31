-- Enable the pgcrypto extension to generate UUIDs automatically
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TYPE quiz_difficulty AS ENUM ('EASY', 'MEDIUM', 'HARD');
CREATE TYPE question_type AS ENUM ('MULTIPLE_CHOICE', 'SINGLE_CHOICE', 'TRUE_FALSE', 'CODING');

--
-- Table: quizzes
--
CREATE TABLE quizzes (
    -- Primary key --> BIGSERIAL hnadles auto-increment Long vals
                         id BIGSERIAL PRIMARY KEY,
    -- Ppublic-facing identifier for the quiz.
    -- Defaulting to gen_random_uuid() generates the UUID in the database.
                         public_id UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),

    -- Core quiz information.
                         title VARCHAR(255) NOT NULL,
                         description VARCHAR(500),
                         category VARCHAR(255),
                         difficulty quiz_difficulty NOT NULL,

    -- Timestamps for auditing, automatically managed by POSTGRESL
                         created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                         updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

--
-- Table: questions
-- Contains indivual questions --> each linked to quiz
--
CREATE TABLE questions (
                           id BIGSERIAL PRIMARY KEY,
                           text TEXT NOT NULL,
                           type question_type NOT NULL,
                           explanation TEXT,

    -- Foreign key (this) question to a quiz.
                           quiz_id BIGINT NOT NULL,

                           created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
                           updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    -- Foreign key constraint.
    -- ON DELETE CASCADE  --> if a quiz is deleted, all its questions are also deleted
                           CONSTRAINT fk_questions_quizzes
                               FOREIGN KEY (quiz_id)
                                   REFERENCES quizzes(id)
                                   ON DELETE CASCADE
);

--
-- Table: question_options
-- Stores the possible answer options for a question (for multiple/single choice types).
--  @ElementCollection  == QuestionEntity.
--
CREATE TABLE question_options (
                                  question_id BIGINT NOT NULL,
                                  option_text VARCHAR(255) NOT NULL,

    -- A question should not have the same option listed twice.
                                  PRIMARY KEY (question_id, option_text),

                                  CONSTRAINT fk_options_questions
                                      FOREIGN KEY (question_id)
                                          REFERENCES questions(id)
                                          ON DELETE CASCADE
);

--
-- Table: question_correct_answers
-- Storage fo Correct answer(s) for a question.
--
CREATE TABLE question_correct_answers (
                                          question_id BIGINT NOT NULL,
                                          correct_answer VARCHAR(255) NOT NULL,

                                          PRIMARY KEY (question_id, correct_answer),

                                          CONSTRAINT fk_correct_answers_questions
                                              FOREIGN KEY (question_id)
                                                  REFERENCES questions(id)
                                                  ON DELETE CASCADE
);

--
-- INDEXES: Fequently queried columns.
--
CREATE INDEX idx_quizzes_public_id ON quizzes(public_id);
CREATE INDEX idx_questions_quiz_id ON questions(quiz_id);

-- Optional: Function automatically updates the 'updated_at' timestamp on any row change.
--  TODO? Handle DB layer like now OR Move to application layer????
CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply ttrigger_set_Timestamp() on all tables that have an 'updated_at' column.
CREATE TRIGGER set_quizzes_timestamp
    BEFORE UPDATE ON quizzes
    FOR EACH ROW
    EXECUTE FUNCTION trigger_set_timestamp();

CREATE TRIGGER set_questions_timestamp
    BEFORE UPDATE ON questions
    FOR EACH ROW
    EXECUTE FUNCTION trigger_set_timestamp();