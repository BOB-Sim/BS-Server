package com.a206.mychelin.util;

import com.a206.mychelin.web.dto.Response;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

@Log4j2
@Service
@NoArgsConstructor
public class ImageServer {
    private AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public void setS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.region)
                .build();
    }

    public String upload(MultipartFile file) throws IOException {
        Date time = new Date();
        String strFileName = file.getOriginalFilename();
        int pos = strFileName.lastIndexOf(".");
        String ext = strFileName.substring(pos + 1);
        String fileName = time.getTime() + randomToken() + "." + ext;
        BufferedImage image = ImageIO.read(file.getInputStream());
        BufferedImage newImage = resizeImage(image);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(newImage, ext, os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());

        s3Client.putObject(new PutObjectRequest(bucket, fileName, is, null)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return s3Client.getUrl(bucket, fileName).toString();
    }

    private static BufferedImage resizeImage(BufferedImage originalImage) {
        double ratio = originalImage.getWidth() / (double) 1200;
        int tWidth = (int) (originalImage.getWidth() / ratio);
        int tHeight = (int) (originalImage.getHeight() / ratio);

        BufferedImage newImage = new BufferedImage(tWidth, tHeight, originalImage.getType());
        Graphics2D graphic = newImage.createGraphics();
        Image image = originalImage.getScaledInstance(tWidth, tHeight, Image.SCALE_SMOOTH);
        graphic.drawImage(image, 0, 0, tWidth, tHeight, null);
        graphic.dispose();

        return newImage;
    }


    private String randomToken() {
        StringBuffer token = new StringBuffer();
        Random rnd = new Random();
        for (int i = 0; i < 10; i++) {
            int rIndex = rnd.nextInt(3);
            switch (rIndex) {
                case 0:
                    // a-z
                    token.append((char) ((rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    // A-Z
                    token.append((char) ((rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    // 0-9
                    token.append((rnd.nextInt(10)));
                    break;
            }
        }
        return token.toString();
    }

    public ResponseEntity<Response> registerImageIntoServer(MultipartFile file) throws IOException {
        String imagePath = upload(file);
        HashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put("image", imagePath);
        return Response.newResult(HttpStatus.OK, "이미지를 서버에 성공적으로 저장했습니다.", hashMap);
    }
}