package com.example.todoapp.repository;

import com.example.todoapp.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    long countByCompleted(boolean completed);

    @Transactional
    @Modifying
    long deleteByCompleted(boolean completed);
}
