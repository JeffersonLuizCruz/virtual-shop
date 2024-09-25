package com.upload_core_editor.repository;

import com.upload_core_editor.model.entity.PhotoImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoImageRepository extends JpaRepository<PhotoImage, Long> {
}
