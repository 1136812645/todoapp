package com.example.todoapp;

import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class TodoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateAndGetTodo() throws Exception {
        String json = "{\"title\":\"Test Todo\",\"description\":\"Desc\",\"priority\":1}";

        mockMvc.perform(post("/api/v1/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code", is(201)))
                .andExpect(jsonPath("$.data.title", is("Test Todo")));

        mockMvc.perform(get("/api/v1/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.total", is(1)));
    }

    @Test
    void shouldToggleTodoCompletion() throws Exception {
        Todo todo = new Todo();
        todo.setTitle("Toggle Todo");
        todo.setCompleted(false);
        todo = repository.save(todo);

        mockMvc.perform(patch("/api/v1/todos/" + todo.getId() + "/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.completed", is(true)));
    }

    @Test
    void shouldDeleteCompletedTodos() throws Exception {
        Todo todo1 = new Todo();
        todo1.setTitle("Done");
        todo1.setCompleted(true);
        repository.save(todo1);
        Todo todo2 = new Todo();
        todo2.setTitle("Todo");
        todo2.setCompleted(false);
        repository.save(todo2);

        mockMvc.perform(delete("/api/v1/todos/completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted_count", is(1)));
    }
}
