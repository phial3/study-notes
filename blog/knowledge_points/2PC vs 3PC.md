```yaml
title: 2PC vs 3PC
author: samin
date: 2022-01-11
```

# 2PC vs 3PC

## 2PC（二阶段提交协议）

2PC（Two-Phase Commit） 即两阶段提交，是将整个事务流程分为两个阶段：准备阶段（Prepare Phase）和提交阶段（Commit Phase）。

部分关系型数据库支持2PC协议，如Oracle、MySql。

![2pc](https://raw.githubusercontent.com/SaminZou/pic-repo/8427edecaac0b947dc52f3474aed3af62862d537/DistributedSystem/%E4%B8%80%E8%87%B4%E6%80%A7/2pc.png)

### 阶段一

1. 事务询问；协调者向各参与者发送事务内容，询问是否可提交事务，并等待各个参与者响应。
2. 各个参与者执行事务，并写Undo/Redo日志，此时事务已经执行，但并没有提交事务。 Undo日志记录的是修改前的数据，用于数据库事务回滚；Redo日志记录的是修改后的数据，用于提交事务后写入数据。
3. 各参与者向协调者反馈事务询问的响应。

### 阶段二

- 成功提交事务

阶段一的事务询问，各个参与者的响应都是Yes

1. 协调者向各参与者发送 commit 请求（事务提交）。
2. 各个参与者收到 commit 请求后，真正的提交事务，并在完成后释放事务执行过程中用到的锁资源。
3. 参与者完成事务后，向协调者发送 Ack 信息。（Ack是确认字符，在数据通信中，接受站发给发送站的一种传输类控制字符，表示发来的数据已确认接收无误。）
4. 协调者接收到所有参与者的 Ack 信息后，完成事务。

- 失败中断事务

任何一个参与者向协调者反馈了No响应，或者在等待超时之后，协调者依然无法获取所有参与者的反馈响应，那么就会中断事务。

1. 协调者向所有参与者发送 RollBack 请求（回滚请求）。
2. 参与者接收到 Rollback 请求后，会利用其在阶段一中记录的 Undo 信息来执行事务回滚操作，并在完成回滚之后释放在整个事务执行期间占用的锁资源。
3. 参与者在完成事务回滚之后，向协调者发送 Ack 信息。
4. 中断事务：协调者接收到所有参与者反馈的 Ack 信息后，完成事务中断。

## 3PC（三阶段提交协议）

3PC（Three Phase Commit），是2PC的改进版，是由canCommit、preCommit、doCommit三个阶段组成的事务处理协议。

![3pc](https://raw.githubusercontent.com/SaminZou/pic-repo/8427edecaac0b947dc52f3474aed3af62862d537/DistributedSystem/%E4%B8%80%E8%87%B4%E6%80%A7/3pc.png)

### canCommit阶段

1. 事务询问

   协调者向所有的参与者发送一个包含事务内容的canCommit请求，询问是否可以执行事务提交的操作，并等待参与者响应。

2. 各参与者向协调者反馈事务询问的响应

   参与者收到canCommit请求后，如果认为自身可以顺利执行事务提交，就向协调者反馈Yes响应，否则返回No响应。

### preCommit阶段

根据事务询问的结果，协调者会选择执行事务预提交，或者中断事务。

- 若所有参与者都反馈了Yes响应，则执行事务预提交：

    1. **发送预提交请求**；协调者向所有的参与者发送 preCommit 请求，并进入prepared阶段。
    2. **事务预提交**；参与者收到preCommit请求后，会执行事务操作，并写入Undo/Redo日志。
    3. **各参与者向协调者反馈预提交结果**；若参与者成功执行了事务，则反馈Ack。

- 若有参与者反馈了No响应，或者等待超时之后，仍然无法获取所有参与者的响应，则执行中断事务：

    1. **发送中断事务请求**；协调者向各参与者发送abort请求。
    2. **中断事务**；参与者收到abort请求或者等待协调者请求超时，参与者都会中断事务。

## doCommit阶段

该阶段也会出现两种情况：分别是事务提交和事务回滚。

- 若协调者收到了所有参与者的Ack响应，则会执行事务提交

    1. **发送事务提交请求**；协调者向所有的参与者发送 doCommit 请求。
    2. **提交事务**；参与者收到 doCommit 请求后，正式执行事务提交操作，并将事务执行过程中占用的锁资源释放。
    3. **反馈事务提交结果**；参与者完成事务提交操作后，向协调者反馈Ack信息。
    4. **完成事务**；协调者收到所有参与者发送的Ack响应，完成事务。

- 否则中断事务

    1. **发送中断请求**；协调者向所有的参与者发送abort请求。
    2. **事务回滚**；参与者收到abort请求后，根据Redo日志进行事务回滚操作，并在回滚完成后释放掉整个事务执行过程中占用的锁资源。
    3. **反馈事务回滚结果**；参与者在事务完成后，向协调者反馈Ack信息。
    4. **中断事务**；协调者收到所有参与者响应的Ack信息，中断事务。

## 优缺点

### 2PC 优点

原理简单，实现方便。

### 2PC 缺点

- 同步阻塞

  这是2PC协议存在的最明显也是最大的问题，在二阶段提交的执行过程中，所有参与该事务提交的逻辑都处于阻塞状态，各参与者在等待其他参与者响应的过程中，无法执行其他操作，这种同步阻塞极大的限制了分布式系统的性能。

- 单点故障问题

  在二阶段提交的执行过程中，协调者是非常重要的，一旦协调者出现问题，那整个流程将无法进行下去，最重要的是：其他参与者会一直处于资源锁定状态，无法完成事务操作。

- 数据不一致

  当协调者发送完所有的 commit 请求后发生了局部网络异常，或者协调者未发送完所有的 commit 请求自身就发生了崩溃，导致只有部分参与者收到了 commit 请求，最终会发生严重的数据不一致。

- 过于保守

  在二阶段提交的询问阶段，参与者出现故障而导致协调者一直无法获取所有参与者的响应的话，协调者只能依靠自身的超时机制去判断是否中断事务，这种策略过于保守。 是二阶段提交协议没有设计完善的容错机制，任何一个节点失败都会导致整个事务的失败。

### 3PC 优点

- 协调者和参与者都设置了超时机制（2PC中只有协调者设置了超时机制），避免了参与者长期无法和协调者通信（协调者故障）的情况下，参与者无法释放锁资源的情况，侧面降低了整个事务阻塞的时间和范围；

- 增加了一个缓冲阶段，保证了在最后提交阶段之前，各节点的状态是一致的；

### 3PC 缺点

3PC进入第三阶段可能会出现的问题

1. 协调者自身出现故障；

2. 协调者与参与者之间网络故障。

以上两种故障只要出现一种，都会导致参与者无法收到协调者发送的doCommit请求或abort请求，这可能会导致严重的数据不一致问题。

> 没有彻底解决数据不一致问题；由于第三阶段可能会发生上面提到的问题，一旦参与者没有正常接收到协调者发送的abort请求，参与者会在自身等待超时后，提交事务并释放资源，将会导致数据不一致问题。