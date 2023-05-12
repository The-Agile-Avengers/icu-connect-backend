package com.agileavengers.icuconnectbackend.service.implementation;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.agileavengers.icuconnectbackend.mapper.FileMapper;
import com.agileavengers.icuconnectbackend.model.Community;
import com.agileavengers.icuconnectbackend.model.File;
import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.FileDto;
import com.agileavengers.icuconnectbackend.repository.CommentRepository;
import com.agileavengers.icuconnectbackend.repository.CommunityRepository;
import com.agileavengers.icuconnectbackend.repository.FileRepository;
import com.agileavengers.icuconnectbackend.repository.InstructorRepository;
import com.agileavengers.icuconnectbackend.repository.PostRepository;
import com.agileavengers.icuconnectbackend.repository.RatingRepository;
import com.agileavengers.icuconnectbackend.repository.UserRepository;
import com.agileavengers.icuconnectbackend.service.IFileService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FileService implements IFileService {
    private final FileStore fileStore;
    private final FileRepository fileRepository;
    PostRepository postRepository;
    CommunityRepository communityRepository;
    InstructorRepository instructorRepository;
    RatingRepository ratingRepository;
    UserRepository userRepository;
    CommentRepository commentRepository;
    private final FileMapper fileMapper;

    @Override
    public FileDto uploadFile(MultipartFile multipartFile, String username, String moduleId) {
        if (multipartFile.isEmpty()) {
            throw new IllegalStateException("Cannot find file");
        }

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user does not exist");
        }

        Optional<Community> community = communityRepository.findCommunityByModuleId(moduleId);
        if (community.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "community does not exist");
        }

        // get file metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", multipartFile.getContentType());
        metadata.put("Content-Length", String.valueOf(multipartFile.getSize()));

        // save pdf file in S3 and in SQL Database
        String fileName = String.format("%s", multipartFile.getOriginalFilename());

        
        // recursive function to change name if already exists
        fileName = rename(fileName, community.get().getModuleId());
        
        File file = File.builder()
        .community(community.get())
        .creation(new Timestamp(System.currentTimeMillis()))
        .creator(user.get())
        .fileName(fileName)
        .build();
        
        try {
            fileStore.uploadFile(fileName, Optional.of(metadata), multipartFile.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload file", e);
        }

        file = fileRepository.save(file);

        file.setFilePath("/communities/" + community.get().getModuleId() + "/files/" + file.getId() + "/download");

        fileRepository.save(file);

        community.get().getUploadedFiles().add(file);

        return fileMapper.toDto(file);
    }

    @Override
    public byte[] downloadFile(Long id, String moduleId) {
        Optional<File> file = fileRepository.findByIdAndCommunity_ModuleId(id, moduleId);

        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File does not exist");
        }

        return fileStore.download(file.get().getFileName());
    }

    @Override
    public void deleteFile(Long id, String moduleId, String username) {
        Optional<File> fileToDelete = fileRepository.findByIdAndCommunity_ModuleId(id, moduleId);

        if (fileToDelete.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file does not exist");
        }

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user does not exist");
        }

        Optional<Community> community = communityRepository.findCommunityByModuleId(moduleId);
        if (community.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "community does not exist");
        }

        if (!user.get().equals(fileToDelete.get().getCreator())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user is not allowed to delete file");
        }

        fileStore.deleteFile(fileToDelete.get().getFileName());
        fileRepository.delete(fileToDelete.get());
    }

    @Override
    public Page<FileDto> getCommunityFiles(String moduleId, int pageNumber, int size, Optional<Integer> year) {

        Pageable page = PageRequest.of(pageNumber, size);
        Optional<Community> community = communityRepository.findCommunityByModuleId(moduleId);

        if (community.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "community does not exist");
        }

        Page<File> filePage;
        if (year.isPresent()) {
            filePage = fileRepository.findAllByCreationBetween(Timestamp.valueOf(year.get() + "-01-01 00:00:00.0"),
                    Timestamp.valueOf(year.get() + "-12-31 23:59:59.9"), page);
        } else {
            filePage = fileRepository.findAllByCommunity_ModuleId(moduleId, page);
        }

        for (File f : filePage.getContent()) {
            f.setFilePath("/communities/" + community.get().getModuleId() + "/files/" + f.getId() + "/download");
        }

        return filePage.map(fileMapper::toDto);
    }

    private String rename(String fileName, String moduleId) {
        int count = 1;
        Optional<File> targetFile = fileRepository.findByFileNameAndCommunity_ModuleId(fileName, moduleId);

        while (targetFile.isPresent()) {
            int extensionIndex = fileName.lastIndexOf(".");
            int parenthesisIndex = fileName.lastIndexOf("(");
            String extension = "";

            if (extensionIndex != -1) {
                extension = fileName.substring(extensionIndex);
                fileName = fileName.substring(0, extensionIndex);
            }

            if (parenthesisIndex != -1) {
                fileName = fileName.substring(0, parenthesisIndex);
            }
            fileName = fileName + "(" + count + ")" + extension;
            count++;
            targetFile = fileRepository.findByFileNameAndCommunity_ModuleId(fileName, moduleId);
        }
        return fileName;
    }
}
