package com.simplecasino.walletservice.dao;

import com.simplecasino.walletservice.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletDao extends CrudRepository<Player, Long> {
}
