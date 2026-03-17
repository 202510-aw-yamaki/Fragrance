package com.fregrance.app.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fregrance.app.dto.QuestionnaireResultResponse;
import com.fregrance.app.form.QuestionnaireResultForm;
import com.fregrance.app.mapper.QuestionnaireResultMapper;
import com.fregrance.app.model.QuestionnaireResult;

@Service
public class QuestionnaireResultService {

    private final Map<String, QuestionnaireResultResponse> questionnaireResults = new ConcurrentHashMap<>();
    private final QuestionnaireResultMapper questionnaireResultMapper;
    private final ObjectMapper objectMapper;

    public QuestionnaireResultService(
        ObjectProvider<QuestionnaireResultMapper> questionnaireResultMapperProvider,
        ObjectProvider<ObjectMapper> objectMapperProvider
    ) {
        this.questionnaireResultMapper = questionnaireResultMapperProvider.getIfAvailable();
        this.objectMapper = objectMapperProvider.getIfAvailable();
    }

    public QuestionnaireResultResponse createResult(QuestionnaireResultForm form) {
        QuestionnaireResultResponse response = buildResponse(generateResultCode(), form);
        questionnaireResults.put(response.resultCode(), response);

        if (questionnaireResultMapper != null && objectMapper != null) {
            QuestionnaireResult questionnaireResult = new QuestionnaireResult();
            questionnaireResult.setResultCode(response.resultCode());
            questionnaireResult.setRouteCode(form.getRouteCode());
            questionnaireResult.setStep1AnswersJson(toJson(form.getStep1Answers()));
            questionnaireResult.setStep2AnswersJson(toJson(form.getStep2Answers()));
            questionnaireResult.setGraphAxesJson(toJson(form.getGraphAxes()));
            questionnaireResultMapper.insert(questionnaireResult);
        }

        return response;
    }

    public QuestionnaireResultResponse findByResultCode(String resultCode) {
        QuestionnaireResultResponse cached = questionnaireResults.get(resultCode);
        if (cached != null) {
            return cached;
        }
        if (questionnaireResultMapper == null || objectMapper == null) {
            return null;
        }

        QuestionnaireResult questionnaireResult = questionnaireResultMapper.findByResultCode(resultCode);
        if (questionnaireResult == null) {
            return null;
        }

        QuestionnaireResultResponse response = new QuestionnaireResultResponse(
            questionnaireResult.getResultCode(),
            questionnaireResult.getRouteCode(),
            readStringMap(questionnaireResult.getStep1AnswersJson()),
            readStringMap(questionnaireResult.getStep2AnswersJson()),
            readIntegerMap(questionnaireResult.getGraphAxesJson())
        );
        questionnaireResults.put(response.resultCode(), response);
        return response;
    }

    private QuestionnaireResultResponse buildResponse(String resultCode, QuestionnaireResultForm form) {
        return new QuestionnaireResultResponse(
            resultCode,
            form.getRouteCode(),
            new LinkedHashMap<>(form.getStep1Answers()),
            new LinkedHashMap<>(form.getStep2Answers()),
            new LinkedHashMap<>(form.getGraphAxes())
        );
    }

    private String generateResultCode() {
        return "QR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize questionnaire result", exception);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> readStringMap(String json) {
        try {
            return objectMapper.readValue(json, LinkedHashMap.class);
        } catch (JsonProcessingException exception) {
            return new LinkedHashMap<>();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Integer> readIntegerMap(String json) {
        try {
            return objectMapper.readValue(json, LinkedHashMap.class);
        } catch (JsonProcessingException exception) {
            return new LinkedHashMap<>();
        }
    }
}