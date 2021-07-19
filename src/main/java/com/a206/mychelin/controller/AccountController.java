package com.a206.mychelin.controller;

import com.a206.mychelin.domain.entity.UserEntity;
import com.a206.mychelin.domain.repository.UserRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AccountController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/account/login")
    @ApiOperation(value = "로그인")
    public void login(@RequestParam(required = true) final String id, @RequestParam(required = true) final String password) {
        Optional<UserEntity> userOpt = userRepository.findUserEntityByIdAndPassword(id, password);
        Optional<UserEntity> userPhone = userRepository.findUserEntityByPhoneNumber("010-1234-5678");

        System.out.println(userOpt.get());


    }

}
