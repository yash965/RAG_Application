package com.RAG.rag_app.Controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/embeddings")
public class EmbeddingController {

    private final EmbeddingModel embeddingModel;

    public EmbeddingController(EmbeddingModel embeddingModel)
    {
        this.embeddingModel = embeddingModel;
    }

    @PostMapping
    public EmbeddingPreview createEmbedding(@Valid @RequestBody TextRequest request)
    {
        float[] vector = embeddingModel.embed(request.text);

        float[] preview = Arrays.copyOf(vector, Math.min(8, vector.length));

        return new EmbeddingPreview(vector.length, preview);
    }

    public record EmbeddingPreview (int dimension, float[] firstValues)
    {}

    public record TextRequest (@NotBlank (message = "Text cannot be empty") String text)
    {}
}
