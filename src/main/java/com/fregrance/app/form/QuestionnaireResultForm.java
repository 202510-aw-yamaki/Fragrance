package com.fregrance.app.form;

import java.util.LinkedHashMap;
import java.util.Map;

public class QuestionnaireResultForm {

    private String routeCode;
    private Map<String, String> step1Answers = new LinkedHashMap<>();
    private Map<String, String> step2Answers = new LinkedHashMap<>();
    private Map<String, Integer> graphAxes = new LinkedHashMap<>();

    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }
    public Map<String, String> getStep1Answers() { return step1Answers; }
    public void setStep1Answers(Map<String, String> step1Answers) { this.step1Answers = step1Answers; }
    public Map<String, String> getStep2Answers() { return step2Answers; }
    public void setStep2Answers(Map<String, String> step2Answers) { this.step2Answers = step2Answers; }
    public Map<String, Integer> getGraphAxes() { return graphAxes; }
    public void setGraphAxes(Map<String, Integer> graphAxes) { this.graphAxes = graphAxes; }
}