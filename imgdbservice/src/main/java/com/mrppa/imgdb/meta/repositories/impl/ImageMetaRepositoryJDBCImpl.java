package com.mrppa.imgdb.meta.repositories.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.meta.entities.ImageMetaAccess;
import com.mrppa.imgdb.meta.repositories.ImageMetaRepository;
import com.mrppa.imgdb.model.AccessMode;
import com.mrppa.imgdb.model.ImageMetaStatus;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

public class ImageMetaRepositoryJDBCImpl implements ImageMetaRepository {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageMetaRepositoryJDBCImpl.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostConstruct
    synchronized void init() throws SQLException {
        LOGGER.info("Initializing JDBC meta repository");
        attemptToCreateTable();
        LOGGER.info("JDBC meta repository initialized");
    }

    private void attemptToCreateTable() {
        jdbcTemplate.execute(CREATE_SCRIPT);
    }

    @Override
    public void insert(ImageMeta imageMeta) {
        try {
            jdbcTemplate.update(INSERT_SCRIPT, imageMeta.getImageId(), imageMeta.getDescription(),
                    imageMeta.getExtension(), imageMeta.getHashedUserKey(), imageMeta.getAccess().getReadAccess().name(),
                    imageMeta.getAccess().getWriteAccess().name(),
                    objectMapper.writeValueAsString(imageMeta.getProperties()), imageMeta.getStatus().name(),
                    imageMeta.getAddedDate(), imageMeta.getUpdatedDate());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(ImageMeta imageMeta) {
        try {
            jdbcTemplate.update(UPDATE_SCRIPT, imageMeta.getDescription(), imageMeta.getExtension(),
                    imageMeta.getHashedUserKey(), imageMeta.getAccess().getReadAccess().name(),
                    imageMeta.getAccess().getWriteAccess().name(),
                    objectMapper.writeValueAsString(imageMeta.getProperties()), imageMeta.getStatus().name(),
                    imageMeta.getAddedDate(), imageMeta.getUpdatedDate(), imageMeta.getImageId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ImageMeta> findById(String imageId) {
        try {
            ImageMeta imageMeta = jdbcTemplate.queryForObject(FIND_BY_ID_SCRIPT, (rs, rowNum) ->
            {
                try {
                    return ImageMeta.builder()
                            .imageId(rs.getString("image_id"))
                            .description(rs.getString("description"))
                            .extension(rs.getString("extension"))
                            .hashedUserKey(rs.getString("hashed_user_key"))
                            .access(ImageMetaAccess.builder()
                                    .readAccess(AccessMode.valueOf(rs.getString("read_access")))
                                    .writeAccess(AccessMode.valueOf(rs.getString("write_access")))
                                    .build())
                            .properties(objectMapper.readValue(rs.getString("properties"), new TypeReference<Map<String, String>>() {
                            }))
                            .status(ImageMetaStatus.valueOf(rs.getString("status")))
                            .addedDate(rs.getTimestamp("added_date").toLocalDateTime())
                            .updatedDate(rs.getTimestamp("updated_date").toLocalDateTime()).build();
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Error while json parsing", e);
                }
            }, imageId);
            return Optional.ofNullable(imageMeta);
        } catch (EmptyResultDataAccessException e) {
            LOGGER.debug("Record not found");
        }
        return Optional.empty();
    }


    @Override
    public void deleteById(String imageId) {
        jdbcTemplate.update(DELETE_BY_ID_SCRIPT, imageId);
    }

    private static final String IMAGE_META_TABLE_NAME = "image_meta";
    private static final String CREATE_SCRIPT = """
                CREATE TABLE IF NOT EXISTS image_meta (
                  image_id varchar(255) NOT NULL,
                  description varchar(255),
                  extension varchar(20),
                  hashed_user_key varchar(255),
                  read_access varchar(20) NOT NULL,
                  write_access varchar(20) NOT NULL,
                  properties varchar(1000) NOT NULL,
                  status varchar(20) NOT NULL,
                  added_date datetime(6),
                  updated_date datetime(6),
                  PRIMARY KEY (image_id)
                )
            """;

    private static final String INSERT_SCRIPT = """
                INSERT INTO image_meta(image_id,description,extension,hashed_user_key,read_access,write_access,
                    properties,status,added_date,updated_date) VALUES (?,?,?,?,?,?,?,?,?,?);
            """;
    private static final String UPDATE_SCRIPT = """
                UPDATE image_meta SET description=?,extension=?,hashed_user_key=?,read_access=?,
                    write_access=?,properties=?,status=?,added_date=?,updated_date=? WHERE image_id=?;
            """;
    private static final String FIND_BY_ID_SCRIPT = """
                SELECT * from image_meta WHERE image_id=?;
            """;
    private static final String DELETE_BY_ID_SCRIPT = """
                DELETE from image_meta WHERE image_id=?;
            """;
}
