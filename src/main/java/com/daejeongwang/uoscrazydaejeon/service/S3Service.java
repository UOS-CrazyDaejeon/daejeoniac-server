package com.daejeongwang.uoscrazydaejeon.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    public String createUploadUrl(String objectKey, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(5))
                        .putObjectRequest(putObjectRequest)
                        .build();

        PresignedPutObjectRequest presignedRequest =
                s3Presigner.presignPutObject(presignRequest);

        return presignedRequest.url().toString();
    }

    public void uploadImage(String objectKey, byte[] imageBytes, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));
    }

    public String createPublicUrl(String objectKey) {
        return "https://" + bucketName
                + ".s3."
                + region
                + ".amazonaws.com/"
                + objectKey;
    }

    public boolean existsObject(String objectKey) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3Client.headObject(request);
            return true;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }

            throw e;
        }
    }

    public void deleteObject(String objectKey) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        s3Client.deleteObject(request);
    }
}
