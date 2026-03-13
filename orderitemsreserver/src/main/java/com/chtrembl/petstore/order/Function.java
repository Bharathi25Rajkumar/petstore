package com.chtrembl.petstore.order;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.logging.Logger;

public class Function {

        private static final String CONTAINER_NAME = "petstorecontainer";

        @FunctionName("OrderItemsReserver")
        public HttpResponseMessage run(
                        @HttpTrigger(name = "req", methods = {
                                        HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,

                        final ExecutionContext context) {

                Logger log = context.getLogger();
                log.info("OrderItemsReserverFunction triggered");

                try {

                        if (request.getBody().isEmpty()) {
                                log.warning("Request body missing");

                                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                                                .body("Order JSON must be provided in request body").build();
                        }

                        String rawSessionId = request.getHeaders().get("x-session-id");

                        if (rawSessionId == null || rawSessionId.isBlank()) {
                                log.warning("Missing X-Session-Id header");

                                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                                                .body("Session Id should be passed in the header").build();
                        }

                        String sessionId = rawSessionId.split(",")[0];
                        log.info("Resolved Session Id: " + sessionId);

                        String orderJson = request.getBody().get();
                        log.info("Order JSON received" + orderJson);

                        String blobPath = sessionId + ".json";

                        String connectionString = System.getenv("AzureWebJobsStorage");

                        if (connectionString == null || connectionString.isBlank()) {
                                log.severe("AzureWebJobsStorage connection string is not configured");

                                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                                                .body("Storage connection is not configured").build();
                        }

                        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                                        .connectionString(connectionString).buildClient();

                        BlobContainerClient blobContainerClient = blobServiceClient
                                        .getBlobContainerClient(CONTAINER_NAME);

                        BlobClient blobClient = blobContainerClient.getBlobClient(blobPath);

                        blobClient.upload(
                                        new ByteArrayInputStream(orderJson.getBytes(StandardCharsets.UTF_8)),
                                        orderJson.getBytes(StandardCharsets.UTF_8).length,
                                        true);

                        log.info("Order stored successfully at: " + CONTAINER_NAME + "/" + blobPath);

                        return request.createResponseBuilder(HttpStatus.OK)
                                        .body("Order stored successfully for session: " + sessionId)
                                        .build();

                } catch (Exception e) {

                        log.severe("Error processing Order");

                        return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("Failed to process order " + e.getMessage()).build();
                }
        }
}
