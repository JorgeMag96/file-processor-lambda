package com.example;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.Context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LambdaHandler implements RequestHandler<S3Event, String> {

    static final Logger LOGGER = LogManager.getLogger(LambdaHandler.class);

    @Override
    public String handleRequest(S3Event input, Context context) {
        String eventName = input.getRecords().get(0).getEventName();

        LOGGER.info("Received event: " + eventName);

        return "ok";
    }
}