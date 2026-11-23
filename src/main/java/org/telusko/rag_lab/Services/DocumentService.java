package org.telusko.rag_lab.Services;

import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.telusko.rag_lab.config.OllamaEmbeddingClient;
import org.telusko.rag_lab.document.Chunker;
import org.telusko.rag_lab.document.TextChunk;

@Service
public class DocumentService {
    private final JdbcTemplate jdbc;
    private final Chunker chunker;
    private final OllamaEmbeddingClient embeddings;

    public DocumentService(
            JdbcTemplate jdbc,
            Chunker chunker,
            OllamaEmbeddingClient embeddings) {
        this.jdbc = jdbc;
        this.chunker = chunker;
        this.embeddings = embeddings;
    }

    public int ingest(String name, String text) {
        int saved = 0;

        for (TextChunk chunk : chunker.split(text)) {
            var vector = embeddings.embed(chunk.text());
            String vectorLiteral = toVectorLiteral(vector);

            jdbc.update("""
                INSERT INTO document_chunks
                    (id, document_name, content, start_offset, end_offset, embedding)
                VALUES (?, ?, ?, ?, ?, ?::vector)
                """,
                    UUID.randomUUID(),
                    name,
                    chunk.text(),
                    chunk.startOffset(),
                    chunk.endOffset(),
                    vectorLiteral
            );
            saved++;
        }
        return saved;
    }

    //since pg vector does not accept List<float> so we convert it to string
    public static String toVectorLiteral(java.util.List<Float> vector) {
        return vector.toString();
    }
}