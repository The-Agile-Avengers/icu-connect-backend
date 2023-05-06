package com.agileavengers.icuconnectbackend.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.agileavengers.icuconnectbackend.model.dto.FileDto;

public interface IFileService {
    public FileDto uploadFile(MultipartFile file, String username, String moduleId);

    public byte[] downloadFile(Long id, String moduleId);

    public void deleteFile(Long id, String moduleId, String username);

    Page<FileDto> getCommunityFiles(String moduleId, int pageNumber, int size, Optional<Integer> year);

}
