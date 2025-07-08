package com.quizplatform.results.business.domain.entity;

import com.quizplatform.results.business.domain.enums.ResultStatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "results")
@Getter
@Setter
@Builder
@NoArgsConstructor()
@AllArgsConstructor()
public class ResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sessionId;

    private Double score;
    private String detailsJson;

    @Enumerated(EnumType.STRING)
    private ResultStatusEnum status;
    private LocalDateTime createdAt;

}
