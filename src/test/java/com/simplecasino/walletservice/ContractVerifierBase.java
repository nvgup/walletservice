package com.simplecasino.walletservice;

import com.simplecasino.walletservice.dao.WalletDao;
import com.simplecasino.walletservice.model.Balance;
import com.simplecasino.walletservice.model.Player;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = WalletServiceApplication.class)
@DirtiesContext
@AutoConfigureMessageVerifier
public class ContractVerifierBase {

    @Autowired
    private WalletController walletController;

    @Autowired
    private WalletDao walletDao;

    @Before
    public void setup() {
        StandaloneMockMvcBuilder standaloneMockMvcBuilder = MockMvcBuilders.standaloneSetup(walletController);
        RestAssuredMockMvc.standaloneSetup(standaloneMockMvcBuilder);

        walletDao.save(new Player(1L, new Balance(BigDecimal.TEN)));
    }

    @After
    public void clear() {
        walletDao.deleteAll();
    }
}
