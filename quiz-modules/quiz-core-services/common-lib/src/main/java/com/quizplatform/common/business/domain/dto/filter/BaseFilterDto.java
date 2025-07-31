package com.quizplatform.common.business.domain.dto.filter;

import com.quizplatform.common.business.domain.BaseDto;
import com.quizplatform.common.system.utils.CommonUtils;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
@Setter
@SuperBuilder
public abstract class BaseFilterDto<ID> implements BaseDto {
    // Temporal filters
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private LocalDateTime modifiedFrom;
    private LocalDateTime modifiedTo;

    // Common filters
    private List<ID> ids;
    private Boolean active;
    private String searchTerm;



    private List<SortCriteria> sortBy;

    // Pagination (can work with BasePageableRequest)
    @Min(0)
    private Integer page = 0;

    @Min(1)
    @Max(100)
    private Integer size = 20;

    public record SortCriteria(
            String field,
            Direction direction
    ) {
        public enum Direction {ASC, DESC}
    }

    // Helper methods
    public Optional<LocalDateTime> getCreatedFrom() {
        return Optional.ofNullable(createdFrom);
    }

    public Optional<LocalDateTime> getCreatedTo() {
        return Optional.ofNullable(createdTo);
    }

    public boolean hasDateRange() {
        return Stream.of(createdFrom, createdTo)
                .anyMatch(Objects::nonNull);
    }

    public boolean hasSearchTerm() {
        return !CommonUtils.isEmptyOrBlank(searchTerm);
    }
}