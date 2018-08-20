package com.simplecasino.walletservice;

import com.simplecasino.walletservice.dto.BalanceResponse;
import com.simplecasino.walletservice.dto.RegisterPlayerRequest;
import com.simplecasino.walletservice.dto.UpdateBalanceRequest;
import com.simplecasino.walletservice.exception.ResourceNotFoundException;
import com.simplecasino.walletservice.model.Player;
import com.simplecasino.walletservice.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/wallet",
        produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},
        consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class WalletController {

    private WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/player")
    public BalanceResponse registerPlayer(@RequestBody RegisterPlayerRequest registerRequest) {
        Player player = walletService.registerPlayer(registerRequest.getPlayerId());

        return new BalanceResponse(player.getBalance().getAmount());
    }

    @PutMapping("/player/{id}/balance")
    public BalanceResponse updateBalance(@PathVariable Long id,
                                         @RequestBody UpdateBalanceRequest updateBalanceRequest) {
        Optional<Player> player = walletService.updateBalance(id, updateBalanceRequest.getBalance());

        return getBalanceResponseFromOptPlayer(player, id);
    }

    @GetMapping("/player/{id}/balance")
    public BalanceResponse getBalance(@PathVariable Long id) {
        Optional<Player> player = walletService.findById(id);

        return getBalanceResponseFromOptPlayer(player, id);
    }

    private BalanceResponse getBalanceResponseFromOptPlayer(Optional<Player> player, Long playerId) {
        return player.map(p -> new BalanceResponse(p.getBalance().getAmount()))
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Player with id '%s' not found", playerId)));
    }
}
