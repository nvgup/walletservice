package com.simplecasino.walletservice.service;

import com.simplecasino.walletservice.model.Player;

import java.math.BigDecimal;
import java.util.Optional;

public interface WalletService {

    Player registerPlayer(Long playerId);

    Optional<Player> updateBalance(Long playerId, BigDecimal amount);

    Optional<Player> findById(Long id);
}