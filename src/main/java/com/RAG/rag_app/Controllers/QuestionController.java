package com.RAG.rag_app.Controllers;

import com.RAG.rag_app.Service.InMemoryKnowledgeBase;
import com.RAG.rag_app.Service.QuestionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService)
    {
        this.questionService = questionService;
    }

    // 202 (accepted) for asynchronous processing
    @PostMapping()
    public QuestionService.AnswerResponse ask (@Valid @RequestBody QuestionRequest request)
    {
        return questionService.answer(request.question());
    }

    // Record
    public record QuestionRequest (@NotBlank(message = "Question cannot be empty") String question)
    {
//        public QuestionRequest
//        {
//            if(question == null || question.isBlank())
//            {
//                throw new IllegalArgumentException("Message cannot be blank");
//            }
//        }
    }
}
