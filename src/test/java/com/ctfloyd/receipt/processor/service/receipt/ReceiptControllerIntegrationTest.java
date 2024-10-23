package com.ctfloyd.receipt.processor.service.receipt;

import com.ctfloyd.receipt.processor.model.receipt.GetReceiptPointsResponse;
import com.ctfloyd.receipt.processor.model.receipt.Item;
import com.ctfloyd.receipt.processor.model.receipt.ProcessReceiptResponse;
import com.ctfloyd.receipt.processor.model.receipt.Receipt;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The test is disabled because it is meant to be run manually when the service is running. Ideally this would live in
 * some sort of continuous deployment pipeline and run automatically before allowing promotion to the next stage.
 */
@Disabled
public class ReceiptControllerIntegrationTest {

    private static final String RESOURCE_ENDPOINT_PREFIX = "http://localhost:8080/receipts/%s";
    private static final ObjectMapper OBJECT_MAPPER = Jackson2ObjectMapperBuilder.json().build();
    private static final HttpClient CLIENT = HttpClient.newBuilder().build();

    @Test
    public void testReceiptProcessAndFetch_expectSuccess() throws Exception {
        Receipt receipt = new Receipt.Builder()
                .withRetailer("M&M Corner Market")
                .withPurchaseDate(LocalDate.of(2022, 3, 20))
                .withPurchaseTime(LocalTime.of(14, 33))
                .withItem(buildItem("Gatorade", "2.25"))
                .withItem(buildItem("Gatorade", "2.25"))
                .withItem(buildItem("Gatorade", "2.25"))
                .withItem(buildItem("Gatorade", "2.25"))
                .withTotal("9.00")
                .build();

        HttpRequest processReceiptRequest = HttpRequest.newBuilder()
                .uri(getProcessEndpointUri())
                .timeout(Duration.of(500, ChronoUnit.MILLIS))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(OBJECT_MAPPER.writeValueAsString(receipt)))
                .build();

        HttpResponse<String> rawResponse = CLIENT.send(processReceiptRequest, HttpResponse.BodyHandlers.ofString());
        ProcessReceiptResponse processReceiptResponse = OBJECT_MAPPER.readValue(rawResponse.body(), ProcessReceiptResponse.class);

        HttpRequest getPointsRequest = HttpRequest.newBuilder()
                .uri(getGetReceiptEndpointUri(processReceiptResponse.getId()))
                .timeout(Duration.of(500, ChronoUnit.MILLIS))
                .GET()
                .build();
        rawResponse = CLIENT.send(getPointsRequest, HttpResponse.BodyHandlers.ofString());
        GetReceiptPointsResponse getPointsResponse = OBJECT_MAPPER.readValue(rawResponse.body(), GetReceiptPointsResponse.class);

        assertEquals(109, getPointsResponse.getPoints());
    }

    private URI getProcessEndpointUri() {
        return URI.create(String.format(RESOURCE_ENDPOINT_PREFIX, "process"));
    }

    private URI getGetReceiptEndpointUri(String receiptId) {
        return URI.create(String.format(RESOURCE_ENDPOINT_PREFIX, receiptId + "/points"));
    }

    private Item buildItem(String description, String price) {
        return new Item.Builder()
                .withShortDescription(description)
                .withPrice(price)
                .build();
    }

}
