package com.agileavengers.icuconnectbackend.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.agileavengers.icuconnectbackend.model.File;
import com.agileavengers.icuconnectbackend.service.IFileService;

@RestController
@RequestMapping("/communities")
public class FileController {
    IFileService fileService;

    @Autowired
    FileController(IFileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping(value = "{moduleId}/files", params = { "page", "size" })
    public Page<File> getCommunityFiles(@PathVariable("moduleId") String moduleId, @RequestParam("page") int pageNumber,
            @RequestParam("size") int size, @RequestParam("year") Optional<Integer> year) {
        return fileService.getCommunityFiles(moduleId, pageNumber, size, year);
    }

    @PostMapping(value = "{moduleId}/files")
    public ResponseEntity<String> uploadFile(@PathVariable("moduleId") String moduleId,
            @RequestParam("file") MultipartFile file) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        fileService.uploadFile(file, principal.getUsername(), moduleId);
        return ResponseEntity.ok("File uploaded");
    }

    @GetMapping(value = "{moduleId}/files/{id}/download")
    public byte[] downloadFile(@PathVariable("moduleId") String moduleId, @PathVariable("id") Long id) {
        return fileService.downloadFile(id, moduleId);
    }
}
