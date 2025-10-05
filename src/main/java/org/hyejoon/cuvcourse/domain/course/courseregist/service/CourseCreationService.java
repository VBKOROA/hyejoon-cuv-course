package org.hyejoon.cuvcourse.domain.course.courseregist.service;

import org.hyejoon.cuvcourse.domain.course.courseregist.exception.CourseRegistExceptionEnum;
import org.hyejoon.cuvcourse.domain.course.entity.Course;
import org.hyejoon.cuvcourse.domain.course.entity.CourseId;
import org.hyejoon.cuvcourse.domain.course.repository.CourseJpaRepository;
import org.hyejoon.cuvcourse.domain.lecture.cache.LectureCacheProvider;
import org.hyejoon.cuvcourse.domain.lecture.entity.Lecture;
import org.hyejoon.cuvcourse.domain.lecture.repository.LectureJpaRepository;
import org.hyejoon.cuvcourse.domain.student.entity.Student;
import org.hyejoon.cuvcourse.global.exception.BusinessException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseCreationService {

    private final CourseJpaRepository courseJpaRepository;
    private final LectureJpaRepository lectureJpaRepository;

    @Transactional
    @CacheEvict(value = LectureCacheProvider.LECTURE_PAGEABLE_CACHE_KEY, allEntries = true)
    public Course createCourseIfAvailable(long lectureId, Student student) {
        Lecture lecture = lectureJpaRepository.findById(lectureId)
            .orElseThrow(() -> new BusinessException(CourseRegistExceptionEnum.LECTURE_NOT_FOUND));

        CourseId courseId = CourseId.of(lecture, student);
        if (courseJpaRepository.existsById(courseId)) {
            throw new BusinessException(CourseRegistExceptionEnum.ALREADY_REGISTERED);
        }

        if (lecture.getTotal() >= lecture.getCapacity()) {
            throw new BusinessException(CourseRegistExceptionEnum.CAPACITY_FULL);
        }

        lecture.increaseTotal();

        Course course = Course.from(courseId);
        return courseJpaRepository.save(course);
    }
}
