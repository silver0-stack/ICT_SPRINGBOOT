package org.myweb.first.files.member.controller;

import lombok.RequiredArgsConstructor;
import org.myweb.first.files.member.model.service.MemberFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile-pictures")
@RequiredArgsConstructor
public class MemberFileController {
    @Autowired
    private MemberFilesService memberFileService;


}
