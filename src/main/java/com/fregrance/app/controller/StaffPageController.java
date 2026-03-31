package com.fregrance.app.controller;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fregrance.app.dto.StaffReservationDetail;
import com.fregrance.app.dto.StaffReservationSummary;
import com.fregrance.app.service.StaffReservationService;

@Controller
@RequestMapping("/staff")
public class StaffPageController {

    private static final List<Integer> PAGE_SIZE_OPTIONS = List.of(10, 20, 30);

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
    public String showReservationList(
        @RequestParam(required = false) String missing,
        @RequestParam(required = false) String visitType,
        @RequestParam(required = false) String instructor,
        @RequestParam(defaultValue = "visitDate") String sort,
        @RequestParam(defaultValue = "asc") String dir,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        Model model) {
        List<StaffReservationSummary> allReservations = staffReservationService.findAllReservations();
        List<StaffReservationSummary> filteredReservations = staffReservationService.findReservations(visitType, instructor, sort, dir);

        int pageSize = PAGE_SIZE_OPTIONS.contains(size) ? size : 10;
        int totalItems = filteredReservations.size();
        int totalPages = totalItems == 0 ? 1 : (int) Math.ceil((double) totalItems / pageSize);
        int currentPage = Math.min(Math.max(page, 1), totalPages);
        int fromIndex = Math.min((currentPage - 1) * pageSize, totalItems);
        int toIndex = Math.min(fromIndex + pageSize, totalItems);
        List<StaffReservationSummary> pageItems = filteredReservations.subList(fromIndex, toIndex);

        model.addAttribute("reservations", pageItems);
        model.addAttribute("missingReservationCode", missing);
        model.addAttribute("visitTypeOptions", staffReservationService.findVisitTypeOptions());
        model.addAttribute("instructorOptions", staffReservationService.findInstructorOptions());
        model.addAttribute("selectedVisitType", visitType == null ? "" : visitType);
        model.addAttribute("selectedInstructor", instructor == null ? "" : instructor);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("page", currentPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("pageSizeOptions", PAGE_SIZE_OPTIONS);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("pageNumbers", IntStream.rangeClosed(1, totalPages).boxed().toList());
        model.addAttribute("hasPrev", currentPage > 1);
        model.addAttribute("hasNext", currentPage < totalPages);
        model.addAttribute("prevPage", currentPage - 1);
        model.addAttribute("nextPage", currentPage + 1);
        model.addAttribute("todayReservationCount", staffReservationService.countTodayReservations());
        model.addAttribute("linkedReservationCount", (int) allReservations.stream().filter(item -> item.questionnaireResultCode() != null).count());
        model.addAttribute("uncheckedReservationCount", (int) allReservations.stream().filter(item -> item.questionnaireResultCode() == null).count());
        model.addAttribute("allReservationCount", allReservations.size());
        model.addAttribute("currentSortLabel", currentSortLabel(sort));
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

    private String currentSortLabel(String sort) {
        return switch (sort) {
            case "reservationCode" -> "予約コード";
            case "visitType" -> "来店種別";
            case "guestCount" -> "人数";
            case "instructor" -> "担当";
            case "reservationCreated" -> "予約日時";
            case "slotStatus" -> "状態";
            default -> "来店日時";
        };
    }

    private boolean isStaffAuthenticated(Authentication authentication) {
        return authentication != null
            && authentication.isAuthenticated()
            && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_STAFF".equals(authority.getAuthority()));
    }
}