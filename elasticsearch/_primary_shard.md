# [Why elasticsearch introduced the _primary_term to obtain optimistic concurrency control?](https://stackoverflow.com/questions/67437853/why-elasticsearch-introduced-the-primary-term-to-obtain-optimistic-concurrency)

I found an example that can be useful to understand the utility of the *_primary_term*:

Suppose we have 3 nodes A, B and C where A is the primary shard. Three operations are written on the primary shard:

1. operation 1: *_seq_no* = 1 *_primary_shard* = 1
2. operation 2: *_seq_no* = 2 *_primary_shard* = 1
3. operation 3: *_seq_no* = 3 *_primary_shard* = 1

the primary shard starts to send these operations to be applied to  the replicas shard. After a while suppose we have this situation:

- Node A (operation 1, 2 and 3 completed)
- Node B (operation 1 and 3 completed)
- Node C (operation 2 completed)

of course node B and C are not aligned with the primary shard yet.  Suppose that before the Node A sends the remain operations fails and  suppose that Node B becames the primary shard. It sends so all its  operations history (all operations after the global checkpoint. All the  operations before the global checkpoint are completed in all the active  nodes. For sake of simplicity we can assume that operation 1 is the  first operation executed after the last global checkpoint) to the  replicas shard. Node C sees that operations 1 and 3 arrived from Node B  have the *_primary_shard* = 2. It understand that the operation 2 is associated with the old primary shard and because of that it  executed the rollback on operation 2 (the *_primary_term* = 1) and executed the operation 1 and 3 to be aligned with the new primary node.

So the primary term is useful to distinguish between old and new primary shard.

> The primary term increments every time a different shard becomes  primary during failover. This helps when resolving changes which  occurred on old primaries which come back online vs. changes which occur on the new primary (the new wins).

