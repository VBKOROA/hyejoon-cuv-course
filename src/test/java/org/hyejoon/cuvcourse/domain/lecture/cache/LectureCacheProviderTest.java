package org.hyejoon.cuvcourse.domain.lecture.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.hyejoon.cuvcourse.domain.lecture.entity.Lecture;
import org.hyejoon.cuvcourse.domain.lecture.repository.LectureJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class LectureCacheProviderTest {

    @Autowired
    private LectureCacheProvider lectureCacheProvider;

    @Autowired
    private RedisCacheManager cacheManager;

    @MockitoSpyBean
    private LectureJpaRepository lectureJpaRepository;

    @BeforeEach
    void setUp() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Optional.ofNullable(cacheManager.getCache(cacheName)).ifPresent(Cache::clear);
        }

        Lecture lecture1 = new Lecture("Math 101", "Prof. Kim", 3, 30);
        Lecture lecture2 = new Lecture("Physics 101", "Prof. Lee", 3, 25);
        lectureJpaRepository.save(lecture1);
        lectureJpaRepository.save(lecture2);
    }

    @Test
    void 캐시가_제대로_저장되고_로드된다() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        LecturePage firstCall = lectureCacheProvider.getLecturesByPageable(pageable);

        // then
        assertThat(firstCall.getLectures()).hasSize(2);
        verify(lectureJpaRepository, times(1)).findAll(pageable);

        // when
        LecturePage secondCall = lectureCacheProvider.getLecturesByPageable(pageable);

        // then
        assertThat(secondCall.getLectures()).hasSize(2);
        assertThat(secondCall.getLectures().get(0).getLectureTitle()).isEqualTo("Math 101");
        assertThat(secondCall.getLectures().get(1).getLectureTitle()).isEqualTo("Physics 101");
        verify(lectureJpaRepository, times(1)).findAll(pageable);
    }
}
