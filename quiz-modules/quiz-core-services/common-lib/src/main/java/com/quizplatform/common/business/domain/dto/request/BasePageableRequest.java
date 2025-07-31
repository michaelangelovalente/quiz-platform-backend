package com.quizplatform.common.business.domain.dto.request;

import com.quizplatform.common.system.utils.CommonUtils;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.With;

import java.util.List;
import java.util.Objects;

/**
 *  Standardization for Rquest with pagination.
 */
@Builder
@With
public record BasePageableRequest(
    @Min(value = 0, message = "Page number cannot be non-negative")
    int page,
    
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    int size,
    
    @Size(max = 10, message = "Maximum 10 sort parameters allowed")
    List<String> sort,
    
    boolean allPages
) {

    public BasePageableRequest() {
        this(0, 20, List.of(), false);
    }
    public BasePageableRequest(int page, int size) {
        this(page, size, List.of(), false);
    }
    
    // Compact constructor for validation
    public BasePageableRequest {
        sort = Objects.requireNonNullElse(sort, List.of());
    }


    /**
     * Calculates the offset for database queries
     * Util for DB offset:
     *  e.g size = 20, page 2 --> OFFSET == 40. DB query search skips first 40 records and starts serch from 41
     */
    public long getOffset() {
        return (long) page * size;
    }
    
    public boolean hasSorting() {
        return CommonUtils.isEmptyOrNull(sort);
    }
    
    public static BasePageableRequest defaultRequest() {
        return new BasePageableRequest(0, 20, List.of(), false);
    }
    
    public static BasePageableRequest allRecords() {
        return new BasePageableRequest(0, 100, List.of(), true);
    }
}