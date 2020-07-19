package com.itodatamp.mpapigateway.controller;

import com.itodatamp.mpapigateway.service.DSPService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final DSPService dspService;

    @GetMapping(value = "/challenge")
    public ResponseEntity<String> getChallenge(
            @RequestParam String dspAccountAddress,
            @RequestParam String dspContractAddress
    ) {
        return dspService.getChallenge(dspAccountAddress, dspContractAddress);
    }

    @PostMapping(value = "/challenge")
    public ResponseEntity<String> returnChallenge(@RequestBody Map<String, Object> payload) {
        return dspService.verifyChallenge(payload);
    }
}
