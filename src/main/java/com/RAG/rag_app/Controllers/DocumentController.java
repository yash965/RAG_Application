package com.RAG.rag_app.Controllers;

import com.RAG.rag_app.Service.InMemoryKnowledgeBase;
import com.RAG.rag_app.Service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final InMemoryKnowledgeBase knowledgeBase;

    public DocumentController( InMemoryKnowledgeBase knowledgeBase)
    {
        this.knowledgeBase = knowledgeBase;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public InMemoryKnowledgeBase.DocumentIngestion upload(@RequestParam("file") MultipartFile file) throws IOException
    {
        String fileName = file.getOriginalFilename();

        if(file.isEmpty() || fileName == null || !fileName.endsWith(".txt"))
        {
            throw new IllegalArgumentException("upload a non-empty .txt file");
        }

        String content = new String(file.getBytes(), StandardCharsets.UTF_8);

        return knowledgeBase.indexDocument(fileName, content);
    }
}
