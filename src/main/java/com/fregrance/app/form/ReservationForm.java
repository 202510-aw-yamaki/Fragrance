package com.fregrance.app.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ReservationForm {

    @NotBlank(message = "slotId is required")
    private String slotId;

    @NotBlank(message = "slotLabel is required")
    private String slotLabel;

    @NotBlank(message = "visitType is required")
    private String visitType;

    @NotBlank(message = "guestCount is required")
    private String guestCount;

    @Size(max = 500, message = "staffMemo must be 500 characters or less")
    private String staffMemo;

    private String summaryHeadline;

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getSlotLabel() {
        return slotLabel;
    }

    public void setSlotLabel(String slotLabel) {
        this.slotLabel = slotLabel;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public String getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(String guestCount) {
        this.guestCount = guestCount;
    }

    public String getStaffMemo() {
        return staffMemo;
    }

    public void setStaffMemo(String staffMemo) {
        this.staffMemo = staffMemo;
    }

    public String getSummaryHeadline() {
        return summaryHeadline;
    }

    public void setSummaryHeadline(String summaryHeadline) {
        this.summaryHeadline = summaryHeadline;
    }
}
