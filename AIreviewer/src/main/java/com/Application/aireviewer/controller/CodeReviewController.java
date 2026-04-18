package com.Application.aireviewer.controller;

import com.Application.aireviewer.dto.CodeReviewRequest;
import com.Application.aireviewer.dto.CodeReviewResponse;
import com.Application.aireviewer.service.CodeReviewService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class CodeReviewController
{
    @Autowired
    private CodeReviewService service;

    @PostMapping("/review")
    public CodeReviewResponse review(@RequestBody CodeReviewRequest request) {

        String result = service.reviewCode(request.getCode());

        CodeReviewResponse response = new CodeReviewResponse();
        response.setResult(result);
        System.out.println("CODE RECEIVED: " + request.getCode());
        return response;
    }
}
