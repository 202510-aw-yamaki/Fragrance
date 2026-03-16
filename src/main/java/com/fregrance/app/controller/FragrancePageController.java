package com.fregrance.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FragrancePageController {

    @GetMapping({"/", "/index.html"})
    public String showTop() {
        return "index";
    }

    @GetMapping({"/questionnaire", "/questionnaire.html"})
    public String showQuestionnaireStep1() {
        return "questionnaire";
    }

    @GetMapping({"/questionnaire/step2", "/questionnaire_step2.html"})
    public String showQuestionnaireStep2() {
        return "questionnaire_step2";
    }

    @GetMapping({"/graph", "/fragrance-graph.html"})
    public String showGraph() {
        return "fragrance-graph";
    }

    @GetMapping({"/reservation", "/reservation.html"})
    public String showReservation() {
        return "reservation";
    }

    @GetMapping({"/reservation/complete", "/reservation-complete.html"})
    public String showReservationComplete() {
        return "reservation-complete";
    }
}
