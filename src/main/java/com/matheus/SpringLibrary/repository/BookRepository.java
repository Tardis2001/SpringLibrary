package com.matheus.SpringLibrary.repository;

import com.matheus.SpringLibrary.model.Book;
import com.matheus.SpringLibrary.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    Optional<Book> findById(UUID id);

    List<Book> findByUser(User user);


}
