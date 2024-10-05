Make sure Kafka is installed and running


Run producer application : mvn spring-boot:run


Run consumer application : mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.kafka.consumer.group-id=account-consumer" (we can run multiple instance of consumer based on number of kafka topic partitions)
