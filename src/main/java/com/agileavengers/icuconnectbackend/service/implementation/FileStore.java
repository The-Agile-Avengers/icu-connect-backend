package com.agileavengers.icuconnectbackend.service.implementation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

@Service
public class FileStore {

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 awsS3Client;

    public void uploadFile(String fileName, Optional<Map<String, String>> optionalMetadata, InputStream iStream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();

        optionalMetadata.ifPresent(map -> {
            if (!map.isEmpty()) {
                map.forEach(objectMetadata::addUserMetadata);
            }
        });

        try {
//            awsS3Client.putObject(bucketName, fileName, iStream, objectMetadata);
            // Create a temporary file
            File tempFile = File.createTempFile("temp", ".tmp");

            // Delete the temporary file when the program exits
            tempFile.deleteOnExit();

            // Write the contents of the input stream to the temporary file
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = iStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Close the streams
            iStream.close();
            outputStream.close();

            awsS3Client.putObject(bucketName, fileName, tempFile);
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to upload, URI: " + bucketName, e);
        }
    }

    public byte[] download(String fileName) {
        try {
            S3Object object = awsS3Client.getObject(bucketName, fileName);
            S3ObjectInputStream objectContent = object.getObjectContent();
            return IOUtils.toByteArray(objectContent);
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to download file", e);
        }
    }
    
    public void deleteFile(final String fileName) {
        final DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, fileName);
        awsS3Client.deleteObject(deleteObjectRequest);
    }
}
