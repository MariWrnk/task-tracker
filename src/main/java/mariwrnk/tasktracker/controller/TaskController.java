package mariwrnk.tasktracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import mariwrnk.tasktracker.controller.dto.TaskApiDto;
import mariwrnk.tasktracker.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Operation(summary = "Get all tasks")
    @GetMapping("/tasks")
    public ResponseEntity<List<TaskApiDto>> getAllTasks() {
        return ResponseEntity.ok(taskService.findAll());
    }

    @Operation(summary = "Get all user tasks")
    @GetMapping("/{userID}/all-tasks")
    public ResponseEntity<List<TaskApiDto>> getAllTasksByUser(@PathVariable("userID") Long userID) {
        return ResponseEntity.ok(taskService.findAllByUser(userID));
    }

    @Operation(summary = "Get all tasks created by user")
    @GetMapping("/{userID}/created-tasks")
    public ResponseEntity<List<TaskApiDto>> getCreatedTasks(@PathVariable("userID") Long userID) {
        return ResponseEntity.ok(taskService.findAllByCreator(userID, false));
    }

    @Operation(summary = "Get all completed tasks created by user")
    @GetMapping("/{userID}/created-tasks-completed")
    public ResponseEntity<List<TaskApiDto>> getCreatedTasksCompleted(@PathVariable("userID") Long userID) {
        return ResponseEntity.ok(taskService.findAllByCreator(userID, true));
    }

    @Operation(summary = "Get all tasks for user to execute")
    @GetMapping("/{userID}/tasks-to-execute")
    public ResponseEntity<List<TaskApiDto>> getTasksToExecute(@PathVariable("userID") Long userID) {
        return ResponseEntity.ok(taskService.findAllByExecutor(userID, false));
    }

    @Operation(summary = "Get all completed tasks for user to execute")
    @GetMapping("/{userID}/tasks-to-execute-completed")
    public ResponseEntity<List<TaskApiDto>> getTasksToExecuteCompleted(@PathVariable("userID") Long userID) {
        return ResponseEntity.ok(taskService.findAllByExecutor(userID, true));
    }

    @Operation(summary = "Get all tasks by project")
    @GetMapping("/{userID}/tasks/{projectID}")
    public ResponseEntity<List<TaskApiDto>> getTasksByProject(@PathVariable("userID") Long userID, @PathVariable("projectID") Long projectID) {
        return ResponseEntity.ok(taskService.findAllByProject(userID, projectID));
    }

    @Operation(summary = "Add new task")
    @PostMapping("/{userID}/tasks")
    public ResponseEntity<TaskApiDto> insertTask(@PathVariable("userID") Long userID, @RequestBody final TaskApiDto task) {
        return ResponseEntity.ok(taskService.insertTask(task, userID));
    }

    @Operation(summary = "Update task")
    @PutMapping("/{userID}/tasks")
    public ResponseEntity<TaskApiDto> updateTask(@PathVariable("userID") Long userID, @RequestBody final TaskApiDto task) {
        return ResponseEntity.ok(taskService.updateTask(task, userID));
    }

    @Operation(summary = "Delete task")
    @PutMapping("/{userID}/tasks/{taskID}")
    public ResponseEntity<String> deleteTask(@PathVariable("userID") Long userID, @PathVariable("taskID") Long taskID) {
        taskService.deleteTask(taskID);
        return ResponseEntity.ok("Task with ID " + taskID + " is deleted");
    }

    @Operation(summary = "Start doing task")
    @PutMapping("/{userID}/tasks/{taskID}/start")
    public ResponseEntity<TaskApiDto> startTask(@PathVariable("userID") Long userID, @PathVariable("taskID") Long taskID) {
        return ResponseEntity.ok(taskService.startTask(userID, taskID));
    }

    @Operation(summary = "Stop doing task")
    @PutMapping("{userID}/tasks/{taskID}/interrupt")
    public ResponseEntity<TaskApiDto> stopTask(@PathVariable("userID") Long userID, @PathVariable("taskID") Long taskID) {
        return ResponseEntity.ok(taskService.endTask(userID, taskID, false));
    }

    @Operation(summary = "Stop doing task")
    @PutMapping("{userID}/tasks/{taskID}/stop")
    public ResponseEntity<TaskApiDto> stopAndCompleteTask(@PathVariable("userID") Long userID, @PathVariable("taskID") Long taskID) {
        return ResponseEntity.ok(taskService.endTask(userID, taskID, true));
    }

    @Operation(summary = "Mark task as completed")
    @PutMapping("/{userID}/tasks/{taskID}/completed")
    public ResponseEntity<TaskApiDto> completeTask(@PathVariable("userID") Long userID, @PathVariable("taskID") Long taskID) {
        return ResponseEntity.ok(taskService.completeTask(userID, taskID));
    }
}
