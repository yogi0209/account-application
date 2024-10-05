package com.yogendra.entity;

import com.yogendra.util.Amount;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Entity
@ToString
@DynamicUpdate
public class Account {
    @Id
    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_holder")
    private String accountHolder;

    @AttributeOverride(name = "value", column = @Column(name = "balance"))
    private Amount balance;

    @Column(name = "opening_date", insertable = false)
    private LocalDate openingDate;

    @Column(name = "email")
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
