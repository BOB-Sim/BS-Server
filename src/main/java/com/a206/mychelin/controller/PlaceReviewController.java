package com.a206.mychelin.controller;

import com.a206.mychelin.config.AuthConstants;
import com.a206.mychelin.exception.PageIndexLessThanZeroException;
import com.a206.mychelin.service.PlaceReviewService;
import com.a206.mychelin.util.TokenUtils;
import com.a206.mychelin.web.dto.ReviewDeleteRequest;
import com.a206.mychelin.web.dto.ReviewEditRequest;
import com.a206.mychelin.web.dto.ReviewRequest;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@CrossOrigin("*")
@RestController
@RequestMapping("/place/review")
@RequiredArgsConstructor
public class PlaceReviewController {
    final private PlaceReviewService placeReviewService;

    @ApiOperation(value = "닉네임을 이용하여 해당 유저의 모든 맛집 리뷰 조회(최신순)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nickName", value = "유저 고유 닉네임"),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호", required = false, dataType = "int", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "pagesize", value = "페이지당 보여주는 데이터 개수", required = false, dataType = "int", paramType = "query", defaultValue = "10"),
    })
    @GetMapping("/user/{nickName}")
    public ResponseEntity getPlaceReviewsByUser(@PathVariable String nickName
            , @RequestParam(defaultValue = "1") int page
            , @RequestParam(defaultValue = "10") int pagesize) throws PageIndexLessThanZeroException {
        try{
            return placeReviewService.getPlaceReviewsByUser(nickName,page,pagesize);
        }
        catch (ArithmeticException | IllegalArgumentException e){
            throw new PageIndexLessThanZeroException();
        }

    }

    @ApiOperation(value = "식당 모든 리뷰 가져오기(최신순)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "placeId", value = "식당 고유 번호"),
            @ApiImplicitParam(name = "page", value = "조회할 페이지 번호", required = false, dataType = "int", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "pagesize", value = "페이지당 보여주는 데이터 개수", required = false, dataType = "int", paramType = "query", defaultValue = "10"),
    })
    @GetMapping("/{placeId}")
    public ResponseEntity getPlaceReviewsByUser(@PathVariable int placeId
            , @RequestParam(defaultValue = "1") int page
            , @RequestParam(defaultValue = "10") int pagesize) throws PageIndexLessThanZeroException {
        try{
            return placeReviewService.getPlaceAllReviewsByPlaceId(placeId,page,pagesize);
        }
        catch (ArithmeticException | IllegalArgumentException e){
            throw new PageIndexLessThanZeroException();
        }

    }

    @ApiOperation(value = "식당 리뷰 추가")
    @PostMapping
    public ResponseEntity addPlaceReviews(@RequestHeader(AuthConstants.AUTH_HEADER) String myToken, @RequestBody ReviewRequest review) {
        String token = TokenUtils.getTokenFromHeader(myToken);
        String userId = TokenUtils.getUserIdFromToken(token);
        return placeReviewService.addPlaceReviews(userId, review);
    }

    @ApiOperation(value = "식당 리뷰 수정 ")
    @PutMapping
    public ResponseEntity editPlaceReviews(@RequestHeader(AuthConstants.AUTH_HEADER) String myToken, @RequestBody ReviewEditRequest review) {
        String token = TokenUtils.getTokenFromHeader(myToken);
        String userId = TokenUtils.getUserIdFromToken(token);
        return placeReviewService.editPlaceReviews(userId, review);
    }

    @ApiOperation(value = "식당 리뷰 삭제")
    @DeleteMapping
    public ResponseEntity deletePlaceReviews(@RequestHeader(AuthConstants.AUTH_HEADER) String myToken, @RequestBody ReviewDeleteRequest review) {
        String token = TokenUtils.getTokenFromHeader(myToken);
        String userId = TokenUtils.getUserIdFromToken(token);
        return placeReviewService.deletePlaceReviews(userId, review);
    }

    @ApiOperation(value = "리뷰 이미지 추가")
    @ApiImplicitParam(name = "file", value = "이미지 파일")
    @PostMapping("/image/{reviewId}")
    public ResponseEntity saveReviewImage(@RequestParam MultipartFile file, @RequestHeader(AuthConstants.AUTH_HEADER) String myToken,@PathVariable int reviewId)throws IOException {
        String token = TokenUtils.getTokenFromHeader(myToken);
        String userId = TokenUtils.getUserIdFromToken(token);
        return placeReviewService.saveReviewImage(file, userId,reviewId);
    }
}