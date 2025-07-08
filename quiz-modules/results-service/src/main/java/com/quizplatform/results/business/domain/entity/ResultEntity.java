package com.quizplatform.results.business.domain.entity;

import com.quizplatform.results.business.domain.enums.ResultStatusEnum;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "results")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sessionId;

    private Double score;

    @Enumerated(EnumType.STRING)
    private ResultStatusEnum status;

}
