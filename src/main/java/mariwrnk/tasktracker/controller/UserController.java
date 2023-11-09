package mariwrnk.tasktracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import mariwrnk.tasktracker.dto.User;
import mariwrnk.tasktracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Add new user")
    @PostMapping("/users")
    public ResponseEntity<User> insertUser(@RequestBody final User user) {
        return ResponseEntity.ok(userService.insertUser(user));
    }

    @Operation(summary = "Delete user")
    @PostMapping("/users/{userID}")
    public ResponseEntity<User> deleteUser(@PathVariable("userID") Long userID) {
        return ResponseEntity.ok(userService.deleteUser(userID));
    }

    @Operation(summary = "Get all users")
    @GetMapping(path = "/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

}
