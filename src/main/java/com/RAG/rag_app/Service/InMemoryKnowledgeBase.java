package com.RAG.rag_app.Service;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class InMemoryKnowledgeBase {

    private final EmbeddingModel embeddingModel;
    private final DocumentChunker documentChunker;
    List<DocumentChunk> chunks = new CopyOnWriteArrayList<>();

    public InMemoryKnowledgeBase (EmbeddingModel embeddingModel, DocumentChunker documentChunker)
    {
        this.embeddingModel = embeddingModel;
        this.documentChunker = documentChunker;
    }

    public DocumentIngestion indexDocument(String documentName, String content)
    {
        String documentId = UUID.randomUUID().toString();
        List<String> textChunks = documentChunker.split(content);

        List<DocumentChunk> newChunks = new ArrayList<>();
        for(int i=0; i< textChunks.size(); i++)
        {
            String chunkText = textChunks.get(i);

            newChunks.add(new DocumentChunk(documentId, documentName, i+1,
                    chunkText, embeddingModel.embed(chunkText)));
        }

        chunks.addAll(newChunks);

        return new DocumentIngestion(documentId, documentName, newChunks.size());
    }

    public List<SearchResult> search(String question, int limit)
    {
        float[] questionVector = embeddingModel.embed(question);

        return chunks.stream().
                map(chunk -> new SearchResult(
                        chunk.documentId, chunk.documentName, chunk.chunkNumber, chunk.content,
                        cosineSimilarity(questionVector, chunk.embeddings)
                )).sorted((a, b) -> Double.compare(b.similarity, a.similarity))
                .limit(limit)
                .toList();
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

    public record DocumentIngestion(String documentId, String documentName, int chunksCreated)
    {}

    private record DocumentChunk(String documentId, String documentName, int chunkNumber, String content, float[] embeddings)
    {}

    public record SearchResult(String documentId, String documentName, int chunkNumber, String content, double similarity)
    {}
}
