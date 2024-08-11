package com.matheus.SpringLibrary.service;

import com.matheus.SpringLibrary.model.User;
import com.matheus.SpringLibrary.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.matheus.SpringLibrary.model.Book;
import com.matheus.SpringLibrary.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(UUID id) {
        return bookRepository.findById(id);
    }

    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    public List<Book> getBooksByUser(String token) {
        String userId = tokenService.getUserIdFromJWT(token);
        Optional<User> user = userRepository.findById(UUID.fromString(userId));
        return bookRepository.findByUser(user.orElseThrow());
    }

    public Long getQuantityfromUser(String userId) {
        Optional<User> user = userRepository.findById(UUID.fromString(userId));
        return Long.valueOf(bookRepository.findByUser(user.orElseThrow()).size());
    }

    public void deleteByBookByUser(UUID bookId, String token) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isEmpty()){
            return;
        }
        bookRepository.delete(optionalBook.get());
    }

    public Book getBookByUser(UUID bookId, String substring) {


        return bookRepository.findById(bookId).get();
    }
}
