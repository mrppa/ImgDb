package com.mrppa.imgdb.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import com.mrppa.imgdb.meta.entities.ImageMetaAccess;
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
	void opperationSaveWhenSucessfulShouldReturnImageId() throws ImageDbException {

		when(imageMetaService.save(any(ImageMeta.class))).thenAnswer((answer) -> {
			ImageMeta imageMeta = answer.getArgument(0);
			imageMeta.setImagId("1");
			assertNotNull(imageMeta.getHashedUserKey());
			return imageMeta;
		});

		doNothing().when(imageStore).storeImage(any(), any());

		byte[] content = "test".getBytes();
		String imageId = imageDbService.addImage(new ByteArrayInputStream(content), "key1", new ImageMetaAccess());
		assertNotNull(imageId);
		verify(imageStore, Mockito.times(1)).storeImage(any(), any());
		verify(imageMetaService, Mockito.times(2)).save(any());
	}

	@Test
	void opperationSaveWhenErrorSaveMetaShouldReturnException() throws ImageDbException {

		when(imageMetaService.save(any(ImageMeta.class))).thenThrow(new RuntimeException());

		byte[] content = "test".getBytes();
		assertThrowsExactly(RuntimeException.class,
				() -> imageDbService.addImage(new ByteArrayInputStream(content), "key2", new ImageMetaAccess()));
		verify(imageStore, Mockito.times(0)).storeImage(any(), any());
	}

	@Test
	void opperationSaveWhenErrorStoreImageShouldDeleteNReturnException() throws ImageDbException {

		when(imageMetaService.save(any(ImageMeta.class))).thenAnswer((answer) -> {
			ImageMeta imageMeta = answer.getArgument(0);
			imageMeta.setImagId("3");
			assertNotNull(imageMeta.getHashedUserKey());
			return imageMeta;
		});

		doThrow(ImageDbException.class).when(imageStore).storeImage(any(), any());

		byte[] content = "test".getBytes();
		assertThrowsExactly(ImageDbException.class,
				() -> imageDbService.addImage(new ByteArrayInputStream(content), "key3", new ImageMetaAccess()));
		verify(imageStore, Mockito.times(1)).storeImage(any(), any());
		verify(imageMetaService, Mockito.times(1)).delete(eq("3"));
	}

	@Test
	void opperationReplaceImgWhenSucessfulShouldReplace() throws ImageDbException {

		ImageMeta imageMeta = new ImageMeta();
		imageMeta.setImagId("4");
		imageMeta.setHashedUserKey(passwordEncoder.encode("key4"));
		imageMeta.setStatus(ImageMetaStatus.ACTIVE);
		when(imageMetaService.get(any())).thenReturn(Optional.of(imageMeta));
		when(imageMetaService.save(any(ImageMeta.class))).thenAnswer((answer) -> answer.getArgument(0));

		doNothing().when(imageStore).storeImage(any(), any());

		byte[] content = "test".getBytes();
		imageDbService.replaceImage("4", new ByteArrayInputStream(content), "key4");
		verify(imageStore, Mockito.times(1)).storeImage(any(), any());
	}

	@Test
	void opperationReplaceImgWhenInvalidStatusShouldThrowError() throws ImageDbException {

		ImageMeta imageMeta = new ImageMeta();
		imageMeta.setImagId("6");
		imageMeta.setHashedUserKey(passwordEncoder.encode("key6"));
		imageMeta.setStatus(ImageMetaStatus.UPLOADING);
		when(imageMetaService.get(any())).thenReturn(Optional.of(imageMeta));

		byte[] content = "test".getBytes();
		assertThrowsExactly(ImageDbException.class,
				() -> imageDbService.replaceImage("6", new ByteArrayInputStream(content), "key6"));
		verify(imageStore, Mockito.times(0)).storeImage(any(), any());
	}

	@Test
	void opperationReplaceImgWhenNoImageFromMetaShouldThrowError() throws ImageDbException {

		when(imageMetaService.get(any())).thenReturn(Optional.empty());

		byte[] content = "test".getBytes();
		assertThrowsExactly(ImageFileNotFoundException.class,
				() -> imageDbService.replaceImage("7", new ByteArrayInputStream(content), "key7"));
		verify(imageStore, Mockito.times(0)).storeImage(any(), any());
	}

	@Test
	void opperationRetriveImageWhenSucessful() throws ImageDbException {

		ImageMeta imageMeta = new ImageMeta();
		imageMeta.setImagId("8");
		imageMeta.setHashedUserKey(passwordEncoder.encode("key8"));
		imageMeta.setStatus(ImageMetaStatus.ACTIVE);
		when(imageMetaService.get(any())).thenReturn(Optional.of(imageMeta));

		imageDbService.retriveImage("8", new ByteArrayOutputStream(), "key8");
	}

	@Test
	void opperationRetriveImageWhenNoImageShouldThrowError() throws ImageDbException {

		when(imageMetaService.get(any())).thenReturn(Optional.empty());

		assertThrowsExactly(ImageFileNotFoundException.class,
				() -> imageDbService.retriveImage("7", new ByteArrayOutputStream(), "key8"));
	}

	@Test
	void opperationRetriveImageWhenNoImageInStoreShouldDeleteMetaNThrowError() throws ImageDbException {

		ImageMeta imageMeta = new ImageMeta();
		imageMeta.setImagId("9");
		imageMeta.setHashedUserKey(passwordEncoder.encode("key9"));
		imageMeta.setStatus(ImageMetaStatus.ACTIVE);
		when(imageMetaService.get(any())).thenReturn(Optional.of(imageMeta));

		doThrow(ImageFileNotFoundException.class).when(imageStore).retriveImage(any(), any());

		assertThrowsExactly(ImageFileNotFoundException.class,
				() -> imageDbService.retriveImage("9", new ByteArrayOutputStream(), "key9"));

		verify(imageMetaService, Mockito.times(1)).delete(eq("9"));
	}

	@Test
	void opperationDeleteImageWhenSucessful() throws ImageDbException {

		ImageMeta imageMeta = new ImageMeta();
		imageMeta.setImagId("10");
		imageMeta.setHashedUserKey(passwordEncoder.encode("key10"));
		imageMeta.setStatus(ImageMetaStatus.ACTIVE);
		when(imageMetaService.get(any())).thenReturn(Optional.of(imageMeta));
		doNothing().when(imageMetaService).delete(any());

		imageDbService.deleteImage("10", "key10");
	}

	@Test
	void opperationDeleteImageWhenNoImageShouldThrowError() throws ImageDbException {

		when(imageMetaService.get(any())).thenReturn(Optional.empty());

		assertThrowsExactly(ImageFileNotFoundException.class, () -> imageDbService.deleteImage("11", "key11"));
	}

	@Test
	void opperationDeleteImageWhenNoImageInStoreShouldSuccess() throws ImageDbException {

		ImageMeta imageMeta = new ImageMeta();
		imageMeta.setImagId("12");
		imageMeta.setHashedUserKey(passwordEncoder.encode("key12"));
		imageMeta.setStatus(ImageMetaStatus.ACTIVE);
		when(imageMetaService.get(any())).thenReturn(Optional.of(imageMeta));

		doThrow(ImageFileNotFoundException.class).when(imageStore).deleteImage(any());

		imageDbService.deleteImage("12", "key112");

		verify(imageMetaService, Mockito.times(1)).delete(eq("12"));
	}
}
