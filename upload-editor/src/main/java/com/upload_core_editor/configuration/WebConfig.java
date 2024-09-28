package com.upload_core_editor.configuration;

import com.upload_core_editor.service.impl.FileStoragePropertiesServiceImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final Path directoryAbsolutePath;
    private final Path directoryPhotoOriginal;
    private final Path directoryPhotoMarca;

    public WebConfig(FileStoragePropertiesServiceImpl fileStorageProperties) {
        this.directoryAbsolutePath = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        this.directoryPhotoOriginal = Paths.get(fileStorageProperties.getUploadDirOriginal()).normalize();
        this.directoryPhotoMarca = Paths.get(fileStorageProperties.getUploadDirMarca()).normalize();
    }


    final String nameMockClient = "JoaquimDaSilvaPereira";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/home/hugo/IdeaProjects/photoizer/upload-editor/uploads/JoaquimDaSilvaPereira/MarcaDagua/**")
                .addResourceLocations("file:/home/hugo/IdeaProjects/photoizer/upload-editor/uploads/JoaquimDaSilvaPereira/MarcaDagua/");
    }
}

