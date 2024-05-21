package com.javatechie.service;

import com.javatechie.entity.FileData;
import com.javatechie.entity.ImageData;
import com.javatechie.respository.FileDataRepository;
import com.javatechie.respository.StorageRepository;
import com.javatechie.util.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class StorageService {

    @Autowired
    private StorageRepository repository;

    @Autowired
    private FileDataRepository fileDataRepository;

    private final String FOLDER_PATH="C:\\Users\\acer\\Documents\\MyProject\\Springboot Project\\image-uploader-downloader\\file-storage\\target\\images\\";
    //C:\Users\acer\Documents\MyProject\Springboot Project\image-uploader-downloader\file-storage\target\images

    //store data in the database
    public String uploadImage(MultipartFile file) throws IOException {
        ImageData imageData = repository.save(ImageData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes())).build());
        if (imageData != null) {
            return "file uploaded successfully : " + file.getOriginalFilename();
        }
        return null;
    }


    //download data from the database
    public byte[] downloadImage(String fileName) {
        Optional<ImageData> dbImageData = repository.findByName(fileName);
        byte[] images = ImageUtils.decompressImage(dbImageData.get().getImageData());
        return images;
    }


    //upload or store image in the file System
    public String uploadImageToFileSystem(MultipartFile file) throws IOException {
        String filePath=FOLDER_PATH+file.getOriginalFilename();
        log.info(FOLDER_PATH);
        System.out.println(FOLDER_PATH);

        FileData fileData=fileDataRepository.save(FileData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .filePath(filePath).build());

        file.transferTo(new File(filePath));

        if (fileData != null) {
            return "file uploaded successfully : " + filePath;
        }
        return null;
    }

    //download (get) image from the file system
    public byte[] downloadImageFromFileSystem(String fileName) throws IOException {
        Optional<FileData> fileData = fileDataRepository.findByName(fileName);
        String filePath=fileData.get().getFilePath();
        byte[] images = Files.readAllBytes(new File(filePath).toPath());
        return images;
    }


    public ResponseEntity<List<FileData>> getAllImage() throws IOException {
        List<FileData> fileDatas = fileDataRepository.findAll();
        return ResponseEntity.of(Optional.of(fileDatas));
    }

    @Transactional
    public ResponseEntity<FileData> deleteImageById(Long id) {
        try {
            log.info("Id from Service : {}", id );
            FileData fileData = fileDataRepository.findById(id);
            log.info("File data is : {}", fileData);
            fileDataRepository.deleteById(id);
            log.info("Deleted Data is : {}",  fileDataRepository.deleteById(id));
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e){
            log.error("Internal Error is : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
