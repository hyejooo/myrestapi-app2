package com.kt.myrestapi.lectures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LectureTest {
    @Test
    public void builder() {
        Lecture lecture = Lecture.builder()
                .name("REST")
                .description("REST API developmemt with Spring")
                .build();
        assertEquals("REST", lecture.getName());
    }
}