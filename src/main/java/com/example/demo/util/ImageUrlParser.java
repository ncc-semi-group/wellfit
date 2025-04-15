package com.example.demo.util;

public class ImageUrlParser {
    private String endpoint;
    private String bucketName;
    public ImageUrlParser(String endpoint, String bucketName) {
        this.endpoint = endpoint;
        this.bucketName = bucketName;
    }
    public String parse(String directoryPath ,String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        return endpoint + "/" + bucketName + "/" + directoryPath + "/" + imageUrl;
    }
}
