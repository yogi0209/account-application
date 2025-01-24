package com.yogendra.requests;

import com.yogendra.util.Amount;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum BalanceAction {
    CREDIT("+") {
        @Override
        public Amount apply(Amount amount1, Amount amount2) {
            return amount1.add(amount2);
        }

        @Override
        public Double apply(Double amount1, Double amount2) {
            return new BigDecimal(String.valueOf(amount1)).add(new BigDecimal(String.valueOf(amount2))).doubleValue();
        }


    },
    DEBIT("-") {
        @Override
        public Amount apply(Amount amount1, Amount amount2) {
            return amount1.subtract(amount2);
        }

        @Override
        public Double apply(Double amount1, Double amount2) {
            return new BigDecimal(String.valueOf(amount1)).subtract(new BigDecimal(String.valueOf(amount2))).doubleValue();
        }
    };
    private final String action;

    private static final Map<String, BalanceAction> stringToEnum = Stream.of(values()).collect(Collectors.toMap(Objects::toString, e -> e));

    BalanceAction(String action) {
        this.action = action;
    }

    public abstract Amount apply(Amount amount1, Amount amount2);

    public abstract Double apply(Double amount1, Double amount2);

    @Override
    public String toString() {
        return this.action;
    }

    public static BalanceAction fromString(String action) {
        return stringToEnum.get(action);
    }
}
