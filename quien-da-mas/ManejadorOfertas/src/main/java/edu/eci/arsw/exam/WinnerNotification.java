package edu.eci.arsw.exam;

import java.io.Serializable;

public class WinnerNotification implements Serializable {

    private final String buyerId;
    private final String productCode;

    public WinnerNotification(String buyerId, String productCode) {
        this.buyerId = buyerId;
        this.productCode = productCode;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getProductCode() {
        return productCode;
    }
}