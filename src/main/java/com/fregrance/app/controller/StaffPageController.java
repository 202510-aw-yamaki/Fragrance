package com.fregrance.app.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fregrance.app.dto.StaffReservationDetail;
import com.fregrance.app.service.StaffReservationService;

@Controller
@RequestMapping("/staff")
public class StaffPageController {

    private final StaffReservationService staffReservationService;

    public StaffPageController(StaffReservationService staffReservationService) {
        this.staffReservationService = staffReservationService;
    }

    @GetMapping
    public String redirectToReservations() {
        return "redirect:/staff/reservations";
    }

    @GetMapping("/login")
    public String showLogin(Authentication authentication, Model model) {
        if (isStaffAuthenticated(authentication)) {
            return "redirect:/staff/reservations";
        }
        model.addAttribute("todayReservationCount", staffReservationService.countTodayReservations());
        model.addAttribute("allReservationCount", staffReservationService.countAllReservations());
        return "staff-login";
    }

    @GetMapping("/reservations")
    public String showReservationList(@RequestParam(required = false) String missing, Model model) {
        model.addAttribute("reservations", staffReservationService.findAllReservations());
        model.addAttribute("missingReservationCode", missing);
        return "staff-reservations";
    }

    @GetMapping("/reservations/{reservationCode}")
    public String showReservationDetail(@PathVariable String reservationCode, RedirectAttributes redirectAttributes, Model model) {
        StaffReservationDetail reservation = staffReservationService.findReservationDetail(reservationCode);
        if (reservation == null) {
            redirectAttributes.addAttribute("missing", reservationCode);
            return "redirect:/staff/reservations";
        }

        model.addAttribute("reservation", reservation);
        return "staff-reservation-detail";
    }

    private boolean isStaffAuthenticated(Authentication authentication) {
        return authentication != null
            && authentication.isAuthenticated()
            && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_STAFF".equals(authority.getAuthority()));
    }
}