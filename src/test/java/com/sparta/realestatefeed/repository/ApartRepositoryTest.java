package com.sparta.realestatefeed.repository;

import com.sparta.realestatefeed.config.TestConfig;
import com.sparta.realestatefeed.entity.Apart;
import com.sparta.realestatefeed.repository.apart.ApartRepository;
import com.sparta.realestatefeed.repository.qna.QnARepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class ApartRepositoryTest {

    @Autowired
    private ApartRepository apartRepository;

    private Apart apart;

    private Apart setTestApart(String apartName, String area) {

        Apart apart = new Apart();
        ReflectionTestUtils.setField(apart, "apartName", apartName);
        ReflectionTestUtils.setField(apart, "area", area);

        return apart;
    }

    @BeforeEach
    void setUp() {
        apart = new Apart();
    }

    @Nested
    @DisplayName("특정 아파트 조회")
    class FindByApartIdTest {

        @Test
        @DisplayName("특정 아파트가 존재할 경우")
        void testFindByApartIdSuccess() {
            // give
            apart = setTestApart("testapart", "Seoul");
            apartRepository.save(apart);

            // when
            Optional<Apart> optionalApart = apartRepository.findByApartId(apart.getId());

            // then
            assertTrue(optionalApart.isPresent());
            assertEquals(apart.getId(), optionalApart.get().getId());
        }

        @Test
        @DisplayName("특정 아파트가 없을 경우")
        void testFindByApartIdFailure() {
            // give
            apart = setTestApart("testapart", "Seoul");
            apartRepository.save(apart);

            // when
            Optional<Apart> optionalApart = apartRepository.findByApartId(999L);

            // then
            assertFalse(optionalApart.isPresent());
        }
    }
}
