package com.simplecasino.walletservice;

import com.simplecasino.walletservice.model.Player;
import com.simplecasino.walletservice.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController("/wallet")
public class WalletController {

    private WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/player")
    public ResponseEntity<?> registerPlayer(@RequestBody RegisterPlayerRequest registerRequest) {
        Player player = walletService.registerPlayer(registerRequest.getPlayerId());
        return ResponseEntity.ok(player.getBalance());
    }

    @PutMapping("/player/{id}/balance")
    public ResponseEntity<?> updateBalance(@PathVariable Long id,
                                           @RequestBody UpdateBalanceRequest updateBalanceRequest) {
        Optional<Player> player = walletService.updateBalance(id, updateBalanceRequest.getAmount());

        return player.map(p -> ResponseEntity.ok(p.getBalance()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/player/{id}/balance")
    public ResponseEntity<?> getBalance(@PathVariable Long id) {
        Optional<Player> player = walletService.findById(id);

        return player.map(p -> ResponseEntity.ok(p.getBalance()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
