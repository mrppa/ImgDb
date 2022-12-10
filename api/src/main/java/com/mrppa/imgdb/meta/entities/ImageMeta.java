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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
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

	@Column
	private String hashedUserKey;

	@Column(insertable = false)
	@Builder.Default
	private ImageMetaAccess access = new ImageMetaAccess();

	private String extension;

}
