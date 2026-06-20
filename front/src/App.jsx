import React, { useEffect, useState, useCallback } from 'react';

const API_BASE = '/api/todos';

const FILTERS = [
  { key: 'all', label: '全部' },
  { key: 'active', label: '未完成' },
  { key: 'completed', label: '已完成' },
];

function App() {
  const [todos, setTodos] = useState([]);
  const [title, setTitle] = useState('');
  const [status, setStatus] = useState('all');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchTodos = useCallback(async (filter) => {
    setLoading(true);
    setError('');
    try {
      const res = await fetch(`${API_BASE}?status=${filter}`);
      if (!res.ok) throw new Error('无法获取待办列表');
      const data = await res.json();
      setTodos(data);
    } catch (err) {
      setError(err.message || '请求失败');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchTodos(status);
  }, [status, fetchTodos]);

  async function addTodo() {
    const trimmed = title.trim();
    if (!trimmed) return;
    setLoading(true);
    setError('');
    try {
      const res = await fetch(API_BASE, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title: trimmed }),
      });
      if (!res.ok) {
        const data = await res.json();
        throw new Error(data.detail || '创建任务失败');
      }
      setTitle('');
      await fetchTodos(status);
    } catch (err) {
      setError(err.message || '创建任务失败');
    } finally {
      setLoading(false);
    }
  }

  async function toggleCompleted(todo) {
    setLoading(true);
    setError('');
    try {
      const response = await fetch(`${API_BASE}/${todo.id}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ is_completed: !todo.is_completed }),
      });
      if (!response.ok) throw new Error('更新任务状态失败');
      await fetchTodos(status);
    } catch (err) {
      setError(err.message || '更新任务失败');
    } finally {
      setLoading(false);
    }
  }

  async function deleteTodo(id) {
    setLoading(true);
    setError('');
    try {
      const response = await fetch(`${API_BASE}/${id}`, { method: 'DELETE' });
      if (!response.ok) throw new Error('删除任务失败');
      await fetchTodos(status);
    } catch (err) {
      setError(err.message || '删除任务失败');
    } finally {
      setLoading(false);
    }
  }

  async function clearCompleted() {
    setLoading(true);
    setError('');
    try {
      const response = await fetch(`${API_BASE}?status=completed`, { method: 'DELETE' });
      if (!response.ok) throw new Error('清除已完成失败');
      await fetchTodos(status);
    } catch (err) {
      setError(err.message || '操作失败');
    } finally {
      setLoading(false);
    }
  }

  async function clearAll() {
    setLoading(true);
    setError('');
    try {
      const response = await fetch(`${API_BASE}?status=all`, { method: 'DELETE' });
      if (!response.ok) throw new Error('清除全部失败');
      await fetchTodos(status);
    } catch (err) {
      setError(err.message || '操作失败');
    } finally {
      setLoading(false);
    }
  }

  const activeCount = todos.filter((item) => !item.is_completed).length;
  const completedCount = todos.filter((item) => item.is_completed).length;

  return (
    <div className="app-shell">
      <div className="card">
        <h1>待办事项</h1>

        <div className="todo-input-row">
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="输入新任务"
            onKeyDown={(e) => e.key === 'Enter' && addTodo()}
            disabled={loading}
          />
          <button className="primary" onClick={addTodo} disabled={loading || title.trim() === ''}>
            添加
          </button>
        </div>

        <div className="status-row">
          <span>共 {todos.length} 条，未完成 {activeCount} 条，已完成 {completedCount} 条</span>
        </div>

        <div className="filter-row">
          {FILTERS.map((item) => (
            <button
              key={item.key}
              className={status === item.key ? 'active' : ''}
              onClick={() => setStatus(item.key)}
            >
              {item.label}
            </button>
          ))}
        </div>

        {error && <div className="error-tip">{error}</div>}

        <div className="todo-list">
          {loading ? (
            <div className="empty-state">加载中...</div>
          ) : todos.length === 0 ? (
            <div className="empty-state">暂无待办事项</div>
          ) : (
            <ol>
              {todos.map((todo) => (
                <li key={todo.id} className={todo.is_completed ? 'completed' : ''}>
                  <span>{todo.title}</span>
                  <div className="item-actions">
                    <button onClick={() => toggleCompleted(todo)}>
                      {todo.is_completed ? '取消完成' : '完成'}
                    </button>
                    <button className="danger" onClick={() => deleteTodo(todo.id)}>
                      删除
                    </button>
                  </div>
                </li>
              ))}
            </ol>
          )}
        </div>

        <div className="action-row">
          <button onClick={clearCompleted} disabled={loading || completedCount === 0}>
            清除已完成
          </button>
          <button className="danger" onClick={clearAll} disabled={loading || todos.length === 0}>
            清除全部
          </button>
        </div>
      </div>
    </div>
  );
}

export default App;
