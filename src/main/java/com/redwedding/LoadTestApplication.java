package com.redwedding;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.fibers.retrofit.FiberRestAdapterBuilder;
import co.paralleluniverse.strands.Strand;
import com.redwedding.request.CreateOrderItemRequest;
import com.redwedding.request.CreateOrderRequest;
import com.redwedding.request.PayOrderRequest;
import com.redwedding.response.CreateOrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import retrofit.converter.JacksonConverter;

import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

@SpringBootApplication
public class LoadTestApplication implements CommandLineRunner {

    Random random = new Random();

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadTestApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LoadTestApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0) {
            String baseUrl = args[0];

            ApiService apiService = new FiberRestAdapterBuilder()
                    .setEndpoint(baseUrl)
                    .setConverter(new JacksonConverter())
                    .build()
                    .create(ApiService.class);

            int totalConections = Integer.valueOf(args[1]);

            for (int i = 0; i < totalConections; i++) {
                fiberSimulateUser(apiService);
            }

            new Scanner(System.in).next();
        }
    }

    @Suspendable
    private void fiberSimulateUser(ApiService apiService) {
        new Fiber<Void>("simulate-user", () -> {
            try {
                while (true) {
                    String id = createOrder(apiService);
                    fiberFirstCustomer(apiService, id); //edita a ordem
                    for (int i = 0; i < 5; i ++) //efetua 5 leituras da mesma ordem
                        getOrder(apiService, id);
                }
            } catch (Exception e) {
                LOGGER.error("error", e);
            }

        }).start();
    }

    @Suspendable
    private void fiberFirstCustomer(final ApiService apiService, final String id) {
        new Fiber<Void>("firstCustomer", () -> {
            //Adiciona 5 itens de um produto a ordem, um OrderItem com o valor 5 na quantidade
            createOrderItem(apiService, id, 5);
            //adiciona novamente um OrderItem com 5 unidades do produto
            createOrderItem(apiService, id, 5);
            //adiciona um produto com somente uma unidade
            createOrderItem(apiService, id, 1);
            //em seguida abate 50 reais na conta 5000
            payOrder(apiService, id, 5000);
            //adiciona mais um item com 4 unidades do produto
            createOrderItem(apiService, id, 4);
            //efetua o pagamento total do saldo devedor da ordem
            payOrder(apiService, id, 10000);
        }).start();

    }

    @Suspendable
    private String createOrder(ApiService apiService) throws Exception {
        return new Fiber<>("createOrder", () -> {
            long start = System.currentTimeMillis();
            CreateOrderRequest request = new CreateOrderRequest("1", "ref-001", "nota", 15000L);
            CreateOrderResponse response = apiService.createOrder(request);
            long end = System.currentTimeMillis() - start;
            LOGGER.info("createOrder ok, time[{}ms], id[{}]", end, response.id);
            return response.id;
        }).start().get();
    }

    @Suspendable
    private void createOrderItem(ApiService apiService, String id, long quantity) {
        new Fiber<Void>("createOrderItem", () -> {
            long start = System.currentTimeMillis();
            UUID sku = UUID.randomUUID();
            CreateOrderItemRequest request = new CreateOrderItemRequest(sku.toString(), 1000L, quantity);
            apiService.createOrderItem(id, request);
            long end = System.currentTimeMillis() - start;
            LOGGER.info("createOrderItem ok, time[{}ms], id[{}]", end, id);
            Fiber.sleep(getRandomInt());
        }).start();
    }

    @Suspendable
    private void payOrder(ApiService apiService, String id, long amount) {
        new Fiber<Void>("payOrder", () -> {
            long start = System.currentTimeMillis();
            PayOrderRequest request = new PayOrderRequest("10", amount, "PAYMENT", "VISA", "1402", "3211");
            apiService.payOrder(id, request);
            long end = System.currentTimeMillis() - start;
            LOGGER.info("payOrder ok, time[{}ms], id[{}]", end, id);
        }).start();
    }

    @Suspendable
    private void getOrder(ApiService apiService, String id) {
        new Fiber<Void>("getOrder", () -> {
            long start = System.currentTimeMillis();
            apiService.getOrder(id);
            long end = System.currentTimeMillis() - start;
            LOGGER.info("getOrder ok, time[{}ms], id[{}]", end, id);
            Fiber.sleep(getRandomInt());
        }).start();
    }

    private int getRandomInt() {
        return random.ints(1, 50, 100).findFirst().getAsInt();
    }

}
