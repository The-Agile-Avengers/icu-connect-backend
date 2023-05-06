package com.agileavengers.icuconnectbackend.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.agileavengers.icuconnectbackend.model.File;

public interface FileRepository extends JpaRepository<File, Long> {
    // /**
    // * Get list of Posts that refer to a specific Community
    // *
    // * @param communityId Id of the relevant community
    // * @return List of all referenced posts
    // */
    Page<File> findAllByCommunity_ModuleId(String moduleId, Pageable pageable);

    Optional<File> findByIdAndCommunity_ModuleId(Long id, String moduleId);

    // Boolean existsByCommunity_ModuleIdAndId(String moduleId, Long id);

    // @Transactional()
    // void deleteFileMetadataById(Long id);

    // @Transactional()
    // void deleteFileMetadataByIdAndCreator_Id(Long id, Long creatorId);

    Optional<File> findByFileName(String fileName);

    List<File> findAllByCommunity_ModuleId(String moduleId);

    Page<File> findAllByCreationBetween(Timestamp begin, Timestamp end, Pageable pageable);

}
