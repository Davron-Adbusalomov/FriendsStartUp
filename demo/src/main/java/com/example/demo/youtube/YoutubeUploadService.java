package com.example.demo.youtube;

import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class YoutubeUploadService {

    private final YouTubeAuthorizationService authorizationService;

    public String upload(
            MultipartFile file,
            String title,
            String description,
            String privacy
    ) throws Exception {

        YouTube youtube = authorizationService.getYoutubeClient();

        Video video = new Video();

        VideoSnippet snippet = new VideoSnippet();
        snippet.setTitle(title);
        snippet.setDescription(description);

        VideoStatus status = new VideoStatus();
        status.setPrivacyStatus(privacy);

        video.setSnippet(snippet);
        video.setStatus(status);


        InputStreamContent mediaContent =
                new InputStreamContent(
                        file.getContentType(),
                        file.getInputStream()
                );

        mediaContent.setLength(file.getSize());


        YouTube.Videos.Insert request =
                youtube.videos()
                        .insert(
                                List.of("snippet", "status"),
                                video,
                                mediaContent
                        );


        // RESUMABLE SETTINGS
        MediaHttpUploader uploader = request.getMediaHttpUploader();

        uploader.setDirectUploadEnabled(false); // MUHIM

        // chunk size (10 MB)
        uploader.setChunkSize(
                10 * 1024 * 1024
        );


        uploader.setProgressListener(
                progress -> {

                    switch (progress.getUploadState()) {

                        case INITIATION_STARTED:
                            System.out.println("Upload initiated");
                            break;

                        case INITIATION_COMPLETE:
                            System.out.println("Uploading...");
                            break;

                        case MEDIA_IN_PROGRESS:
                            double percent =
                                    progress.getProgress() * 100;

                            System.out.println(
                                    "Progress: " + percent + "%"
                            );
                            break;


                        case MEDIA_COMPLETE:
                            System.out.println("Upload completed");
                            break;
                    }
                }
        );


        Video uploaded = request.execute();


        return "https://youtu.be/" + uploaded.getId();
    }
}