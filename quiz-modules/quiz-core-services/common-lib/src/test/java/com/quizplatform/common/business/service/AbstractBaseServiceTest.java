package com.quizplatform.common.business.service;

import com.quizplatform.common.business.domain.BaseDto;
import com.quizplatform.common.business.domain.entity.BaseEntity;
import com.quizplatform.common.business.repository.BaseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@DisplayName("AbstractBaseService Test Suite with Fixtures")
class AbstractBaseServiceTest {

    // ------- TEST FIXTURES Concrete Impl. -----
    static class SimpleEntity extends BaseEntity<Long> {
        private String name;
        private String description;
        private Integer priority;

        public static SimpleEntity create(Long id, String name, String description, Integer priority) {
            SimpleEntity entity = new SimpleEntity();
            entity.setId(id);
            entity.name = name;
            entity.description = description;
            entity.priority = priority;
            return entity;
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getPriority() {
            return priority;
        }

        public void setPriority(Integer priority) {
            this.priority = priority;
        }

        @Override
        public String toString() {
            return String.format("SimpleEntity[id=%d, name='%s', priority=%d]",
                    getId(), name, priority);
        }
    }

    record SimpleFilterDto(
            String nameContains,
            Integer minPriority,
            Integer maxPriority
    ) implements BaseDto {
    }


    static class ConcreteSimpleService extends AbstractBaseService<SimpleEntity, Long> {
        private final BaseRepository<SimpleEntity, Long> repository;
        private final List<String> operationLog = new ArrayList<>(); // For Log String pattern

        ConcreteSimpleService(BaseRepository<SimpleEntity, Long> repository) {
            this.repository = repository;
        }

        @Override
        protected BaseRepository<SimpleEntity, Long> getRepository() {
            operationLog.add("getRepository");
            return repository;
        }

        @Override
        public <FilterDTO extends BaseDto> Page<SimpleEntity> findAllWithFilter(
                FilterDTO filter, Pageable pageable) {
            operationLog.add("findAllWithFilter");

            // Demonstratation/"DOC" how concrete services would implement filtering
            if (filter instanceof SimpleFilterDto simpleFilter) {
                // In a real implementation, this  would proablle build Specifications or Criteria...
                // For test case --> use the mock repository's findAll
                return getRepository().findAll(pageable);
            }
            return super.findAllWithFilter(filter, pageable);
        }

        public List<String> getOperationLog() {
            return new ArrayList<>(operationLog);
        }

        @Transactional
        public <R> R processWithFunction(Long id, Function<SimpleEntity, R> processor) {
            operationLog.add("processWithFunction");
            return findAndTransform(id, processor).orElse(null);
        }
    }

    // ------------------------------------------
    @Mock
    private BaseRepository<SimpleEntity, Long> mockRepository;

    @Captor
    private ArgumentCaptor<SimpleEntity> entityCaptor;

    @Captor
    private ArgumentCaptor<List<SimpleEntity>> entityListCaptor;

    private ConcreteSimpleService service;
    private List<SimpleEntity> testDataFixture;

    @BeforeEach
    void setUp() {
        service = new ConcreteSimpleService(mockRepository);

        testDataFixture = IntStream.rangeClosed(1, 5)
                .mapToObj(i -> SimpleEntity.create(
                        (long) i,
                        String.format("Entity-%d", i),
                        String.format("Description for entity %d", i),
                        (int) i * 10
                ))
                .toList();
    }


    @Nested
    @DisplayName("Save Operations - Single and Batch")
    class SaveOperations {

        @Test
        @DisplayName("should save single entity and return persisted version")
        void save_singleEntity_returnsPersisted() {
            SimpleEntity newEntity = SimpleEntity.create(null, "New", "New Description", 50);
            SimpleEntity persistedEntity = SimpleEntity.create(6L, "New", "New Description", 50);

            when(mockRepository.save(newEntity)).thenReturn(persistedEntity);

            SimpleEntity result = service.save(newEntity);

            assertThat(result)
                    .isNotNull()
                    .satisfies(entity -> {
                        assertThat(entity.getId()).isEqualTo(6L);
                        assertThat(entity.getName()).isEqualTo("New");
                        assertThat(entity.getPriority()).isEqualTo(50);
                    });

            // Verify rrepository interaction
            verify(mockRepository, times(1)).save(newEntity);

            // Verify operation log (Log String pattern)
            assertThat(service.getOperationLog()).contains("getRepository");
        }

