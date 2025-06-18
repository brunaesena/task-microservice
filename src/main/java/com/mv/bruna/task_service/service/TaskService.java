package com.mv.bruna.task_service.service;

import com.mv.bruna.task_service.dto.TaskDTO;
import com.mv.bruna.task_service.entity.Task;
import com.mv.bruna.task_service.enums.TaskStatus;
import com.mv.bruna.task_service.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository repository;

    public List<TaskDTO> listAll() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    public TaskDTO findById(Long id) {
        return repository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));
    }

    public TaskDTO create(TaskDTO dto) {
        Task task = new Task();
        copyToEntity(dto, task);
        return toDTO(repository.save(task));
    }

    public TaskDTO update(Long id, TaskDTO dto) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));

        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new IllegalStateException("Tarefas concluídas não podem ser editadas");
        }

        copyToEntity(dto, task);
        return toDTO(repository.save(task));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<TaskDTO> getTasksByUserId(Long userId) {
        List<Task> tasks = repository.findByUserId(userId);
        return tasks.stream()
                .map(TaskDTO::new) // supondo que seu DTO tem construtor TaskDTO(Task)
                .collect(Collectors.toList());
    }


    public List<TaskDTO> filter(TaskStatus status, Long userId) {
        if (status != null && userId != null)
            return repository.findByStatusAndUserId(status, userId).stream().map(this::toDTO).toList();
        else if (status != null)
            return repository.findByStatus(status).stream().map(this::toDTO).toList();
        else if (userId != null)
            return repository.findByUserId(userId).stream().map(this::toDTO).toList();
        else
            return listAll();
    }

    private TaskDTO toDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        BeanUtils.copyProperties(task, dto);
        return dto;
    }

    private void copyToEntity(TaskDTO dto, Task task) {
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setDueDate(dto.getDueDate());
        task.setUserId(dto.getUserId());
    }
}
