package org.hyejoon.cuvcourse.domain.lecture.lectureView.dto;

import org.hyejoon.cuvcourse.domain.lecture.entity.Lecture;

public record LectureViewResponse(Long id, String lectureTitle, String professorName, int credits,
                                  int totalStudents, int capacity) {
    public static LectureViewResponse of(Lecture lecture) {
        return new LectureViewResponse(lecture.getId(), lecture.getLectureTitle(), lecture.getProfessorName(), lecture.getCredits(), lecture.getTotal(), lecture.getCapacity());
    }
}
