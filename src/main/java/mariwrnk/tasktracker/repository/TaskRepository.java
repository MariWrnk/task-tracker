package mariwrnk.tasktracker.repository;

import mariwrnk.tasktracker.dto.Task;
import mariwrnk.tasktracker.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(value = "SELECT * FROM tt_task WHERE creator_id = :userId OR executor_id = :userId", nativeQuery = true)
    List<Task> findTasksByUser(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM tt_task WHERE creator_id = :userId AND is_completed = :isCompleted", nativeQuery = true)
    List<Task> findTasksByCreator(@Param("userId") Long userId, @Param("isCompleted") Boolean isCompleted);

    @Query(value = "SELECT * FROM tt_task WHERE executor_id = :userId AND is_completed = :isCompleted", nativeQuery = true)
    List<Task> findTasksByExecutor(@Param("userId") Long userId, @Param("isCompleted") Boolean isCompleted);

    @Query(value = "SELECT * FROM tt_task WHERE project_id = :projectId AND executor_id = :userId", nativeQuery = true)
    List<Task> findTasksByProject(@Param("projectId") Long projectId, @Param("userId") Long userId);

}
