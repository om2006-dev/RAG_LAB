package org.telusko.rag_lab.EndPoints;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telusko.rag_lab.Services.DocumentService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    @PostMapping
    public IngestResponse ingest(@Valid @RequestBody IngestRequest request) {
        int count = service.ingest(request.name(), request.text());
        return new IngestResponse(request.name(), count);
    }

    public record IngestRequest(
            @NotBlank String name,
            @NotBlank String text) {}

    public record IngestResponse(String name, int chunksSaved) {}
}
