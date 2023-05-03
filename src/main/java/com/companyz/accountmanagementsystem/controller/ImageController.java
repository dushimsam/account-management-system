package com.companyz.accountmanagementsystem.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/image")
@AllArgsConstructor
public class ImageController {

    @GetMapping("/load")
    public ResponseEntity<Resource> serveImage(@RequestParam("path") String dir) {
        Resource image = new FileSystemResource(dir);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
}
