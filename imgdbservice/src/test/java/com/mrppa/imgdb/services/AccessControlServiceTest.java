package com.mrppa.imgdb.services;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.security.SecureRandom;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mrppa.imgdb.exception.ImageDBAccessDeniedException;
import com.mrppa.imgdb.meta.entities.AccessMode;
import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.meta.entities.ImageMetaAccess;
import com.mrppa.imgdb.model.Operation;

@ExtendWith(MockitoExtension.class)
class AccessControlServiceTest {

	@Spy
	PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10, new SecureRandom());

	@InjectMocks
	AccessControlService accessControlService;

	@Test
	void whenPublicAccessShouldAllowRegardlessOfUserKey() throws ImageDBAccessDeniedException {
		ImageMetaAccess imageMetaAccess = ImageMetaAccess.builder().readAccess(AccessMode.PUBLIC)
				.writeAccess(AccessMode.PUBLIC).build();

		ImageMeta imageMeta = ImageMeta.builder().access(imageMetaAccess).build();

		accessControlService.validateRowLevelAccess(null, imageMeta, Operation.VIEW);
		accessControlService.validateRowLevelAccess(null, imageMeta, Operation.MODIFY);
	}

	@Test
	void whenReadRestrictedAndWriteAllowedThenForReadInvalidKeyShouldBlock() throws ImageDBAccessDeniedException {
		ImageMetaAccess imageMetaAccess = ImageMetaAccess.builder().readAccess(AccessMode.RESTRICTED)
				.writeAccess(AccessMode.PUBLIC).build();

		ImageMeta imageMeta = ImageMeta.builder().access(imageMetaAccess).hashedUserKey(passwordEncoder.encode("mykey"))
				.build();

		assertThrowsExactly(ImageDBAccessDeniedException.class,
				() -> accessControlService.validateRowLevelAccess(null, imageMeta, Operation.VIEW));

		assertThrowsExactly(ImageDBAccessDeniedException.class,
				() -> accessControlService.validateRowLevelAccess("wrongKey", imageMeta, Operation.VIEW));

		accessControlService.validateRowLevelAccess("mykey", imageMeta, Operation.VIEW);

		accessControlService.validateRowLevelAccess(null, imageMeta, Operation.MODIFY);

	}

	@Test
	void whenWriteRestrictedAndReadAllowedThenForWriteInvalidKeyShouldBlock() throws ImageDBAccessDeniedException {
		ImageMetaAccess imageMetaAccess = ImageMetaAccess.builder().readAccess(AccessMode.PUBLIC)
				.writeAccess(AccessMode.RESTRICTED).build();

		ImageMeta imageMeta = ImageMeta.builder().access(imageMetaAccess).hashedUserKey(passwordEncoder.encode("mykey"))
				.build();

		accessControlService.validateRowLevelAccess(null, imageMeta, Operation.VIEW);

		assertThrowsExactly(ImageDBAccessDeniedException.class,
				() -> accessControlService.validateRowLevelAccess(null, imageMeta, Operation.MODIFY));

		assertThrowsExactly(ImageDBAccessDeniedException.class,
				() -> accessControlService.validateRowLevelAccess("wrongKey", imageMeta, Operation.MODIFY));

		accessControlService.validateRowLevelAccess("mykey", imageMeta, Operation.MODIFY);

	}

}
