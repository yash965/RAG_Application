package com.RAG.rag_app.Service;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InMemoryKnowledgeBase {

    private final EmbeddingModel embeddingModel;
    List<DocumentChunk> chunks;

    public InMemoryKnowledgeBase (EmbeddingModel embeddingModel)
    {
        this.embeddingModel = embeddingModel;

        List<DocumentSeed> seedChunks = List.of(
                new DocumentSeed(
                        "leave-policy",
                        "Employees receive 20 days of annual leave each calendar year. " +
                                "Leave requests must be approved by the employee's manager."
                ),
                new DocumentSeed(
                        "remote-work-policy",
                        "Employees may work remotely up to three days per week after " +
                                "their manager approves the arrangement."
                ),
                new DocumentSeed(
                        "expenses-policy",
                        "Business travel expenses must be submitted within 30 days. " +
                                "Receipts are required for all expenses above 25 dollars."
                )
        );

        this.chunks = seedChunks.stream().map(seed -> new DocumentChunk(
                seed.id, seed.content(), embeddingModel.embed(seed.content())
        )).toList();
    }

    public SearchResult search(String question)
    {
        float[] questionVector = embeddingModel.embed(question);

        return chunks.stream().map(chunk ->
                new SearchResult(chunk.id, chunk.content, cosineSimilarity(questionVector, chunk.embeddings)))
                .max((left, right) -> Double.compare(left.similarity, right.similarity))
                .orElseThrow();
    }

    private double cosineSimilarity(float[] first, float[] second)
    {
        if(first.length != second.length)
        {
            throw new IllegalArgumentException("Vectors must have same dimensions");
        }

        double dotProduct = 0;
        double firstMagnitutde = 0;
        double secondMagnitude = 0;

        for(int idx = 0; idx<first.length; idx++)
        {
            dotProduct += first[idx] * second[idx];
            firstMagnitutde += first[idx] * first[idx];
            secondMagnitude += second[idx] * second[idx];
        }

        return dotProduct/ (
                    Math.sqrt(firstMagnitutde) * Math.sqrt(secondMagnitude)
                );
    }

    private record DocumentSeed(String id, String content)
    {}

    private record DocumentChunk(String id, String content, float[] embeddings)
    {}

    public record SearchResult(String sourceId, String content, double similarity)
    {}
}
