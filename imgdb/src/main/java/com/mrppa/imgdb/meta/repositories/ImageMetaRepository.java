package com.mrppa.imgdb.meta.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mrppa.imgdb.meta.entities.ImageMeta;

public interface ImageMetaRepository extends JpaRepository<ImageMeta, String> {

}
