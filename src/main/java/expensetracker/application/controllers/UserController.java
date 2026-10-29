package expensetracker.application.controllers;

import expensetracker.application.dto.LoginRequest;
import expensetracker.application.dto.LoginResponse;
import expensetracker.application.dto.UserRegistrationRequest;
import expensetracker.application.dto.UserResponse;
import expensetracker.application.entities.User;
import expensetracker.application.services.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody UserRegistrationRequest request){
        return userService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        System.out.println("LOGIN CONTROLLER REACHED");
        return userService.login(request);
    }
}