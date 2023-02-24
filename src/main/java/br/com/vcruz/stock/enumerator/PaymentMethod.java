package br.com.vcruz.stock.enumerator;

import lombok.Getter;

/**
 *
 * @author vcruz
 */
@Getter
public enum PaymentMethod {
    PIX("PIX"),
    MONEY("Dinheiro em espécie"),
    CREDIT("Cartão de crédito"),
    DEBIT("Cartão de débito");

    private final String value;

    private PaymentMethod(String value) {
        this.value = value;
    }
}
