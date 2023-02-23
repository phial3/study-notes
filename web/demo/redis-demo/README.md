# setnx 实现的分布式锁存在的问题

- `不可重入`：同一个线程无法多次获取同一把锁（eg：方法A调用方法B，在方法A中先获取锁，然后去调用方法B，方法B也需要获取同一把锁，这种情况下如果锁不可重入，方法B显然获取不到锁，会出现死锁的情况）

- `不可重试`：获取锁只尝试一次就返回 false，没有重试机制

- `超时释放`：超时释放虽然能够避免死锁，但如果业务执行执行时间较长导致锁释放，会存在安全隐患

- `主从一致性`：主从数据同步存在延迟，比如：线程在主节点获取了锁，尚未同步给从节点时主节点宕机，此时会选一个从节点作为新的主节点，这个从节点由于没有完成同步不存在锁的标识，此时其他线程可以趁虚而入拿到锁，这就造成多个线程同时拿到锁，就会出现安全问题）

使用 Redisson 的分布式锁可以避免这些