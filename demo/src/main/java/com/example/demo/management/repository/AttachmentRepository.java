package com.example.demo.management.repository;

import com.example.demo.enums.AttachmentOwnerType;
import com.example.demo.management.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    @Query(value = "SELECT a FROM Attachment a WHERE a.ownerType = :ownerType AND a.ownerId = :ownerId")
    List<Attachment> findAllByOwnerTypeAndOwnerId(
            @Param("ownerType") AttachmentOwnerType ownerType,
            @Param("ownerId") UUID ownerId
    );

}
