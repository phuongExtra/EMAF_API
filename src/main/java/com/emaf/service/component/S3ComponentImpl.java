package com.emaf.service.component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.emaf.service.common.constant.AWSConstant;
import com.emaf.service.common.constant.AppConstant;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * S3ComponentImpl
 *
 * @author khale
 * @since 2022/01/11
 */
@Component
public class S3ComponentImpl implements S3Component {

    private static final String PATH_SLASH = "/";
    private final AmazonS3 amazonS3;
    private final AWSConstant awsConstant;

    public S3ComponentImpl(final AmazonS3 amazonS3,
                           final AWSConstant awsConstant) {
        this.amazonS3 = amazonS3;
        this.awsConstant = awsConstant;
    }

    /**
     * Upload file to s3
     *
     * @param directory the sub-directories is used storage upload file
     * @param mFile     the file is uploaded to s3
     * @return String - public access link of file
     * @throws IOException
     */
    @Override
    public String upload(final String directory, final MultipartFile mFile) throws IOException, URISyntaxException {
        File file = new File(mFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(mFile.getBytes());

        String endpoint = upload(directory, file);

        file.delete();
        fos.close();

        return endpoint;
    }

    /**
     * Upload file to s3
     *
     * @param directory the sub-directories is used storage upload file
     * @param file      the file is uploaded to s3
     * @return String - public access link of file
     * @throws IOException
     */
    @Override
    public String upload(final String directory, final File file) throws URISyntaxException {
        String key = new StringBuilder(directory)
                .append(AppConstant.PATH_SLASH)
                .append(file.getName())
                .toString();
        if (amazonS3.doesObjectExist(awsConstant.getAwsS3Bucket(), key)) {
            amazonS3.deleteObject(awsConstant.getAwsS3Bucket(), key);
        }
        PutObjectRequest por = new PutObjectRequest(awsConstant.getAwsS3Bucket(), key, file)
                .withCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3.putObject(por);

        return amazonS3.getUrl(awsConstant.getAwsS3Bucket(), key).toURI().toString();
    }

    /**
     * Upload file to s3
     *
     * @param directory the sub-directories is used storage upload file
     * @param url       the url of file is uploaded to s3
     * @return String - public access link of file
     * @throws URISyntaxException
     */
    @Override
    public String upload(final String directory, final String url) throws IOException, URISyntaxException {
        String extension = url.substring(url.lastIndexOf(".") + 1);

        URL fileURL = new URL(url);
        BufferedImage bufferedImage = ImageIO.read(fileURL);

        String fileName = url.substring(url.lastIndexOf(AppConstant.PATH_SLASH) + 1);
        String filePath = new StringBuilder(awsConstant.getAwsS3TempPath())
                .append(AppConstant.PATH_SLASH)
                .append(fileName)
                .toString();
        File file = new File(filePath);
        ImageIO.write(bufferedImage, extension.toLowerCase(), file);

        String endpoint = upload(directory, file);
        file.delete();

        return endpoint;
    }

    /**
     * Delete file from s3
     *
     * @param directory the sub-directories is used storage upload file
     * @param fileName  the file name
     * @return String - public access link of file
     * @throws IOException
     */
    @Override
    public void delete(final String directory, final String fileName) {
        String key = new StringBuilder(directory)
                .append(PATH_SLASH)
                .append(fileName)
                .toString();
        if (amazonS3.doesObjectExist(awsConstant.getAwsS3Bucket(), key))
            amazonS3.deleteObject(awsConstant.getAwsS3Bucket(), key);
    }

    @Override
    public String get(final String key) {
        if (amazonS3.doesObjectExist(awsConstant.getAwsS3Bucket(), key)) {
             return buildObjectURL(key);
        }
        return "";
    }

    private String buildObjectURL(final String key) {
        return new StringBuilder("https://")
                .append(awsConstant.getAwsS3Bucket())
                .append(".s3.")
                .append(awsConstant.getAwsRegion())
                .append(".amazonaws.com")
                .append(PATH_SLASH)
                .append(key)
                .toString();
    }
}
