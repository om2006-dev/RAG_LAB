package org.telusko.rag_lab.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OllamaEmbeddingClient {
    //used to make external api request
    private final RestClient client;
    //stores the model name
    private final String model;

    public OllamaEmbeddingClient(
            @Value("${OLLAMA_URL}") String baseUrl,
            @Value("${OLLAMA_MODEL}") String model) {
        this.client = RestClient.builder().baseUrl(baseUrl).build();
        this.model = model;
    }

    public List<Float> embed(String text) {
        //sends a post req to the ollama host
        EmbedResponse response = client.post()
                //sends a req to ollamas embed endpoint
                .uri("/api/embed")
                .body(new EmbedRequest(model, text))
                .retrieve()
                //receives the response body
                .body(EmbedResponse.class);

        if (response == null || response.embeddings() == null
                || response.embeddings().isEmpty()) {
            throw new IllegalStateException("Ollama returned no embedding");
        }
        return response.embeddings().getFirst();
    }
    //used to send a response to the ollama
    private record EmbedRequest(String model, String input) {}
    //to collect the op from ollama ...returned as a nested list since ollama supports multiple embeds at once
    private record EmbedResponse(List<List<Float>> embeddings) {}
}