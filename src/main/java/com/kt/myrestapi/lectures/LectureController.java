package com.kt.myrestapi.lectures;

import com.kt.myrestapi.common.ErrorsResource;
import com.kt.myrestapi.lectures.dto.LectureReqDto;
import com.kt.myrestapi.lectures.dto.LectureResDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.validation.Errors;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@Controller
@RequestMapping(value = "/api/lectures", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class LectureController {

    private final LectureRepository lectureRepository;
    private final ModelMapper modelMapper;
    private final LectureValidator lectureValidator;

    @PutMapping("/{id}")
    public ResponseEntity updateLecture(@PathVariable Integer id,
                                        @RequestBody @Valid LectureReqDto lectureReqDto,
                                        Errors errors) {
        Optional<Lecture> optionalLecture = this.lectureRepository.findById(id);
        //id와 매핑되는 Entity가 없으면 404 에러
        if(optionalLecture.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(id + " Lecture Not Found!");
        }
        //입력항목 체크해서 오류가 있다면 400 에러
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        //입력항목 Biz로직 체크해서 오류가 있다면 400 에러
        this.lectureValidator.validate(lectureReqDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        //id와 매핑되는 Lecture 엔티티가 있으면 Optional에서 꺼내기
        Lecture existingLecture = optionalLecture.get();
        //LectureReqDto -> Lecture 타입으로 매핑
        this.modelMapper.map(lectureReqDto, existingLecture);
        //Lecture 엔티티를 DB에 저장
        Lecture savedLecture = this.lectureRepository.save(existingLecture);
        //저장된 Lecture 엔티티 -> LectureResDto 타입으로 매핑
        LectureResDto lectureResDto = modelMapper.map(savedLecture, LectureResDto.class);
        //SelfLink와 함께 전달하기 위해서 LectureResDto를 LectureResource로 래핑한다
        LectureResource lectureResource = new LectureResource(lectureResDto);
        return ResponseEntity.ok(lectureResource);
    }

    @GetMapping("/{id}")
    public ResponseEntity getLecture(@PathVariable Integer id) {
        Optional<Lecture> optionalLecture = this.lectureRepository.findById(id);
        if (optionalLecture.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(id + "Lecture Not found");
        }
        Lecture lecture = optionalLecture.get();
        LectureResDto lectureResDto = modelMapper.map(lecture, LectureResDto.class);
        LectureResource lectureResource = new LectureResource(lectureResDto);
        return ResponseEntity.ok(lectureResource);
    }

    @GetMapping
    public ResponseEntity queryLecture(Pageable pageable,
                                       PagedResourcesAssembler<LectureResDto> assembler) {
        Page<Lecture> lecturePage = this.lectureRepository.findAll(pageable);
        // 1단계: first, prev, next, last 링크
        Page<LectureResDto> lectureResDtoPage =
                lecturePage.map(lecture -> modelMapper.map(lecture, LectureResDto.class));
//        PagedModel<EntityModel<LectureResDto>> pagedResources = assembler.toModel(lectureResDtoPage);
        // 2단계: first, prev, next, last 링크 + self 링크 포함
        PagedModel<LectureResource> pagedResources =
                assembler.toModel(lectureResDtoPage,
                        lectureResDto -> {
                            return new LectureResource(lectureResDto);
                        });
        // RepresentationModelAssembler의 추상에서도 R toModel(T entity)
        return ResponseEntity.ok(pagedResources);
    }

    @PostMapping
    public ResponseEntity createLecture(@RequestBody @Valid LectureReqDto lectureReqDto,
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

    private ResponseEntity<ErrorsResource> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
