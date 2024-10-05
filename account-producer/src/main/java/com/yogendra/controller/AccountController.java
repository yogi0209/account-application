package com.yogendra.controller;

import ch.qos.logback.classic.Logger;
import com.yogendra.entity.Account;
import com.yogendra.requests.UpdateBalance;
import com.yogendra.service.AccountService;
import com.yogendra.util.Amount;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("accounts")
public class AccountController {

    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    private final AccountService accountService;


    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Void> addAccount(@RequestBody Account account) {
        account.setBalance(Amount.valueOf("0.00"));
        logger.info("Account : {}", account);
        Account newAccount = accountService.save(account);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(account.getAccountNumber())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("balance")
    public void UpdateBalance(@RequestBody UpdateBalance updateBalance) {
        accountService.updateBalance(updateBalance);
    }
}
