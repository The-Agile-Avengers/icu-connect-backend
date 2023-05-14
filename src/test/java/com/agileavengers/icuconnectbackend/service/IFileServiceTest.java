package com.agileavengers.icuconnectbackend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.agileavengers.icuconnectbackend.mapper.CommentMapper;
import com.agileavengers.icuconnectbackend.mapper.CommunityMapper;
import com.agileavengers.icuconnectbackend.mapper.FileMapper;
import com.agileavengers.icuconnectbackend.mapper.InstructorMapper;
import com.agileavengers.icuconnectbackend.mapper.PostMapper;
import com.agileavengers.icuconnectbackend.mapper.RatingMapper;
import com.agileavengers.icuconnectbackend.mapper.RatingMapperImpl;
import com.agileavengers.icuconnectbackend.mapper.UserMapper;
import com.agileavengers.icuconnectbackend.model.Community;
import com.agileavengers.icuconnectbackend.model.File;
import com.agileavengers.icuconnectbackend.model.Instructor;
import com.agileavengers.icuconnectbackend.model.User;
import com.agileavengers.icuconnectbackend.model.dto.FileDto;
import com.agileavengers.icuconnectbackend.repository.CommentRepository;
import com.agileavengers.icuconnectbackend.repository.CommunityRepository;
import com.agileavengers.icuconnectbackend.repository.FileRepository;
import com.agileavengers.icuconnectbackend.repository.InstructorRepository;
import com.agileavengers.icuconnectbackend.repository.PostRepository;
import com.agileavengers.icuconnectbackend.repository.RatingRepository;
import com.agileavengers.icuconnectbackend.repository.UserRepository;
import com.agileavengers.icuconnectbackend.service.implementation.CommunityService;
import com.agileavengers.icuconnectbackend.service.implementation.FileService;
import com.agileavengers.icuconnectbackend.service.implementation.FileStore;
import com.agileavengers.icuconnectbackend.service.implementation.MappingService;

@ExtendWith({ SpringExtension.class, MockitoExtension.class })
@SpringBootTest()
@MockitoSettings(strictness = Strictness.LENIENT)
class IFileServiceTest {

        IFileService fileService;
        ICommunityService communityService;

        @Mock
        CommunityRepository communityRepository;
        @Mock
        InstructorRepository instructorRepository;
        @Mock
        RatingRepository ratingRepository;
        @Mock
        UserRepository userRepository;
        @Mock
        PostRepository postRepository;
        @Mock
        CommentRepository commentRepository;
        @Mock
        FileRepository fileRepository;
        @Mock
        FileStore fileStore;

