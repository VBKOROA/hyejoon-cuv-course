package org.hyejoon.cuvcourse.domain.lecture.cache;

import org.hyejoon.cuvcourse.domain.lecture.entity.Lecture;
import org.hyejoon.cuvcourse.domain.lecture.repository.LectureJpaRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LectureCacheProvider {
    public static final String LECTURE_PAGEABLE_CACHE_KEY = "lectures:pageable";

    private final LectureJpaRepository lectureJpaRepository;

    @Cacheable(value = LECTURE_PAGEABLE_CACHE_KEY, key = "#pageable.pageSize + '-' + #pageable.pageNumber")
    public LecturePage getLecturesByPageable(Pageable pageable) {
        Page<Lecture> page = lectureJpaRepository.findAll(pageable);
        return LecturePage.of(page);
    }

    public Page<Lecture> getLecturesByPageableWithoutCache(Pageable pageable) {
        return lectureJpaRepository.findAll(pageable);
    }
}
