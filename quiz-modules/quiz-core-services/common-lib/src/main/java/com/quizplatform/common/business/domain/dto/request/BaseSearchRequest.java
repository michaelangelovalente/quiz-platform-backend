package com.quizplatform.common.business.domain.dto.request;

import com.quizplatform.common.system.utils.CommonUtils;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.With;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 *  Standardization free text search
 */
@Builder
@With
public record BaseSearchRequest(
    int page,
    int size,
    List<String> sort,
    boolean allPages,

    @Size(max = 100, message = "Search query cannot exceed 100 characters")
    String query,

    @Size(max = 20, message = "Maximum 20 filters allowed")
    Map<String, Object> filters
) {

    public BaseSearchRequest() {
        this(0, 20, List.of(), false, null, Map.of());
    }

    public BaseSearchRequest(int page, int size) {
        this(page, size, List.of(), false, null, Map.of());
    }

    // Constructor with search query
    public BaseSearchRequest(String query) {
        this(0, 20, List.of(), false, query, Map.of());
    }

    public BaseSearchRequest {
        sort = Objects.requireNonNullElse(sort, List.of());
        filters = Objects.requireNonNullElse(filters, Map.of());

        // Sanitize query
        query = Optional.ofNullable(query)
            .map(String::trim)
            .filter(Predicate.not(String::isEmpty))
            .orElse(null);
    }

    public BasePageableRequest toPageableRequest() {
        return new BasePageableRequest(page, size, sort, allPages);
    }


    public long getOffset() {
        return (long) page * size;
    }

    public static BaseSearchRequest defaultRequest() {
        return new BaseSearchRequest();
    }

    public static BaseSearchRequest of(String query) {
        String trimmedQuery = CommonUtils.isEmptyOrBlank(query) ? query.trim() : null;
        return new BaseSearchRequest(trimmedQuery);
    }
}