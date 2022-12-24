package com.mrppa.imgdb.img.service.impl;

import com.mrppa.imgdb.img.service.ImageStore;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-local.properties")
@Tag("integrationTest")
class S3ImageStoreIntegrationTest extends ImageStoreTest {
    private final Logger LOGGER = LoggerFactory.getLogger(S3ImageStoreIntegrationTest.class);

    @Autowired
    ImageStore imageStore;

    @Override
    public ImageStore initImageStore() {
        LOGGER.warn("This is an integration test. Make sure there's a application-local.properties property file with aws configs!");
        return imageStore;
    }

    @TestConfiguration
    static class S3ImageStoreIntegrationTestConfig {
        @Bean
        @Primary
        public ImageStore s3ImageStore() {
            return new S3ImageStore();
        }
    }

}



