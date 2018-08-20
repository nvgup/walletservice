package com.simplecasino.walletservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecasino.walletservice.dao.WalletDao;
import com.simplecasino.walletservice.dto.RegisterPlayerRequest;
import com.simplecasino.walletservice.dto.UpdateBalanceRequest;
import com.simplecasino.walletservice.exception.InsufficientBalanceException;
import com.simplecasino.walletservice.model.Balance;
import com.simplecasino.walletservice.model.Player;
import org.junit.Before;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;

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

    @Test
    public void registerPlayer_ifIdNotExist_thenReturnPlayerBalanceZero() throws Exception {
        //Player player = new Player(4L, new Balance());
        RegisterPlayerRequest registerPlayerRequest = new RegisterPlayerRequest();
        registerPlayerRequest.setPlayerId(new Random().nextLong());

        mvc.perform(
                post("/wallet/player")
                        .content(asJsonString(registerPlayerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance", is(0)));

        walletDao.deleteById(registerPlayerRequest.getPlayerId());
    }

    @Test
    public void registerPlayer_ifIdAlreadyExist_thenReturnHttpStatus409() throws Exception {
        Long playerId = 1L;
        walletDao.save(new Player(playerId, new Balance()));

        RegisterPlayerRequest registerPlayerRequest = new RegisterPlayerRequest();
        registerPlayerRequest.setPlayerId(playerId);

        mvc.perform(
                post("/wallet/player")
                        .content(asJsonString(registerPlayerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(String.format("Player with id '%s' already registered", playerId))));

        walletDao.deleteById(registerPlayerRequest.getPlayerId());
    }

    @Test
    public void updateBalance_ifPlayerNotFoundById_thenReturnHttpStatus404() throws Exception {
        Long playerId = 1L;

        UpdateBalanceRequest updateBalanceRequest = new UpdateBalanceRequest();
        updateBalanceRequest.setBalance(BigDecimal.ONE);

        mvc.perform(
                put(String.format("player/%s/balance", playerId))
                        .content(asJsonString(updateBalanceRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                //.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(String.format("Player with id '%s' not found", playerId))));
    }

    /*@Test
    public void updateBalance_ifPlayerFoundByIdAndPositiveUpdatedBalance_thenReturnPlayerWithUpdatedBalance() {
        Player player = new Player(TEST_ID, new Balance());

        when(walletDao.findById(TEST_ID)).thenReturn(Optional.of(player));
        Optional<Player> updatedPlayer = walletService.updateBalance(TEST_ID, BigDecimal.ONE);

        assertTrue(updatedPlayer.isPresent());
        assertEquals(BigDecimal.ONE, player.getBalance().getAmount());
    }

    @Test
    public void updateBalance_ifPlayerFoundByIdAndNegativeUpdatedBalanceAndEnoughFunds_thenReturnPlayerWithUpdatedBalance() {
        Player player = new Player(TEST_ID, new Balance(BigDecimal.TEN));

        when(walletDao.findById(TEST_ID)).thenReturn(Optional.of(player));
        Optional<Player> updatedPlayer = walletService.updateBalance(TEST_ID, BigDecimal.valueOf(-4));

        assertTrue(updatedPlayer.isPresent());
        assertEquals(BigDecimal.valueOf(6), player.getBalance().getAmount());
    }

    @Test(expected = InsufficientBalanceException.class)
    public void updateBalance_ifPlayerFoundByIdAndNegativeUpdatedBalanceAndNotEnoughFunds_thenThrowInsufficientBalanceException() {
        Player player = new Player(TEST_ID, new Balance(BigDecimal.ONE));

        when(walletDao.findById(TEST_ID)).thenReturn(Optional.of(player));
        walletService.updateBalance(TEST_ID, BigDecimal.valueOf(-4));
    }*/

    private String asJsonString(Object obj) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    private <T> T fromJsonString(String value, Class<T> clazz) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(value, clazz);
    }
}
