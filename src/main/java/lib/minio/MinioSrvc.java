package lib.minio;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import lib.i18n.utility.MessageUtil;
import lib.minio.confirguration.property.MinioProp;
import lib.minio.exception.MinioServiceDownloadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MinioSrvc {
    private static final Long DEFAULT_EXPIRY = TimeUnit.HOURS.toSeconds(10);

    private final MinioClient minio;
    private final MinioProp prop;

    private final MessageUtil message;

    private static String bMsg(String bucket) {
        return "bucket " + bucket;
    }

    private static String bfMsg(String bucket, String filename) {
        return bMsg(bucket) + " of file " + filename;
    }

    public String getLink(String filename, Long expiry){
        try {
            return minio.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(prop.getBucket())
                    .object(filename)
                    .expiry(Math.toIntExact(expiry), TimeUnit.SECONDS)
                    .build());
          } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
              | InvalidResponseException | NoSuchAlgorithmException | XmlParserException | ServerException
              | IllegalArgumentException | IOException e) {
            log.error(message.get(prop.getGetErrorMessage(), bfMsg(prop.getBucket(), filename)) + ": " + e.getLocalizedMessage(), e);
            throw new MinioServiceDownloadException(
                message.get(prop.getGetErrorMessage(), bfMsg(prop.getBucket(), filename)), e);
          }
    }

    public String getPublicLink(String filename) {
        return this.getLink(filename, DEFAULT_EXPIRY);
    }

    public String uploadFileToMinio(Long customerId, String customerName, MultipartFile file) throws IOException{
        if(file == null){
            return null;
        }
        String sanitizedCustomerName = sanitizeForFilename(customerName);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String generatedFileName = String.format("%d_%s_%s%s", customerId,         sanitizedCustomerName, timestamp, fileExtension);

        try (InputStream InputStream = file.getInputStream()){
            minio.putObject(
                PutObjectArgs.builder()
                .bucket(prop.getBucket())
                .object(generatedFileName)
                .stream(InputStream, file.getSize(), -1)
                .contentType(file.getContentType())
                .build());
        } catch (Exception e) {
            log.error("Failed to upload {} to MinIO: {}", e.getMessage());
            throw new IOException("Failed to upload photo to MinIO", e);
        }
        log.info("{} uploaded to MinIO with filename: {}", generatedFileName);
        return generatedFileName;
    }

    private String sanitizeForFilename(String input) {
        return input.replaceAll("[^a-zA-Z0-9]", "_");
      }

    private String getFileExtension(String filename) {
    int dotIndex = filename.lastIndexOf('.');
    return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }
}
