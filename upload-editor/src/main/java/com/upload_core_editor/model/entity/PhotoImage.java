package com.upload_core_editor.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
@Entity
public class PhotoImage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String client;

    private String nameFileOriginal;
    private String pathFileOriginal;
    private String urlFileOriginal;

    private String nameFileWatermark;
    private String pathFileWatermark;
    private String urlFileWatermark;

    @CreationTimestamp
    private LocalDateTime uploadAt;
    private String editor;
}
