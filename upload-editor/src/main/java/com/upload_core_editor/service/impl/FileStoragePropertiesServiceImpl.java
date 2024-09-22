package com.upload_core_editor.service.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration @ConfigurationProperties(prefix = "file")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class FileStoragePropertiesServiceImpl {

    private String uploadDir;
    private String uploadDirOriginal;
    private String uploadDirMarca;
}
