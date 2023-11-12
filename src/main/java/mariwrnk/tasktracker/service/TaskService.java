package mariwrnk.tasktracker.service;

import mariwrnk.tasktracker.controller.dto.TaskApiDto;
import mariwrnk.tasktracker.dto.Project;
import mariwrnk.tasktracker.dto.Task;
import mariwrnk.tasktracker.dto.User;
import mariwrnk.tasktracker.exception.TaskNotFoundException;
import mariwrnk.tasktracker.exception.UserAndProjectMismatchException;
import mariwrnk.tasktracker.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;

    public List<TaskApiDto> findAll() {
        return convertToApiDtoList(taskRepository.findAll());
    }

    public List<TaskApiDto> findAllByUser(Long userID) {
        userService.getUserIfExists(userID);
        return convertToApiDtoList(taskRepository.findTasksByUser(userID));
    }

    public List<TaskApiDto> findAllByProject(Long userID, Long projectID) {
        projectService.getProjectIfExists(projectID);
        return convertToApiDtoList(taskRepository.findTasksByProject(userID, projectID));
    }

    public List<TaskApiDto> findAllByCreator(Long userID, Boolean isCompleted) {
        userService.getUserIfExists(userID);
        return convertToApiDtoList(taskRepository.findTasksByCreator(userID, isCompleted));
    }

    public List<TaskApiDto> findAllByExecutor(Long userID, Boolean isCompleted) {
        userService.getUserIfExists(userID);
        return convertToApiDtoList(taskRepository.findTasksByExecutor(userID, isCompleted));
    }

    public TaskApiDto insertTask(TaskApiDto apiDto, Long userID) {
        // по умолчанию текущее время
        if (apiDto.getTaskCreated() == null) {
            apiDto.setTaskCreated(new Date());
        }
        Project taskProject = projectService.getProjectIfExists(apiDto.getProjectId());
        User taskCreator = userService.getUserIfExists(userID);
        User taskExecutor = userService.getUserIfExists(apiDto.getExecutorId());
        // исполнитель по умолчанию - создатель задачи
        if (taskExecutor == null) {
            taskExecutor = taskCreator;
        }

        if (projectService.checkUserIsNotInProject(taskProject, taskCreator) || projectService.checkUserIsNotInProject(taskProject, taskExecutor)) {
            throw new UserAndProjectMismatchException();
        }

        // если задача с переданным id уже существует, то генерируем новый
        if(taskRepository.existsById(apiDto.getId())) {
            apiDto.setId(null);
        }

        Task task = new Task(apiDto, taskProject, taskCreator, taskExecutor);
        return new TaskApiDto(taskRepository.save(task));
    }

    public TaskApiDto updateTask(TaskApiDto apiDto, Long userID) {
        Task task = getTaskIfExists(apiDto.getId());
        Project taskProject = projectService.getProjectIfExists(apiDto.getProjectId());
        User taskCreator = userService.getUserIfExists(userID);
        User taskExecutor = userService.getUserIfExists(apiDto.getExecutorId());

        if (projectService.checkUserIsNotInProject(taskProject, taskCreator) || projectService.checkUserIsNotInProject(taskProject, taskExecutor)) {
            throw new UserAndProjectMismatchException();
        }

        task.updateTaskParams(apiDto, taskProject, taskCreator, taskExecutor);
        return new TaskApiDto(taskRepository.save(task));
    }

    public TaskApiDto startTask(Long userID, Long taskID) {
        Task task = getTaskIfExists(taskID);
        User user = userService.getUserIfExists(userID);
        task.setTaskStart(new Date());
        task.setExecutor(user);
        return new TaskApiDto(taskRepository.save(task));
    }

    public TaskApiDto endTask(Long userID, Long taskID, boolean isCompleted) {
        Task task = getTaskIfExists(taskID);
        if (task.getTaskStart() == null || !task.getExecutor().getId().equals(userID)) {
            return null;
        }
        task.setTaskEnd(new Date());
        task.setIsCompleted(isCompleted);
        return new TaskApiDto(taskRepository.save(task));
    }

    public TaskApiDto completeTask(Long userID, Long taskID) {
        Task task = getTaskIfExists(taskID);
        User user = userService.getUserIfExists(userID);
        task.setIsCompleted(true);
        task.setExecutor(user);
        return new TaskApiDto(taskRepository.save(task));
    }

    public void deleteTask(Long taskID) {
        getTaskIfExists(taskID);
        deleteTaskFromDB(taskID);
    }

    @ResponseStatus(HttpStatus.OK)
    private void deleteTaskFromDB(Long taskID) {
        taskRepository.deleteById(taskID);
    }

    private Task getTaskIfExists(Long taskId) {
        Task task = null;
        if (taskId != null) {
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (taskOpt.isPresent()) {
                task = taskOpt.get();
            } else {
                throw new TaskNotFoundException("Project with ID " + taskId + "is not found!");
            }
        }
        return task;
    }

    private List<TaskApiDto> convertToApiDtoList(List<Task> taskList) {
        return taskList.stream().map(TaskApiDto::new).collect(Collectors.toList());
    }

}
