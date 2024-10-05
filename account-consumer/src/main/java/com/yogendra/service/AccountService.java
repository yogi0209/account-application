package com.yogendra.service;

import ch.qos.logback.classic.Logger;
import com.yogendra.dao.AccountDao;
import com.yogendra.entity.Account;
import com.yogendra.requests.BalanceAction;
import com.yogendra.requests.UpdateBalance;
import com.yogendra.util.Amount;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountService {

    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    private final AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateBalance(UpdateBalance updateBalance) {
        Optional<Account> optionalAccount = accountDao.findById(updateBalance.getAccountNumber());
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            logger.info("Current Balance of {} is {}", account.getAccountNumber(), account.getBalance());
            BalanceAction balanceAction = BalanceAction.fromString(updateBalance.getAction());
            Amount updatedAmount = balanceAction.apply(account.getBalance(), Amount.valueOf(updateBalance.getAmount()));
            account.setBalance(updatedAmount);
            accountDao.save(account);
            logger.info("Balance of {} after {}{} is {}", account.getAccountNumber(), updateBalance.getAmount(), balanceAction, updatedAmount);
        }
    }
}
