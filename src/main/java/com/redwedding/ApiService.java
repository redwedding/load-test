package com.redwedding;

import co.paralleluniverse.fibers.Suspendable;
import com.redwedding.request.CreateOrderItemRequest;
import com.redwedding.request.CreateOrderRequest;
import com.redwedding.request.PayOrderRequest;
import com.redwedding.response.CreateOrderResponse;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by davi on 8/8/16.
 */
@Suspendable
public interface ApiService {

    @POST("/orders")
    CreateOrderResponse createOrder(@Body CreateOrderRequest request);

    @POST("/orders/{id}/items")
    Response createOrderItem(@Path("id") String id, @Body CreateOrderItemRequest request);

    @POST("/orders/{id}/transactions")
    Response payOrder(@Path("id") String id, @Body PayOrderRequest request);

    @GET("/orders/{id}")
    Response getOrder(@Path("id") String id);
}
