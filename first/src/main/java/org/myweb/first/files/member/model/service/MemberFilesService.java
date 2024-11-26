package org.myweb.first.files.member.model.service;

import lombok.RequiredArgsConstructor;
import org.myweb.first.files.member.jpa.entity.MemberFilesEntity;
import org.myweb.first.files.member.jpa.repository.MemberFilesRepository;
import org.myweb.first.files.member.model.dto.MemberFiles;
import org.myweb.first.member.jpa.entity.MemberEntity;
import org.myweb.first.member.jpa.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberFilesService {
    private final MemberFilesRepository memberFilesRepository;
    private final MemberRepository memberRepository;

    private final String uploadDir = "uploads/profile_pictures"; // 파일 업로드 디렉토리

    /**
     * 회원 UUID로 프로필 사진 조회
     * @param memUuid 회원 UUID
     * @return MemberFiles DTO 또는 null
     */
    @Transactional(readOnly = true)
    public MemberFiles getMemberFileByMemberUuid(String memUuid) {
        Optional<MemberFilesEntity> memberFiles = memberFilesRepository.findByMember_MemUuid(memUuid);
        return memberFiles.map(MemberFilesEntity::toDto).orElse(null);
    }


    @Transactional
    public MemberFiles uploadMemberFiles(String memberUuid, MultipartFile uploadFile) throws IOException {
        // 회원 존재 여부 확인
        Optional<MemberEntity> member = memberRepository.findByMemUuid(memberUuid.toString());
        if(member.isEmpty()){
            throw new IllegalArgumentException("회원이 존재하지 않습니다.");
        }

        // 파일 유효성 검사
        if(uploadFile.isEmpty()){
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        // 원본 파일명과 저장할 파일명 설정

    }


}
