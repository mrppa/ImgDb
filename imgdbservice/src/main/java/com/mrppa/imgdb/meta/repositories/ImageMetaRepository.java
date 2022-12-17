package com.mrppa.imgdb.meta.repositories;

import com.mrppa.imgdb.meta.entities.ImageMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageMetaRepository extends JpaRepository<ImageMeta, String> {

}
