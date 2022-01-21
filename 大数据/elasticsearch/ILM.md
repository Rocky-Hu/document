Index lifecycle policies can trigger actions such as:

- **Rollover**: Creates a new write index when the current one reaches a certain size, number of docs, or age.
- **Shrink**: Reduces the number of [primary shards](https://www.elastic.co/guide/en/elasticsearch/reference/current/glossary.html#glossary-primary-shard) in an index.
- **Force merge**: Manually triggers a [merge](https://www.elastic.co/guide/en/elasticsearch/reference/current/glossary.html#glossary-merge) to reduce the number of [segments](https://www.elastic.co/guide/en/elasticsearch/reference/current/glossary.html#glossary-segment) in an index’s [shards](https://www.elastic.co/guide/en/elasticsearch/reference/current/glossary.html#glossary-shard).
- **Freeze**: Makes an index read-only and minimizes its memory footprint.
- **Delete**: Permanently remove an index, including all of its data and metadata

