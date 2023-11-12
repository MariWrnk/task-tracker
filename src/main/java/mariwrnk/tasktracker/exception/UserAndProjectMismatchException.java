package mariwrnk.tasktracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Given user is not in project")
public class UserAndProjectMismatchException extends RuntimeException {
}
