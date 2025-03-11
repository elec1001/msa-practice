package com.practice.spring.boardservice.controller;

import com.practice.spring.boardservice.dto.BoardDetailResponseDTO;
import com.practice.spring.boardservice.dto.BoardListResponseDTO;
import com.practice.spring.boardservice.model.Article;
import com.practice.spring.boardservice.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardApiController {

    private final BoardService boardService;

    @GetMapping
    public BoardListResponseDTO getBoards(@RequestParam(name = "page",defaultValue ="1")int page,
                                          @RequestParam(name = "size",defaultValue = "10")int size){
        //게시글 목록 가져오기
        List<Article> articles=boardService.getBoardArticles(page,size);
        //전체 게시글 수 가져오기
        int totalarticleCnt=boardService.getTotalArticleCnt();
        //마지막 페이지 여부 계산
        boolean last=(page*size)>=totalarticleCnt;

        return BoardListResponseDTO.builder()
                .articles(articles)
                .last(last)
                .build();
    }

    @PostMapping
    public void saveArticle(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("hiddenUserId") String userId,
            @RequestParam("file") MultipartFile file){
        boardService.saveAricle(userId,title,content,file);
    }

    @PostMapping("/{id}")
    public BoardDetailResponseDTO getBoardDetail(@PathVariable long id){
        return boardService
                .getBoardDetail(id)
                .toBoardDetailResponseDTO();
    }

    @GetMapping("/file/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Resource resource = boardService.downloadFile(fileName);

        // 한글 파일명을 URL 인코딩
        String encoded = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .body(resource);
    }
}
