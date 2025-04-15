package com.example.demo.ncp.storage;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.demo.util.ImageUrlParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service("ncpObjectStorageComponent")
public class NcpObjectStorageService implements ObjectStorageService{
    AmazonS3 s3;
    ImageUrlParser imageUrlParser;
    public NcpObjectStorageService(NaverConfig naverConfig){
        this.imageUrlParser = new ImageUrlParser(naverConfig.getEndPoint(), naverConfig.getBucketName());
        System.out.println("NcpObjectStorageService 생성");
        System.out.println("naverConfig.getEndPoint()="+naverConfig.getEndPoint());
        System.out.println("naverConfig.getRegionName()="+naverConfig.getRegionName());
        System.out.println("naverConfig.getAccessKey()="+naverConfig.getAccessKey());
        System.out.println("naverConfig.getSecretKey()="+naverConfig.getSecretKey());
        System.out.println("naverConfig.getBucketName()="+naverConfig.getBucketName());

        s3 = AmazonS3ClientBuilder.standard().withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                        naverConfig.getEndPoint(), naverConfig.getRegionName()))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                        naverConfig.getAccessKey(), naverConfig.getSecretKey()))).build();
        s3.listBuckets().stream().map(r->r.getName()).forEach(System.out::println);
    }
    @Override
    public String uploadFile(String bucketName, String directoryPath, MultipartFile file) {
        System.out.println("uploadFile="+file.getOriginalFilename());
        if (file.isEmpty()) {
            return null;
        }
        try (InputStream fileIn = file.getInputStream()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_");
            String filename = sdf.format(new Date())+UUID.randomUUID().toString().substring(0,10)+"."+file.getOriginalFilename().split("\\.")[1];

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());

            PutObjectRequest objectRequest = new PutObjectRequest(
                    bucketName,
                    directoryPath +"/"+ filename,
                    fileIn,
                    objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead);
            System.out.println("access control list : " + objectRequest.getAccessControlList());
            try{
                s3.putObject(objectRequest);
                System.out.println("s3.putObject success");
            }catch (AmazonS3Exception e){
                e.printStackTrace();
            }catch (SdkClientException e) {
                e.printStackTrace();
            }

            return imageUrlParser.parse(directoryPath, filename);
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 오류", e);
        }
    }
    public String uploadImage(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        try (InputStream fileIn = file.getInputStream()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_");
            String filename = "profile_" + sdf.format(new Date()) + UUID.randomUUID().toString().substring(0,10)
                    + "." + file.getOriginalFilename().split("\\.")[1];

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());

            PutObjectRequest objectRequest = new PutObjectRequest(
                    "bitcamp-bucket-122",
                    "wellfit/" + filename,
                    fileIn,
                    objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead);

            s3.putObject(objectRequest);

            return "https://kr.object.ncloudstorage.com/bitcamp-bucket-122/wellfit/" + filename;

        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 오류", e);
        }
    }
    @Override
    public void deleteFile(String bucketName, String directoryPath, String fileName) {
        String path = directoryPath+"/"+fileName;
        boolean isExist = s3.doesObjectExist(bucketName, path);
        if(isExist){
            s3.deleteObject(bucketName, path);
        }
    }
}