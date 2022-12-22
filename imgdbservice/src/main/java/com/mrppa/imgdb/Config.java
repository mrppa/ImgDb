package com.mrppa.imgdb;

import com.mrppa.imgdb.img.service.ImageStore;
import com.mrppa.imgdb.img.service.impl.LocalFileImageStore;
import com.mrppa.imgdb.meta.repositories.ImageMetaRepository;
import com.mrppa.imgdb.meta.repositories.impl.ImageMetaRepositoryJDBCImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.FileSystems;
import java.security.SecureRandom;

@Configuration
@Profile("!test")
public class Config {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10, new SecureRandom());
    }

    @Bean
    public ImageStore imageStore() {
        String userDirectory = FileSystems.getDefault().getPath("images").toAbsolutePath().toString();
        return new LocalFileImageStore(userDirectory);
    }

    @Bean
    public ImageMetaRepository imageMetaRepository(){
        return new ImageMetaRepositoryJDBCImpl();
    }

}
