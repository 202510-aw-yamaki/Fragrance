package com.fregrance.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fregrance.app.dto.QuestionnaireResultResponse;
import com.fregrance.app.form.QuestionnaireResultForm;
import com.fregrance.app.service.QuestionnaireResultService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/questionnaire-results")
public class QuestionnaireApiController {

    private final QuestionnaireResultService questionnaireResultService;

    public QuestionnaireApiController(QuestionnaireResultService questionnaireResultService) {
        this.questionnaireResultService = questionnaireResultService;
    }

    @PostMapping
    public ResponseEntity<QuestionnaireResultResponse> createQuestionnaireResult(@Valid @RequestBody QuestionnaireResultForm form) {
        QuestionnaireResultResponse response = questionnaireResultService.createResult(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{resultCode}")
    public ResponseEntity<QuestionnaireResultResponse> getQuestionnaireResult(@PathVariable String resultCode) {
        QuestionnaireResultResponse response = questionnaireResultService.findByResultCode(resultCode);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}
