package dev.seeds.fileuploadanddownload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class FileUploadAndDownloadApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileUploadAndDownloadApplication.class, args);
    }

}
