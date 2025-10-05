package org.hyejoon.cuvcourse.domain.course.cousecancel.service;

import org.hyejoon.cuvcourse.domain.course.CourseConstants;
import org.hyejoon.cuvcourse.global.lock.DistributedLock;
import org.hyejoon.cuvcourse.global.lock.LockManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseCancelService {

    private final LockManager lockManager;
    private final DistributedLock distributedLock;
    private final CourseDeletionService courseDeletionService;

    public void courseCancel(Long lectureId, Long studentId) {
        log.debug("Lock type: {}", distributedLock.getType());

        String lockKey = CourseConstants.buildCourseLockKey(lectureId);
        
        lockManager.executeWithLock(distributedLock, lockKey, () -> courseDeletionService.deleteCourse(lectureId, studentId));
    }
}
