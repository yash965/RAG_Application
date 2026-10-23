package com.RAG.rag_app.Service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final ChatModel chatModel;
    private final InMemoryKnowledgeBase knowledgeBase;

    public QuestionService(ChatModel chatModel, InMemoryKnowledgeBase knowledgeBase)
    {
        this.chatModel = chatModel;
        this.knowledgeBase = knowledgeBase;
    }

    public AnswerResponse answer(String question)
    {
        List<InMemoryKnowledgeBase.SearchResult> results = knowledgeBase.search(question, 3);

        if(results.isEmpty())
        {
            return new AnswerResponse("No documents have been uploaded yet", List.of());
        }

        String context = results.stream()
                .map(result -> """
                        source : %s (chunk %d)
                        %s
                        """.formatted(result.documentName(), result.chunkNumber(), result.content()))
                .collect(Collectors.joining("\n---\n"));

        String prompt = """
        Answer the question using only the context below.
        If the answer is not in the context, say:
        "I don't know based on the available documents."

        Context:
        %s

        Question:
        %s
        """.formatted(context, question);

        String answer = chatModel.call(prompt);

        List<Source> sources = results.stream()
                .map(searchResult ->
                        new Source(searchResult.documentName(),
                                searchResult.chunkNumber(),
                                searchResult.similarity()))
                .toList();

        return new AnswerResponse(
                answer,sources
        );
    }

    public record AnswerResponse(String answer, List<Source> sources)
    {}

    public record Source(String documentName, int chunkNumber, double similarity)
    {}
}
