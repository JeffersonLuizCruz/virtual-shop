package com.upload_core_editor.controller;

import com.upload_core_editor.model.entity.PhotoImage;
import com.upload_core_editor.service.impl.UploadPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/uploads/photo")
public class UploadPhotoController {

    @Autowired private UploadPhotoService uploadPhotoService;

    @PostMapping
    public List<PhotoImage> uploadPhoto(@RequestParam("files") MultipartFile... files){
        System.out.println(uploadPhotoService);
        return uploadPhotoService.uploadPhoto(files);
    }
}
