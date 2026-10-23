package com.RAG.rag_app.Service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentChunker {
    private static final int CHUNK_SIZE_WORDS = 120;
    private static final int CHUNK_OVERLAP_WORDS = 20;

    public List<String> split(String text)
    {
        String normalizedText = text.replaceAll("\\s+", " ").trim();

        if(normalizedText.isBlank())
            return List.of();

        List<String> words = List.of(normalizedText.split(" "));
        List<String> chunks = new ArrayList<>();

        int start = 0;

        while(start < words.size())
        {
            int end = Math.min(start + CHUNK_SIZE_WORDS, words.size());
            chunks.add(String.join(" ", words.subList(start, end)));

            if(end == words.size())
            {
                break;
            }

            start = end - CHUNK_OVERLAP_WORDS;
        }

        return chunks;
    }
}
