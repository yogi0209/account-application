package com.yogendra.service;

import ch.qos.logback.classic.Logger;
import com.yogendra.dao.AccountDao;
import com.yogendra.entity.Account;
import com.yogendra.requests.UpdateBalance;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AccountService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AccountDao accountDao;
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

    public AccountService(AccountDao accountDao, KafkaTemplate<String, Object> kafkaTemplate) {
        this.accountDao = accountDao;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public Account save(Account account) {
        return accountDao.save(account);
    }

    public void updateBalance(UpdateBalance updateBalance) {
        log.info("Sending message to Kafka {}", updateBalance);
        kafkaTemplate.send("account", updateBalance.getAccountNumber(), updateBalance);
    }


}
