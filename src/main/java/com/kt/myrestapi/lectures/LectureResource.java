package com.kt.myrestapi.lectures;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.kt.myrestapi.lectures.dto.LectureResDto;
import org.springframework.hateoas.RepresentationModel;

public class LectureResource extends RepresentationModel<LectureResource> {
    @JsonUnwrapped
    private LectureResDto lectureResDto;

    public LectureResource(LectureResDto resDto) {
        this.lectureResDto = resDto;
    }

    public LectureResDto getLectureResDto() {
        return lectureResDto;
    }
}
