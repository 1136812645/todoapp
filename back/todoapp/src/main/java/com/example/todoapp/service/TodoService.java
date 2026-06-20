package com.example.todoapp.service;

import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {
    private final TodoRepository repo;

    @PersistenceContext
    private EntityManager entityManager;

    public TodoService(TodoRepository repo) {
        this.repo = repo;
    }

    public List<Todo> findAll(Boolean completed, int limit, int offset) {
        String jpql = "select t from Todo t where (:completed is null or t.completed = :completed) order by t.id asc";
        TypedQuery<Todo> query = entityManager.createQuery(jpql, Todo.class);
        query.setParameter("completed", completed);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public long count(Boolean completed) {
        if (completed == null) {
            return repo.count();
        }
        return repo.countByCompleted(completed);
    }

    public Optional<Todo> findById(Long id) { return repo.findById(id); }

    @Transactional
    public Todo save(Todo t) { return repo.save(t); }

    @Transactional
    public Optional<Todo> toggle(Long id) {
        return repo.findById(id).map(todo -> {
            todo.setCompleted(todo.getCompleted() == null ? Boolean.TRUE : !todo.getCompleted());
            return repo.save(todo);
        });
    }

    @Transactional
    public long deleteByCompleted() {
        return repo.deleteByCompleted(true);
    }

    @Transactional
    public long deleteAllTodos() {
        long count = repo.count();
        repo.deleteAll();
        return count;
    }

    @Transactional
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
