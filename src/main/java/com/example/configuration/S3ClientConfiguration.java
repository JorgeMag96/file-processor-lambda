package com.example.configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.net.URISyntaxException;

public class S3ClientConfiguration {

    public static S3Client localS3Client(){
        try {
            return S3Client.builder()
                    // TODO: Make the IP able to be resolved to avoid hardcoding
                    .endpointOverride(new URI("http://172.18.0.2:4566")) // Should match your localstack container IP
                    .region(Region.of("us-west-2")).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
