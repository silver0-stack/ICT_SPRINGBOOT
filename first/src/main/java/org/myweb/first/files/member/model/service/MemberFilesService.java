package org.myweb.first.files.member.model.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.files.member.jpa.entity.MemberFilesEntity;
import org.myweb.first.files.member.jpa.repository.MemberFilesRepository;
import org.myweb.first.files.member.model.dto.MemberFiles;
import org.myweb.first.member.jpa.entity.MemberEntity;
import org.myweb.first.member.jpa.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberFilesService {
    private final MemberFilesRepository memberFilesRepository;
    private final MemberRepository memberRepository;

    @Value("${file.upload-dir}") // 기본 파일 업로드 디렉토리 주입
    private String uploadDir;

    /**
     * 멤버 프로필 사진 경로 반환
     * 프론트엔드에서 이미지 파일을 가져오려면 경로를 반환해야 한다.
     *
     * @param memUuid 회원 UUID
     * @return Path 프로필 사진 경로
     */
    public Path getProfilePicturePath(String memUuid) {
        // 1. DB에서 UUID(memUuid)를 기반으로 파일 이름(mfRename) 조회
        String fileName = memberFilesRepository.findByMember_MemUuid(memUuid)
                .map(MemberFilesEntity::getMfRename)
                .orElseThrow(() -> new IllegalArgumentException("프로필 사진이 존재하지 않습니다."));

        // 2. 기본 경로(uploadDir)와 파일 이름 결합하여 전체 경로 생성
        return Paths.get(uploadDir, "member").resolve(fileName);
    }

    /**
     * 회원 UUID로 프로필 사진 조회
     *
     * @param memUuid 회원 UUID
     * @return MemberFiles DTO 또는 null
     */
    @Transactional(readOnly = true)
    public MemberFiles getMemberFileByMemberUuid(String memUuid) {
        Optional<MemberFilesEntity> memberFiles = memberFilesRepository.findByMember_MemUuid(memUuid);
        return memberFiles.map(MemberFilesEntity::toDto).orElse(null);
    }

    /**
     * 프로필 사진 업로드
     *
     * @param memberUuid 회원 UUID
     * @param uploadFile 업로드할 파일
     * @return MemberFiles DTO
     * @throws IOException 예외 처리
     */
    @Transactional
    public MemberFiles uploadMemberFiles(String memberUuid, MultipartFile uploadFile) throws IOException {
        // 1. 회원 존재 여부 확인
        MemberEntity member = memberRepository.findByMemUuid(memberUuid)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 2. 파일 유효성 검사
        if (uploadFile == null || uploadFile.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        String originalName = StringUtils.cleanPath(Objects.requireNonNull(uploadFile.getOriginalFilename()));
        if (!isValidFileExtension(originalName)) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다.");
        }

        // 3. 저장할 파일명 생성
        String rename = memberUuid + "_" + originalName;

        // 4. 파일 저장
        Path uploadPath = Paths.get(uploadDir, "member").toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(rename);
        Files.copy(uploadFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 5. 기존 파일 삭제 및 DB 업데이트
        memberFilesRepository.findByMember_MemUuid(memberUuid).ifPresent(existing -> {
            try {
                Files.deleteIfExists(Paths.get(uploadDir, "member", existing.getMfRename()));
            } catch (IOException e) {
                log.error("기존 파일 삭제 실패: {}", e.getMessage());
            }
            memberFilesRepository.delete(existing);
        });

        // 6. 새 파일 정보 저장
        MemberFilesEntity entity = MemberFilesEntity.builder()
                .member(member)
                .mfOriginalName(originalName)
                .mfRename(rename)
                .build();

        memberFilesRepository.save(entity);

        return entity.toDto();
    }

    /**
     * 파일 확장자 검증 메서드
     *
     * @param fileName 파일 이름
     * @return boolean 허용된 확장자 여부
     */
    private boolean isValidFileExtension(String fileName) {
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif"};
        return Arrays.stream(allowedExtensions).anyMatch(fileName.toLowerCase()::endsWith);
    }

    /**
     * 파일 삭제
     *
     * @param memUuid 회원 UUID
     * @return 성공 여부 (1: 성공, 0: 실패)
     */
    @Transactional
    public int deleteMemberFile(String memUuid) {
        Optional<MemberFilesEntity> memberFiles = memberFilesRepository.findByMember_MemUuid(memUuid);
        if (memberFiles.isPresent()) {
            try {
                Files.delete(Paths.get(uploadDir, "member", memberFiles.get().getMfRename()));
                memberFilesRepository.delete(memberFiles.get());
                return 1;
            } catch (IOException e) {
                log.error("파일 삭제 실패: {}", e.getMessage());
            }
        }
        return 0;
    }
}
