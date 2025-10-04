package org.hyejoon.cuvcourse.domain.course.courseregist.service;

import org.hyejoon.cuvcourse.domain.course.courseregist.dto.CourseResponse;
import org.hyejoon.cuvcourse.domain.course.courseregist.exception.CourseRegistExceptionEnum;
import org.hyejoon.cuvcourse.domain.course.entity.Course;
import org.hyejoon.cuvcourse.domain.course.entity.CourseId;
import org.hyejoon.cuvcourse.domain.course.repository.CourseJpaRepository;
import org.hyejoon.cuvcourse.domain.lecture.entity.Lecture;
import org.hyejoon.cuvcourse.domain.lecture.repository.LectureJpaRepository;
import org.hyejoon.cuvcourse.domain.student.entity.Student;
import org.hyejoon.cuvcourse.domain.student.repository.StudentJpaRepository;
import org.hyejoon.cuvcourse.global.exception.BusinessException;
import org.hyejoon.cuvcourse.global.lock.DistributedLock;
import org.hyejoon.cuvcourse.global.lock.LockManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseRegistService {

    private static final String COURSE_REGIST_LOCK_KEY = "lock:course:regist:";

    private final CourseCreationService courseCreationService;
    private final LockManager lockManager;
    private final DistributedLock distributedLock;
    private final CourseJpaRepository courseJpaRepository;
    private final LectureJpaRepository lectureJpaRepository;
    private final StudentJpaRepository studentJpaRepository;

    public CourseResponse registerCourse(long studentId, long lectureId) {
        log.debug("Lock type: {}", distributedLock.getType());

        Student student = studentJpaRepository.findById(studentId)
            .orElseThrow(() -> new BusinessException(CourseRegistExceptionEnum.STUDENT_NOT_FOUND));
        Lecture lecture = lectureJpaRepository.findById(lectureId)
            .orElseThrow(() -> new BusinessException(CourseRegistExceptionEnum.LECTURE_NOT_FOUND));
        CourseId courseId = CourseId.of(lecture, student);

        String lockKey = COURSE_REGIST_LOCK_KEY + lectureId;

        Course course = lockManager.executeWithLock(distributedLock, lockKey, () -> {
            if (courseJpaRepository.existsById(courseId)) {
                throw new BusinessException(CourseRegistExceptionEnum.ALREADY_REGISTERED);
            }
            return courseCreationService.createCourseIfAvailable(lecture, courseId);
        });

        return CourseResponse.from(course);
    }
}
