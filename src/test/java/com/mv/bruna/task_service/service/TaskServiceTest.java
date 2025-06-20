package com.mv.bruna.task_service.service;

import com.mv.bruna.task_service.dto.TaskDTO;
import com.mv.bruna.task_service.entity.Task;
import com.mv.bruna.task_service.enums.TaskStatus;
import com.mv.bruna.task_service.exception.CustomHttpException;
import com.mv.bruna.task_service.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository repository;

    private Task sampleTask;

    @BeforeEach
    void setup() {
        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setTitle("Tarefa");
        sampleTask.setDescription("Descrição");
        sampleTask.setStatus(TaskStatus.PENDING);
        sampleTask.setDueDate(LocalDate.now().plusDays(3));
        sampleTask.setUserId(100L);
    }

    @Test
    void shouldListAllTasks() {
        Mockito.when(repository.findAll()).thenReturn(List.of(sampleTask));

        List<TaskDTO> tasks = taskService.listAll();

        assertEquals(1, tasks.size());
        assertEquals("Tarefa", tasks.get(0).getTitle());
    }

    @Test
    void shouldFindById_WhenExists() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(sampleTask));

        TaskDTO dto = taskService.findById(1L);

        assertEquals("Tarefa", dto.getTitle());
    }

    @Test
    void shouldThrowWhenNotFoundById() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        CustomHttpException ex = assertThrows(CustomHttpException.class, () -> taskService.findById(1L));
        assertEquals("Tarefa não encontrada", ex.getErrorResponse().getErrorMessage());
    }

    @Test
    void shouldCreateTask() {
        TaskDTO dto = new TaskDTO();
        dto.setDescription("test description");
        dto.setTitle("test title");
        dto.setUserId(1L);
        dto.setDueDate(LocalDate.now());

        Mockito.when(repository.save(Mockito.any(Task.class)))
                .thenAnswer(invocation -> {
                    Task t = invocation.getArgument(0);
                    t.setId(99L);
                    return t;
                });

        TaskDTO result = taskService.create(dto);

        assertEquals(dto.getTitle(), result.getTitle());
        assertNotNull(result.getId());
    }

    @Test
    void shouldUpdateTask_WhenNotCompleted() {
        TaskDTO dto = new TaskDTO();
        dto.setTitle("updated title");
        dto.setDueDate(LocalDate.now());

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(sampleTask));
        Mockito.when(repository.save(Mockito.any(Task.class))).thenAnswer(i -> i.getArgument(0));

        TaskDTO updated = taskService.update(1L, dto);
        assertEquals(sampleTask.getDescription(), updated.getDescription());
        assertEquals(dto.getTitle(), updated.getTitle());
    }

    @Test
    void shouldThrowOnUpdate_WhenTaskIsCompleted() {
        sampleTask.setStatus(TaskStatus.COMPLETED);
        TaskDTO dto = new TaskDTO();
        dto.setTitle("updated title");
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(sampleTask));
        CustomHttpException ex = assertThrows(CustomHttpException.class, () -> taskService.update(1L, dto));
        assertEquals("Tarefas concluídas não podem ser editadas", ex.getErrorResponse().getErrorMessage());
        assertNotEquals(sampleTask.getTitle(), dto.getTitle());
    }

    @Test
    void shouldDeleteTask_WhenExists() {
        Mockito.when(repository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> taskService.delete(1L));
    }

    @Test
    void shouldThrowOnDelete_WhenNotExists() {
        Mockito.when(repository.existsById(1L)).thenReturn(false);

        CustomHttpException ex = assertThrows(CustomHttpException.class, () -> taskService.delete(1L));
        assertEquals("Tarefa não encontrada", ex.getErrorResponse().getErrorMessage());
    }

    @Test
    void shouldFilterTasks_ByStatusAndUserId() {
        Mockito.when(repository.findByStatusAndUserId(TaskStatus.PENDING, 100L)).thenReturn(List.of(sampleTask));

        List<TaskDTO> result = taskService.filter(TaskStatus.PENDING, 100L);

        assertEquals(1, result.size());
        assertEquals("Tarefa", result.get(0).getTitle());
    }

    @Test
    void shouldGetTasksByUserId() {
        Mockito.when(repository.findByUserId(100L)).thenReturn(List.of(sampleTask));

        List<TaskDTO> result = taskService.getTasksByUserId(100L);

        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getUserId());
    }
}
