package com.fregrance.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuestionnaireApiTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM questionnaire_results WHERE route_code = ?", "test-route");
    }

    @Test
    void createQuestionnaireResultPersistsToDatabase() {
        Map<String, Object> request = buildRequest();

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "/api/questionnaire-results",
            request,
            Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsEntry("routeCode", "test-route");
        assertThat(response.getBody()).containsKey("resultCode");

        String resultCode = String.valueOf(response.getBody().get("resultCode"));
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM questionnaire_results WHERE result_code = ?",
            Integer.class,
            resultCode
        );

        assertThat(count).isEqualTo(1);
    }

    @Test
    void getQuestionnaireResultReturnsPersistedPayload() {
        Map<String, Object> request = buildRequest();
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(
            "/api/questionnaire-results",
            request,
            Map.class
        );

        String resultCode = String.valueOf(createResponse.getBody().get("resultCode"));
        ResponseEntity<Map> getResponse = restTemplate.getForEntity(
            "/api/questionnaire-results/" + resultCode,
            Map.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).containsEntry("resultCode", resultCode);
        assertThat(getResponse.getBody()).containsEntry("routeCode", "test-route");

        Map<String, String> step1Answers = (Map<String, String>) getResponse.getBody().get("step1Answers");
        Map<String, String> step2Answers = (Map<String, String>) getResponse.getBody().get("step2Answers");
        Map<String, Integer> graphAxes = (Map<String, Integer>) getResponse.getBody().get("graphAxes");

        assertThat(step1Answers).containsEntry("Q1", "alpha");
        assertThat(step2Answers).containsEntry("Q6", "delta");
        assertThat(graphAxes).containsEntry("floral", 72);
        assertThat(graphAxes).containsEntry("woody", 36);
    }

    @Test
    void createQuestionnaireResultReturnsBadRequestWhenRequiredFieldsAreMissing() {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("routeCode", "");
        request.put("step1Answers", Map.of());
        request.put("step2Answers", Map.of());
        request.put("graphAxes", Map.of());

        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/questionnaire-results",
            request,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("VALIDATION_ERROR");
        assertThat(response.getBody()).contains("routeCode");
        assertThat(response.getBody()).contains("step1Answers");
        assertThat(response.getBody()).contains("step2Answers");
        assertThat(response.getBody()).contains("graphAxes");
    }

    @Test
    void createQuestionnaireResultReturnsBadRequestWhenGraphAxisIsOutOfRange() {
        Map<String, Object> request = buildRequest();
        @SuppressWarnings("unchecked")
        Map<String, Integer> graphAxes = (Map<String, Integer>) request.get("graphAxes");
        graphAxes.put("floral", 120);

        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/questionnaire-results",
            request,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("VALIDATION_ERROR");
        assertThat(response.getBody()).contains("graphAxes[floral]");
    }

    private Map<String, Object> buildRequest() {
        Map<String, String> step1Answers = new LinkedHashMap<>();
        step1Answers.put("Q1", "alpha");
        step1Answers.put("Q2", "beta");

        Map<String, String> step2Answers = new LinkedHashMap<>();
        step2Answers.put("Q6", "delta");
        step2Answers.put("Q7", "epsilon");

        Map<String, Integer> graphAxes = new LinkedHashMap<>();
        graphAxes.put("floral", 72);
        graphAxes.put("fresh", 61);
        graphAxes.put("woody", 36);
        graphAxes.put("spicy", 24);
        graphAxes.put("sweet", 48);

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("routeCode", "test-route");
        request.put("step1Answers", step1Answers);
        request.put("step2Answers", step2Answers);
        request.put("graphAxes", graphAxes);
        return request;
    }
}
