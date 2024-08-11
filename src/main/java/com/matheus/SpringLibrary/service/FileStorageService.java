package com.matheus.SpringLibrary.service;

import com.matheus.SpringLibrary.repository.BookRepository;
import com.matheus.SpringLibrary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String fileStorageLocation;

    private final Path fileStoragePath;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;
    public FileStorageService(@Value("${file.upload-dir}") String fileStorageLocation) {
        this.fileStorageLocation = fileStorageLocation;
        this.fileStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStoragePath);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file, UUID bookId) {
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file.");
        }
        String fileName = bookId.toString() + "-" + StringUtils.cleanPath(file.getOriginalFilename());
        if (!fileName.toLowerCase().endsWith(".pdf")) {
            throw new RuntimeException("Invalid file type. Only PDFs are allowed.");
        }

        try {
            Path targetLocation = this.fileStoragePath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public byte[] loadFileAsBytes(String filePath) throws IOException {
        Path path = Paths.get(filePath).toAbsolutePath().normalize();
        return Files.readAllBytes(path);
    }

    public String storeImage(MultipartFile file, UUID bookId) {
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file.");
        }
            String fileName = bookId.toString() + "-" + StringUtils.cleanPath(file.getOriginalFilename());
            if (fileName.toLowerCase().endsWith(".png") || fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {

                try {
                    Path targetLocation = this.fileStoragePath.resolve(fileName);
                    Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                    return targetLocation.toString();
                } catch (IOException ex) {
                    throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
                }
            }
            throw new RuntimeException("Invalid file type. Only JPG,PNG,JPEG are allowed.");


        }

    public void deleteFile (UUID bookId, String token){
        String fileName = bookRepository.findById(bookId).get().getFilePath();
        String fileImage= bookRepository.findById(bookId).get().getImagePath();
        if (fileName != null && fileImage != null) {
            Path filePath = this.fileStoragePath.resolve(fileName).normalize();
            Path fileImagePath = this.fileStoragePath.resolve(fileImage).normalize();
            try {
                Files.deleteIfExists(filePath);
                Files.deleteIfExists(fileImagePath);
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Couldn't find file");
    }

}
