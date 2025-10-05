package org.hyejoon.cuvcourse.domain.course.courseregist.service;

import org.hyejoon.cuvcourse.domain.student.repository.StudentJpaRepository;
import org.hyejoon.cuvcourse.global.lock.DistributedLock;
import org.hyejoon.cuvcourse.global.lock.LockManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestCourseRegistConfig {

    @Bean
    @Qualifier("courseRegistRedissonService")
    public CourseRegistService redissonCourseRegistService(
        CourseCreationService courseCreationService,
        LockManager lockManager,
        @Qualifier("redissonLock") DistributedLock redissonLock,
        StudentJpaRepository studentJpaRepository) {
        return new CourseRegistService(courseCreationService, lockManager, redissonLock,
            studentJpaRepository);
    }

    @Bean
    @Qualifier("courseRegistPubSubLockService")
    public CourseRegistService pubSubCourseRegistService(
        CourseCreationService courseCreationService,
        LockManager lockManager,
        @Qualifier("pubSubLock") DistributedLock pubSubLock,
        StudentJpaRepository studentJpaRepository) {
        return new CourseRegistService(courseCreationService, lockManager, pubSubLock,
            studentJpaRepository);
    }

    @Bean
    @Qualifier("courseRegistSpinLockService")
    public CourseRegistService spinCourseRegistService(
        CourseCreationService courseCreationService,
        LockManager lockManager,
        @Qualifier("spinLock") DistributedLock spinLock,
        StudentJpaRepository studentJpaRepository) {
        return new CourseRegistService(courseCreationService, lockManager, spinLock,
            studentJpaRepository);
    }

    @Bean
    @Qualifier("courseRegistNoLockService")
    public CourseRegistService noLockCourseRegistService(
        CourseCreationService courseCreationService,
        LockManager lockManager,
        @Qualifier("noLock") DistributedLock noLock,
        StudentJpaRepository studentJpaRepository) {
        return new CourseRegistService(courseCreationService, lockManager, noLock,
            studentJpaRepository);
    }
}
