package com.matheus.SpringLibrary.controller;

import com.matheus.SpringLibrary.dto.response.ResponseBookDTO;
import com.matheus.SpringLibrary.model.Book;
import com.matheus.SpringLibrary.model.User;
import com.matheus.SpringLibrary.service.BookService;
import com.matheus.SpringLibrary.service.FileStorageService;
import com.matheus.SpringLibrary.service.TokenService;
import com.matheus.SpringLibrary.service.UserService;
import com.matheus.SpringLibrary.utils.recoverToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private recoverToken recoverToken;

    private final String IMAGE_DIRECTORY = "uploads";
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,@RequestParam("image") MultipartFile image,
                                             @RequestParam("title") String title,
                                             @RequestParam("author") String author,
                                             @RequestHeader("Authorization") String token) {

        Optional<User> userOptional = userService.getUserByToken(recoverToken.recoverToken(token));
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOptional.get();
        Book book = new Book();
        System.out.println(book.getId());


        book.setTitle(title);
        book.setAuthor(author);
        book.setUser(user);
        bookService.saveBook(book);
        try {
            String filePath = fileStorageService.storeFile(file, book.getId());
            String imagePath = fileStorageService.storeImage(image, book.getId());

            if (filePath == null || imagePath == null) {
                return ResponseEntity.badRequest().body("Arquivo Incorreto");
            }

            book.setFilePath(filePath);
            book.setImagePath(imagePath);
            bookService.saveBook(book);
            userService.addUpload(token.substring(7));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar o arquivo");
        }
        return ResponseEntity.ok("File uploaded successfully");
    }
    @PostMapping("/upload/image/")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile image, @RequestParam("bookId") UUID bookId){
        String filePath = fileStorageService.storeImage(image,bookId);
        if(filePath == null){
            return ResponseEntity.badRequest().body("Arquivo Incorreto");
        }
        Optional<Book> optionalBook = bookService.getBookById(bookId);
        if (optionalBook.isEmpty()) {
            return ResponseEntity.badRequest().body("Book not found");
        }

        Book book = new Book();
        book.setId(book.getId());
        book.setImagePath(filePath);
        bookService.saveBook(book);
        return ResponseEntity.ok("Image uploaded succcessfully");
    }
    @GetMapping("/download/{bookId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable UUID bookId,@RequestHeader("Authorization") String token) {
        Optional<Book> bookOptional = bookService.getBookById(bookId);
        if (bookOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Book book = bookOptional.get();
        userService.addDownload(token.substring(7));
        try {
            byte[] data = fileStorageService.loadFileAsBytes(book.getFilePath());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + book.getTitle() + "\"")
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("delete/{bookId}")
    public ResponseEntity<String> deleteBook(@PathVariable UUID bookId, @RequestHeader("Authorization") String token) {
        fileStorageService.deleteFile(bookId,token.substring(7));
        bookService.deleteByBookByUser(bookId,token.substring(7));
//        userService.deleteBook(token.substring(7));
        return ResponseEntity.ok("Removido com sucesso");
    }
    @GetMapping("/")
    public List<Book> getUserBooks(@RequestHeader("Authorization") String token) {
        return bookService.getBooksByUser(token.substring(7));
    }
    @GetMapping("/{bookId}")
    public Book getUserBook(@PathVariable UUID bookId, @RequestHeader("Authorization") String token) {
        return bookService.getBookByUser(bookId,token.substring(7));
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);

                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                throw new RuntimeException("Arquivo não encontrado: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erro ao carregar a imagem: " + filename, e);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao detectar o tipo de conteúdo da imagem: " + filename, e);
        }
    }
}
