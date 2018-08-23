package com.simplecasino.walletservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecasino.walletservice.dao.WalletDao;
import com.simplecasino.walletservice.dto.RegisterPlayerRequest;
import com.simplecasino.walletservice.dto.UpdateBalanceRequest;
import com.simplecasino.walletservice.exception.RestApiException;
import com.simplecasino.walletservice.model.Balance;
import com.simplecasino.walletservice.model.Player;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = WalletServiceApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-integrationtest.yaml")
public class WalletControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private WalletDao walletDao;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void registerPlayer_ifIdNotExist_thenReturnHttpStatus200AndPlayerBalanceZero() throws Exception {
        RegisterPlayerRequest registerPlayerRequest = new RegisterPlayerRequest();
        registerPlayerRequest.setPlayerId(new Random().nextLong());

        mvc.perform(
                post("/player")
                        .content(asJsonString(registerPlayerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance", is(0)));
    }

    @Test
    public void registerPlayer_ifIdAlreadyExist_thenReturnHttpStatus409() throws Exception {
        Long playerId = getId();
        walletDao.save(new Player(playerId, new Balance()));

        RegisterPlayerRequest registerPlayerRequest = new RegisterPlayerRequest();
        registerPlayerRequest.setPlayerId(playerId);

        mvc.perform(
                post("/player")
                        .content(asJsonString(registerPlayerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(HttpStatus.CONFLICT.value())))
                .andExpect(jsonPath("$.message", is(RestApiException.Type.PLAYER_ALREADY_EXIST.getMessage())))
                .andExpect(jsonPath("$.code", is(RestApiException.Type.PLAYER_ALREADY_EXIST.getCode())));
    }

    @Test
    public void updateBalance_ifPlayerNotFoundById_thenReturnHttpStatus404() throws Exception {
        Long playerId = getId();

        UpdateBalanceRequest updateBalanceRequest = new UpdateBalanceRequest();
        updateBalanceRequest.setBalance(BigDecimal.ONE);

        mvc.perform(
                put(String.format("/player/%s/balance", playerId))
                        .content(asJsonString(updateBalanceRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(RestApiException.Type.PLAYER_NOT_FOUND.getMessage())))
                .andExpect(jsonPath("$.code", is(RestApiException.Type.PLAYER_NOT_FOUND.getCode())));
    }

    @Test
    public void updateBalance_ifPlayerFoundByIdAndPositiveUpdatedBalance_thenReturnHttpStatus200AndUpdatedBalance() throws Exception {
        Long playerId = getId();
        walletDao.save(new Player(playerId, new Balance()));

        UpdateBalanceRequest updateBalanceRequest = new UpdateBalanceRequest();
        updateBalanceRequest.setBalance(BigDecimal.TEN);

        mvc.perform(
                put(String.format("/player/%s/balance", playerId))
                        .content(asJsonString(updateBalanceRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance", is(BigDecimal.TEN.doubleValue())));
    }

    @Test
    public void updateBalance_ifPlayerFoundByIdAndNegativeUpdatedBalanceAndNotEnoughFunds_thenReturnHttpStatus409AndCurrentBalance() throws Exception {
        Long playerId = getId();
        walletDao.save(new Player(playerId, new Balance(BigDecimal.ONE)));

        UpdateBalanceRequest updateBalanceRequest = new UpdateBalanceRequest();
        updateBalanceRequest.setBalance(BigDecimal.valueOf(-2));

        mvc.perform(
                put(String.format("/player/%s/balance", playerId))
                        .content(asJsonString(updateBalanceRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Insufficient funds")))
                .andExpect(jsonPath("$.additionalInfo.balance", is(BigDecimal.ONE.doubleValue())));
    }

    @Test
    public void updateBalance_ifPlayerFoundByIdAndNegativeUpdatedBalanceAndEnoughFunds_thenReturnHttpStatus200AndUpdatedBalance() throws Exception {
        Long playerId = getId();
        walletDao.save(new Player(playerId, new Balance(BigDecimal.TEN)));

        UpdateBalanceRequest updateBalanceRequest = new UpdateBalanceRequest();
        updateBalanceRequest.setBalance(BigDecimal.valueOf(-2));

        mvc.perform(
                put(String.format("/player/%s/balance", playerId))
                        .content(asJsonString(updateBalanceRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance", is(BigDecimal.valueOf(8).doubleValue())));
    }

    @Test
    public void getBalance_ifPlayerFoundById_theReturnHttpStatus200AndPlayerBalance() throws Exception {
        Long playerId = getId();
        walletDao.save(new Player(playerId, new Balance(BigDecimal.TEN)));

        mvc.perform(
                get(String.format("/player/%s/balance", playerId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance", is(BigDecimal.TEN.doubleValue())));
    }

    @Test
    public void getBalance_ifPlayerNotFoundById_theReturnHttpStatus404() throws Exception {
        mvc.perform(
                get(String.format("/player/%s/balance", getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.message", is(RestApiException.Type.PLAYER_NOT_FOUND.getMessage())))
                .andExpect(jsonPath("$.code", is(RestApiException.Type.PLAYER_NOT_FOUND.getCode())));
    }

    @After
    public void cleanDb() {
        walletDao.deleteAll();
    }

    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private Long getId() {
        return new Random().nextLong();
    }
}
