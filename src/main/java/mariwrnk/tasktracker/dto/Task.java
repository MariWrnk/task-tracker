package mariwrnk.tasktracker.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mariwrnk.tasktracker.controller.dto.TaskApiDto;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tt_task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String taskName;

    private String description;

    private Date taskStart;

    private Date taskEnd;

    private Date taskCreated;

    private Boolean isCompleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executor_id")
    private User executor;

    public Task(TaskApiDto apiDto, Project project, User creator, User executor) {
        this.id = apiDto.getId();
        this.taskName = apiDto.getName();
        this.description = apiDto.getDescription();
        this.taskStart = apiDto.getTaskStart();
        this.taskEnd = apiDto.getTaskEnd();
        this.taskCreated = apiDto.getTaskCreated();
        this.isCompleted = apiDto.getIsCompleted();
        this.project = project;
        this.creator = creator;
        this.executor = executor;
    }

    public void updateTaskParams(TaskApiDto apiDto, Project project, User creator, User executor) {
        this.taskName = apiDto.getName() != null ? apiDto.getName() : this.taskName;
        this.description = apiDto.getDescription() != null ? apiDto.getDescription() : this.description;
        this.taskStart = apiDto.getTaskStart() != null ? apiDto.getTaskStart() : this.taskStart;
        this.taskEnd = apiDto.getTaskEnd() != null ? apiDto.getTaskEnd() : this.taskEnd;
        this.taskCreated = apiDto.getTaskCreated() != null ? apiDto.getTaskCreated() : this.taskCreated;
        this.isCompleted = apiDto.getIsCompleted() != null ? apiDto.getIsCompleted() : this.isCompleted;
        this.project = project != null ? project : this.project;
        this.creator = creator != null ? creator : this.creator;
        this.executor = executor != null ? executor : this.executor;
    }

}
