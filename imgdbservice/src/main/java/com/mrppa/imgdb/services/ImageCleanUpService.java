package com.mrppa.imgdb.services;

import com.mrppa.imgdb.exception.ImageDbException;
import com.mrppa.imgdb.exception.ImageFileNotFoundException;
import com.mrppa.imgdb.img.service.ImageStore;
import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.meta.repositories.ImageMetaRepository;
import com.mrppa.imgdb.model.ImageMetaStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ImageCleanUpService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageCleanUpService.class);
    private static final int MAX_RECORDS_PER_TURN = 10000;

    @Autowired
    ImageMetaRepository imageMetaRepository;

    @Autowired
    ImageStore imageStore;

    @Scheduled(fixedDelay = 30000, initialDelay = 30000)
    public void cleanupMetaData() {
        cleanupRecordByStatusAndUpdatedDate(ImageMetaStatus.PENDING_DELETE,LocalDateTime.now().minusDays(1));
        cleanupRecordByStatusAndUpdatedDate(ImageMetaStatus.CREATED,LocalDateTime.now().minusDays(1));
    }

    public void cleanupRecordByStatusAndUpdatedDate(ImageMetaStatus imageMetaStatus, LocalDateTime updatedDate) {
        LOGGER.info("Start cleaning up records updated before {] with status {}", updatedDate, imageMetaStatus);
        List<ImageMeta> metaRecords = imageMetaRepository.listByStatusAndUpdatedDateBefore(imageMetaStatus,
                updatedDate, MAX_RECORDS_PER_TURN);
        int cleanCount=0;
        for(ImageMeta imageMeta:metaRecords){
            try {
                imageStore.deleteImage(imageMeta.getImageId());
            } catch (ImageFileNotFoundException ignored) {
            } catch (ImageDbException e) {
                LOGGER.error("Image cleanup failed",e);
                return;
            }
            imageMetaRepository.deleteById(imageMeta.getImageId());
            cleanCount++;
        }

        LOGGER.info("Cleanup Completed for {} of records", cleanCount);
    }
}
