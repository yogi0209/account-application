package com.yogendra.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@NoArgsConstructor
public class UpdateBalance {
    private String accountNumber;
    private String amount;
    private String action;
}
