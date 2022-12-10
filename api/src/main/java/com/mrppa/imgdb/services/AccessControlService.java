package com.mrppa.imgdb.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mrppa.imgdb.exception.ImageDBAccessDeniedException;
import com.mrppa.imgdb.meta.entities.AccessMode;
import com.mrppa.imgdb.meta.entities.ImageMeta;

@Component
public class AccessControlService {
	@Autowired
	PasswordEncoder passwordEncoder;

	public void validateRowLevelAccess(String userKey, ImageMeta imageMeta, String opperation)
			throws ImageDBAccessDeniedException {
		AccessMode accessMode = opperation.equals("view") ? imageMeta.getAccess().getReadAccess()
				: imageMeta.getAccess().getWriteAccess();
		if (AccessMode.PUBLIC == accessMode) {
			return;
		} else if (AccessMode.RESTRICTED == accessMode) {
			if (passwordEncoder.matches(userKey == null ? "" : userKey, imageMeta.getHashedUserKey())) {
				return;
			}

		}
		throw new ImageDBAccessDeniedException("Row level access denined for opperation " + opperation);
	}
}
