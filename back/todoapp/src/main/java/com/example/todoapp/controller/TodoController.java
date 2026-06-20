package com.example.todoapp.controller;

import com.example.todoapp.dto.ApiResponse;
import com.example.todoapp.dto.CountResponse;
import com.example.todoapp.dto.PagedResponse;
import com.example.todoapp.model.Todo;
import com.example.todoapp.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todos")
public class TodoController {
    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<Todo>> list(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        List<Todo> todos = service.findAll(completed, limit, offset);
        long total = service.count(completed);
        return ResponseEntity.ok(new PagedResponse<>(200, "success", todos, total));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Todo>> create(@RequestBody Todo t) {
        Todo saved = service.save(t);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Todo created successfully", saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Todo>> get(@PathVariable Long id) {
        return service.findById(id)
                .map(todo -> ResponseEntity.ok(new ApiResponse<>(200, "success", todo)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(404, "Todo not found", null)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Todo>> update(@PathVariable Long id, @RequestBody Todo t) {
        return service.findById(id).map(existing -> {
            t.setId(existing.getId());
            Todo updated = service.save(t);
            return ResponseEntity.ok(new ApiResponse<>(200, "Todo updated successfully", updated));
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(404, "Todo not found", null)));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Todo>> toggle(@PathVariable Long id) {
        return service.toggle(id)
                .map(todo -> ResponseEntity.ok(new ApiResponse<>(200, "Todo status toggled successfully", todo)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(404, "Todo not found", null)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Todo deleted successfully", null));
    }

    @DeleteMapping("/completed")
    public ResponseEntity<CountResponse> deleteCompleted() {
        long deletedCount = service.deleteByCompleted();
        return ResponseEntity.ok(new CountResponse(200, "Completed todos deleted successfully", deletedCount));
    }

    @DeleteMapping("/all")
    public ResponseEntity<CountResponse> deleteAll() {
        long deletedCount = service.deleteAllTodos();
        return ResponseEntity.ok(new CountResponse(200, "All todos deleted successfully", deletedCount));
    }
}
