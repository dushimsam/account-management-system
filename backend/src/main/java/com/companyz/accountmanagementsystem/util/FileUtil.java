package com.companyz.accountmanagementsystem.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileUtil {


    private MultipartFile file;

    public  ResponseEntity<?> saveFile(long MAX_FILE_SIZE, String directory, String existingDir){

        try{

            // check if file is empty
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select a file to upload");
            }

            // check if file size is too large
            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest().body("File size exceeds the limit of " + MAX_FILE_SIZE + " bytes");
            }

            // get file extension
            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);

            // generate a unique file name
            String filename = UUID.randomUUID().toString() + "." + extension;

            // create directory if it doesn't exist
            Path directoryPath = Paths.get(directory);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            // save the file locally
            Path filePath = Paths.get(directory + "/" + filename);
            Files.write(filePath, file.getBytes());

            // check if existinDir is not null then continue

            if(existingDir != null){
                // delete the existing file
                Path ofExistingDir = Path.of(existingDir);
                if (Files.exists(ofExistingDir)) {
                    Files.deleteIfExists(ofExistingDir);
                }
            }

            return ResponseEntity.ok(filePath);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
