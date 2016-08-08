package com.redwedding.request;

/**
 * Created by davi on 8/8/16.
 */
public class PayOrderRequest {

    public String external_id;
    public Long amount;
    public String type;
    public String card_brand;
    public String card_bin;
    public String card_last;

    public PayOrderRequest(String external_id, Long amount, String type, String card_brand, String card_bin, String card_last) {
        this.external_id = external_id;
        this.amount = amount;
        this.type = type;
        this.card_brand = card_brand;
        this.card_bin = card_bin;
        this.card_last = card_last;
    }
}
