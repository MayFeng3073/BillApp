项目简介
本项目是《Android 应用开发》课程综合实训作品，是一款纯本地运行的个人记账应用。项目从第5周至第8周逐步迭代，完整实现了从基础界面、四大组件、异步通信到 Room 数据库与 ContentProvider 数据共享的全流程开发。

主要功能
- **账单管理**：新增收入/支出记录、查看历史账单、左滑删除账单
- **数据持久化**：使用 Room 数据库存储账单，重启应用数据不丢失
- **状态通知**：支持广播通知与后台服务，实现账目更新提醒
- **异步处理**：使用线程池与 Handler 实现后台任务与主线程通信
- **数据安全**：通过 ContentProvider 封装数据操作，实现页面与数据库解耦

使用技术
- **开发语言**：Java
- **数据存储**：Room 持久化框架
- **数据共享**：ContentProvider + ContentResolver
- **列表展示**：RecyclerView
- **异步处理**：Thread / ExecutorService / Handler
- **四大组件**：Activity / Service / BroadcastReceiver / ContentProvider

项目结构
com.example.myapplication
├── database/                # Room 数据库层
│   ├── AppDatabase.java     # 数据库单例
│   ├── ExpenseDao.java      # 数据访问接口
│   └── ExpenseEntity.java   # 账单实体类
├── ExpenseContract.java     # 数据契约类（URI、字段常量）
├── ExpenseProvider.java     # ContentProvider 实现
├── MainActivity.java        # 主界面与账单列表
├── AddExpenseActivity.java  # 新增账单页面
└── DatabaseExecutor.java    # 数据库异步执行器

运行方式
1. 使用 Android Studio 打开项目
2. 同步 Gradle 依赖
3. 连接 Android 设备或模拟器
4. 点击「运行」按钮即可安装并启动应用

开发过程
本项目分阶段完成：
1. 核心记账功能（基础功能）
- 添加收入/支出记录
- 填写：金额、分类、备注、日期
- 列表展示所有记录
- 左滑删除单条记录
- 数据持久化存储（关闭 App 不丢失）
 2. 第5周：Android 四大组件完整实现
- **Activity**：主页面 + 添加记录页面
- **Service**：后台服务，支持启动/关闭，用于账目监控
- **BroadcastReceiver**：全局广播接收，用于更新账目、发送状态提示
- **ContentProvider**：对外提供数据访问接口（第8周完善）
3. 第7周：异步线程 & 消息通信机制
- 主线程 / 子线程严格区分
- 使用 `Thread` 实现异步耗时操作
- 使用 `ExecutorService` 线程池管理后台任务
- 使用 `Handler + Message` 实现线程间消息通信
- 完整异步流程：加载中 → 成功 → 失败 → 清空状态
4. 第8周：Room 数据库 + ContentProvider 数据共享
- 使用 **Room** 替代原生 SQLite，更安全、更简洁
- 完整实现 **ContentProvider** 封装数据操作
- 页面通过 **ContentResolver** 访问数据，实现页面与数据库解耦
- 支持：**新增、查询、删除、更新（预留）**
- 异步线程处理所有数据库操作

作者信息
- 作者：冯诗韵
- 课程：移动应用设计
