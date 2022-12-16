package com.mrppa.imgdb.meta.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.EmptyResultDataAccessException;

import com.mrppa.imgdb.ImgdbApplicationTests;
import com.mrppa.imgdb.exception.ImageMetaException;
import com.mrppa.imgdb.meta.entities.AccessMode;
import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.meta.services.ImageMetaService;

@SpringBootTest(classes = ImgdbApplicationTests.class)
class ImageMetaServiceImplTest {

	@Autowired
	ImageMetaService imageMetaService;

	@Test
	void testSaveGetDeleteHappyPath() throws ImageMetaException {
		ImageMeta imageMeta = ImageMeta.builder().build();
		imageMeta = imageMetaService.save(imageMeta);

		Optional<ImageMeta> optFetchedImageMeta1 = imageMetaService.get(imageMeta.getImagId());
		assertTrue(optFetchedImageMeta1.isPresent());

		imageMetaService.delete(imageMeta.getImagId());

		Optional<ImageMeta> optFetchedImageMeta2 = imageMetaService.get(imageMeta.getImagId());
		assertTrue(optFetchedImageMeta2.isEmpty());

	}

	@Test
	void whenSavingAutoGenFieldsShouldPopulate() throws ImageMetaException {
		ImageMeta imageMeta = ImageMeta.builder().build();
		imageMeta = imageMetaService.save(imageMeta);
		assertNotNull(imageMeta.getImagId());
		assertNotNull(imageMeta.getAddedDate());
		assertNotNull(imageMeta.getUpdatedDate());
		assertNotNull(imageMeta.getAccess());
		assertEquals(AccessMode.PUBLIC, imageMeta.getAccess().getReadAccess());
		assertEquals(AccessMode.PUBLIC, imageMeta.getAccess().getWriteAccess());
	}

	@Test
	void whenDeletingNotAvailableItemShouldThrowRuntimeError() {
		assertThrowsExactly(EmptyResultDataAccessException.class, () -> {
			imageMetaService.delete("THIS ID NOT AVAILABLE");
		});
	}

	@Test
	void whenUpdatingResultShouldPersist() throws ImageMetaException {
		ImageMeta imageMeta = ImageMeta.builder().build();
		imageMeta = imageMetaService.save(imageMeta);

		imageMeta.setDescription("UPDATED");

		imageMeta = imageMetaService.save(imageMeta);

		Optional<ImageMeta> optFetchedImageMeta1 = imageMetaService.get(imageMeta.getImagId());
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
