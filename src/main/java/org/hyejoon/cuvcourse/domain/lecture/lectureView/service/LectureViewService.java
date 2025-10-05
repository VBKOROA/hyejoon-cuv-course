package org.hyejoon.cuvcourse.domain.lecture.lectureView.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import org.hyejoon.cuvcourse.domain.lecture.cache.LectureCacheProvider;
import org.hyejoon.cuvcourse.domain.lecture.cache.LecturePage;
import org.hyejoon.cuvcourse.domain.lecture.lectureView.dto.LectureViewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureViewService {

    private final LectureCacheProvider lectureCacheProvider;

    @Transactional(readOnly = true)
    public Page<LectureViewResponse> findAll(Pageable pageable) {

        LecturePage lecturePage = lectureCacheProvider.getLecturesByPageable(pageable);
        return new PageImpl<>(lecturePage.getLectures().stream().map(LectureViewResponse::of).toList(), pageable, lecturePage.getTotalElements());
    }
}
