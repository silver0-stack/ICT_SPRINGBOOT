package org.myweb.first.files.notice.model.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.files.notice.jpa.entity.NoticeFilesEntity;
import org.myweb.first.files.notice.jpa.repository.NoticeFilesRepository;
import org.myweb.first.files.notice.model.dto.NoticeFiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeFilesService {

    @Autowired
    private NoticeFilesRepository noticeFilesRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 공지사항 파일 업로드
     */
    @Transactional
    public NoticeFiles uploadNoticeFile(String noticeId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 존재하지 않습니다.");
        }

        String originalName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String rename = UUID.randomUUID() + "_" + originalName;

        Path uploadPath = Paths.get(uploadDir, "notice").toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        Path filePath = uploadPath.resolve(rename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        NoticeFilesEntity entity = NoticeFilesEntity.builder()
                .nfId(UUID.randomUUID().toString())
                .nfNotId(noticeId)
                .nfOriginalName(originalName)
                .nfRename(rename)
                .build();

        return noticeFilesRepository.save(entity).toDto();
    }

    /**
     * 공지사항 파일 삭제
     */
    @Transactional
    public void deleteNoticeFile(String nfId) throws IOException {
        NoticeFilesEntity entity = noticeFilesRepository.findById(nfId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));
        Files.deleteIfExists(Paths.get(uploadDir, "notice", entity.getNfRename()));
        noticeFilesRepository.delete(entity);
    }

    /**
     * 공지사항 파일 조회
     */
    public List<NoticeFiles> getNoticeFiles(String noticeId) {
        return noticeFilesRepository.findByNfNotId(noticeId).stream()
                .map(NoticeFilesEntity::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 공지사항 파일의 Path 반환 (이미지 등)
     * @param fileId 파일 ID
     * @return Path 파일 경로
     */
    public Path getNoticeFilePath(String fileId) {
        String fileName = noticeFilesRepository.findById(fileId)
                .map(NoticeFilesEntity::getNfRename)
                .orElseThrow(() -> new IllegalArgumentException("파일이 존재하지 않습니다."));

        // 파일 저장 경로 (uploads/notice)
        return Paths.get(uploadDir, "notice").resolve(fileName);
    }

}
