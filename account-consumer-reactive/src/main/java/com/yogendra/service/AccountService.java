package com.yogendra.service;

import ch.qos.logback.classic.Logger;
import com.yogendra.dao.AccountDao;
import com.yogendra.entity.Account;
import com.yogendra.requests.BalanceAction;
import com.yogendra.requests.UpdateBalance;
import com.yogendra.util.Amount;
import io.opentelemetry.api.trace.Span;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class AccountService {

    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    private final AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public Mono<Account> updateBalance(UpdateBalance updateBalance, Span span) {
        return accountDao.findById(updateBalance.getAccountNumber()).flatMap(account -> {
            logger.info("Current Balance of {} is {}", account.getAccountNumber(), account.getBalance());
            BalanceAction balanceAction = BalanceAction.fromString(updateBalance.getAction());
            Double updatedAmount = balanceAction.apply(account.getBalance(), Double.valueOf(updateBalance.getAmount()));
            account.setBalance(updatedAmount);
            return accountDao.save(account).doOnNext(updatedAccount -> {
                logger.info("Balance of {} after {}{} is {}", updatedAccount.getAccountNumber(), updateBalance.getAmount(), balanceAction, updatedAmount);
                span.addEvent("balance-update-complete", Instant.now());
                span.end();
            });
        });
    }
}
