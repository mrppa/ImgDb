package com.mrppa.imgdb.meta.services.impl;

import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.meta.repositories.ImageMetaRepository;
import com.mrppa.imgdb.meta.repositories.impl.ImageMetaRepositoryJDBCImpl;
import com.mrppa.imgdb.meta.services.ImageMetaService;
import com.mrppa.imgdb.model.AccessMode;
import com.mrppa.imgdb.model.ImageMetaStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
class ImageMetaServiceTest {

    @Autowired
    ImageMetaService imageMetaService;

    @Test
    void testSaveGetDeleteHappyPath() {
        ImageMeta imageMeta = ImageMeta.builder().status(ImageMetaStatus.CREATED).build();
        imageMeta = imageMetaService.insert(imageMeta);

        Optional<ImageMeta> optFetchedImageMeta1 = imageMetaService.get(imageMeta.getImageId());
        assertTrue(optFetchedImageMeta1.isPresent());

        imageMetaService.delete(imageMeta.getImageId());

        Optional<ImageMeta> optFetchedImageMeta2 = imageMetaService.get(imageMeta.getImageId());
        assertTrue(optFetchedImageMeta2.isEmpty());

    }

    @Test
    void whenSavingAutoGenFieldsShouldPopulate() {
        ImageMeta imageMeta = ImageMeta.builder().status(ImageMetaStatus.CREATED).build();
        imageMeta = imageMetaService.insert(imageMeta);
        assertNotNull(imageMeta.getImageId());
        assertNotNull(imageMeta.getAddedDate());
        assertNotNull(imageMeta.getUpdatedDate());
        assertNotNull(imageMeta.getAccess());
        assertEquals(AccessMode.PUBLIC, imageMeta.getAccess().getReadAccess());
        assertEquals(AccessMode.PUBLIC, imageMeta.getAccess().getWriteAccess());
    }

    @Test
    void whenUpdatingResultShouldPersist() {
        ImageMeta imageMeta = ImageMeta.builder().status(ImageMetaStatus.CREATED).build();
        imageMeta = imageMetaService.insert(imageMeta);

        imageMeta.setDescription("UPDATED");

        imageMetaService.update(imageMeta);

        Optional<ImageMeta> optFetchedImageMeta1 = imageMetaService.get(imageMeta.getImageId());
        assertTrue(optFetchedImageMeta1.isPresent());
        assertEquals("UPDATED", optFetchedImageMeta1.get().getDescription());

    }

    @TestConfiguration
    static class ImageMetaServiceImplTestConfig {
        @Bean
        public ImageMetaService imageMetaService() {
            return new ImageMetaServiceImpl();
        }
        @Bean
        public ImageMetaRepository imageMetaRepository() {
            return new ImageMetaRepositoryJDBCImpl();
        }
    }

}
