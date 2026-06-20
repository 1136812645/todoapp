package com.example.todoapp;

import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class TodoRepositoryTest {

    @Autowired
    private TodoRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldCountByCompleted() {
        Todo todo1 = new Todo();
        todo1.setTitle("Todo1");
        todo1.setCompleted(true);
        repository.save(todo1);

        Todo todo2 = new Todo();
        todo2.setTitle("Todo2");
        todo2.setCompleted(false);
        repository.save(todo2);

        assertEquals(1, repository.countByCompleted(true));
        assertEquals(1, repository.countByCompleted(false));
    }

    @Test
    void shouldDeleteByCompleted() {
        Todo todo1 = new Todo();
        todo1.setTitle("Done");
        todo1.setCompleted(true);
        repository.save(todo1);

        Todo todo2 = new Todo();
        todo2.setTitle("Todo");
        todo2.setCompleted(false);
        repository.save(todo2);

        long deleted = repository.deleteByCompleted(true);
        assertEquals(1, deleted);
        assertEquals(1, repository.count());
    }
}
