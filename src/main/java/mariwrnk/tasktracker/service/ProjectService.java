package mariwrnk.tasktracker.service;

import mariwrnk.tasktracker.dto.Project;
import mariwrnk.tasktracker.dto.User;
import mariwrnk.tasktracker.exception.ProjectNotFoundException;
import mariwrnk.tasktracker.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserService userService;

    public Project getProjectIfExists(Long projectID) {
        Project project = null;
        if (projectID != null) {
            Optional<Project> projectOpt = projectRepository.findById(projectID);
            if (projectOpt.isPresent()) {
                project = projectOpt.get();
            } else {
                throw new ProjectNotFoundException("Project with ID " + projectID + "is not found!");
            }
        }
        return project;
    }

    public List<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public Project insertProject(Project project) {
        return projectRepository.save(project);
    }

    public void deleteProject(Long projectID) {
        getProjectIfExists(projectID);
        projectRepository.deleteById(projectID);
    }

    public Project addUserToProject(Long projectID, Long userID) {
        Project project = getProjectIfExists(projectID);
        User user = userService.getUserIfExists(userID);

        if (project.getUsers() == null) {
            project.setUsers(Collections.singletonList(user));
        } else {
            project.getUsers().add(user);
        }

        return projectRepository.save(project);
    }

    public List<Long> getProjectUsers(Long projectID) {
        Project project = getProjectIfExists(projectID);
        return project.getUsers() == null ? null : project.getUsers().stream().map(User::getId).collect(Collectors.toList());
    }

    public Project deleteUserFromProject(Long projectID, Long userID) {
        Project project = getProjectIfExists(projectID);
        User user = userService.getUserIfExists(userID);

        if (project.getUsers() != null) {
            project.getUsers().remove(user);
        }

        return projectRepository.save(project);
    }

    public boolean checkUserIsNotInProject(Project project, User user) {
        if (project == null || user == null) {
            return false;
        }
        return project.getUsers() == null || !project.getUsers().contains(user);
    }
}
