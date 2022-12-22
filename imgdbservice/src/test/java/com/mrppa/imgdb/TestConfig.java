package com.mrppa.imgdb;

import com.mrppa.imgdb.img.service.ImageStore;
import com.mrppa.imgdb.img.service.impl.LocalFileImageStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Configuration
@Profile("test")
public class TestConfig extends Config{
    @Bean
    public ImageStore imageStore() {
        File tempDir;
        try {
            tempDir = Files.createTempDirectory("tempImageStore").toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String userDirectory = tempDir.getAbsolutePath();
        return new LocalFileImageStore(userDirectory);
    }
}
