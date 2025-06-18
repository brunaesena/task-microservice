package com.mv.bruna.task_service.repository;

import com.mv.bruna.task_service.entity.Task;
import com.mv.bruna.task_service.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByUserId(Long userId);
    List<Task> findByStatusAndUserId(TaskStatus status, Long userId);
}
