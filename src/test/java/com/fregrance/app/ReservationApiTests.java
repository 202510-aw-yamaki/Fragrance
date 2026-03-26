package com.fregrance.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReservationApiTests {

    private static final LocalDate SLOT_API_TEST_DATE = LocalDate.of(2099, 12, 31);
    private static final LocalDate RESERVATION_TEST_DATE = LocalDate.of(2099, 12, 30);
    private static final LocalDate EMPTY_SLOT_TEST_DATE = LocalDate.of(2099, 12, 29);
    private static final Pattern RESERVATION_CODE_PATTERN = Pattern.compile("\\\"reservationCode\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM reservations WHERE reservation_slot_id IN (SELECT id FROM reservation_slots WHERE slot_date IN (?, ?, ?))",
            RESERVATION_TEST_DATE, SLOT_API_TEST_DATE, EMPTY_SLOT_TEST_DATE);
        jdbcTemplate.update("DELETE FROM reservation_slots WHERE slot_date IN (?, ?, ?)", RESERVATION_TEST_DATE, SLOT_API_TEST_DATE, EMPTY_SLOT_TEST_DATE);
        jdbcTemplate.update("DELETE FROM questionnaire_results WHERE route_code = ?", "reservation-test");
    }

    @Test
    void reservationSlotsEndpointReturnsDatabaseDataWhenSlotsExist() {
        jdbcTemplate.update("DELETE FROM reservation_slots WHERE slot_date = ?", SLOT_API_TEST_DATE);
        jdbcTemplate.update(
            "INSERT INTO reservation_slots (slot_date, slot_time, status, instructor_name, created_at, updated_at) VALUES (?, '18:00:00', 'open', 'DbVerifier', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
            SLOT_API_TEST_DATE
        );

        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/reservation-slots?date=" + SLOT_API_TEST_DATE,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("2099-12-31_1800");
        assertThat(response.getBody()).contains("DbVerifier");
        assertThat(response.getBody()).doesNotContain("2099-12-31_1030");
    }

    @Test
    void reservationSlotsEndpointReturnsEmptyArrayWhenNoSlotsExist() {
        jdbcTemplate.update("DELETE FROM reservation_slots WHERE slot_date = ?", EMPTY_SLOT_TEST_DATE);

        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/reservation-slots?date=" + EMPTY_SLOT_TEST_DATE,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("[]");
    }

    @Test
    void createReservationPersistsToDatabaseWhenSlotExists() {
        prepareReservationSlot("DbPersist");

        String reservationCode = createReservationAndExtractCode(null);
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM reservations WHERE reservation_code = ?",
            Integer.class,
            reservationCode
        );

        assertThat(count).isEqualTo(1);
    }

    @Test
    void createReservationPersistsQuestionnaireResultCodeWhenProvided() {
        prepareReservationSlot("DbQuestionnaire");
        jdbcTemplate.update(
            "INSERT INTO questionnaire_results (result_code, route_code, step1_answers_json, step2_answers_json, graph_axes_json, created_at, updated_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
            "QR-LINKED1",
            "reservation-test",
            "{\"Q1\":\"alpha\"}",
            "{\"Q6\":\"beta\"}",
            "{\"floral\":70}"
        );

        String reservationCode = createReservationAndExtractCode("QR-LINKED1");
        String linkedCode = jdbcTemplate.queryForObject(
            "SELECT questionnaire_result_code FROM reservations WHERE reservation_code = ?",
            String.class,
            reservationCode
        );

        assertThat(linkedCode).isEqualTo("QR-LINKED1");
    }

    @Test
    void getReservationReturnsPersistedReservationForCompletePageReload() {
        prepareReservationSlot("DbReload");

        String reservationCode = createReservationAndExtractCode(null);
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/reservations/" + reservationCode,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(reservationCode);
        assertThat(response.getBody()).contains("12/30 10:30");
        assertThat(response.getBody()).contains("初回ワークショップ");
        assertThat(response.getBody()).contains("1名");
    }

    @Test
    void createReservationReturnsBadRequestWhenRequiredFieldsAreMissing() {
        Map<String, String> request = Map.of(
            "slotId", "",
            "slotLabel", "",
            "visitType", "初回ワークショップ",
            "guestCount", ""
        );

        ResponseEntity<String> response = restTemplate.postForEntity("/api/reservations", jsonRequest(request), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("VALIDATION_ERROR");
        assertThat(response.getBody()).contains("slotId");
        assertThat(response.getBody()).contains("slotLabel");
        assertThat(response.getBody()).contains("guestCount");
    }

    @Test
    void createReservationReturnsBadRequestWhenStaffMemoExceedsLimit() {
        Map<String, String> request = Map.of(
            "slotId", "2099-12-30_1030",
            "slotLabel", "12/30 10:30",
            "visitType", "初回ワークショップ",
            "guestCount", "1名",
            "staffMemo", "a".repeat(501)
        );

        ResponseEntity<String> response = restTemplate.postForEntity("/api/reservations", jsonRequest(request), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("VALIDATION_ERROR");
        assertThat(response.getBody()).contains("staffMemo");
        assertThat(response.getBody()).contains("500 characters or less");
    }

    @Test
    void createReservationReturnsNotFoundWhenSlotDoesNotExist() {
        Map<String, String> request = Map.of(
            "slotId", "2099-12-28_1030",
            "slotLabel", "12/28 10:30",
            "visitType", "初回ワークショップ",
            "guestCount", "1名",
            "staffMemo", "存在しない枠"
        );

        ResponseEntity<String> response = restTemplate.postForEntity("/api/reservations", jsonRequest(request), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("NOT_FOUND");
    }

    @Test
    void createReservationReturnsConflictWhenSameSlotIsReservedTwice() {
        prepareReservationSlot("DbConflict");

        String reservationCode = createReservationAndExtractCode(null);
        assertThat(reservationCode).isNotBlank();

        Map<String, String> secondRequest = Map.of(
            "slotId", "2099-12-30_1030",
            "slotLabel", "12/30 10:30",
            "visitType", "初回ワークショップ",
            "guestCount", "1名",
            "staffMemo", "重複予約"
        );

        ResponseEntity<String> secondResponse = restTemplate.postForEntity("/api/reservations", jsonRequest(secondRequest), String.class);

        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(secondResponse.getBody()).contains("RESERVATION_CONFLICT");
    }

    private void prepareReservationSlot(String instructorName) {
        jdbcTemplate.update("DELETE FROM reservation_slots WHERE slot_date = ?", RESERVATION_TEST_DATE);
        jdbcTemplate.update(
            "INSERT INTO reservation_slots (slot_date, slot_time, status, instructor_name, created_at, updated_at) VALUES (?, '10:30:00', 'open', ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
            RESERVATION_TEST_DATE,
            instructorName
        );
    }

    private String createReservationAndExtractCode(String questionnaireResultCode) {
        Map<String, Object> request = new java.util.LinkedHashMap<>();
        request.put("slotId", "2099-12-30_1030");
        request.put("slotLabel", "12/30 10:30");
        request.put("visitType", "初回ワークショップ");
        request.put("guestCount", "1名");
        request.put("staffMemo", "テスト予約");
        if (questionnaireResultCode != null) {
            request.put("questionnaireResultCode", questionnaireResultCode);
        }

        ResponseEntity<String> response = restTemplate.postForEntity("/api/reservations", jsonRequest(request), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return extractReservationCode(response.getBody());
    }

    private HttpEntity<Map<String, ?>> jsonRequest(Map<String, ?> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    private String extractReservationCode(String responseBody) {
        Matcher matcher = RESERVATION_CODE_PATTERN.matcher(responseBody);
        assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }
}