package org.hyejoon.cuvcourse.domain.course.cousecancel.service;

import org.hyejoon.cuvcourse.domain.course.cousecancel.exception.CourseCancelExceptionEnum;
import org.hyejoon.cuvcourse.domain.course.entity.Course;
import org.hyejoon.cuvcourse.domain.course.repository.CourseJpaRepository;
import org.hyejoon.cuvcourse.domain.lecture.cache.LectureCacheProvider;
import org.hyejoon.cuvcourse.domain.lecture.entity.Lecture;
import org.hyejoon.cuvcourse.global.exception.BusinessException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseDeletionService {

    private final CourseJpaRepository courseJpaRepository;

    @Transactional
    @CacheEvict(value = LectureCacheProvider.LECTURE_PAGEABLE_CACHE_KEY, allEntries = true)
    public void deleteCourse(Long lectureId, Long studentId) {
        Course course = courseJpaRepository.findByLectureAndStudent(lectureId, studentId)
            .orElseThrow(() -> new BusinessException(CourseCancelExceptionEnum.COURSE_NOT_FOUND));

        Lecture lecture = course.getId().getLecture();
        lecture.decreaseTotal();

        courseJpaRepository.delete(course);
    }
}
