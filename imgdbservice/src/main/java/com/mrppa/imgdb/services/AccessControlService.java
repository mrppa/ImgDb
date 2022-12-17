package com.mrppa.imgdb.services;

import com.mrppa.imgdb.exception.ImageDBAccessDeniedException;
import com.mrppa.imgdb.model.AccessMode;
import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.model.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AccessControlService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessControlService.class);

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Validate the user access
     *
     * @param userKey   row level user access key
     * @param imageMeta image meta fetched from the db
     * @param operation performing operation
     */
    public void validateRowLevelAccess(CharSequence userKey, ImageMeta imageMeta, Operation operation)
            throws ImageDBAccessDeniedException {
        LOGGER.debug("Validating row level access with operation:{} , imageMeta:{}", operation, imageMeta);

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
                "Row level access denied for operation " + operation + " for image " + imageMeta.getImageId());
    }
}
