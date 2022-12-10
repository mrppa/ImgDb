package com.mrppa.imgdb.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import com.mrppa.imgdb.exception.ImageDbException;
import com.mrppa.imgdb.exception.ImageFileNotFoundException;
import com.mrppa.imgdb.img.service.ImageStore;
import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.meta.entities.ImageMetaStatus;
import com.mrppa.imgdb.meta.services.ImageMetaService;

@ExtendWith(MockitoExtension.class)
public class ImageDbServiceTest {

	@Mock
	ImageMetaService imageMetaService;

	@Mock
	ImageStore imageStore;

	@Spy
	PasswordEncoder passwordEncoder = new Pbkdf2PasswordEncoder();

	@Mock
	AccessControlService accessControlService;

	@InjectMocks
	ImageDbService imageDbService;

	@Test
	void saveWhenSucessfulShouldReturnImageMeta() throws ImageDbException {

		when(imageMetaService.save(any())).thenReturn(ImageMeta.builder().imagId("TEST0001").build());
		doNothing().when(imageStore).storeImage(any(), any());

		ImageMeta imageMeta = imageDbService.addImage(new ByteArrayInputStream(sampleObj), ImageMeta.builder().build(),
				"mykey");
		assertNotNull(imageMeta);
		assertEquals("TEST0001", imageMeta.getImagId());
		assertEquals(ImageMetaStatus.ACTIVE, imageMeta.getStatus());

		verify(imageStore, Mockito.times(1)).storeImage(any(), any());
		verify(imageMetaService, Mockito.times(2)).save(any());
	}

	@Test
	void validDeleteShouldPromptSuccess() throws ImageDbException {

		when(imageMetaService.get("TEST0001")).thenReturn(Optional.of(ImageMeta.builder().imagId("TEST0001").build()));
		doNothing().when(imageMetaService).delete(any());

		doNothing().when(imageStore).deleteImage(any());
		doNothing().when(accessControlService).validateRowLevelAccess(any(), any(), any());

		imageDbService.deleteImage("TEST0001", "mykey");

		verify(imageStore, Mockito.times(1)).deleteImage(any());
		verify(imageMetaService, Mockito.times(1)).delete(any());
	}

	@Test
	void deleteWithInvalidImageIdShouldThrowError() throws ImageDbException {

		when(imageMetaService.get("TEST0001")).thenReturn(Optional.empty());

		assertThrowsExactly(ImageFileNotFoundException.class, () -> imageDbService.deleteImage("TEST0001", "mykey"));

	}

	@Test
	void validRetrieveImageShouldReturnImageData() throws ImageDbException {

		when(imageMetaService.get("TEST0001"))
				.thenReturn(Optional.of(ImageMeta.builder().imagId("TEST0001").status(ImageMetaStatus.ACTIVE).build()));

		doNothing().when(imageStore).retriveImage(any(), any());
		doNothing().when(accessControlService).validateRowLevelAccess(any(), any(), any());

		imageDbService.retrieveImage("TEST0001", null, "mykey");

		verify(imageStore, Mockito.times(1)).retriveImage(any(), any());
	}

	@Test
	void validRetrieveImageMetaShouldReturnImageMeta() throws ImageDbException {

		when(imageMetaService.get("TEST0001"))
				.thenReturn(Optional.of(ImageMeta.builder().imagId("TEST0001").status(ImageMetaStatus.ACTIVE).build()));

		doNothing().when(accessControlService).validateRowLevelAccess(any(), any(), any());

		ImageMeta imageMeta = imageDbService.retrieveImageMetaData("TEST0001", "mykey");
		assertNotNull(imageMeta);
		assertEquals("TEST0001", imageMeta.getImagId());

	}

	@Test
	void validImageMetaShouldUpdateSucessfully() throws ImageDbException {

		when(imageMetaService.get("TEST0001"))
				.thenReturn(Optional.of(ImageMeta.builder().imagId("TEST0001").status(ImageMetaStatus.ACTIVE).build()));
		when(imageMetaService.save(any())).thenReturn(ImageMeta.builder().imagId("TEST0001").build());

		ImageMeta imageMetaUpdated = imageDbService.updateImageMetaData(ImageMeta.builder().imagId("TEST0001").build(),
				"mykey");

		assertNotNull(imageMetaUpdated);
		assertEquals("TEST0001", imageMetaUpdated.getImagId());

		verify(imageMetaService, Mockito.times(1)).save(any());
	}

	private static final byte[] sampleObj = "test".getBytes();
}
