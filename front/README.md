# Todo 应用前端

本目录为待办事项应用前端实现，基于 React 和 Webpack。

## 目录结构

- `src/`：React 源代码
- `public/`：HTML 模板
- `package.json`：依赖与启动脚本
- `webpack.config.js`：Webpack 构建配置
- `.babelrc`：Babel 转译配置

## 依赖安装

```powershell
cd d:\AI辅助编程test3\front
npm install
```

## 启动开发服务器

```powershell
npm start
```

默认前端入口地址：`http://localhost:3000`

## 接口规范

前端已配置开发代理，将所有 `/api` 请求转发到后端服务：

- 后端服务地址：`http://localhost:8000`
- API 根路径：`/api/todos`

本前端已通过以下接口接入后端：

- `GET /api/todos?status=...`：获取任务列表
- `POST /api/todos`：创建任务
- `PATCH /api/todos/{id}`：更新完成状态
- `DELETE /api/todos/{id}`：删除单个任务
- `DELETE /api/todos?status=completed`：清除已完成
- `DELETE /api/todos?status=all`：清除全部

## 功能说明

- 输入框 + 添加按钮：新建任务并提交至后端
- 列表项操作：完成 / 取消完成、删除
- 筛选条件：全部、未完成、已完成
- 底部操作：清除已完成、清除全部
- 页面显示任务统计信息

## 注意事项

- 启动前请先启动 backend 服务：
  ```powershell
  cd d:\AI辅助编程test3\backend
  uvicorn main:app --reload --port 8000
  ```

- 如果后端地址发生变化，请修改 `webpack.config.js` 中的 `proxy` 配置。