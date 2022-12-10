package com.mrppa.imgdb.meta.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Data
@Entity
public class ImageMeta {
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	private String imagId;

	private String description;

	@Enumerated
	private ImageMetaStatus status;

	@CreationTimestamp
	private LocalDateTime addedDate;

	@UpdateTimestamp
	private LocalDateTime updatedDate;

	@Version
	private int version;

	@Column(nullable = false)
	private String hashedUserKey;

	private ImageMetaAccess access;

	private String extension;

}
