package com.simplecasino.walletservice.service;

import com.simplecasino.walletservice.dao.WalletDao;
import com.simplecasino.walletservice.exception.InsufficientBalanceException;
import com.simplecasino.walletservice.exception.PlayerAlreadyExistException;
import com.simplecasino.walletservice.model.Balance;
import com.simplecasino.walletservice.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService {

    private WalletDao walletDao;

    @Autowired
    public WalletServiceImpl(WalletDao walletDao) {
        this.walletDao = walletDao;
    }

    @Transactional
    @Override
    public Player registerPlayer(Long playerId) {
        Player player = new Player(playerId, new Balance());
        if (walletDao.existsById(playerId)) {
            throw new PlayerAlreadyExistException(String.format("Player with id '%s' already registered", playerId));
        }

        return walletDao.save(player);
    }

    @Transactional
    @Override
    public Optional<Player> updateBalance(Long playerId, BigDecimal amount) {
        Optional<Player> player = walletDao.findById(playerId);
        player.ifPresent(p -> {
            BigDecimal currentBalance = p.getBalance().getAmount();
            BigDecimal newBalance = currentBalance.add(amount);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientBalanceException("Insufficient funds", currentBalance);
            }

            p.setBalance(new Balance(currentBalance.add(amount)));
        });

        return player;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Player> findById(Long id) {
        return walletDao.findById(id);
    }
}
