package com.yogendra.util;

import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;


@NoArgsConstructor
public class Amount {

    private BigDecimal value;

    public Amount(String value) {
        this.value = new BigDecimal(value);
    }

    public static Amount valueOf(String amount){
        if(amount == null || amount.isEmpty()){
            throw new IllegalArgumentException("Invalid amount");
        }
        if(amount.startsWith("₹")){
            amount = amount.substring(1);
        }
      return new Amount(amount);
    }

    public BigDecimal getValue() {
        return value;
    }

    public Amount add(Amount amount){
        return new Amount(value.add(amount.value).toString());
    }

    public Amount subtract(Amount amount){
        return new Amount(value.subtract(amount.value).toString());
    }

    @Override
    public String toString(){
        return "₹" + value.toString();
    }

    public int asInteger(){
        return value.intValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amount amount = (Amount) o;
        return Objects.equals(value, amount.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
