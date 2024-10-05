package com.yogendra.service;

import ch.qos.logback.classic.Logger;
import com.github.loki4j.slf4j.marker.LabelMarker;
import com.yogendra.requests.UpdateBalance;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AccountConsumerService {

    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    private final Marker KAFKA_MESSAGE = MarkerFactory.getMarker("type=KAFKA_MESSAGE");
    private final AccountService accountService;

    public AccountConsumerService(AccountService accountService) {
        this.accountService = accountService;
    }

    @KafkaListener(id = "account", topics = "account")
    public void consume(ConsumerRecord<String, UpdateBalance> record) {
        logger.info(KAFKA_MESSAGE, "{}", record);
        UpdateBalance updateBalance = record.value();
        accountService.updateBalance(updateBalance);
    }

}
