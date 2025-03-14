package com.practice.spring.boardservice.service;

import com.practice.spring.boardservice.dto.BoardDeleteRequestDTO;
import com.practice.spring.boardservice.mapper.BoardMapper;
import com.practice.spring.boardservice.model.Article;
import com.practice.spring.boardservice.model.Paging;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper boardMapper;
    private final FileService fileService;

    public List<Article> getBoardArticles(int page, int size) {
        int offset = (page - 1) * size;//페이지 1부터 시작 offset 계산
        return boardMapper.getArticles(
                Paging.builder()
                        .offset(offset)
                        .size(size)
                        .build()
        );
    }

    @Transactional
    public void saveAricle(String userId, String title, String content, MultipartFile file) {
        String path = null;
        if (!file.isEmpty()) {
            path = fileService.fileUpload(file);
        }

        boardMapper.saveArticle(
                Article.builder()
                        .title(title)
                        .content(content)
                        .userId(userId)
                        .filePath(path)
                        .build()
        );
    }

    public int getTotalArticleCnt() {
        return boardMapper.getArticleCnt();
    }

    public Article getBoardDetail(long id) {
        return boardMapper.getArticleById(id);
    }

    public Resource downloadFile(String fileName) {
        return fileService.downloadFile(fileName);
    }

    public void updateArticle(Long id, String title, String content, MultipartFile file, Boolean fileChanged, String filePath) {
        String path = null;

        if (!file.isEmpty()) {
            path = fileService.fileUpload(file);
        }
        if (fileChanged) {
            fileService.deleteFile(filePath);
        } else {
            path = filePath;
        }
        boardMapper.updateArticle(
                Article.builder()
                        .id(id)
                        .title(title)
                        .content(content)
                        .filePath(path)
                        .build()
        );
    }

    public void deleteBoardById(Long id, BoardDeleteRequestDTO boardDeleteRequestDTO) {
        fileService.deleteFile(boardDeleteRequestDTO.getFilePath());
        boardMapper.deleteBoardById(id);
    }
}

