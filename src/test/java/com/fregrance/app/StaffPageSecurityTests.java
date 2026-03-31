package com.fregrance.app;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class StaffPageSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void staffLoginPageIsPublic() throws Exception {
        mockMvc.perform(get("/staff/login"))
            .andExpect(status().isOk());
    }

    @Test
    void staffReservationsRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/staff/reservations"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/staff/login"));
    }

    @Test
    void nonStaffUserCannotOpenStaffReservations() throws Exception {
        mockMvc.perform(get("/staff/reservations").with(user("viewer").roles("USER")))
            .andExpect(status().isForbidden());
    }

    @Test
    void staffRoleCanOpenStaffReservations() throws Exception {
        mockMvc.perform(get("/staff/reservations").with(user("staff").roles("STAFF")))
            .andExpect(status().isOk());
    }
}
