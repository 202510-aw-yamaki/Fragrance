package com.fregrance.app.form;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class QuestionnaireResultForm {

    @NotBlank(message = "routeCode is required")
    @Size(max = 32, message = "routeCode must be 32 characters or less")
    private String routeCode;

    @NotEmpty(message = "step1Answers is required")
    private Map<
        @NotBlank(message = "step1Answers key must not be blank")
        @Size(max = 40, message = "step1Answers key must be 40 characters or less")
        String,
        @NotBlank(message = "step1Answers value must not be blank")
        @Size(max = 255, message = "step1Answers value must be 255 characters or less")
        String
    > step1Answers = new LinkedHashMap<>();

    @NotEmpty(message = "step2Answers is required")
    private Map<
        @NotBlank(message = "step2Answers key must not be blank")
        @Size(max = 40, message = "step2Answers key must be 40 characters or less")
        String,
        @NotBlank(message = "step2Answers value must not be blank")
        @Size(max = 255, message = "step2Answers value must be 255 characters or less")
        String
    > step2Answers = new LinkedHashMap<>();

    @NotEmpty(message = "graphAxes is required")
    private Map<
        @NotBlank(message = "graphAxes key must not be blank")
        @Size(max = 40, message = "graphAxes key must be 40 characters or less")
        String,
        @NotNull(message = "graphAxes value is required")
        @Min(value = 0, message = "graphAxes value must be between 0 and 100")
        @Max(value = 100, message = "graphAxes value must be between 0 and 100")
        Integer
    > graphAxes = new LinkedHashMap<>();

    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }
    public Map<String, String> getStep1Answers() { return step1Answers; }
    public void setStep1Answers(Map<String, String> step1Answers) { this.step1Answers = step1Answers; }
    public Map<String, String> getStep2Answers() { return step2Answers; }
    public void setStep2Answers(Map<String, String> step2Answers) { this.step2Answers = step2Answers; }
    public Map<String, Integer> getGraphAxes() { return graphAxes; }
    public void setGraphAxes(Map<String, Integer> graphAxes) { this.graphAxes = graphAxes; }
}