        @Test
        @DisplayName("should save multiple entities in batch operation")
        void saveAll_multipleEntities_returnsPersisted() {
            List<SimpleEntity> entitiesToSave = List.of(
                    SimpleEntity.create(null, "Batch1", "Desc1", 10),
                    SimpleEntity.create(null, "Batch2", "Desc2", 20),
                    SimpleEntity.create(null, "Batch3", "Desc3", 30)
            );

            List<SimpleEntity> persistedEntities = List.of(
                    SimpleEntity.create(6L, "Batch1", "Desc1", 10),
                    SimpleEntity.create(7L, "Batch2", "Desc2", 20),
                    SimpleEntity.create(8L, "Batch3", "Desc3", 30)
            );

            when(mockRepository.saveAll(entitiesToSave)).thenReturn(persistedEntities);

            List<SimpleEntity> results = service.saveAll(entitiesToSave);

            assertThat(results)
                    .hasSize(3)
                    .extracting(SimpleEntity::getId)
                    .containsExactly(6L, 7L, 8L);

            assertThat(results)
                    .extracting(SimpleEntity::getName)
                    .containsExactly("Batch1", "Batch2", "Batch3");

            verify(mockRepository).saveAll(entityListCaptor.capture());
            assertThat(entityListCaptor.getValue()).isEqualTo(entitiesToSave);
        }

        @Test
        @DisplayName("should handle empty list in saveAll")
        void saveAll_emptyList_returnsEmpty() {
            List<SimpleEntity> emptyList = Collections.emptyList();
            when(mockRepository.saveAll(emptyList)).thenReturn(emptyList);

            List<SimpleEntity> results = service.saveAll(emptyList);

            assertThat(results).isEmpty();
            verify(mockRepository).saveAll(emptyList);
        }
    }


    @Nested
    @DisplayName("Find Operations - Query and Retrieval")
    class FindOperations {

        @Test
        @DisplayName("should find entity by ID when exists")
        void findById_existingEntity_returnsOptional() {
            Long searchId = 2L;
            SimpleEntity expectedEntity = testDataFixture.get(1);

            when(mockRepository.findById(searchId))
                    .thenReturn(Optional.of(expectedEntity));

            Optional<SimpleEntity> result = service.findById(searchId);

            assertThat(result)
                    .isPresent()
                    .hasValueSatisfying(entity -> {
                        assertThat(entity.getId()).isEqualTo(searchId);
                        assertThat(entity.getName()).isEqualTo("Entity-2");
                        assertThat(entity.getPriority()).isEqualTo(20);
                    });

            verify(mockRepository).findById(searchId);
        }

        @Test
        @DisplayName("should return empty Optional for non-existent ID")
        void findById_nonExistent_returnsEmpty() {
            Long nonExistentId = 999L;
            when(mockRepository.findById(nonExistentId))
                    .thenReturn(Optional.empty());

            Optional<SimpleEntity> result = service.findById(nonExistentId);

            assertThat(result).isEmpty();
            verify(mockRepository).findById(nonExistentId);
        }

        @Test
        @DisplayName("should handle pagination correctly")
        void findAll_withPagination_returnsPage() {
            Pageable pageable = PageRequest.of(0, 2, Sort.by("name"));
            List<SimpleEntity> pageContent = testDataFixture.subList(0, 2);
            Page<SimpleEntity> expectedPage = new PageImpl<>(
                    pageContent, pageable, testDataFixture.size()
            );

            when(mockRepository.findAll(pageable)).thenReturn(expectedPage);

            Page<SimpleEntity> result = service.findAll(pageable);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(5);
            assertThat(result.getNumber()).isEqualTo(0);
            assertThat(result.getTotalPages()).isEqualTo(3);

            verify(mockRepository).findAll(pageable);
        }

        @Test
        @DisplayName("should apply transformation function to found entity")
        void findAndTransform_withFunction_appliesTransformation() {
            Long searchId = 3L;
            SimpleEntity entity = testDataFixture.get(2);

            // Transformation pipeline using function composition
            Function<SimpleEntity, String> nameExtractor = SimpleEntity::getName;
            Function<String, String> upperCaser = String::toUpperCase;
            Function<String, String> prefixer = s -> String.format("TRANSFORMED: %s", s);

            // Compose functions
            Function<SimpleEntity, String> pipeline = nameExtractor
                    .andThen(upperCaser)
                    .andThen(prefixer);

            when(mockRepository.findById(searchId))
                    .thenReturn(Optional.of(entity));

            Optional<String> result = service.findAndTransform(searchId, pipeline);

            assertThat(result)
                    .isPresent()
                    .hasValue("TRANSFORMED: ENTITY-3");

            verify(mockRepository).findById(searchId);
        }

