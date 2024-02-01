package com.heromakers.app.minutes.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heromakers.app.minutes.model.AccountModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getAccountList() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/api/account")
                        .param("useFlag", "1")
                        .param("humanName", "admin")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(result -> {
                    System.out.println("result = " + result.getResponse().getContentAsString());
                });
    }

    @Test
    void accountList() throws Exception {
        String requestBody = "query { accountList { humanName } }";
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .post("/graphql")
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsBytes(requestBody))
//                                .contentType(MediaType.APPLICATION_GRAPHQL_RESPONSE)
                                .characterEncoding("UTF-8")
                )
//                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(result -> {
                    System.out.println("result = " + result.getResponse().getContentAsString());
                });
//.andExpect {
//MockMvcResultMatchers.request().asyncStarted()
//MockMvcResultMatchers.request().asyncResult(CoreMatchers.notNullValue())
//}.andReturn()
    }

    @Test
    void insertAccount() throws Exception {
        AccountModel account = AccountModel.builder()
                .accountKey("admin")
                .humanName("admin")
                .password("admin")
                .phone("010-1234-5678")
                .address("서울시 강남구")
                .build();
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/account")
                .content(objectMapper.writeValueAsString(account))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(result -> {
                    System.out.println("result = " + result.getResponse().getContentAsString());
                });
    }
}
