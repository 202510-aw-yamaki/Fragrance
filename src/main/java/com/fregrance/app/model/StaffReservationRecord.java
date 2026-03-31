package com.fregrance.app.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class StaffReservationRecord {

    private String reservationCode;
    private Long reservationSlotId;
    private String visitTypeLabel;
    private Integer guestCount;
    private String staffMemo;
    private String summaryHeadline;
    private String questionnaireResultCode;
    private String slotLabel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate slotDate;
    private LocalTime slotTime;
    private String slotStatus;
    private String instructorName;
    private String routeCode;
    private String step1AnswersJson;
    private String step2AnswersJson;
    private String graphAxesJson;

    public String getReservationCode() { return reservationCode; }
    public void setReservationCode(String reservationCode) { this.reservationCode = reservationCode; }
    public Long getReservationSlotId() { return reservationSlotId; }
    public void setReservationSlotId(Long reservationSlotId) { this.reservationSlotId = reservationSlotId; }
    public String getVisitTypeLabel() { return visitTypeLabel; }
    public void setVisitTypeLabel(String visitTypeLabel) { this.visitTypeLabel = visitTypeLabel; }
    public Integer getGuestCount() { return guestCount; }
    public void setGuestCount(Integer guestCount) { this.guestCount = guestCount; }
    public String getStaffMemo() { return staffMemo; }
    public void setStaffMemo(String staffMemo) { this.staffMemo = staffMemo; }
    public String getSummaryHeadline() { return summaryHeadline; }
    public void setSummaryHeadline(String summaryHeadline) { this.summaryHeadline = summaryHeadline; }
    public String getQuestionnaireResultCode() { return questionnaireResultCode; }
    public void setQuestionnaireResultCode(String questionnaireResultCode) { this.questionnaireResultCode = questionnaireResultCode; }
    public String getSlotLabel() { return slotLabel; }
    public void setSlotLabel(String slotLabel) { this.slotLabel = slotLabel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDate getSlotDate() { return slotDate; }
    public void setSlotDate(LocalDate slotDate) { this.slotDate = slotDate; }
    public LocalTime getSlotTime() { return slotTime; }
    public void setSlotTime(LocalTime slotTime) { this.slotTime = slotTime; }
    public String getSlotStatus() { return slotStatus; }
    public void setSlotStatus(String slotStatus) { this.slotStatus = slotStatus; }
    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }
    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }
    public String getStep1AnswersJson() { return step1AnswersJson; }
    public void setStep1AnswersJson(String step1AnswersJson) { this.step1AnswersJson = step1AnswersJson; }
    public String getStep2AnswersJson() { return step2AnswersJson; }
    public void setStep2AnswersJson(String step2AnswersJson) { this.step2AnswersJson = step2AnswersJson; }
    public String getGraphAxesJson() { return graphAxesJson; }
    public void setGraphAxesJson(String graphAxesJson) { this.graphAxesJson = graphAxesJson; }
}
