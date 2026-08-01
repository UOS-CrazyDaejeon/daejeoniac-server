package com.daejeongwang.uoscrazydaejeon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.access-key:}")
    private String accessKey;

    @Value("${aws.secret-key:}")
    private String secretKey;

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        //Access Key를 직접 설정한 경우
        boolean hasAccessKey = !accessKey.isBlank();
        boolean hasSecretKey = !secretKey.isBlank();

        if (hasAccessKey != hasSecretKey) {
            throw new IllegalStateException(
                    "AWS access key와 secret key는 둘 다 설정하거나 둘 다 비워야 합니다."
            );
        }

        if (hasAccessKey) {
            AwsBasicCredentials credentials =
                    AwsBasicCredentials.create(accessKey, secretKey);

            return StaticCredentialsProvider.create(credentials);
        }
        // 기본 자격 증명 체인 사용(EC2 IAM Role 포함)
        return DefaultCredentialsProvider.create();
    }

    @Bean
    public S3Client s3Client(AwsCredentialsProvider credentialsProvider) {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(
            AwsCredentialsProvider credentialsProvider
    ) {
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .build();
    }
}