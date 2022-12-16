package com.mrppa.imgdb.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mrppa.imgdb.exception.ImageDBAccessDeniedException;
import com.mrppa.imgdb.meta.entities.AccessMode;
import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.model.Operation;

@Component
public class AccessControlService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessControlService.class);

	@Autowired
	PasswordEncoder passwordEncoder;

	/**
	 * Validate the user access
	 *
	 * @param userKey    row level user access key
	 * @param imageMeta  image meta fetched from the db
	 * @param opperation performing operation
	 * @throws ImageDBAccessDeniedException
	 */
	public void validateRowLevelAccess(CharSequence userKey, ImageMeta imageMeta, Operation operation)
			throws ImageDBAccessDeniedException {
		LOGGER.debug("Validating rowlevel access with opperation:{} , imageMeta:{}", operation, imageMeta);

		// Find suitable access-mode based on operation
		AccessMode accessMode = Operation.VIEW == operation ? imageMeta.getAccess().getReadAccess()
				: imageMeta.getAccess().getWriteAccess();

		if (AccessMode.PUBLIC == accessMode) {
			return;
		} else if (AccessMode.RESTRICTED == accessMode
				&& passwordEncoder.matches(userKey == null ? "" : userKey, imageMeta.getHashedUserKey())) {
			return;

		}
		throw new ImageDBAccessDeniedException(
				"Row level access denined for opperation " + operation + " for image " + imageMeta.getImagId());
	}
}
