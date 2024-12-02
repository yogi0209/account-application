package com.yogendra.service;

import ch.qos.logback.classic.Logger;
import com.yogendra.dao.AccountDao;
import com.yogendra.entity.Account;
import com.yogendra.requests.SpanContextAndUpdateBalanceCarrier;
import com.yogendra.requests.UpdateBalance;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
public class AccountService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AccountDao accountDao;
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    private final ContextPropagators contextPropagators = ContextPropagators.create(
            TextMapPropagator.composite(
                    W3CTraceContextPropagator.getInstance()
            )
    );


    public AccountService(AccountDao accountDao, KafkaTemplate<String, Object> kafkaTemplate) {
        this.accountDao = accountDao;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public Account save(Account account) {
        return accountDao.save(account);
    }

    public void updateBalance(UpdateBalance updateBalance, Span span) {
        logger.info("Sending message to Kafka {}", updateBalance);
        SpanContextAndUpdateBalanceCarrier spanContextAndUpdateBalance = new SpanContextAndUpdateBalanceCarrier();
        spanContextAndUpdateBalance.setUpdateBalance(updateBalance);
        contextPropagators.getTextMapPropagator().inject(
                span.storeInContext(Context.current()),
                spanContextAndUpdateBalance,
                (carrier, key, value) -> carrier.getMap().put(key, value)
        );
        span.addEvent("message-sent", Instant.now());
        kafkaTemplate.send("account", updateBalance.getAccountNumber(), spanContextAndUpdateBalance);
        span.end();
    }
}
