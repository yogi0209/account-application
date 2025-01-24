package com.yogendra.service;

import ch.qos.logback.classic.Logger;
import com.yogendra.dao.AccountDao;
import com.yogendra.entity.Account;
import com.yogendra.requests.BalanceAction;
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
import reactor.core.publisher.Mono;

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


    public AccountService(KafkaTemplate<String, Object> kafkaTemplate, AccountDao accountDao) {
        this.kafkaTemplate = kafkaTemplate;
        this.accountDao = accountDao;
    }

    public Mono<Account> save(Account account) {
        return accountDao.save(account);
    }

    public Mono<Account> fetchAccountById(String accountId) {
        return accountDao.findById(accountId);
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

    @Transactional
    public Mono<Account> updateBalanceV2(UpdateBalance updateBalance, Span span) {
        return accountDao.findById(updateBalance.getAccountNumber()).flatMap(account -> {
            logger.info("Current Balance of {} is {}", account.getAccountNumber(), account.getBalance());
            BalanceAction balanceAction = BalanceAction.fromString(updateBalance.getAction());
            Double updatedAmount = balanceAction.apply(account.getBalance(), Double.valueOf(updateBalance.getAmount()));
            account.setBalance(updatedAmount);
            return accountDao.save(account).doOnNext(updatedAccount -> {
                logger.info("Balance of {} after {}{} is {}", updatedAccount.getAccountNumber(), updateBalance.getAmount(), balanceAction, updatedAmount);
                span.end();
            });
        });
    }
}
