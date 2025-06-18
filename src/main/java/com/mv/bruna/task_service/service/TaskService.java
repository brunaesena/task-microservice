package com.mv.bruna.task_service.service;

import com.mv.bruna.task_service.dto.TaskDTO;
import com.mv.bruna.task_service.entity.Task;
import com.mv.bruna.task_service.enums.TaskStatus;
import com.mv.bruna.task_service.exception.CustomHttpException;
import com.mv.bruna.task_service.exception.ErrorResponse;
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
        try {
            return repository.findAll().stream().map(this::toDTO).toList();
        } catch (Exception e) {
            throw buildInternalError("Erro ao listar tarefas", e);
        }
    }

    public TaskDTO findById(Long id) {
        try {
            return repository.findById(id)
                    .map(this::toDTO)
                    .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));
        } catch (EntityNotFoundException e) {
            throw buildNotFoundError(e.getMessage(), "EntityNotFoundException");
        } catch (Exception e) {
            throw buildInternalError("Erro ao buscar tarefa por ID", e);
        }
    }

    public TaskDTO create(TaskDTO dto) {
        try {
            Task task = new Task();
            copyToEntity(dto, task);
            return toDTO(repository.save(task));
        } catch (Exception e) {
            throw buildInternalError("Erro ao criar tarefa", e);
        }
    }

    public TaskDTO update(Long id, TaskDTO dto) {
        try {
            Task task = repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));

            if (task.getStatus() == TaskStatus.COMPLETED) {
                throw new IllegalStateException("Tarefas concluídas não podem ser editadas");
            }

            copyToEntity(dto, task);
            return toDTO(repository.save(task));
        } catch (EntityNotFoundException e) {
            throw buildNotFoundError(e.getMessage(), "EntityNotFoundException");
        } catch (IllegalStateException e) {
            throw buildBusinessError(e.getMessage(), "IllegalStateException");
        } catch (Exception e) {
            throw buildInternalError("Erro ao atualizar tarefa", e);
        }
    }

    public void delete(Long id) {
        try {
            if (!repository.existsById(id)) {
                throw new EntityNotFoundException("Tarefa não encontrada");
            }
            repository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw buildNotFoundError(e.getMessage(), "EntityNotFoundException");
        } catch (Exception e) {
            throw buildInternalError("Erro ao excluir tarefa", e);
        }
    }

    public List<TaskDTO> getTasksByUserId(Long userId) {
        try {
            List<Task> tasks = repository.findByUserId(userId);
            return tasks.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw buildInternalError("Erro ao listar tarefas do usuário", e);
        }
    }

    public List<TaskDTO> filter(TaskStatus status, Long userId) {
        try {
            if (status != null && userId != null)
                return repository.findByStatusAndUserId(status, userId).stream().map(this::toDTO).toList();
            else if (status != null)
                return repository.findByStatus(status).stream().map(this::toDTO).toList();
            else if (userId != null)
                return repository.findByUserId(userId).stream().map(this::toDTO).toList();
            else
                return listAll();
        } catch (Exception e) {
            throw buildInternalError("Erro ao filtrar tarefas", e);
        }
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

    private CustomHttpException buildNotFoundError(String msg, String cause) {
        return new CustomHttpException(new ErrorResponse(404, msg, cause));
    }

    private CustomHttpException buildBusinessError(String msg, String cause) {
        return new CustomHttpException(new ErrorResponse(400, msg, cause));
    }

    private CustomHttpException buildInternalError(String msg, Exception e) {
        return new CustomHttpException(new ErrorResponse(500, msg, e.getMessage()));
    }
}