package com.example.demo.management.dto.request;

import com.example.demo.enums.AttachmentOwnerType;
import com.example.demo.enums.AttachmentType;
import lombok.Data;

import java.util.UUID;

@Data
public class UploadAttachmentRequest {
    private String title;
    private AttachmentType type;
    private AttachmentOwnerType ownerType;
    private UUID ownerId;
}
