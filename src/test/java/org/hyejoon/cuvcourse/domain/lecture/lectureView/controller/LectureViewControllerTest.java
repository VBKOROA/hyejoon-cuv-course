package org.hyejoon.cuvcourse.domain.lecture.lectureView.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;
import org.hyejoon.cuvcourse.domain.lecture.lectureView.service.LectureViewService;
import org.hyejoon.cuvcourse.domain.lecture.lectureView.dto.LectureViewResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LectureViewController.class)
public class LectureViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LectureViewService lectureViewService;

    @Test
    void 강의_목록_조회에_성공한다() throws Exception {
        // given
        LectureViewResponse lecture1 = new LectureViewResponse(1L, "test 강의1", "test1", 3, 1, 30);
        LectureViewResponse lecture2 = new LectureViewResponse(2L, "test 강의2", "test2", 2, 3, 40);
        Page<LectureViewResponse> pageResponse =
            new PageImpl<>(List.of(lecture1, lecture2));

        given(lectureViewService.findAll(any(Pageable.class)))
            .willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/api/lectures")
                .param("page", "0")
                .param("size", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("조회 성공"))
            .andExpect(jsonPath("$.data.content[0].id").value(1))
            .andExpect(jsonPath("$.data.content[0].lectureTitle").value("test 강의1"))
            .andExpect(jsonPath("$.data.content[1].id").value(2))
            .andExpect(jsonPath("$.data.content[1].lectureTitle").value("test 강의2"));
    }
}
