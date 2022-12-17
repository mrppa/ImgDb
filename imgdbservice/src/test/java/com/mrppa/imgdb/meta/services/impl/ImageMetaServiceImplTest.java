package com.mrppa.imgdb.meta.services.impl;

import com.mrppa.imgdb.ImgDbApplicationTests;
import com.mrppa.imgdb.meta.entities.AccessMode;
import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.meta.services.ImageMetaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ImgDbApplicationTests.class)
class ImageMetaServiceImplTest {

    @Autowired
    ImageMetaService imageMetaService;

    @Test
    void testSaveGetDeleteHappyPath() {
        ImageMeta imageMeta = ImageMeta.builder().build();
        imageMeta = imageMetaService.save(imageMeta);

        Optional<ImageMeta> optFetchedImageMeta1 = imageMetaService.get(imageMeta.getImageId());
        assertTrue(optFetchedImageMeta1.isPresent());

        imageMetaService.delete(imageMeta.getImageId());

        Optional<ImageMeta> optFetchedImageMeta2 = imageMetaService.get(imageMeta.getImageId());
        assertTrue(optFetchedImageMeta2.isEmpty());

    }

    @Test
    void whenSavingAutoGenFieldsShouldPopulate() {
        ImageMeta imageMeta = ImageMeta.builder().build();
        imageMeta = imageMetaService.save(imageMeta);
        assertNotNull(imageMeta.getImageId());
        assertNotNull(imageMeta.getAddedDate());
        assertNotNull(imageMeta.getUpdatedDate());
        assertNotNull(imageMeta.getAccess());
        assertEquals(AccessMode.PUBLIC, imageMeta.getAccess().getReadAccess());
        assertEquals(AccessMode.PUBLIC, imageMeta.getAccess().getWriteAccess());
    }

    @Test
    void whenDeletingNotAvailableItemShouldThrowRuntimeError() {
        assertThrowsExactly(EmptyResultDataAccessException.class, () -> imageMetaService.delete("THIS ID NOT AVAILABLE"));
    }

    @Test
    void whenUpdatingResultShouldPersist() {
        ImageMeta imageMeta = ImageMeta.builder().build();
        imageMeta = imageMetaService.save(imageMeta);

        imageMeta.setDescription("UPDATED");

        imageMeta = imageMetaService.save(imageMeta);

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
    }

}
