package com.simplecasino.walletservice;

import com.simplecasino.walletservice.dao.WalletDao;
import com.simplecasino.walletservice.exception.InsufficientBalanceException;
import com.simplecasino.walletservice.exception.PlayerAlreadyExistException;
import com.simplecasino.walletservice.model.Balance;
import com.simplecasino.walletservice.model.Player;
import com.simplecasino.walletservice.service.WalletServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WalletServiceImplTest {

    private static final Long TEST_ID = 6L;

    @Mock
    private WalletDao walletDao;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Test
    public void registerPlayer_ifIdNotExist_thenReturnSavedPlayer() {
        when(walletDao.existsById(TEST_ID)).thenReturn(false);

        Player player = new Player(TEST_ID, new Balance());
        when(walletDao.save(any(Player.class))).thenReturn(player);

        Player returnedPlayer = walletService.registerPlayer(TEST_ID);
        assertEquals(player, returnedPlayer);
    }

    @Test(expected = PlayerAlreadyExistException.class)
    public void registerPlayer_ifIdAlreadyExist_thenThrowPlayerAlreadyExistException() {
        when(walletDao.existsById(TEST_ID)).thenReturn(true);
        walletService.registerPlayer(TEST_ID);
    }

    @Test
    public void updateBalance_ifPlayerNotFoundById_thenReturnEmptyPlayer() {
        when(walletDao.findById(TEST_ID)).thenReturn(Optional.empty());
        Optional<Player> updatedPlayer = walletService.updateBalance(TEST_ID, BigDecimal.ONE);

        assertFalse(updatedPlayer.isPresent());
    }

    @Test
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
    }

    @Test
    public void findById_ifPlayerNotFoundById_thenReturnEmptyPlayer() {
        when(walletDao.findById(TEST_ID)).thenReturn(Optional.empty());
        Optional<Player> returnedPlayer = walletService.findById(TEST_ID);

        assertFalse(returnedPlayer.isPresent());
    }

    @Test
    public void findById_ifPlayerFoundById_thenReturnFoundPlayer() {
        Player player = new Player(TEST_ID, new Balance(BigDecimal.ONE));
        when(walletDao.findById(TEST_ID)).thenReturn(Optional.of(player));

        Optional<Player> returnedPlayer = walletService.findById(TEST_ID);

        assertEquals(player, returnedPlayer.get());
    }
}