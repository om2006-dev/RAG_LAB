package org.telusko.rag_lab.document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Chunker {
    private final int chunkSize;
    private final int overlap;

    public Chunker(
            @Value("${chunk_size}") int chunkSize,
            @Value("${chunk_overlap}") int overlap) {
        if (chunkSize <= 0 || overlap < 0 || overlap >= chunkSize) {
            throw new IllegalArgumentException("Invalid chunk size or overlap");
        }
        this.chunkSize = chunkSize;
        this.overlap = overlap;
    }

    public List<TextChunk> split(String text) {
        List<TextChunk> chunks = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            String part = text.substring(start, end).trim();

            if (!part.isEmpty()) {
                chunks.add(new TextChunk(part, start, end));
            }
            if (end == text.length()) break;
            start = end - overlap;
        }
        return chunks;
    }
}
