package expensetracker.application.services;

import expensetracker.application.config.JwtService;
import expensetracker.application.dto.LoginRequest;
import expensetracker.application.dto.LoginResponse;
import expensetracker.application.dto.UserRegistrationRequest;
import expensetracker.application.dto.UserResponse;
import expensetracker.application.entities.User;
import expensetracker.application.exceptions.DuplicateEmailException;
import expensetracker.application.exceptions.UnauthorizedException;
import expensetracker.application.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtService jwtService){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.jwtService=jwtService;
    }

    public UserResponse register(UserRegistrationRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateEmailException("Email already exists");
        }
        User user=new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode((request.getPassword())));
        user.setCreatedAt(LocalDateTime.now());

        User saved=userRepository.save(user);

        UserResponse response=new UserResponse();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setEmail(saved.getEmail());
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }

    public LoginResponse login(LoginRequest request){
        Optional<User> userOptional=userRepository.findByEmail(request.getEmail());
        if(userOptional.isEmpty()){
            throw new UnauthorizedException("Invalid Email or Password");
        }

        User user=userOptional.get();
        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new UnauthorizedException("Invalid Email or Password");
        }

        LoginResponse response=new LoginResponse();
        String token=jwtService.generateToken(user);

        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setId(user.getId());
        response.setName(user.getName());

        return response;
    }
}
