package com.example.webflux_jwt.controller;

import com.example.webflux_jwt.models.reqResBodies.ReqLogin;
import com.example.webflux_jwt.reqResonseModel.ReqResModel;
import com.example.webflux_jwt.services.JWTService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class AuthController {
final ReactiveUserDetailsService users;
final JWTService jwtService;
final PasswordEncoder encoder;

    public AuthController(ReactiveUserDetailsService users, JWTService jwtService, PasswordEncoder encoder) {
        this.users = users;
        this.jwtService = jwtService;
        this.encoder = encoder;
    }

    @GetMapping("/auth")
    public Mono<ResponseEntity<ReqResModel>> auth(){
        return Mono.just(
                ResponseEntity.ok(
                        new ReqResModel<>("welcome to the private model", "")
                )
        );
    }



    @PostMapping("/login")
    public Mono<ResponseEntity<ReqResModel<String>>> login(@RequestBody ReqLogin user){
        Mono<UserDetails> foundUser = users.findByUsername(user.getEmail()).defaultIfEmpty(null);
        //check if user was found or not
      return foundUser.flatMap(u ->{
            if(u !=null){
                if(encoder.matches(user.getPassword(), u.getPassword())){
                    return Mono.just(
                            ResponseEntity.ok(
                                    new ReqResModel<>(jwtService.generate(u.getUsername()),"success")
                            )
                    );
                }
                return Mono.just(
                        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ReqResModel<>("", "Invalid Credentials"))
                );
            }
                     return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ReqResModel<>("", "User Not found please Register")));

        }
        );

    }

}
//27.03 sec