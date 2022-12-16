package com.mrppa.imgdb.meta.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mrppa.imgdb.meta.entities.ImageMeta;

@Repository
public interface ImageMetaRepository extends JpaRepository<ImageMeta, String> {

}
