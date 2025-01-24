package com.yogendra.controller;

import ch.qos.logback.classic.Logger;
import com.yogendra.entity.Account;
import com.yogendra.requests.UpdateBalance;
import com.yogendra.service.AccountService;
import com.yogendra.util.Amount;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("accounts")
public class AccountController {

    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    private final AccountService accountService;
    private final OpenTelemetrySdk openTelemetrySdk;


    public AccountController(AccountService accountService, OpenTelemetrySdk openTelemetrySdk) {
        this.accountService = accountService;
        this.openTelemetrySdk = openTelemetrySdk;
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
        Tracer tracer = openTelemetrySdk.getTracer("balance-update");
        Span span = tracer
                .spanBuilder("balance-update-producer")
                .setSpanKind(SpanKind.PRODUCER)
                .startSpan();
        accountService.updateBalance(updateBalance, span);
    }

    @PatchMapping("v2/balance")
    public void UpdateBalanceV2(@RequestBody UpdateBalance updateBalance) throws InterruptedException {
        Tracer tracer = openTelemetrySdk.getTracer("balance-update");
        Span span = tracer
                .spanBuilder("balance-update-producer")
                .setSpanKind(SpanKind.PRODUCER)
                .startSpan();
        accountService.updateBalanceV2(updateBalance, span);
    }
}
