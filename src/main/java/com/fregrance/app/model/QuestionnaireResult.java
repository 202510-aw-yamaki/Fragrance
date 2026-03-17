package com.fregrance.app.model;

import java.time.LocalDateTime;

public class QuestionnaireResult {

    private Long id;
    private String resultCode;
    private String routeCode;
    private String step1AnswersJson;
    private String step2AnswersJson;
    private String graphAxesJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getResultCode() { return resultCode; }
    public void setResultCode(String resultCode) { this.resultCode = resultCode; }
    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }
    public String getStep1AnswersJson() { return step1AnswersJson; }
    public void setStep1AnswersJson(String step1AnswersJson) { this.step1AnswersJson = step1AnswersJson; }
    public String getStep2AnswersJson() { return step2AnswersJson; }
    public void setStep2AnswersJson(String step2AnswersJson) { this.step2AnswersJson = step2AnswersJson; }
    public String getGraphAxesJson() { return graphAxesJson; }
    public void setGraphAxesJson(String graphAxesJson) { this.graphAxesJson = graphAxesJson; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}