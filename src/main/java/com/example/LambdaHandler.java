package com.example;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.Context;

import com.example.configuration.S3ClientConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class LambdaHandler implements RequestHandler<S3Event, String> {

    static final Logger LOGGER = LogManager.getLogger(LambdaHandler.class);
    static final S3Client s3Client = S3ClientConfiguration.localS3Client();
    static final String OUTPUT_BUCKET = "output-bucket";
    static final Function<String, String> TO_PROCESSED_FILE_NAME = f -> String.format(
            "processed_%s", f);

    @Override
    public String handleRequest(S3Event input, Context context) {

        String eventName = input.getRecords().get(0).getEventName();
        LOGGER.info("Received event: " + eventName);

        String eventBucket = input.getRecords().get(0).getS3().getBucket().getName();
        String objectKey = input.getRecords().get(0).getS3().getObject().getKey();
        LOGGER.info(String.format("Bucket: %s\tObject key: %s", eventBucket, objectKey));

        try(final InputStream s3Object = getObject(eventBucket, objectKey);
            final InputStreamReader inputStreamReader = new InputStreamReader(s3Object, StandardCharsets.UTF_8);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader, 1024);
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){

            bufferedReader.lines().forEach( line -> {
                LOGGER.info(String.format("Processing input file line content: %s", line));
                // TODO: do something with the line
                try {
                    outputStream.write(line.getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            putObject(outputStream, OUTPUT_BUCKET, TO_PROCESSED_FILE_NAME.apply(objectKey));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info(String.format("Finished processing file: %s", objectKey));
        return "ok";
    }

    private InputStream getObject(String bucket, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3Client.getObject(getObjectRequest);
    }

    private void putObject(ByteArrayOutputStream outputStream, String bucket, String key) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Length", String.valueOf(outputStream.size()));
        metadata.put("Content-Type", "text/plain");

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .metadata(metadata)
                .build();
        LOGGER.info("Putting object: " + key + " into bucket: " + bucket);
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(outputStream.toByteArray()));
    }
}