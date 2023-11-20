package com.example.demo;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import java.util.concurrent.CountDownLatch;

public class LoadTesting {

    private static final String BASE_URL = "https://my-demo-project-405617.as.r.appspot.com";
    private static final int NUM_REQUESTS = 100; // Количество запросов для эмуляции

    public static void main(String[] args) throws Exception {
        try (CloseableHttpAsyncClient client = HttpAsyncClients.createDefault()) {
            client.start();
            CountDownLatch latch = new CountDownLatch(NUM_REQUESTS * 2); // Умножаем на 2, так как у нас два вида запросов

            for (int i = 0; i < NUM_REQUESTS; i++) {
                // Запросы на запись
                HttpPost postRequest = new HttpPost(BASE_URL + "/set/user" + i + "/id" + i);
                String json = "{\"data\":\"test" + i + "\"}";
                StringEntity entity = new StringEntity(json);
                postRequest.setEntity(entity);
                postRequest.setHeader("Content-type", "application/json");
                client.execute(postRequest, new FutureCallback<HttpResponse>() {
                    @Override
                    public void completed(final HttpResponse response) {
                        latch.countDown();
                    }

                    @Override
                    public void failed(final Exception ex) {
                        latch.countDown();
                    }

                    @Override
                    public void cancelled() {
                        latch.countDown();
                    }
                });

                // Запросы на чтение
                HttpGet getRequest = new HttpGet(BASE_URL + "/get/user" + i + "/id" + i);
                client.execute(getRequest, new FutureCallback<HttpResponse>() {
                    @Override
                    public void completed(final HttpResponse response) {
                        latch.countDown();
                    }

                    @Override
                    public void failed(final Exception ex) {
                        latch.countDown();
                    }

                    @Override
                    public void cancelled() {
                        latch.countDown();
                    }
                });
            }

            latch.await(); // Ожидаем завершения всех запросов
        }
    }
}
