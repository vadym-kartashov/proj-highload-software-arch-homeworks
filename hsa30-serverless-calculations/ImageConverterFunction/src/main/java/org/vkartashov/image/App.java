package org.vkartashov.image;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<S3Event, String> {

    public static final Logger LOG = LogManager.getLogger();

    public static final String[] FORMATS = new String[]{"bmp", "gif", "jpg"};
    public static final String DESTINATION_BUCKET_ENV = "DESTINATION_BUCKETNAME";

    @Override
    public String handleRequest(S3Event event, Context context) {
        String destinationBucket = System.getenv(DESTINATION_BUCKET_ENV);
        if (destinationBucket == null || destinationBucket.isEmpty()) {
            throw new IllegalArgumentException(DESTINATION_BUCKET_ENV + " is required");
        }
        Map<String, String> result = new LinkedHashMap<>();
        for (S3EventNotification.S3EventNotificationRecord record : event.getRecords()) {
            String region = record.getAwsRegion();
            String sourceBucket = record.getS3().getBucket().getName();
            if (Objects.equals(destinationBucket, sourceBucket)) {
                throw new IllegalStateException("Recursion detected.");
            }
            String key = record.getS3().getObject().getKey();
            LOG.info("Start processing " + region + " " + sourceBucket + " " + key);
            AmazonS3 s3Api = AmazonS3ClientBuilder.standard()
                    .withRegion(region)
                    .withCredentials(new DefaultAWSCredentialsProviderChain())
                    .build();
            try {
                // Download the image from S3
                InputStream objectData = s3Api.getObject(new GetObjectRequest(sourceBucket, key)).getObjectContent();
                BufferedImage img = ImageIO.read(objectData);
                LOG.info("Image {}x{}px", img.getWidth(), img.getHeight());
                // Convert and save each format
                for (String format : FORMATS) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(img, format, os);
                    InputStream is = new ByteArrayInputStream(os.toByteArray());
                    String outputKey = key
                            .replace(".jpeg", "." + format.toLowerCase());
                    LOG.info("Saving to {}", outputKey);
                    s3Api.putObject(destinationBucket, outputKey, is, null);
                }
                result.put(key, String.format("Processing %s from %s#%s: %s", key, sourceBucket, region, "OK"));
            } catch (Throwable e) {
                LOG.error("#" + e.getMessage());
                result.put(key, String.format("Error processing %s from %s#%s: %s", key, sourceBucket, region, e.getMessage()));
            }
        }
        return result.toString();
    }


}
