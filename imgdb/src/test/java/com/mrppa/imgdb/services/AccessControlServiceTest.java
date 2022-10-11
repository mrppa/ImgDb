package com.mrppa.imgdb.services;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import com.mrppa.imgdb.exception.ImageDBAccessDeniedException;
import com.mrppa.imgdb.meta.entities.AccessMode;
import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.meta.entities.ImageMetaAccess;

@ExtendWith(MockitoExtension.class)
class AccessControlServiceTest {

	@Spy
	PasswordEncoder passwordEncoder = new Pbkdf2PasswordEncoder();

	@InjectMocks
	AccessControlService accessControlService;

	@Test
	void whenPublicAccessShouldAllow() throws ImageDBAccessDeniedException {
		ImageMeta imageMeta = new ImageMeta();
		imageMeta.setAccess(new ImageMetaAccess());
		imageMeta.getAccess().setReadAccess(AccessMode.PUBLIC);
		imageMeta.getAccess().setWriteAccess(AccessMode.PUBLIC);

		accessControlService.validateRowLevelAccess(null, imageMeta, "view");
		accessControlService.validateRowLevelAccess(null, imageMeta, "anyother");
	}

	@Test
	void whenRestrictedAndInvalidHashShouldBlock() {
		ImageMeta imageMeta = new ImageMeta();
		imageMeta.setAccess(new ImageMetaAccess());
		imageMeta.setHashedUserKey(passwordEncoder.encode("mykey"));
		imageMeta.getAccess().setReadAccess(AccessMode.RESTRICTED);
		imageMeta.getAccess().setWriteAccess(AccessMode.RESTRICTED);

		assertThrowsExactly(ImageDBAccessDeniedException.class,
				() -> accessControlService.validateRowLevelAccess(null, imageMeta, "view"));

		assertThrowsExactly(ImageDBAccessDeniedException.class,
				() -> accessControlService.validateRowLevelAccess(null, imageMeta, "aotheropperation"));

		assertThrowsExactly(ImageDBAccessDeniedException.class,
				() -> accessControlService.validateRowLevelAccess("diffKey", imageMeta, "view"));

		assertThrowsExactly(ImageDBAccessDeniedException.class,
				() -> accessControlService.validateRowLevelAccess("diffKey", imageMeta, "aotheropperation"));
	}

	@Test
	void whenReadPublicWriteRestricted() throws ImageDBAccessDeniedException {
		ImageMeta imageMeta = new ImageMeta();
		imageMeta.setAccess(new ImageMetaAccess());
		imageMeta.setHashedUserKey(passwordEncoder.encode("mykey"));
		imageMeta.getAccess().setReadAccess(AccessMode.PUBLIC);
		imageMeta.getAccess().setWriteAccess(AccessMode.RESTRICTED);

		accessControlService.validateRowLevelAccess(null, imageMeta, "view");

		assertThrowsExactly(ImageDBAccessDeniedException.class,
				() -> accessControlService.validateRowLevelAccess(null, imageMeta, "aotheropperation"));

		accessControlService.validateRowLevelAccess("diffKey", imageMeta, "view");

		assertThrowsExactly(ImageDBAccessDeniedException.class,
				() -> accessControlService.validateRowLevelAccess("diffKey", imageMeta, "aotheropperation"));

		accessControlService.validateRowLevelAccess("mykey", imageMeta, "view");
		accessControlService.validateRowLevelAccess("mykey", imageMeta, "aotheropperation");

	}

	@Test
	void whenReadRestrictedWritePublic() throws ImageDBAccessDeniedException {
		ImageMeta imageMeta = new ImageMeta();
		imageMeta.setAccess(new ImageMetaAccess());
		imageMeta.setHashedUserKey(passwordEncoder.encode("mykey"));
		imageMeta.getAccess().setReadAccess(AccessMode.RESTRICTED);
		imageMeta.getAccess().setWriteAccess(AccessMode.PUBLIC);

		accessControlService.validateRowLevelAccess(null, imageMeta, "aotheropperation");

		assertThrowsExactly(ImageDBAccessDeniedException.class,
				() -> accessControlService.validateRowLevelAccess(null, imageMeta, "view"));

		accessControlService.validateRowLevelAccess("diffKey", imageMeta, "aotheropperation");

		assertThrowsExactly(ImageDBAccessDeniedException.class,
				() -> accessControlService.validateRowLevelAccess("diffKey", imageMeta, "view"));

		accessControlService.validateRowLevelAccess("mykey", imageMeta, "aotheropperation");
		accessControlService.validateRowLevelAccess("mykey", imageMeta, "view");

	}

}