    void setupSecurity(User user) {
        when(userRepository.findByUsername(user.getUsername())).thenAnswer(i ->
        Optional.of(user));

        Authentication authentication = mock(Authentication.class);
        // Mockito.whens() for your authorization object
        when(authentication.getPrincipal()).thenReturn(new
        org.springframework.security.core.userdetails.User(user.getUsername(),
        user.getPassword(), new ArrayList<>()));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

        @BeforeEach
        void setup() {
                communityRepository = mock(CommunityRepository.class);
                instructorRepository = mock(InstructorRepository.class);
                communityRepository = mock(CommunityRepository.class);
                userRepository = mock(UserRepository.class);
                ratingRepository = mock(RatingRepository.class);
                commentRepository = mock(CommentRepository.class);
                fileRepository = mock(FileRepository.class);
                MappingService mappingService = new MappingService(userRepository, ratingRepository);
                FileStore fileStore = mock(FileStore.class);
                UserMapper userMapper = Mappers.getMapper(UserMapper.class);
                userMapper.setMappingService(mappingService);
                RatingMapper ratingMapper = new RatingMapperImpl(userMapper);
                ratingMapper.setMappingService(mappingService);
                InstructorMapper instructorMapper = Mappers.getMapper(InstructorMapper.class);
                postRepository = mock(PostRepository.class);
                CommunityMapper communityMapper = Mappers.getMapper(CommunityMapper.class);
                communityMapper.setMappingService(mappingService);
                CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
                commentMapper.setMappingService(mappingService);
                PostMapper postMapper = Mappers.getMapper(PostMapper.class);
                postMapper.setMappingService(mappingService, commentRepository,
                                commentMapper);
                FileMapper fileMapper = Mappers.getMapper(FileMapper.class);
                fileMapper.setMappingService(mappingService);

                this.communityService = new CommunityService(communityRepository,
                                instructorRepository, ratingRepository, userRepository,
                                communityMapper, ratingMapper, instructorMapper, postRepository, postMapper,
                                commentMapper, commentRepository);

                this.fileService = new FileService(fileStore, fileRepository, postRepository, communityRepository,
                                instructorRepository, ratingRepository, userRepository, commentRepository, fileMapper);
        }

        @Test
        void uploadFile() {

                setupSecurity(User.builder().username("test1").password("pw").build());

                Instructor instructor = Instructor.builder().id(2L).name("TestInstructor").build();
                HashSet<File> fileSet = new HashSet<>();

                Community community = Community.builder().id(1L).name("TestCommunity").instructor(instructor)
                                .moduleId("UZH1234").uploadedFiles(fileSet).build();
                when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                                .thenAnswer(i -> Optional.of(community));

                User user1 = User.builder().username("Test1").password("anything").id(2L)
                                .subscriptionSet(Set.of(community))
                                .build();

                final MultipartFile mockFile = mock(MultipartFile.class);
                when(mockFile.getSize()).thenAnswer(i -> 10L);
                when(mockFile.getContentType()).thenAnswer(i -> "application/pdf");
                when(mockFile.isEmpty()).thenAnswer(i -> false);
                when(mockFile.getOriginalFilename()).thenAnswer(i -> "TestFile.pdf");

                doNothing().when(fileStore).uploadFile(Mockito.anyString(), Mockito.any(), Mockito.any());

                when(userRepository.findByUsername(Mockito.anyString()))
                                .thenAnswer(i -> Optional.of(user1));

                File file = File.builder().id(1L).community(community)
                                .creation(new Timestamp(System.currentTimeMillis())).creator(user1)
                                .fileName("TestFile.pdf")
                                .filePath("/communities/" + community.getModuleId() + "/files/" + 1 + "/download")
                                .build();

                when(fileRepository.save(Mockito.any(File.class))).thenAnswer(i -> file);

                FileDto result = fileService.uploadFile(mockFile, user1.getUsername(), community.getModuleId());

                assertEquals(file.getFileName(), result.getFileName(), "Expected to be same FileName");
                assertEquals(file.getFilePath(), result.getFilePath(), "Expected to be the same filePath");
        }

        @Test
        void getFiles() {

                setupSecurity(User.builder().username("test1").password("pw").build());

                Instructor instructor = Instructor.builder().id(2L).name("TestInstructor").build();
                HashSet<File> fileSet = new HashSet<>();

                Community community = Community.builder().id(1L).name("TestCommunity").instructor(instructor)
                                .moduleId("UZH1234").uploadedFiles(fileSet).build();
                when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                                .thenAnswer(i -> Optional.of(community));

                User user1 = User.builder().username("Test1").password("anything").id(2L)
                                .subscriptionSet(Set.of(community))
                                .build();

                final MultipartFile mockFile = mock(MultipartFile.class);
                when(mockFile.getSize()).thenAnswer(i -> 10L);
                when(mockFile.getContentType()).thenAnswer(i -> "application/pdf");
                when(mockFile.isEmpty()).thenAnswer(i -> false);
                when(mockFile.getOriginalFilename()).thenAnswer(i -> "TestFile.pdf");

                doNothing().when(fileStore).uploadFile(Mockito.anyString(), Mockito.any(), Mockito.any());

                when(userRepository.findByUsername(Mockito.anyString()))
                                .thenAnswer(i -> Optional.of(user1));

                File file = File.builder().id(1L).community(community)
                                .creation(new Timestamp(System.currentTimeMillis())).creator(user1)
                                .fileName("TestFile.pdf")
                                .filePath("/communities/" + community.getModuleId() + "/files/" + 1 + "/download")
                                .build();

                when(fileRepository.save(Mockito.any(File.class))).thenAnswer(i -> file);
                Pageable pageable = PageRequest.of(0, 3);
                List<File> fileList = List.of(file, file, file);

                when(fileRepository.findAllByCommunity_ModuleId(community.getModuleId(), pageable))
                                .thenAnswer(i -> {
                                        Pageable argument = (Pageable) i.getArguments()[1];
                                        return new PageImpl<>(fileList, argument, fileList.size());
                                });

                Page<FileDto> result = fileService.getCommunityFiles(community.getModuleId(), 0, 3,
                                java.util.Optional.empty());

                assertNotNull(result, "Page should not be null.");
                assertEquals(3L, result.getTotalElements(), "Result should contain three elements.");
                assertEquals(1, result.getTotalPages(), "Result should contain two pages.");
                assertEquals(3, result.getContent().size(), "Result should contain three elements.");
        }

        @Test
        void deleteFileThrowsFileNotExist() {

                setupSecurity(User.builder().username("test1").password("pw").build());

                Instructor instructor = Instructor.builder().id(2L).name("TestInstructor").build();
                HashSet<File> fileSet = new HashSet<>();

                Community community = Community.builder().id(1L).name("TestCommunity").instructor(instructor)
                                .moduleId("UZH1234").uploadedFiles(fileSet).build();
                when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                                .thenAnswer(i -> Optional.of(community));

                User user1 = User.builder().username("Test1").password("anything").id(2L)
                                .subscriptionSet(Set.of(community))
                                .build();

                final MultipartFile mockFile = mock(MultipartFile.class);
                when(mockFile.getSize()).thenAnswer(i -> 10L);
                when(mockFile.getContentType()).thenAnswer(i -> "application/pdf");
                when(mockFile.isEmpty()).thenAnswer(i -> false);
                when(mockFile.getOriginalFilename()).thenAnswer(i -> "TestFile.pdf");

                doNothing().when(fileStore).uploadFile(Mockito.anyString(), Mockito.any(), Mockito.any());

                when(userRepository.findByUsername(Mockito.anyString()))
                                .thenAnswer(i -> Optional.of(user1));

                File file = File.builder().id(1L).community(community)
                                .creation(new Timestamp(System.currentTimeMillis())).creator(user1)
                                .fileName("TestFile.pdf")
                                .filePath("/communities/" + community.getModuleId() + "/files/" + 1 + "/download")
                                .build();

                when(fileRepository.save(Mockito.any(File.class))).thenAnswer(i -> file);

                ResponseStatusException thrown = assertThrows(ResponseStatusException.class, () -> {
                        fileService.deleteFile(file.getId(), community.getModuleId(), user1.getUsername());
                });

                assertEquals("file does not exist", thrown.getReason());
        }

        @Test
        void deleteFile() {

                setupSecurity(User.builder().username("test1").password("pw").build());

                Instructor instructor = Instructor.builder().id(2L).name("TestInstructor").build();
                HashSet<File> fileSet = new HashSet<>();

                Community community = Community.builder().id(1L).name("TestCommunity").instructor(instructor)
                                .moduleId("UZH1234").uploadedFiles(fileSet).build();
                when(communityRepository.findCommunityByModuleId(community.getModuleId()))
                                .thenAnswer(i -> Optional.of(community));

                User user1 = User.builder().username("Test1").password("anything").id(2L)
                                .subscriptionSet(Set.of(community))
                                .build();

                final MultipartFile mockFile = mock(MultipartFile.class);
                when(mockFile.getSize()).thenAnswer(i -> 10L);
                when(mockFile.getContentType()).thenAnswer(i -> "application/pdf");
                when(mockFile.isEmpty()).thenAnswer(i -> false);
                when(mockFile.getOriginalFilename()).thenAnswer(i -> "TestFile.pdf");

                doNothing().when(fileStore).uploadFile(Mockito.anyString(), Mockito.any(), Mockito.any());

                when(userRepository.findByUsername(Mockito.anyString()))
                                .thenAnswer(i -> Optional.of(user1));

                File file = File.builder().id(1L).community(community)
                                .creation(new Timestamp(System.currentTimeMillis())).creator(user1)
                                .fileName("TestFile.pdf")
                                .filePath("/communities/" + community.getModuleId() + "/files/" + 1 + "/download")
                                .build();

                when(fileRepository.save(Mockito.any(File.class))).thenAnswer(i -> file);
                when(fileRepository.findByIdAndCommunity_ModuleId(file.getId(), community.getModuleId()))
                                .thenAnswer(i -> Optional.of(file));

                doNothing().when(fileStore).deleteFile(Mockito.anyString());

                fileService.deleteFile(file.getId(), community.getModuleId(), user1.getUsername());

                verify(fileRepository, times(1)).delete(Mockito.any(File.class));
        }
}
