package com.example.demo.test.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class MediaService {
    String connectionString = "DefaultEndpointsProtocol=https;AccountName=startupone;AccountKey=nVXUNnqSIKBVpdGhAp/qD8c22WECVTdDP6FLAxjLSp9sY9lwzryL3R07IqRuL1968YQcVUGeoWrL+ASt+HI22w==;EndpointSuffix=core.windows.net";
    private final BlobServiceClient blobServiceClient=new BlobServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient();;

    public String uploadMedia(MultipartFile file) {
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("startup241850a30-bd53-4b82-822b-21f5a91c9277");

        String mediaName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        try {
            BlobClient blobClient = containerClient.getBlobClient(mediaName);
            blobClient.upload(file.getInputStream(), file.getSize(), true);

            return blobClient.getBlobUrl();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

