//package com.mv.bruna.task_service.service;
//
//import com.mv.bruna.task_service.dto.TaskDTO;
//import com.mv.bruna.task_service.entity.Task;
//import com.mv.bruna.task_service.enums.TaskStatus;
//import com.mv.bruna.task_service.exception.CustomHttpException;
//import com.mv.bruna.task_service.repository.TaskRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.annotation.Order;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Transactional
//public class TaskServiceIntegrationTest {
//
//    @Autowired
//    private TaskService taskService;
//
//    @Autowired
//    private TaskRepository taskRepository;
//
//    private Task savedTask;
//
//    @BeforeEach
//    void setup() {
//        Task task = new Task();
//        task.setTitle("Tarefa Teste");
//        task.setDescription("Descrição");
//        task.setStatus(TaskStatus.PENDING);
//        task.setUserId(1L);
//        task.setDueDate(LocalDate.now().plusDays(5));
//
//        savedTask = taskRepository.save(task);
//    }
//
//    @Test
//    @Order(1)
//    void shouldListAllTasks() {
//        List<TaskDTO> tasks = taskService.listAll();
//        assertFalse(tasks.isEmpty());
//    }
//
//    @Test
//    @Order(2)
//    void shouldFindTaskById() {
//        TaskDTO dto = taskService.findById(savedTask.getId());
//        assertEquals("Tarefa Teste", dto.getTitle());
//    }
//
//    @Test
//    @Order(3)
//    void shouldCreateTask() {
//        TaskDTO dto = new TaskDTO(null, "Nova", "Nova Desc", TaskStatus.PENDING, LocalDate.now().plusDays(2), 2L);
//        TaskDTO result = taskService.create(dto);
//
//        assertNotNull(result.getId());
//        assertEquals("Nova", result.getTitle());
//    }
//
//    @Test
//    @Order(4)
//    void shouldUpdateTask_WhenNotCompleted() {
//        TaskDTO updateDto = new TaskDTO(null, "Atualizada", "Desc", TaskStatus.IN_PROGRESS, LocalDate.now().plusDays(1), 1L);
//        TaskDTO updated = taskService.update(savedTask.getId(), updateDto);
//
//        assertEquals("Atualizada", updated.getTitle());
//        assertEquals(TaskStatus.IN_PROGRESS, updated.getStatus());
//    }
//
//    @Test
//    @Order(5)
//    void shouldNotUpdate_WhenTaskIsCompleted() {
//        savedTask.setStatus(TaskStatus.COMPLETED);
//        taskRepository.save(savedTask);
//
//        TaskDTO updateDto = new TaskDTO(null, "Tentativa", "desc", TaskStatus.PENDING, LocalDate.now(), 1L);
//
//        CustomHttpException ex = assertThrows(CustomHttpException.class, () ->
//                taskService.update(savedTask.getId(), updateDto)
//        );
//        assertEquals(400, ex.getErrorResponse().getStatusCode());
//    }
//
//    @Test
//    @Order(6)
//    void shouldFilterTasksByUserAndStatus() {
//        List<TaskDTO> result = taskService.filter(TaskStatus.PENDING, 1L);
//        assertEquals(1, result.size());
//        assertEquals("Tarefa Teste", result.get(0).getTitle());
//    }
//
//    @Test
//    @Order(7)
//    void shouldDeleteTask() {
//        taskService.delete(savedTask.getId());
//        assertFalse(taskRepository.existsById(savedTask.getId()));
//    }
//
//    @Test
//    @Order(8)
//    void shouldThrowWhenDeleteNonExistingTask() {
//        CustomHttpException ex = assertThrows(CustomHttpException.class, () -> taskService.delete(999L));
//        assertEquals(404, ex.getErrorResponse().getStatusCode());
//    }
//}
//
