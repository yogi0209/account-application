package com.yogendra.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SpanContextAndUpdateBalanceCarrier {
    private UpdateBalance updateBalance;
    private Map<String, String> map = new HashMap<>();
}
