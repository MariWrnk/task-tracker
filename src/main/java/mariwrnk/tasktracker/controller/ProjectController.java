package mariwrnk.tasktracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import mariwrnk.tasktracker.dto.Project;
import mariwrnk.tasktracker.dto.User;
import mariwrnk.tasktracker.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Operation(summary = "Add new project")
    @PostMapping("/projects")
    public ResponseEntity<Project> insertProject(@RequestBody final Project project) {
        return ResponseEntity.ok(projectService.insertProject(project));
    }

    @Operation(summary = "Delete project")
    @PostMapping("/projects/{projectID}")
    public ResponseEntity<String> deleteProject(@PathVariable("projectID") Long projectID) {
        projectService.deleteProject(projectID);
        return ResponseEntity.ok("Project with ID " + projectID + " was deleted");
    }

    @Operation(summary = "Add user to project")
    @PutMapping("/projects/{projectID}/add/{userID}")
    public ResponseEntity<List<User>> addUserToProject(@PathVariable("projectID") Long projectID, @PathVariable("userID") Long userID) {
        return ResponseEntity.ok(projectService.addUserToProject(projectID, userID).getUsers());
    }

    @Operation(summary = "Delete user from project")
    @PutMapping("/projects/{projectID}/delete/{userID}")
    public ResponseEntity<List<User>> deleteUserFromProject(@PathVariable("projectID") Long projectID, @PathVariable("userID") Long userID) {
        return ResponseEntity.ok(projectService.deleteUserFromProject(projectID, userID).getUsers());
    }

    @Operation(summary = "Get all user IDs in a project")
    @GetMapping("/projects/{projectID}/users")
    public ResponseEntity<List<Long>> getProjectUsers(@PathVariable("projectID") Long projectID) {
        return ResponseEntity.ok(projectService.getProjectUsers(projectID));
    }

    @Operation(summary = "Get all projects")
    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.findAllProjects());
    }
}
