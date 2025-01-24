package com.yogendra.controller;

import ch.qos.logback.classic.Logger;
import com.yogendra.entity.Account;
import com.yogendra.requests.UpdateBalance;
import com.yogendra.service.AccountService;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("accounts")
public class AccountController {

    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    private final OpenTelemetrySdk openTelemetrySdk;
    private final AccountService accountService;

    public AccountController(OpenTelemetrySdk openTelemetrySdk, AccountService accountService) {
        this.openTelemetrySdk = openTelemetrySdk;
        this.accountService = accountService;
    }


    @PostMapping
    public Mono<Account> addAccount(@RequestBody Account account) {
        return accountService.save(account).doOnNext(newAccount -> {
            logger.info("Account : {}", newAccount);
        });
    }

    @GetMapping("/{accountId}")
    public Mono<Account> fetchAccount(@PathVariable String accountId) {
        logger.info("Fetching account for Id {}", accountId);
        return accountService.fetchAccountById(accountId).doOnNext(account -> {
            logger.info("Account {}", account);
        });
    }

    @PatchMapping("balance")
    public void UpdateBalance(@RequestBody UpdateBalance updateBalance) {
        Tracer tracer = openTelemetrySdk.getTracer("balance-update-reactive");
        Span span = tracer
                .spanBuilder("balance-update-producer-reactive")
                .setSpanKind(SpanKind.PRODUCER)
                .startSpan();
        logger.info("Span {}", span.getSpanContext().getSpanId());
        logger.info("Trace {}", span.getSpanContext().getTraceId());
        accountService.updateBalance(updateBalance, span);
    }

    @PatchMapping("/v2/balance")
    public Mono<Account> UpdateBalanceV2(@RequestBody UpdateBalance updateBalance) {
        Tracer tracer = openTelemetrySdk.getTracer("balance-update-reactive");
        Span span = tracer
                .spanBuilder("balance-update-producer-reactive")
                .setSpanKind(SpanKind.PRODUCER)
                .startSpan();
        return accountService.updateBalanceV2(updateBalance, span);
    }
}
