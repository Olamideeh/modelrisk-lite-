package com.example.modelrisk.service;

import com.example.modelrisk.dto.CreditModelPredictionRequest;
import com.example.modelrisk.dto.CreditModelPredictionResponse;
import com.example.modelrisk.exception.ModelEndpointException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class CreditModelClient {

    private final RestClient restClient;

    public CreditModelClient(
            RestClient.Builder restClientBuilder
    ) {
        this.restClient = restClientBuilder.build();
    }

    public CreditModelPredictionResponse predict(
            String predictionEndpointUrl,
            CreditModelPredictionRequest request
    ) {
        try {
            CreditModelPredictionResponse response = restClient
                    .post()
                    .uri(predictionEndpointUrl)
                    .body(request)
                    .retrieve()
                    .body(CreditModelPredictionResponse.class);

            if (response == null) {
                throw new ModelEndpointException(
                        "AI model returned an empty response",
                        null
                );
            }

            return response;

        } catch (RestClientException exception) {
            throw new ModelEndpointException(
                    "Unable to call AI model endpoint",
                    exception
            );
        }
    }
}