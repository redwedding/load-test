package com.redwedding.request;

/**
 * Created by davi on 8/8/16.
 */
public class CreateOrderRequest {

    public String number;
    public String reference;
    public String notes;
    public Long price;

    public CreateOrderRequest(String number, String reference, String notes, Long price) {
        this.number = number;
        this.reference = reference;
        this.notes = notes;
        this.price = price;
    }

}
