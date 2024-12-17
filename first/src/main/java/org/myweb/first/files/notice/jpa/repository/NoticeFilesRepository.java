package org.myweb.first.files.notice.jpa.repository;

import org.myweb.first.files.notice.jpa.entity.NoticeFilesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeFilesRepository extends JpaRepository<NoticeFilesEntity, String> {
    List<NoticeFilesEntity> findByNfNotId(String nfNotId);
}
