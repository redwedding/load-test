package com.redwedding.request;

/**
 * Created by davi on 8/8/16.
 */
public class CreateOrderItemRequest {

    public String sku;
    public Long unit_price;
    public Long quantity;

    public CreateOrderItemRequest(String sku, Long unit_price, Long quantity) {
        this.sku = sku;
        this.unit_price = unit_price;
        this.quantity = quantity;
    }
}
