package com.quizplatform.common.business.api;


import com.quizplatform.common.business.domain.BaseDto;
import com.quizplatform.common.business.domain.dto.request.BasePageableRequest;
import com.quizplatform.common.business.domain.dto.response.BaseListResponse;
import com.quizplatform.common.business.domain.dto.response.BaseResponse;
import com.quizplatform.common.business.domain.dto.response.GenericResponseDto;
import com.quizplatform.common.business.domain.entity.BaseEntity;
import com.quizplatform.common.business.mapper.BaseMapper;
import com.quizplatform.common.business.service.BaseService;
import com.quizplatform.common.system.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Supplier;

/**
 * Common Controller defines basic CRUD operations
 *
 * @param <E>           The entity type managed by this controller.
 * @param <RequestDTO>  The DTO type for incoming requests (Query/Request).
 * @param <ResponseDTO> The DTO type for outgoing responses (Response/Status).
 * @param <FilterDTO>   The DTO type for filtering operations.
 * @param <ID>          The type of the entity's identifier.
 */
@Slf4j
public abstract class BaseController<E extends BaseEntity<ID>, ID, RequestDTO extends BaseDto, ResponseDTO extends BaseDto, FilterDTO extends BaseDto> {

    protected abstract BaseService<E, ID> getService();

    protected abstract BaseMapper<E, RequestDTO, ResponseDTO, FilterDTO> getMapper();


    public BaseResponse<ResponseDTO> create(RequestDTO request) {
        try {
            E entity = getMapper().requestToEntity(request);
            E saved = getService().save(entity);
            return BaseResponse.success(getMapper().entityToResponse(saved));
        } catch (Exception e) {
            return BaseResponse.error("CREATION_ERROR", e.getMessage());
        }
    }

    public BaseListResponse<ResponseDTO> saveAll(
            List<RequestDTO> dtos) {

        return executeWithErrorHandling(() -> {
            List<E> entities = getMapper().listRequestToListEntity(dtos);
            List<E> saved = getService().saveAll(entities);
            List<ResponseDTO> responses = getMapper().listEntityToListResponse(saved);

            log.info("Batch created {} {}s", saved.size(), getResourceName());
            return BaseListResponse.success(responses);
        }, String.format("Failed to batch create %s", getResourceName()));
    }


    public BaseResponse<ResponseDTO> findById(ID id) {
        return executeWithErrorHandling(() ->
                        getService().findById(id)
                                .map(entity -> BaseResponse.success(getMapper().entityToResponse(entity)))
                                .orElse(BaseResponse.notFound(String.format("%s %s", getResourceName(), id.toString()))),
                String.format("Failed to retrieve %s", getResourceName())
        );
    }


    public BaseListResponse<ResponseDTO> findAll(
            FilterDTO filter,
            BasePageableRequest pageRequest) {

        return executeWithErrorHandling(() -> {
            Pageable pageable = createPageable(pageRequest);

            Page<E> page = getService().findAllWithFilter(filter, pageable);
            return getMapper().pageToBaseListResponse(page);
        }, "Failed to retrieve records");
    }


    public List<ResponseDTO> findAllAsList(
            FilterDTO filter,
            BasePageableRequest pageRequest) {

        return executeWithErrorHandling(() -> {
            if (pageRequest.allPages()) {
                List<E> allEntities = getService().findAllWithFilter(filter);
                return getMapper().listEntityToListResponse(allEntities);
            }

            Pageable pageable = createPageable(pageRequest);
            Page<E> page = getService().findAllWithFilter(filter, pageable);
            return getMapper().listEntityToListResponse(page.getContent());
        }, "Failed to retrieve list");
    }

    public BaseListResponse<ResponseDTO> findAll(FilterDTO filter) {
        BasePageableRequest defaultPageable = BasePageableRequest.defaultRequest();
        return findAll(filter, defaultPageable);
    }

    public BaseListResponse<ResponseDTO> findAll() {
        return executeWithErrorHandling(() -> {
            BasePageableRequest defaultPageable = BasePageableRequest.defaultRequest();
            Pageable pageable = createPageable(defaultPageable);

            Page<E> page = getService().findAll(pageable);
            return getMapper().pageToBaseListResponse(page);
        }, "Failed to retrieve all records");
    }


    public BaseResponse<ResponseDTO> update(
            ID id,
            RequestDTO dto) {

        return executeWithErrorHandling(() -> {
            E updated = getService().update(id, entity ->
                    getMapper().updateEntityFromRequest(dto, entity)
            );

            log.info("{} updated with id: {}", getResourceName(), id);
            return BaseResponse.success(getMapper().entityToResponse(updated));
        }, String.format("Failed to update %s", getResourceName()));
    }

    public BaseResponse<GenericResponseDto> deleteById(ID id) {
        return executeWithErrorHandling(() -> {
            if (!getService().existsById(id)) {
                return BaseResponse.notFound(String.format("%s with id: %s", getResourceName(), id.toString()));
            }

            getService().deleteById(id);
            log.info("{} deleted with id: {}", getResourceName(), id);
            return BaseResponse.deleted(getResourceName());
        }, String.format("Failed to delete %s", getResourceName()));
    }


    public BaseResponse<GenericResponseDto> deleteAll(List<ID> ids) {
        return executeWithErrorHandling(() -> {
            long deletedCount = getService().deleteAllByIds(ids);
            log.info("Deleted {} {}s", deletedCount, getResourceName());
            return BaseResponse.deleteAll(deletedCount);
        }, String.format("Failed to batch delete %s", getResourceName()));
    }

    public BaseResponse<GenericResponseDto> exists(ID id) {
        return BaseResponse.exists(getService().existsById(id));
    }

    public BaseResponse<GenericResponseDto> count(FilterDTO filter) {
        return executeWithErrorHandling(() ->
                        BaseResponse.count(getService().countWithFilter(filter))
                , String.format("Failed to count %s", getResourceName()));
    }

    public BaseResponse<GenericResponseDto> countAll() {
        return executeWithErrorHandling(() ->
                        BaseResponse.count(getService().count())
                , String.format("Failed to count all %s", getResourceName()));
    }


    // ------------ Helper methods ------------------------------

    /**
     * Create Pageable from BasePageableRequest
     */
    protected Pageable createPageable(BasePageableRequest pageRequest) {
        if (pageRequest.allPages()) {
            return Pageable.unpaged();
        }

        Sort sort = createSort(pageRequest.sort());
        return PageRequest.of(pageRequest.page(), pageRequest.size(), sort);
    }

    /**
     * Create Sort from string list
     */
    protected Sort createSort(List<String> sortParams) {
        if (CommonUtils.isEmptyOrNull(sortParams)) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = sortParams.stream()
                .map(this::parseSortParameter)
                .toList();

        return Sort.by(orders);
    }

    /**
     * Parse sort parameter (e.g., "name,asc" or "name,desc")
     */
    protected Sort.Order parseSortParameter(String sortParam) {
        String[] parts = sortParam.split(",");
        if (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())) {
            return Sort.Order.desc(parts[0].trim());
        }
        return Sort.Order.asc(parts[0].trim());
    }

    /**
     * Execute with consistent error handling
     */
    protected  <T> T executeWithErrorHandling(
            Supplier<T> operation,
            String errorMessage) {
        try {
            return operation.get();
        } catch (Exception e) {
            log.error(String.format("%s: %s", errorMessage, e.getMessage()), e);
            throw new RuntimeException(String.format("%s: %s", errorMessage, e.getMessage()), e);
        }
    }

    /**
     * Get resource name for logging/messages
     */
    protected abstract String getResourceName();

}
