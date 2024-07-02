package lib.minio.confirguration;

import org.springframework.context.annotation.Bean;

import io.minio.MinioClient;
import lib.minio.confirguration.property.MinioProp;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    @Bean
    public MinioClient minioClient(MinioProp props){
        return MinioClient.builder()
            .endpoint(props.getUrl())
            .credentials(props.getUsername(), props.getPassword())
            .build();
    }
}
