package mariwrnk.tasktracker.service;

import mariwrnk.tasktracker.dto.User;
import mariwrnk.tasktracker.exception.UserNotFoundException;
import mariwrnk.tasktracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserIfExists(Long userID) {
        User user = null;
        if (userID != null) {
            Optional<User> userOpt = userRepository.findById(userID);
            if (userOpt.isPresent()) {
                user = userOpt.get();
            } else {
                throw new UserNotFoundException("User with ID " + userID + "is not found!");
            }
        }
        return user;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User insertUser(User user) {
        return userRepository.save(user);
    }

    public User deleteUser(Long userID) {
        User user = getUserIfExists(userID);
        userRepository.deleteById(userID);
        return user;
    }

}
