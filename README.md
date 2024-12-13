Make sure Kafka is installed and running


Run producer application : mvn spring-boot:run


Run consumer application : mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.kafka.consumer.group-id=account-consumer" (we can run multiple instance of consumer based on number of kafka topic partitions)


Sample account transfer request:


 ```
 http://localhost:9191/accounts/balance
 {
    "accountNumber":"68123978",
    "amount":"100",
    "action": "+"
}
 ```
Required table schema:

```
CREATE TABLE IF NOT EXISTS public.account
(
    account_number character varying(10) COLLATE pg_catalog."default" NOT NULL,
    account_holder character varying(50) COLLATE pg_catalog."default" NOT NULL,
    balance numeric(8,2) NOT NULL DEFAULT 0,
    opening_date date NOT NULL DEFAULT CURRENT_DATE,
    email character varying(30) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT pk_account PRIMARY KEY (account_number),
    CONSTRAINT unique_email UNIQUE (email),
    CONSTRAINT account_balance_check CHECK (balance >= 0::numeric)
)
```
