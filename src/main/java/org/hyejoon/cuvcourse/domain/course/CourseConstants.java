package org.hyejoon.cuvcourse.domain.course;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CourseConstants {
    public static final String COURSE_LOCK_KEY = "lock:course:lecture:";

    public static String buildCourseLockKey(long lectureId) {
        return COURSE_LOCK_KEY+lectureId;
    }
}
