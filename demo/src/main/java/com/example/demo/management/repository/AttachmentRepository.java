package com.example.demo.management.repository;

import com.example.demo.enums.AttachmentOwnerType;
import com.example.demo.management.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {

    List<Attachment> findAllByOwnerTypeAndOwnerId(
            AttachmentOwnerType ownerType,
            UUID ownerId
    );
}
