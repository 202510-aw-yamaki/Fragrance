package com.fregrance.app.model;
import java.time.LocalDateTime;
public class Reservation {
    private Long id;
    private String reservationCode;
    private Long reservationSlotId;
    private Long visitTypeId;
    private String visitTypeLabel;
    private Integer guestCount;
    private String staffMemo;
    private String summaryHeadline;
    private String slotLabel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReservationCode() { return reservationCode; }
    public void setReservationCode(String reservationCode) { this.reservationCode = reservationCode; }
    public Long getReservationSlotId() { return reservationSlotId; }
    public void setReservationSlotId(Long reservationSlotId) { this.reservationSlotId = reservationSlotId; }
    public Long getVisitTypeId() { return visitTypeId; }
    public void setVisitTypeId(Long visitTypeId) { this.visitTypeId = visitTypeId; }
    public String getVisitTypeLabel() { return visitTypeLabel; }
    public void setVisitTypeLabel(String visitTypeLabel) { this.visitTypeLabel = visitTypeLabel; }
    public Integer getGuestCount() { return guestCount; }
    public void setGuestCount(Integer guestCount) { this.guestCount = guestCount; }
    public String getStaffMemo() { return staffMemo; }
    public void setStaffMemo(String staffMemo) { this.staffMemo = staffMemo; }
    public String getSummaryHeadline() { return summaryHeadline; }
    public void setSummaryHeadline(String summaryHeadline) { this.summaryHeadline = summaryHeadline; }
    public String getSlotLabel() { return slotLabel; }
    public void setSlotLabel(String slotLabel) { this.slotLabel = slotLabel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
