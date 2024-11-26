package org.myweb.first.files.member.model.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.files.member.jpa.entity.MemberFilesEntity;
import org.myweb.first.files.member.jpa.repository.MemberFilesRepository;
import org.myweb.first.files.member.model.dto.MemberFiles;
import org.myweb.first.member.jpa.entity.MemberEntity;
import org.myweb.first.member.jpa.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberFilesService {
    private final MemberFilesRepository memberFilesRepository;
    private final MemberRepository memberRepository;

    private final String uploadDir = "uploads/profile_pictures"; // 파일 업로드 디렉토리

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
     * @1. 회원 존재 여부 확인
     * @2. 업로드할 파일의 유효성 검사
     * @3. 파일이름을 고유하게 변경하여 저장(rename)
     * @4. 기존 프로필 사진이 있다면 삭제
     * @5. 새로운 프로필 사진 엔터티를 생성하고 저장한다.
     *
     * @param memberUuid 회원 UUID
     * @param uploadFile 업로드할 파일
     * @return 성공여부(0, 1)
     * @throws IOException
     */
    @Transactional
    public int uploadMemberFiles(String memberUuid, MultipartFile uploadFile) throws IOException {
        try {
            // 회원 존재 여부 확인
            Optional<MemberEntity> member = memberRepository.findByMemUuid(memberUuid);
            if (member.isEmpty()) {
                throw new IllegalArgumentException("회원이 존재하지 않습니다.");
            }

            // 파일 유효성 검사
            if (uploadFile.isEmpty()) {
                throw new IllegalArgumentException("업로드할 파일이 없습니다.");
            }

            // 원본 파일명과 저장할 파일명 설정
            String originalName = uploadFile.getOriginalFilename();
            String rename = memberUuid + "_" + originalName; // 멤버UUID_원래파일명

            // 파일 저장 경로 생성
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일 저장
            Path filePath = uploadPath.resolve(rename);
            Files.copy(uploadFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 기존 프로필 사진이 있다면 삭제
            Optional<MemberFilesEntity> existingProfile = memberFilesRepository.findByMember_MemUuid(memberUuid);
            existingProfile.ifPresent(memberFilesRepository::delete);

            // 프로필 사진 엔터티 생성 및 저장
            MemberFilesEntity memberFilesEntity = MemberFilesEntity.builder()
                    .mfMemUuid(memberUuid)
                    .mfOriginalName(originalName)
                    .mfRename(rename)
                    .build();
            memberFilesRepository.save(memberFilesEntity).toDto();
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            return 0;
        }
    }


    /**
     * 프로필 사진 삭제
     * @param memUuid 회원 UUID
     * @return 성공여부(0, 1)
     */
    @Transactional
    public int deleteMemberFile(String memUuid) {

        Optional<MemberFilesEntity> memberFiles = memberFilesRepository.findByMember_MemUuid(memUuid);
        if (memberFiles.isPresent()) {
            MemberFilesEntity memberFilesEntity = memberFiles.get();
            // 파일 삭제
            Path filePath = Paths.get(uploadDir, memberFilesEntity.getMfRename());
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                e.printStackTrace();
                return 0;
            }

            // 데이터베이스에서 삭제
            memberFilesRepository.delete(memberFilesEntity);
            return 1;
        }

        return 0;
    }


}
