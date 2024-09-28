package com.upload_core_editor.controller;

import com.upload_core_editor.model.entity.PhotoImage;
import com.upload_core_editor.service.impl.PhotoManagementServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/uploads/photo")
public class PhotoManagementController {

    @Autowired private PhotoManagementServiceImpl photoManagement;

    @PostMapping
    public List<PhotoImage> uploadPhoto(@RequestParam("files") MultipartFile... files){

        return photoManagement.uploadPhoto(files);
    }

    public List<PhotoImage> findAll(){
        return photoManagement.findAll();
    }
}
