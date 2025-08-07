package com.quizplatform.common.business.service;

import com.quizplatform.common.business.domain.entity.BasePublicEntity;
import com.quizplatform.common.business.repository.BasePublicRepository;
import com.quizplatform.common.business.repository.BaseRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AbstractBasePublicService - Public Entity Operations Test Suite")
class AbstractBasePublicServiceTest {

    // ------- TEST FIXTURES Concrete Impl. -----
    static class PublicTestEntity extends BasePublicEntity<Long> {
        private String title;
        private String content;
        private String status;

        public static PublicTestEntity create(Long id, UUID publicId,
                                              String title, String content, String status) {
            PublicTestEntity entity = new PublicTestEntity();
            entity.setId(id);
            entity.setPublicId(publicId);
            entity.title = title;
            entity.content = content;
            entity.status = status;
            return entity;
        }


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return String.format("PublicTestEntity[id=%d, publicId=%s, title='%s', status='%s']",
                    getId(), getPublicId(), title, status);
        }
    }

    static class ConcretePublicService extends AbstractBasePublicService<PublicTestEntity, Long> {
        private final BasePublicRepository<PublicTestEntity, Long> repository;
        private final List<String> methodCallLog = new ArrayList<>();

        ConcretePublicService(BasePublicRepository<PublicTestEntity, Long> repository) {
            this.repository = repository;
        }

        @Override
        protected BaseRepository<PublicTestEntity, Long> getRepository() {
            methodCallLog.add("getRepository");
            return repository;
        }

        public List<String> getMethodCallLog() {
            return new ArrayList<>(methodCallLog);
        }

        @Transactional
        public String getEntityStatus(UUID publicId) {
            methodCallLog.add("getEntityStatus");
            return findByPublicId(publicId)
                    .map(PublicTestEntity::getStatus)
                    .orElse("NOT_FOUND");
        }
    }
    // ------------------------------------------

    @Mock
    private BasePublicRepository<PublicTestEntity, Long> mockPublicRepository;

    @Captor
    private ArgumentCaptor<PublicTestEntity> entityCaptor;

    @Captor
    private ArgumentCaptor<Consumer<PublicTestEntity>> consumerCaptor;

    private ConcretePublicService service;
    private Map<UUID, PublicTestEntity> publicIdIndex;
    private List<PublicTestEntity> testDataFixture;

    @BeforeEach
    void setUp() {
        service = new ConcretePublicService(mockPublicRepository);

        // Create test data with both internal and public IDs
        publicIdIndex = new ConcurrentHashMap<>();
        testDataFixture = new ArrayList<>();

        LongStream.rangeClosed(1, 5).forEach(i -> {
            UUID publicId = UUID.randomUUID();
            PublicTestEntity entity = PublicTestEntity.create(
                    i,
                    publicId,
                    String.format("Entity %s", i),
                    String.format("Content for entity %s", i),
                    String.format("Status %s", i % 2 == 0 ? "ACTIVE" : "INACTIVE")
            );
            testDataFixture.add(entity);
            publicIdIndex.put(publicId, entity);
        });
    }

    @Nested
    @DisplayName("Public ID Operations - Core Functionality")
    class PublicIdOperations {

        @Test
        @DisplayName("should find entity by public ID")
        void findByPublicId_existingEntity_returnsOptional() {
            UUID publicId = publicIdIndex.keySet().iterator().next();
            PublicTestEntity expectedEntity = publicIdIndex.get(publicId);

            when(mockPublicRepository.findByPublicId(publicId))
                    .thenReturn(Optional.of(expectedEntity));

            Optional<PublicTestEntity> result = service.findByPublicId(publicId);

            assertThat(result)
                    .isPresent()
                    .hasValueSatisfying(entity -> {
                        assertThat(entity.getPublicId()).isEqualTo(publicId);
                        assertThat(entity.getTitle()).isEqualTo(expectedEntity.getTitle());
                        assertThat(entity.getStatus()).isEqualTo(expectedEntity.getStatus());
                    });

            verify(mockPublicRepository).findByPublicId(publicId);
        }

        @Test
        @DisplayName("should return empty Optional for non-existent public ID")
        void findByPublicId_nonExistent_returnsEmpty() {
            UUID nonExistentPublicId = UUID.randomUUID();

            when(mockPublicRepository.findByPublicId(nonExistentPublicId))
                    .thenReturn(Optional.empty());

            Optional<PublicTestEntity> result = service.findByPublicId(nonExistentPublicId);

            assertThat(result).isEmpty();
            verify(mockPublicRepository).findByPublicId(nonExistentPublicId);
        }

        @Test
        @DisplayName("should validate public ID is not null")
        void findByPublicId_nullId_throwsException() {
            assertThatThrownBy(() -> service.findByPublicId(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Public Id cannot be null");

            verify(mockPublicRepository, never()).findByPublicId(any());
        }
    }

    @Nested
    @DisplayName("Update by Public ID Operations")
    class UpdateByPublicIdOperations {

        @Test
        @DisplayName("should update entity by public ID using consumer")
        void updateByPublicId_existingEntity_appliesUpdate() {
            // ARRANGE
            UUID publicId = publicIdIndex.keySet().iterator().next();
            PublicTestEntity originalEntity = publicIdIndex.get(publicId);

            Consumer<PublicTestEntity> updateFunction = entity -> {
                entity.setTitle("Updated Title");
                entity.setStatus("PUBLISHED");
                entity.setContent("Updated content");
            };

            when(mockPublicRepository.findByPublicId(publicId))
                    .thenReturn(Optional.of(originalEntity));
            when(mockPublicRepository.save(any(PublicTestEntity.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            PublicTestEntity result = service.updateByPublicId(publicId, updateFunction);

            assertThat(result)
                    .satisfies(entity -> {
                        assertThat(entity.getTitle()).isEqualTo("Updated Title");
                        assertThat(entity.getStatus()).isEqualTo("PUBLISHED");
                        assertThat(entity.getContent()).isEqualTo("Updated content");
                    });

            // Verify the operation sequence
            InOrder inOrder = inOrder(mockPublicRepository);
            inOrder.verify(mockPublicRepository).findByPublicId(publicId);
            inOrder.verify(mockPublicRepository).save(entityCaptor.capture());

            assertThat(entityCaptor.getValue().getTitle()).isEqualTo("Updated Title");
        }

        @Test
        @DisplayName("should throw exception when updating non-existent entity")
        void updateByPublicId_nonExistent_throwsException() {
            UUID nonExistentPublicId = UUID.randomUUID();
            Consumer<PublicTestEntity> updateFunction = e -> e.setTitle("Won't work");

            when(mockPublicRepository.findByPublicId(nonExistentPublicId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    service.updateByPublicId(nonExistentPublicId, updateFunction))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(String.format("Resource not found with public ID: %s", nonExistentPublicId));

            verify(mockPublicRepository, never()).save(any());
        }

        @Test
        @DisplayName("should compose multiple update operations by public ID")
        void updateByPublicId_composedConsumers_appliesAll() {
            UUID publicId = publicIdIndex.keySet().iterator().next();
            PublicTestEntity entity = publicIdIndex.get(publicId);

            // Create a chain of update operations
            Consumer<PublicTestEntity> statusUpdate = e -> e.setStatus("ARCHIVED");
            Consumer<PublicTestEntity> titleUpdate = e ->
                    e.setTitle(String.format("%s (Archived)", e.getTitle()));
            Consumer<PublicTestEntity> contentUpdate = e ->
                    e.setContent(String.format("Archived: %s", e.getContent()));

            Consumer<PublicTestEntity> composedUpdate = statusUpdate
                    .andThen(titleUpdate)
                    .andThen(contentUpdate);

            when(mockPublicRepository.findByPublicId(publicId))
                    .thenReturn(Optional.of(entity));
            when(mockPublicRepository.save(any()))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            PublicTestEntity result = service.updateByPublicId(publicId, composedUpdate);

            assertThat(result.getStatus()).isEqualTo("ARCHIVED");
            assertThat(result.getTitle()).endsWith(" (Archived)");
            assertThat(result.getContent()).startsWith("Archived: ");
        }
    }

    @Nested
    @DisplayName("Existence Check Operations")
    class ExistenceOperations {

        @Test
        @DisplayName("should check existence by public ID")
        void existsByPublicId_variousCases_returnsCorrectBoolean() {
            UUID existingPublicId = publicIdIndex.keySet().iterator().next();
            UUID nonExistentPublicId = UUID.randomUUID();

            //  Existing entity
            when(mockPublicRepository.existsByPublicId(existingPublicId))
                    .thenReturn(true);
            assertThat(service.existsByPublicId(existingPublicId)).isTrue();

            //  Non-existent entity
            when(mockPublicRepository.existsByPublicId(nonExistentPublicId))
                    .thenReturn(false);
            assertThat(service.existsByPublicId(nonExistentPublicId)).isFalse();

            verify(mockPublicRepository).existsByPublicId(existingPublicId);
            verify(mockPublicRepository).existsByPublicId(nonExistentPublicId);
        }
    }

    @Nested
    @DisplayName("Transform Operations with Public ID")
    class TransformOperations {

        @Test
        @DisplayName("should find and transform entity by public ID")
        void findAndTransformByPublicId_withFunction_appliesTransformation() {
            UUID publicId = publicIdIndex.keySet().iterator().next();
            PublicTestEntity entity = publicIdIndex.get(publicId);

            Function<PublicTestEntity, Map<String, Object>> transformer = e -> {
                Map<String, Object> summary = new HashMap<>();
                summary.put("publicId", e.getPublicId());
                summary.put("title", e.getTitle());
                summary.put("isActive", "ACTIVE".equals(e.getStatus()));
                summary.put("contentLength", e.getContent().length());
                return summary;
            };

            when(mockPublicRepository.findByPublicId(publicId))
                    .thenReturn(Optional.of(entity));

            Optional<Map<String, Object>> result =
                    service.findAndTransformByPublicId(publicId, transformer);

            assertThat(result)
                    .isPresent()
                    .hasValueSatisfying(map -> {
                        assertThat(map.get("publicId")).isEqualTo(publicId);
                        assertThat(map.get("title")).isEqualTo(entity.getTitle());
                        assertThat(map.get("isActive")).isEqualTo("ACTIVE".equals(entity.getStatus()));
                        assertThat(map.get("contentLength")).isEqualTo(entity.getContent().length());
                    });
        }

        @Test
        @DisplayName("should handle transformation with function composition")
        void findAndTransformByPublicId_functionComposition_works() {
            UUID publicId = publicIdIndex.keySet().iterator().next();
            PublicTestEntity entity = publicIdIndex.get(publicId);

            // CComposed transformation
            Function<PublicTestEntity, String> getTitle = PublicTestEntity::getTitle;
            Function<String, Integer> getLength = String::length;
            Function<Integer, String> formatResult = len ->
                    String.format("Title has %d characters", len);

            Function<PublicTestEntity, String> pipeline = getTitle
                    .andThen(getLength)
                    .andThen(formatResult);

            when(mockPublicRepository.findByPublicId(publicId))
                    .thenReturn(Optional.of(entity));

            Optional<String> result = service.findAndTransformByPublicId(publicId, pipeline);

            assertThat(result)
                    .isPresent()
                    .hasValueSatisfying(value ->
                            assertThat(value).matches("Title has \\d+ characters")
                    );
        }
    }

    @Nested
    @DisplayName("Repository Type Validation")
    class RepositoryTypeValidation {

        @Test
        @DisplayName("should accept BasePublicRepository implementation")
        void getPublicRepository_correctType_returnsRepository() {
            // ACT & ASSERT - Should not throw exception
            assertThatCode(() -> service.getPublicRepository())
                    .doesNotThrowAnyException();

            assertThat(service.getPublicRepository()).isEqualTo(mockPublicRepository);
        }

        @Test
        @DisplayName("should reject non-BasePublicRepository implementation")
        void getPublicRepository_wrongType_throwsException() {
            // ARRANGE - Create a service with wrong repository type
            @SuppressWarnings("unchecked")
            BaseRepository<PublicTestEntity, Long> wrongTypeRepo =
                    mock(BaseRepository.class);

            ConcretePublicService serviceWithWrongRepo =
                    new ConcretePublicService(null) {
                        @Override
                        protected BaseRepository<PublicTestEntity, Long> getRepository() {
                            return wrongTypeRepo;
                        }
                    };

            assertThatThrownBy(serviceWithWrongRepo::getPublicRepository)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Repository must implement BasePublicRepository")
                    .hasMessageContaining(wrongTypeRepo.getClass().getSimpleName());
        }
    }

    @Nested
    @DisplayName("Integration with Base Service Operations")
    class BaseServiceIntegration {

        @Test
        @DisplayName("should inherit base service save operation")
        void save_inheritedFromBase_works() {
            PublicTestEntity newEntity = PublicTestEntity.create(
                    null,
                    UUID.randomUUID(),
                    "New Entity",
                    "New Content",
                    "DRAFT"
            );

            PublicTestEntity savedEntity = PublicTestEntity.create(
                    6L,
                    newEntity.getPublicId(),
                    "New Entity",
                    "New Content",
                    "DRAFT"
            );

            when(mockPublicRepository.save(newEntity)).thenReturn(savedEntity);

            PublicTestEntity result = service.save(newEntity);

            assertThat(result.getId()).isEqualTo(6L);
            assertThat(result.getPublicId()).isEqualTo(newEntity.getPublicId());
            verify(mockPublicRepository).save(newEntity);
        }

        @Test
        @DisplayName("should inherit base service findById operation")
        void findById_inheritedFromBase_works() {
            Long internalId = 3L;
            PublicTestEntity entity = testDataFixture.get(2);

            when(mockPublicRepository.findById(internalId))
                    .thenReturn(Optional.of(entity));

            Optional<PublicTestEntity> result = service.findById(internalId);

            assertThat(result)
                    .isPresent()
                    .hasValueSatisfying(e -> {
                        assertThat(e.getId()).isEqualTo(internalId);
                        assertThat(e.getPublicId()).isNotNull();
                    });

            verify(mockPublicRepository).findById(internalId);
        }
    }


}
