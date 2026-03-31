package com.fregrance.app;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
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
    void configuredStaffCredentialsCanLogin() throws Exception {
        mockMvc.perform(post("/staff/login")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("username", "rohera")
                .param("password", "Staff"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/staff/reservations"));
    }

    @Test
    void staffReservationsRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/staff/reservations"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/staff/login"));
    }

    @Test
    void invalidCredentialsRedirectBackToLogin() throws Exception {
        mockMvc.perform(post("/staff/login")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .param("username", "rohera")
                .param("password", "wrong"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/staff/login?error"));
    }

    @Test
    @WithMockUser(username = "rohera", roles = "STAFF")
    void authenticatedStaffCanOpenStaffReservations() throws Exception {
        mockMvc.perform(get("/staff/reservations"))
            .andExpect(status().isOk());
    }
}