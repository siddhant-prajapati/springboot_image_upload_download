package com.javatechie.controller;

import com.javatechie.entity.FileData;
import com.javatechie.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("image")
public class ImageController {
  @Autowired
  private StorageService service;

  //for uploading image in the database
  @PostMapping
  public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
    String uploadImage = service.uploadImage(file);
    return ResponseEntity.status(HttpStatus.OK)
        .body(uploadImage);
  }

  //for getting image with image name
  @GetMapping("/{fileName}")
  public ResponseEntity<?> downloadImage(@PathVariable String fileName){
    byte[] imageData=service.downloadImage(fileName);
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.valueOf("image/png"))
        .body(imageData);

  }


  //for uploading image in the folder
  @PostMapping("/fileSystem ")
  public ResponseEntity<?> uploadImageToFIleSystem(
      @RequestParam("image")MultipartFile file
      ) throws IOException {
    String uploadImage = service.uploadImageToFileSystem(file);
    return ResponseEntity.status(HttpStatus.OK)
        .body(uploadImage);
  }

  //for getting in the folder using image name
  @GetMapping("/fileSystem/{fileName}")
  public ResponseEntity<?> downloadImageFromFileSystem(@PathVariable String fileName) throws IOException {
    byte[] imageData=service.downloadImageFromFileSystem(fileName);
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.valueOf("image/png"))
        .body(imageData);

  }

  //for getting all image
  @GetMapping("/fileSystem")
  public ResponseEntity<List<FileData>> getAllImageFromFolder() throws IOException {
    return service.getAllImage();
  }

  //for deleting unwanted images using id
  @DeleteMapping("/fileSystem/{id}")
  public ResponseEntity<FileData> deleteImageFromFolder(@PathVariable("id") Long id){
    log.info("Id is : {}", id);
    return service.deleteImageById(id);
  }

}
