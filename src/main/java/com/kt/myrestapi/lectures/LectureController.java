package com.kt.myrestapi.lectures;

import com.kt.myrestapi.lectures.dto.LectureReqDto;
import com.kt.myrestapi.lectures.dto.LectureResDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.validation.Errors;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@Controller
@RequestMapping(value="/api/lectures", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class LectureController {

    private final LectureRepository lectureRepository;
    private final ModelMapper modelMapper;
    private final LectureValidator lectureValidator;

    @PostMapping
    public ResponseEntity createLecture (@RequestBody @Valid LectureReqDto lectureReqDto,
                                         Errors errors) {
        // 입력항목 체크
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        // 입력항목의 비즈니스 로직 체크
        lectureValidator.validate(lectureReqDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        Lecture lecture = modelMapper.map(lectureReqDto, Lecture.class);

        // free, offline 값을 갱신
        lecture.update();

        Lecture savedLecture = lectureRepository.save(lecture);
        LectureResDto lectureResDto = modelMapper.map(savedLecture, LectureResDto.class);
        WebMvcLinkBuilder linkBuilder = linkTo(LectureController.class).slash(lectureResDto.getId()); // http://localhost:8080/api/lectures/10
        URI createUri = linkBuilder.toUri();

        // HATEOAS
        LectureResource lectureResource = new LectureResource(lectureResDto);
        lectureResource.add(linkTo(LectureController.class).withRel("query-lectures"));
//        lectureResource.add(linkBuilder.withSelfRel());  // self link 삭제 (리소스 생성자에 코드 추가)
        lectureResource.add(linkBuilder.withRel("update-lecture"));

        return ResponseEntity.created(createUri).body(lectureResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(errors);
    }
}
