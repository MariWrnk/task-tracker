package mariwrnk.tasktracker.service;

import mariwrnk.tasktracker.controller.dto.TaskApiDto;
import mariwrnk.tasktracker.dto.Project;
import mariwrnk.tasktracker.dto.Task;
import mariwrnk.tasktracker.dto.User;
import mariwrnk.tasktracker.exception.ProjectNotFoundException;
import mariwrnk.tasktracker.exception.TaskNotFoundException;
import mariwrnk.tasktracker.exception.UserAndProjectMismatchException;
import mariwrnk.tasktracker.exception.UserNotFoundException;
import mariwrnk.tasktracker.repository.ProjectRepository;
import mariwrnk.tasktracker.repository.TaskRepository;
import mariwrnk.tasktracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class TaskServiceTest {

    @MockBean
    private TaskRepository taskRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ProjectRepository projectRepository;

    @Autowired
    private TaskService taskService;

    private static final Long existingUserId = 0L;
    private User existingUser;

    @BeforeEach
    public void init() {
        existingUser = createTestUser(existingUserId);
        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
    }

    @Test
    void whenFindAll_thenGetTestTasks() {
        List<Task> expectedTaskList = Arrays.asList(createTestTask(0L, null), createTestTask(1L, null));
        when(taskRepository.findAll()).thenReturn(expectedTaskList);

        List<TaskApiDto> actualTaskList = taskService.findAll();

        assertEquals(expectedTaskList.size(), actualTaskList.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void givenExistingUser_whenFindTasksByUser_thenGetTestTasks() {
        List<Task> expectedTaskList = Arrays.asList(createTestTask(0L, existingUser), createTestTask(1L, existingUser));
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        when(taskRepository.findTasksByUser(argumentCaptor.capture())).thenReturn(expectedTaskList);

        List<TaskApiDto> actualTaskList = taskService.findAllByUser(existingUser.getId());

        assertEquals(expectedTaskList.size(), actualTaskList.size());
        verify(taskRepository, times(1)).findTasksByUser(any());
        assertEquals(argumentCaptor.getValue(), existingUserId);
    }

    @Test
    void givenNotExistingUser_whenFindTasksByUser_thenException() {
        Long nonExistingUserId = 1L;
        assertThrows(UserNotFoundException.class, () -> {
            User testUser = createTestUser(nonExistingUserId);
            List<Task> expectedTaskList = Arrays.asList(createTestTask(0L, testUser), createTestTask(1L, testUser));
            when(taskRepository.findTasksByUser(any())).thenReturn(expectedTaskList);
            when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

            taskService.findAllByUser(testUser.getId());
        });
    }

    @Test
    void givenNewTask_whenInsert_thenUpdateNameColumn() {
        Long taskId = 0L;
        Task testTask = createTestTask(taskId, existingUser);
        ArgumentCaptor<Task> insertedTask = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.save(insertedTask.capture())).thenReturn(testTask);
        when(taskRepository.existsById(taskId)).thenReturn(false);

        TaskApiDto inputDto = new TaskApiDto(testTask);
        TaskApiDto taskApiDto = taskService.insertTask(inputDto, existingUserId);

        assertNotNull(taskApiDto);
        assertEquals(taskId, taskApiDto.getId());
        assertNotNull(insertedTask.getValue());
        assertEquals(existingUser, insertedTask.getValue().getExecutor());
        assertNotNull(insertedTask.getValue().getTaskCreated());
    }

    @Test
    void givenNewTaskWithExistingId_whenInsert_thenNewId() {
        Long taskId = 0L;
        Task testTask = createTestTask(taskId, existingUser);
        ArgumentCaptor<Task> insertedTask = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.save(insertedTask.capture())).thenReturn(testTask);
        when(taskRepository.existsById(taskId)).thenReturn(true);

        TaskApiDto inputDto = new TaskApiDto(testTask);
        TaskApiDto taskApiDto = taskService.insertTask(inputDto, existingUserId);

        assertNotNull(taskApiDto);
        assertEquals(taskId, taskApiDto.getId());
        assertNotNull(insertedTask.getValue());
        assertNotEquals(taskId, insertedTask.getValue().getId());
        assertNotNull(insertedTask.getValue().getTaskCreated());
    }

    @Test
    void givenExistingTask_whenUpdateName_thenUpdateNameColumn() {
        Long taskId = 0L;
        Task testTask = createTestTask(taskId, existingUser);
        String expectedName = "new test task name";
        String oldTaskDescription = testTask.getDescription();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any())).thenReturn(testTask);

        TaskApiDto inputDto = new TaskApiDto();
        inputDto.setId(taskId);
        inputDto.setName(expectedName);
        TaskApiDto taskApiDto = taskService.updateTask(inputDto, existingUserId);

        assertNotNull(taskApiDto);
        assertEquals(taskId, taskApiDto.getId());
        assertEquals(expectedName, taskApiDto.getName());
        assertEquals(oldTaskDescription, taskApiDto.getDescription());
    }

    @Test
    void givenExistingTask_whenUpdateWithNotExistingProject_thenException() {
        assertThrows(ProjectNotFoundException.class, () -> {
            Long taskId = 0L;
            Task testTask = createTestTask(taskId, existingUser);
            Long projectId = 888L;
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
            when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

            TaskApiDto inputDto = new TaskApiDto();
            inputDto.setId(taskId);
            inputDto.setProjectId(projectId);
            taskService.updateTask(inputDto, existingUserId);
        });
    }

    @Test
    void givenExistingTask_whenUpdateWithUserNotInProject_thenDoNothing() {
        assertThrows(UserAndProjectMismatchException.class, () -> {
            Long taskId = 0L;
            Task testTask = createTestTask(taskId, existingUser);
            Project testProject = createTestProject(0L);
            testProject.setUsers(Collections.emptyList());
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
            when(projectRepository.findById(testProject.getId())).thenReturn(Optional.of(testProject));

            TaskApiDto inputDto = new TaskApiDto();
            inputDto.setProjectId(testProject.getId());
            taskService.updateTask(inputDto, existingUserId);
        });
    }

    @Test
    void givenNotExistingTask_whenStartTask_thenException() {
        assertThrows(TaskNotFoundException.class, () -> {
            Long taskId = 0L;
            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

            taskService.startTask(existingUserId, taskId);
        });
    }

    @Test
    void givenExistingTaskAndNotExistingExecutor_whenStartTask_thenException() {
        assertThrows(UserNotFoundException.class, () -> {
            Long taskId = 0L;
            Task testTask = createTestTask(taskId, existingUser);
            User testUser = createTestUser(888L);
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
            when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

            taskService.startTask(testUser.getId(), taskId);
        });
    }

    @Test
    void givenExistingTaskAndExistingExecutor_whenStartTask_thenUpdateStartAndExecutorColumns() {
        Long taskId = 0L;
        Task testTask = createTestTask(taskId, existingUser);
        testTask.setExecutor(null);
        testTask.setTaskStart(null);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any())).thenReturn(testTask);

        TaskApiDto taskApiDto = taskService.startTask(existingUserId, taskId);

        assertNotNull(taskApiDto);
        assertEquals(taskId, taskApiDto.getId());
        assertEquals(existingUserId, taskApiDto.getExecutorId());
        assertNotNull(taskApiDto.getTaskStart());
    }

    @Test
    void givenExistingTaskAndUserIsExecutor_whenEndTask_thenIsCompletedEndDateColumnsUpdate() {
        Long taskId = 0L;
        Task testTask = createTestTask(taskId, existingUser);
        testTask.setExecutor(existingUser);
        testTask.setTaskStart(new Date());
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any())).thenReturn(testTask);

        TaskApiDto taskApiDto = taskService.endTask(existingUserId, taskId, true);

        assertNotNull(taskApiDto);
        assertEquals(taskId, taskApiDto.getId());
        assertTrue(taskApiDto.getIsCompleted());
        assertNotNull(taskApiDto.getTaskEnd());
    }

    @Test
    void givenExistingTaskAndUserIsExecutor_whenSuspendTask_thenIsCompletedEndDateColumnUpdate() {
        Long taskId = 0L;
        Task testTask = createTestTask(taskId, existingUser);
        testTask.setExecutor(existingUser);
        testTask.setTaskStart(new Date());
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any())).thenReturn(testTask);

        TaskApiDto taskApiDto = taskService.endTask(existingUserId, taskId, false);

        assertNotNull(taskApiDto);
        assertEquals(taskId, taskApiDto.getId());
        assertFalse(taskApiDto.getIsCompleted());
        assertNotNull(taskApiDto.getTaskEnd());
    }

    @Test
    void givenNotExistingTask_whenEndTask_thenException() {
        assertThrows(TaskNotFoundException.class, () -> {
            Long taskId = 0L;
            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

            taskService.endTask(existingUserId, taskId, true);
        });
    }

    @Test
    void givenExistingTaskAndUserIsNotExecutor_whenEndTask_thenDoNothing() {
        Long taskId = 0L;
        Task testTask = createTestTask(taskId, existingUser);
        User testUser = createTestUser(88L);
        testTask.setExecutor(testUser);
        testTask.setTaskStart(new Date());
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        TaskApiDto taskApiDto = taskService.endTask(existingUserId, taskId, false);

        assertNull(taskApiDto);
    }

    @Test
    void givenExistingTaskAndNoStartDate_whenEndTask_thenDoNothing() {
        Long taskId = 0L;
        Task testTask = createTestTask(taskId, existingUser);
        testTask.setExecutor(existingUser);
        testTask.setTaskStart(null);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        TaskApiDto taskApiDto = taskService.endTask(existingUserId, taskId, false);

        assertNull(taskApiDto);
    }

    @Test
    void givenExistingTask_whenComplete_thenIsCompletedColumnUpdate() {
        Long taskId = 0L;
        Task testTask = createTestTask(taskId, existingUser);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any())).thenReturn(testTask);

        TaskApiDto taskApiDto = taskService.completeTask(existingUserId, taskId);

        assertNotNull(taskApiDto);
        assertEquals(taskId, taskApiDto.getId());
        assertTrue(taskApiDto.getIsCompleted());
    }

    @Test
    void givenNotExistingTask_whenComplete_thenException() {
        assertThrows(TaskNotFoundException.class, () -> {
            Long taskId = 0L;
            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

            taskService.completeTask(existingUserId, taskId);
        });
    }

    private Task createTestTask(Long id, User creator) {
        Task task = new Task();
        task.setId(id);
        task.setTaskName("test task");
        task.setDescription("test task description");
        task.setCreator(creator);
        task.setIsCompleted(false);
        return task;
    }

    private User createTestUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("Test User");
        return user;
    }

    private Project createTestProject(Long id) {
        Project project = new Project();
        project.setId(id);
        return project;
    }
}