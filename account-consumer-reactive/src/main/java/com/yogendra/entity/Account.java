package com.yogendra.entity;

import com.yogendra.util.Amount;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@ToString
@Table
public class Account {
    @Id
    private String accountNumber;


    private String accountHolder;


    private Double balance;


    private LocalDate openingDate;


    private String email;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountNumber, account.accountNumber) && Objects.equals(accountHolder, account.accountHolder) && Objects.equals(balance, account.balance) && Objects.equals(openingDate, account.openingDate) && Objects.equals(email, account.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber, accountHolder, balance, openingDate, email);
    }
}
