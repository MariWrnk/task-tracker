package mariwrnk.tasktracker.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mariwrnk.tasktracker.dto.Task;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskApiDto {

    private Long id;

    private String name;

    private String description;

    private Date taskStart;

    private Date taskEnd;

    private Date taskCreated;

    private Boolean isCompleted;

    private Long projectId;

    private Long creatorId;

    private Long executorId;

    public TaskApiDto(Task task) {
        this.id = task.getId();
        this.name = task.getTaskName();
        this.description = task.getDescription();
        this.taskStart = task.getTaskStart();
        this.taskEnd = task.getTaskEnd();
        this.taskCreated = task.getTaskCreated();
        this.isCompleted = task.getIsCompleted();
        this.projectId = task.getProject() == null ? null : task.getProject().getId();
        this.creatorId = task.getCreator() == null ? null : task.getCreator().getId();
        this.executorId = task.getExecutor() == null ? null : task.getExecutor().getId();
    }

}
