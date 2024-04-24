package com.example.demo.test.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class MediaService {
    private final static String connectionString = "DefaultEndpointsProtocol=https;AccountName=startupone;AccountKey=nVXUNnqSIKBVpdGhAp/qD8c22WECVTdDP6FLAxjLSp9sY9lwzryL3R07IqRuL1968YQcVUGeoWrL+ASt+HI22w==;EndpointSuffix=core.windows.net";

    public String uploadImageToAzureAndGetUrl(BufferedImage image, String fileName) throws IOException {
        // Convert BufferedImage to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        byte[] imageData = outputStream.toByteArray();

        // Initialize BlobServiceClient using the connection string
        BlobContainerClient containerClient = new BlobServiceClientBuilder().connectionString(MediaService.connectionString)
                .buildClient()
                .getBlobContainerClient("startup1");

        // Upload image to Azure Blob Storage
        BlobClient blobClient = containerClient.getBlobClient(fileName);
        blobClient.upload(new ByteArrayInputStream(imageData), imageData.length, true);

        // Generate URL for the uploaded image
        return blobClient.getBlobUrl();
    }
}

