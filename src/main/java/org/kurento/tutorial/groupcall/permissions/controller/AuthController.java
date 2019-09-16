package org.kurento.tutorial.groupcall.permissions.controller;

import lombok.RequiredArgsConstructor;
import org.kurento.tutorial.groupcall.permissions.dto.UserDTO;
import org.kurento.tutorial.groupcall.permissions.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("signIn")
    public ResponseEntity signIn(@Validated @NotNull @RequestBody UserDTO user) {
        return ResponseEntity.ok(authenticationService.signIn(user));
    }

    @PostMapping("signUp")
    public ResponseEntity signUp(@Validated @NotNull @RequestBody UserDTO user) {
        return ResponseEntity.ok(authenticationService.signUp(user));
    }
}