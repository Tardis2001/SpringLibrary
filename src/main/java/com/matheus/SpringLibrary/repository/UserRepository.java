package com.matheus.SpringLibrary.repository;

import com.matheus.SpringLibrary.model.Book;
import com.matheus.SpringLibrary.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository <User, UUID> {
    Optional<User> findByEmail(String email);

}
