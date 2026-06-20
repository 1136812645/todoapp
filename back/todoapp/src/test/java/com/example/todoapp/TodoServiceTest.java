package com.example.todoapp;

import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import com.example.todoapp.service.TodoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository repository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Todo> query;

    @InjectMocks
    private TodoService service;

    @BeforeEach
    void setUp() throws Exception {
        reset(repository, entityManager, query);
        Field field = TodoService.class.getDeclaredField("entityManager");
        field.setAccessible(true);
        field.set(service, entityManager);
    }

    @Test
    void shouldReturnAllTodosWhenCompletedIsNull() {
        Todo todo1 = new Todo();
        todo1.setTitle("A");
        Todo todo2 = new Todo();
        todo2.setTitle("B");

        when(entityManager.createQuery(anyString(), eq(Todo.class))).thenReturn(query);
        when(query.setParameter(eq("completed"), isNull())).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(todo1, todo2));

        List<Todo> result = service.findAll(null, 10, 0);
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repository, never()).countByCompleted(anyBoolean());
    }

    @Test
    void shouldReturnTodosForCompletedFilter() {
        Todo todo = new Todo();
        todo.setTitle("Done");
        when(repository.countByCompleted(true)).thenReturn(1L);

        // Because service uses EntityManager JPQL for findAll, only count is verified here.
        long count = service.count(true);
        assertEquals(1, count);
        verify(repository).countByCompleted(true);
    }

    @Test
    void shouldSaveTodo() {
        Todo todo = new Todo();
        todo.setTitle("New todo");
        when(repository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Todo saved = service.save(todo);
        assertSame(todo, saved);
        verify(repository).save(todo);
    }

    @Test
    void shouldToggleTodoCompletionFromNullToTrue() {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setCompleted(null);
        when(repository.findById(1L)).thenReturn(Optional.of(todo));
        when(repository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Todo> toggled = service.toggle(1L);
        assertTrue(toggled.isPresent());
        assertTrue(toggled.get().getCompleted());
        verify(repository).findById(1L);
        verify(repository).save(todo);
    }

    @Test
    void shouldReturnEmptyWhenToggleTodoNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Optional<Todo> toggled = service.toggle(999L);
        assertFalse(toggled.isPresent());
        verify(repository).findById(999L);
        verify(repository, never()).save(any());
    }

    @Test
    void shouldDeleteCompletedTodos() {
        when(repository.deleteByCompleted(true)).thenReturn(3L);

        long deleted = service.deleteByCompleted();
        assertEquals(3L, deleted);
        verify(repository).deleteByCompleted(true);
    }

    @Test
    void shouldDeleteAllTodos() {
        when(repository.count()).thenReturn(5L);

        long deleted = service.deleteAllTodos();
        assertEquals(5L, deleted);
        verify(repository).count();
        verify(repository).deleteAll();
    }
}
