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
    @Value("${file.upload-dir}") // 파일 업로드 디렉토리 경로 주입
    private String uploadDir;


    // 지금은 프로필 사진 조회 컨트롤러 메소드는 MemberFiles DTO를 ResponseEntity로 감싸서 반환한다
    /* 하지만 프론트엔드에서 이미지 파일을 가져오려면 응답으로 JSON이 아니라 실제 이미지 파일을 반환해야 한다.
    * 따라서 이미지 Path를 반환하는 메소드를 추가해야 함*/
    public Path getProfilePicturePath(String memUuid){
        // 1. DB에서 UUID(memUuid)를 기반으로 파일 이름(mfRename) 조회
        String fileName=memberFilesRepository.findByMember_MemUuid(memUuid)
                .map(MemberFilesEntity::getMfRename)  // MemberFilesEntity에서 mfRename(저장된 파일 이름) 추출
                .orElseThrow(() -> new IllegalArgumentException("프로필 사진이 존재하지 않습니다"));

        // 2. 기본 경로(uploadDir)와 파일 이름(fileName)을 결합하여 전체 경로 생성
        // Paths.get(String first, String ... more)는 Java NIO에서 제공하는 메소드
        // 파일 시스템 경로를 나타내는 Path 객체를 생성한다.
        // uploadDir: 파일이 저장된 기본 디렉토리 경로
        // Paths.get(uploadDir) -> Path 객체를 반환하며, 이는 파일 시스템의 경로를 표현한다

        // .resolve(fileName)는 현재 경로에 하위 경로를 결합해 새로운 경로를 반환한다.
        // fileName: 실제 파일 이름(예: 45s4df45ds-sfdf-fdfdfsd5f_profile123.jpg)
        // 호출 결과:
        // uploadDir: /uploads/profile_pictures
        // fileName: 45s4df45ds-sfdf-fdfdfsd5f_profile123.jpg
        // -> /uploads/profile_pictures/45s4df45ds-sfdf-fdfdfsd5f_profile123.jpg
        // 즉, 경로를 동적으로 생성하여 파일의 전체 경로를 얻는다.
        return Paths.get(uploadDir).resolve(fileName);

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
     * @1. 회원 존재 여부 확인
     * @2. 업로드할 파일의 유효성 검사
     * @3. 파일이름을 고유하게 변경하여 저장(rename)
     * @4. 기존 프로필 사진이 있다면 삭제
     * @5. 새로운 프로필 사진 엔터티를 생성하고 저장한다.
     *df
     * @param memberUuid 회원 UUID
     * @param uploadFile 업로드할 파일
     * @return 성공여부(0, 1)
     * @throws IOException
     */
    @Transactional
    public MemberFiles uploadMemberFiles(String memberUuid, MultipartFile uploadFile) throws IOException {
        // 회원 존재 여부 확인
        MemberEntity member = memberRepository.findByMemUuid(memberUuid)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 파일 유효성 검사
        if (uploadFile == null || uploadFile.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }


        // StringUtils.cleanPath는 문자열 경로에서 불필요하거나 잘못된 경로 요소를 정리해주는 메소드
        // .나 ..과 같은 상대 경로를 제거
        // 경로에서 잘못된 요소가 있을 경우 정리
        // 사용 이유: 파일 이름을 정리하고 잠재적으로 문제가 될 수 있는 경로 요소를 제거하여 보안을 강화한다.

        // String result = StringUtils.cleanPath("../myfolder/..file.txt");
        // System.out.println(result);  // 출력: "file.txt"

        // Object.requireNonNull은 객체가 null인지 확인하고, null이면 NullPointerException을 던진다.
        // 객체가 null인지 확인해, 런타임에서 예외가 발생하지 않도록 보장.
        // 예외 메시지를 커스텀할 수도 있음
        String originalName = StringUtils.cleanPath(Objects.requireNonNull(uploadFile.getOriginalFilename(), "파일 이름은 null일 수 없습니다."));
        // NullPointerException 발생: "파일 이름은 null일 수 없습니다."

        // 파일 확장자 검증
        if (!isValidFileExtension(originalName)) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다.");
        }

        // 저장할 파일명 생성 (멤버 UUID + 원본 파일명)
        String rename = memberUuid + "_" + originalName;

        // 파일 저장 경로 생성
        // Paths.get은 문자열로 표현된 경로를 Path 객체로 변환한다.
        // 문자열 경로를 기반으로 파일 시스템 경로를 나타내는 Path 객체 생성
        // Path 객체는 파일/디렉토리 작업에 사용된다.
        // 사용이유: uploadDir와 같은 디렉토리를 작업할 때 문자열 대신 Path 객체를 사용하여 타입 안정성을 확보

        // toAbsolutePath():
        // 경로를 절대경로로 변환한다.
        // 작업 디렉토리에 관계없이 파일이 저장될 정확한 경로를 보장

        // normalize()
        // 경로를 간소화하여 중복 요소 제거
        // .(현재 디렉토리)와 ..(부모 디렉토리) 제거
        // 경로를 명확하게 만들어 파일 작업 중 혼란을 방지
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath); // 업로드 디렉토리가 없으면 생성
        }

        // Path.resolve(uploadPath): 경로에 하위 폴더 추가
        Path filePath = uploadPath.resolve(rename);

        // Files.copy: 파일 또는 스트림 복사
        // 입력 스트림(InputStream)에서 읽어 파일에 저장
        // 기존 파일이 있는 경우 덮어쓸지 결정 (StandardCopyOption.REPLACE_EXISTING 사용 시 덮어씀)
        // 사용이유: 업로드된 파일의 내용을 서버 디스크에 저장
        Files.copy(uploadFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 기존 프로필 사진 삭제
        Optional<MemberFilesEntity> existingProfile = memberFilesRepository.findByMember_MemUuid(memberUuid);
        existingProfile.ifPresent(memberFilesEntity -> {
            Path existingFilePath = Paths.get(uploadDir, memberFilesEntity.getMfRename());
            try {
                Files.deleteIfExists(existingFilePath); // 기존 파일 삭제
            } catch (IOException e) {
                log.error("기존 프로필 사진 삭제 실패: {}", e.getMessage(), e);
            }
            memberFilesRepository.delete(memberFilesEntity); // DB에서 엔티티 삭제
        });

        // 새로운 프로필 사진 엔티티 생성 및 저장
        MemberFilesEntity memberFilesEntity = MemberFilesEntity.builder()
                .member(member)
                .mfOriginalName(originalName)
                .mfRename(rename)
                .build();
        MemberFilesEntity savedEntity = memberFilesRepository.save(memberFilesEntity);

        // 관계 설정
        member.setProfilePicture(savedEntity);
        memberRepository.save(member);

        return savedEntity.toDto(); // DTO로 반환
    }

    private boolean isValidFileExtension(String fileName) {
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif"};
        return Arrays.stream(allowedExtensions).anyMatch(fileName.toLowerCase()::endsWith);
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
