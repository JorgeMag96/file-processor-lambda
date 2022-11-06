package com.example.configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.net.URISyntaxException;

public class S3ClientConfiguration {

    public static S3Client localS3Client(){
        try {
            return S3Client.builder()
                    .endpointOverride(new URI(
                            String.format("%s%s%s",
                                    "http://", System.getenv("LOCALSTACK_HOSTNAME"), ":4566")))
                    .region(Region.of("us-west-2")).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
