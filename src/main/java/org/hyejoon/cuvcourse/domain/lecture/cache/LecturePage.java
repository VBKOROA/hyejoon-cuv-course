package org.hyejoon.cuvcourse.domain.lecture.cache;

import java.util.ArrayList;
import java.util.List;

import org.hyejoon.cuvcourse.domain.lecture.entity.Lecture;
import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LecturePage {
    private List<Lecture> lectures;
    private long totalElements;
    
    public static LecturePage of(Page<Lecture> lectures) {
        // UnmodifiableList 대신 ArrayList로 변환하여 직렬화 문제 방지
        return new LecturePage(new ArrayList<>(lectures.getContent()), lectures.getTotalElements());
    }
}
