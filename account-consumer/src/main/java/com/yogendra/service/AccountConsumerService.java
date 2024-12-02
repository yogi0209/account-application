package com.yogendra.service;

import ch.qos.logback.classic.Logger;
import com.yogendra.requests.SpanContextAndUpdateBalanceCarrier;
import com.yogendra.requests.UpdateBalance;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AccountConsumerService {

    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    private final Marker KAFKA_MESSAGE = MarkerFactory.getMarker("type=KAFKA_MESSAGE");
    private final AccountService accountService;
    private final OpenTelemetrySdk openTelemetrySdk;
    private final ContextPropagators contextPropagators = ContextPropagators.create(
            TextMapPropagator.composite(
                    W3CTraceContextPropagator.getInstance()
            )
    );

    public AccountConsumerService(AccountService accountService, OpenTelemetrySdk openTelemetrySdk) {
        this.accountService = accountService;
        this.openTelemetrySdk = openTelemetrySdk;
    }

    @KafkaListener(id = "account", topics = "account")
    public void consume(ConsumerRecord<String, SpanContextAndUpdateBalanceCarrier> record) throws InterruptedException {
        logger.info("{} {}", KAFKA_MESSAGE.getName(), record.value().getUpdateBalance());
        Context extractedContext = contextPropagators
                .getTextMapPropagator()
                .extract(
                        Context.current(),
                        record.value(),
                        new MessageGetter());
        Tracer tracer = openTelemetrySdk.getTracer("balance-update");
        Span span = tracer
                .spanBuilder("balance-update-consumer")
                .setSpanKind(SpanKind.CONSUMER)
                .setParent(extractedContext)
                .startSpan();
        span.addEvent("message-received", Instant.now());
        UpdateBalance updateBalance = record.value().getUpdateBalance();
        span.addEvent("balance-update-start", Instant.now());
        accountService.updateBalance(updateBalance, span);
    }

    private static class MessageGetter implements TextMapGetter<SpanContextAndUpdateBalanceCarrier> {
        @Override
        public Iterable<String> keys(SpanContextAndUpdateBalanceCarrier carrier) {
            return carrier.getMap().keySet();
        }

        @Override
        public String get(SpanContextAndUpdateBalanceCarrier carrier, String key) {
            if (carrier == null) {
                return null;
            }
            return carrier.getMap().get(key);
        }
    }
}
