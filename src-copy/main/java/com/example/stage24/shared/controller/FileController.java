package com.example.stage24.shared.controller;

import com.example.stage24.shared.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/files")
public class FileController {


    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String filename = fileStorageService.store(file);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Uploaded the file successfully: " + filename);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body("Could not upload the file: " + file.getOriginalFilename() + "!");
        }
    }

    @GetMapping()
    public ResponseEntity<List<String>> getListFiles() {
        List<String> fileInfos = fileStorageService.loadAll()
                .map(path -> MvcUriComponentsBuilder
                        .fromMethodName(FileController.class, "getFile", path.getFileName().toString())
                        .build().toString())
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK)
                .body(fileInfos);
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = fileStorageService.loadAsResource(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @DeleteMapping("/{filename:.+}")
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        try {
            fileStorageService.delete(filename);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Deleted the file successfully: " + filename);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body("Could not delete the file: " + filename + "!");
        }
    }
}