        @Test
        @DisplayName("should handle complex transformation with null safety")
        void findAndTransform_complexTransformation_handlesNulls() {
            Long searchId = 1L;
            SimpleEntity entity = testDataFixture.getFirst();

            // Complex transformation that could potentially fail
            Function<SimpleEntity, Optional<Integer>> safeTransform = e ->
                    Optional.ofNullable(e)
                            .map(SimpleEntity::getPriority)
                            .filter(p -> p > 0)
                            .map(p -> p * 2);

            when(mockRepository.findById(searchId))
                    .thenReturn(Optional.of(entity));

            Optional<Optional<Integer>> result = service.findAndTransform(searchId, safeTransform);

            assertThat(result)
                    .isPresent()
                    .satisfies(opt ->
                            assertThat(opt.get()).hasValue(20) // 10 * 2
                    );
        }
    }


    @Nested
    @DisplayName("Update Operations - Modification with Consumers")
    class UpdateOperations {

        @Test
        @DisplayName("should update entity using consumer function")
        void update_withConsumer_modifiesAndSaves() {
            Long entityId = 1L;
            SimpleEntity originalEntity = SimpleEntity.create(1L, "Original", "Original Desc", 10);

            // Define update behavior as a Consumer
            Consumer<SimpleEntity> updateFunction = entity -> {
                entity.setName("Updated");
                entity.setDescription("Updated Description");
                entity.setPriority(100);
            };

            when(mockRepository.findById(entityId))
                    .thenReturn(Optional.of(originalEntity));
            when(mockRepository.save(any(SimpleEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            SimpleEntity result = service.update(entityId, updateFunction);

            assertThat(result)
                    .satisfies(entity -> {
                        assertThat(entity.getName()).isEqualTo("Updated");
                        assertThat(entity.getDescription()).isEqualTo("Updated Description");
                        assertThat(entity.getPriority()).isEqualTo(100);
                    });

            // Verify the sequence of operations
            InOrder inOrder = inOrder(mockRepository);
            inOrder.verify(mockRepository).findById(entityId);
            inOrder.verify(mockRepository).save(entityCaptor.capture());

            // Verify the captured entity has been modified
            SimpleEntity capturedEntity = entityCaptor.getValue();
            assertThat(capturedEntity.getName()).isEqualTo("Updated");
        }

        @Test
        @DisplayName("should compose multiple update operations")
        void update_composedConsumers_appliesAllUpdates() {
            Long entityId = 2L;
            SimpleEntity entity = SimpleEntity.create(2L, "Entity-2", "Desc-2", 20);

            Consumer<SimpleEntity> updateName = e -> e.setName(e.getName().toUpperCase());
            Consumer<SimpleEntity> updatePriority = e -> e.setPriority(e.getPriority() * 2);
            Consumer<SimpleEntity> updateDescription = e ->
                    e.setDescription(String.format("Modified: %s", e.getDescription()));

            // Compose all updates
            Consumer<SimpleEntity> composedUpdate = updateName
                    .andThen(updatePriority)
                    .andThen(updateDescription);

            when(mockRepository.findById(entityId))
                    .thenReturn(Optional.of(entity));
            when(mockRepository.save(any(SimpleEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            SimpleEntity result = service.update(entityId, composedUpdate);

            assertThat(result)
                    .satisfies(updated -> {
                        assertThat(updated.getName()).isEqualTo("ENTITY-2");
                        assertThat(updated.getPriority()).isEqualTo(40);
                        assertThat(updated.getDescription()).isEqualTo("Modified: Desc-2");
                    });
        }

        @Test
        @DisplayName("should throw EntityNotFoundException for non-existent entity")
        void update_nonExistentEntity_throwsException() {
            // Testing error path (Crash Test Dummy )
            Long nonExistentId = 999L;
            Consumer<SimpleEntity> updateFunction = e -> e.setName("Won't work");

            when(mockRepository.findById(nonExistentId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(nonExistentId, updateFunction))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Entity not found with id: 999");

            // Verify save was never called
            verify(mockRepository, never()).save(any());
        }
    }


    @Nested
    @DisplayName("Delete Operations - Removal and Validation")
    class DeleteOperations {

        @Test
        @DisplayName("should delete existing entity by ID")
        void deleteById_existingEntity_deletesSuccessfully() {
            Long entityId = 3L;

            when(mockRepository.existsById(entityId)).thenReturn(true);
            doNothing().when(mockRepository).deleteById(entityId);

            assertThatCode(() -> service.deleteById(entityId))
                    .doesNotThrowAnyException();

            // Verify operation sequence
            InOrder inOrder = inOrder(mockRepository);
            inOrder.verify(mockRepository).existsById(entityId);
            inOrder.verify(mockRepository).deleteById(entityId);
        }

        @Test
        @DisplayName("should throw exception when deleting non-existent entity")
        void deleteById_nonExistent_throwsException() {
            Long nonExistentId = 999L;

            when(mockRepository.existsById(nonExistentId)).thenReturn(false);

            assertThatThrownBy(() -> service.deleteById(nonExistentId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Cannot delete - Entity not found with id: 999");

            // Verify delete was never called
            verify(mockRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("should delete multiple entities and return count")
        void deleteAllByIds_multipleIds_returnsDeletedCount() {
            List<Long> idsToDelete = List.of(1L, 2L, 3L);
            List<SimpleEntity> entitiesToDelete = testDataFixture.subList(0, 3);

            when(mockRepository.findAllById(idsToDelete))
                    .thenReturn(entitiesToDelete);
            doNothing().when(mockRepository).deleteAll(entitiesToDelete);

            int deletedCount = service.deleteAllByIds(idsToDelete);

            assertThat(deletedCount).isEqualTo(3);

            verify(mockRepository).findAllById(idsToDelete);
            verify(mockRepository).deleteAll(entitiesToDelete);
        }

        @Test
        @DisplayName("should handle partial deletion when some entities don't exist")
        void deleteAllByIds_partialExistence_deletesExisting() {
            List<Long> idsToDelete = List.of(1L, 999L, 2L);
            List<SimpleEntity> existingEntities = List.of(
                    testDataFixture.get(0),
                    testDataFixture.get(1)
            );

            when(mockRepository.findAllById(idsToDelete))
                    .thenReturn(existingEntities);
            doNothing().when(mockRepository).deleteAll(existingEntities);

            int deletedCount = service.deleteAllByIds(idsToDelete);

            assertThat(deletedCount).isEqualTo(2);
            verify(mockRepository).deleteAll(existingEntities);
        }
    }


    @Nested
    @DisplayName("Utility Operations - Count and Existence")
    class UtilityOperations {

        @Test
        @DisplayName("should check entity existence correctly")
        void existsById_variousCases_returnsCorrectBoolean() {
            // ARRANGE & ACT & ASSERT --> Testing both cases
            when(mockRepository.existsById(1L)).thenReturn(true);
            assertThat(service.existsById(1L)).isTrue();

            when(mockRepository.existsById(999L)).thenReturn(false);
            assertThat(service.existsById(999L)).isFalse();

            verify(mockRepository, times(2)).existsById(anyLong());
        }

        @Test
        @DisplayName("should count all entities")
        void count_returnsTotal() {
            when(mockRepository.count()).thenReturn(5L);
            assertThat(service.count()).isEqualTo(5L);
            verify(mockRepository).count();
        }

        @Test
        @DisplayName("should use default implementation for countWithFilter")
        void countWithFilter_defaultImplementation_delegatesToCount() {
            SimpleFilterDto filter = new SimpleFilterDto("test", 10, 50);
            when(mockRepository.count()).thenReturn(5L);

            assertThat(service.countWithFilter(filter)).isEqualTo(5L);
            verify(mockRepository).count();
        }
    }


    @Nested
    @DisplayName("Filter Operations - Default and Custom Implementations")
    class FilterOperations {

        @Test
        @DisplayName("should use default implementation for findAllWithFilter with pageable")
        void findAllWithFilter_withPageable_defaultImplementation_delegatesToFindAll() {
            SimpleFilterDto filter = new SimpleFilterDto("test", 10, 50);
            Pageable pageable = PageRequest.of(0, 3, Sort.by("name"));
            List<SimpleEntity> pageContent = testDataFixture.subList(0, 3);
            Page<SimpleEntity> expectedPage = new PageImpl<>(
                    pageContent, pageable, testDataFixture.size()
            );

            when(mockRepository.findAll(pageable)).thenReturn(expectedPage);

            Page<SimpleEntity> result = service.findAllWithFilter(filter, pageable);

            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(5);
            assertThat(result.getNumber()).isEqualTo(0);

            verify(mockRepository).findAll(pageable);
            assertThat(service.getOperationLog()).contains("findAllWithFilter", "getRepository");
        }

        @Test
        @DisplayName("should use default implementation for findAllWithFilter without pageable")
        void findAllWithFilter_withoutPageable_defaultImplementation_delegatesToFindAll() {
            SimpleFilterDto filter = new SimpleFilterDto("entity", 5, 100);

            when(mockRepository.findAll()).thenReturn(testDataFixture);

            List<SimpleEntity> result = service.findAllWithFilter(filter);

            assertThat(result).hasSize(5);
            assertThat(result).isEqualTo(testDataFixture);

            verify(mockRepository).findAll();
            assertThat(service.getOperationLog()).contains("getRepository");
        }

        @Test
        @DisplayName("should use overridden implementation when filter matches concrete type")
        void findAllWithFilter_overriddenImplementation_usesCustomLogic() {
            SimpleFilterDto filter = new SimpleFilterDto("Entity-1", 5, 15);
            Pageable pageable = PageRequest.of(0, 2);
            List<SimpleEntity> filteredContent = testDataFixture.subList(0, 2);
            Page<SimpleEntity> expectedPage = new PageImpl<>(
                    filteredContent, pageable, filteredContent.size()
            );

            when(mockRepository.findAll(pageable)).thenReturn(expectedPage);

            Page<SimpleEntity> result = service.findAllWithFilter(filter, pageable);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);

            verify(mockRepository).findAll(pageable);
            assertThat(service.getOperationLog()).contains("findAllWithFilter", "getRepository");
        }

        @Test
        @DisplayName("should handle null filter gracefully")
        void findAllWithFilter_nullFilter_defaultBehavior() {
            Pageable pageable = PageRequest.of(0, 5);
            Page<SimpleEntity> expectedPage = new PageImpl<>(
                    testDataFixture, pageable, testDataFixture.size()
            );

            when(mockRepository.findAll(pageable)).thenReturn(expectedPage);

            Page<SimpleEntity> result = service.findAllWithFilter(null, pageable);

            assertThat(result.getContent()).hasSize(5);
            assertThat(result.getTotalElements()).isEqualTo(5);

            verify(mockRepository).findAll(pageable);
        }

        @Test
        @DisplayName("should handle empty results from filter operations")
        void findAllWithFilter_emptyResults_returnsEmptyCollection() {
            SimpleFilterDto filter = new SimpleFilterDto("nonexistent", 1000, 2000);
            
            when(mockRepository.findAll()).thenReturn(Collections.emptyList());

            List<SimpleEntity> result = service.findAllWithFilter(filter);

            assertThat(result).isEmpty();
            verify(mockRepository).findAll();
        }
    }

    // ---- SELF SHUNT PATTER ---

    @Nested
    @DisplayName("Test as Consumer")
    class SelfShuntPatternTest implements Consumer<SimpleEntity> {

        private final List<String> processedEntities = new ArrayList<>();
        private SimpleEntity lastProcessedEntity;
        private int processCount = 0;

        @Override
        public void accept(SimpleEntity entity) {
            // The test class itself acts as the consumer
            processCount++;
            lastProcessedEntity = entity;
            processedEntities.add(String.format("Processed[%d]: %s",
                    processCount, entity.getName()));
        }

        @Test
        @DisplayName("should use test class as consumer (Self Shunt)")
        void selfShunt_testActsAsConsumer() {
            Long entityId = 1L;
            SimpleEntity entity = SimpleEntity.create(1L, "TestEntity", "Test", 10);

            when(mockRepository.findById(entityId))
                    .thenReturn(Optional.of(entity));
            when(mockRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            //Use 'this' as the consumer
            service.update(entityId, this);

            assertThat(processCount).isEqualTo(1);
            assertThat(lastProcessedEntity).isEqualTo(entity);
            assertThat(processedEntities).contains("Processed[1]: TestEntity");
        }
    }
}