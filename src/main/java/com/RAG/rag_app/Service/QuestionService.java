package com.RAG.rag_app.Service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

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
        InMemoryKnowledgeBase.SearchResult result = knowledgeBase.search(question);

        String prompt = """
        Answer the question using only the context below.
        If the answer is not in the context, say:
        "I don't know based on the available documents."

        Context:
        %s

        Question:
        %s
        """.formatted(result.content(), question);

        String answer = chatModel.call(prompt);

        return new AnswerResponse(
                answer, result.sourceId(), result.similarity()
        );
    }

    public record AnswerResponse(String answer, String question, double similarity)
    {}
}
