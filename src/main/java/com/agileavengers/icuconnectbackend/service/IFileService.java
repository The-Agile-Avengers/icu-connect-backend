package com.agileavengers.icuconnectbackend.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.agileavengers.icuconnectbackend.model.File;

public interface IFileService {
    public String uploadFile(MultipartFile file, String username, String moduleId);

    public byte[] downloadFile(Long id, String moduleId);

    public void deleteFile(String fileName);

    Page<File> getCommunityFiles(String moduleId, int pageNumber, int size, Optional<Integer> year);

    // default File convertMultiPartToFile(MultipartFile file) {
    // File convertedFile = new File(file.getOriginalFilename());
    // try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
    // fos.write(file.getBytes());
    // } catch (IOException e) {
    // System.err.println("Error converting multipartFile to file");
    // }

    // return convertedFile;
    // }
}
