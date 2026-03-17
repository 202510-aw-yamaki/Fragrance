package com.fregrance.app.dto;

import java.util.Map;

public record QuestionnaireResultResponse(
    String resultCode,
    String routeCode,
    Map<String, String> step1Answers,
    Map<String, String> step2Answers,
    Map<String, Integer> graphAxes
) {
}