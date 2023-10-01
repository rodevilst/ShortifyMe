## Part 2 - What If
To handle such high loads, our service needs to be asynchronous. URL generation will work through message queues Kafka or RabbitMQ. URL resolution will also be asynchronous, with a queue and caching in Redis for fast access. Data will be stored in MongoDB, and it would be reasonable to retain data for a period of one to two months