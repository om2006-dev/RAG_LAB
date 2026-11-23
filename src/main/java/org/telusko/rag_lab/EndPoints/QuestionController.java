package org.telusko.rag_lab.EndPoints;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.telusko.rag_lab.Services.DocumentService;
import org.telusko.rag_lab.config.OllamaEmbeddingClient;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private final JdbcTemplate jdbc;
    private final OllamaEmbeddingClient embeddings;
    private final int topK;

    public QuestionController(
            JdbcTemplate jdbc,
            OllamaEmbeddingClient embeddings,
            @Value("${top-k}") int topK) {
        this.jdbc = jdbc;
        this.embeddings = embeddings;
        this.topK = topK;
    }

    @PostMapping
    public AskResponse ask(@RequestBody AskRequest request) {
        var queryVector = embeddings.embed(request.question());
        String vectorLiteral = DocumentService.toVectorLiteral(queryVector);

        List<RetrievedChunk> chunks = jdbc.query("""
            SELECT document_name, content, start_offset, end_offset,
                   embedding <=> ?::vector AS distance
            FROM document_chunks
            ORDER BY embedding <=> ?::vector
            LIMIT ?
            """,
                (rs, rowNum) -> new RetrievedChunk(
                        rs.getString("document_name"),
                        rs.getString("content"),
                        rs.getInt("start_offset"),
                        rs.getInt("end_offset"),
                        rs.getDouble("distance")
                ),
                vectorLiteral, vectorLiteral, topK
        );

        return new AskResponse(request.question(), chunks);
    }

    public record AskRequest(String question) {}
    public record RetrievedChunk(
            String documentName,
            String text,
            int startOffset,
            int endOffset,
            double distance) {}
    public record AskResponse(String question, List<RetrievedChunk> matches) {}
}