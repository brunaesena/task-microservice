//package com.mv.bruna.task_service.service;
//
//import com.mv.bruna.task_service.dto.TaskDTO;
//import com.mv.bruna.task_service.entity.Task;
//import com.mv.bruna.task_service.enums.TaskStatus;
//import com.mv.bruna.task_service.exception.CustomHttpException;
//import com.mv.bruna.task_service.repository.TaskRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//public class TaskServiceTest {
//
//    @InjectMocks
//    private TaskService taskService;
//
//    @Mock
//    private TaskRepository repository;
//
//    private Task sampleTask;
//
//    @BeforeEach
//    void setup() {
//        sampleTask = new Task(1L, "Tarefa", "Descrição", TaskStatus.PENDING, LocalDate.now().plusDays(3), 100L);
//    }
//
//    @Test
//    void shouldListAllTasks() {
//        Mockito.when(repository.findAll()).thenReturn(List.of(sampleTask));
//
//        List<TaskDTO> tasks = taskService.listAll();
//
//        assertEquals(1, tasks.size());
//        assertEquals("Tarefa", tasks.get(0).getTitle());
//    }
//
//    @Test
//    void shouldFindById_WhenExists() {
//        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(sampleTask));
//
//        TaskDTO dto = taskService.findById(1L);
//
//        assertEquals("Tarefa", dto.getTitle());
//    }
//
//    @Test
//    void shouldThrowWhenNotFoundById() {
//        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());
//
//        CustomHttpException ex = assertThrows(CustomHttpException.class, () -> taskService.findById(1L));
//        assertEquals("Tarefa não encontrada", ex.getErrorResponse().getErrorMessage());
//    }
//
//    @Test
//    void shouldCreateTask() {
//        TaskDTO dto = new TaskDTO(null, "Nova", "Descricao", TaskStatus.PENDING, LocalDate.now(), 1L);
//        Mockito.when(repository.save(Mockito.any(Task.class)))
//                .thenAnswer(invocation -> {
//                    Task t = invocation.getArgument(0);
//                    t.setId(99L);
//                    return t;
//                });
//
//        TaskDTO result = taskService.create(dto);
//
//        assertEquals("Nova", result.getTitle());
//        assertNotNull(result.getId());
//    }
//
//    @Test
//    void shouldUpdateTask_WhenNotCompleted() {
//        TaskDTO dto = new TaskDTO(null, "Atualizada", "desc", TaskStatus.IN_PROGRESS, LocalDate.now(), 1L);
//        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(sampleTask));
//        Mockito.when(repository.save(Mockito.any(Task.class))).thenAnswer(i -> i.getArgument(0));
//
//        TaskDTO updated = taskService.update(1L, dto);
//
//        assertEquals("Atualizada", updated.getTitle());
//    }
//
//    @Test
//    void shouldThrowOnUpdate_WhenTaskIsCompleted() {
//        sampleTask.setStatus(TaskStatus.COMPLETED);
//        TaskDTO dto = new TaskDTO(null, "Edit", "Edit", TaskStatus.PENDING, LocalDate.now(), 1L);
//        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(sampleTask));
//
//        CustomHttpException ex = assertThrows(CustomHttpException.class, () -> taskService.update(1L, dto));
//        assertEquals("Tarefas concluídas não podem ser editadas", ex.getErrorResponse().getErrorMessage());
//    }
//
//    @Test
//    void shouldDeleteTask_WhenExists() {
//        Mockito.when(repository.existsById(1L)).thenReturn(true);
//        Mockito.doNothing().when(repository).deleteById(1L);
//
//        assertDoesNotThrow(() -> taskService.delete(1L));
//    }
//
//    @Test
//    void shouldThrowOnDelete_WhenNotExists() {
//        Mockito.when(repository.existsById(1L)).thenReturn(false);
//
//        CustomHttpException ex = assertThrows(CustomHttpException.class, () -> taskService.delete(1L));
//        assertEquals("Tarefa não encontrada", ex.getErrorResponse().getErrorMessage());
//    }
//
//    @Test
//    void shouldFilterTasks_ByStatusAndUserId() {
//        Mockito.when(repository.findByStatusAndUserId(TaskStatus.PENDING, 100L)).thenReturn(List.of(sampleTask));
//
//        List<TaskDTO> result = taskService.filter(TaskStatus.PENDING, 100L);
//
//        assertEquals(1, result.size());
//        assertEquals("Tarefa", result.get(0).getTitle());
//    }
//
//    @Test
//    void shouldGetTasksByUserId() {
//        Mockito.when(repository.findByUserId(100L)).thenReturn(List.of(sampleTask));
//
//        List<TaskDTO> result = taskService.getTasksByUserId(100L);
//
//        assertEquals(1, result.size());
//        assertEquals(100L, result.get(0).getUserId());
//    }
//}
