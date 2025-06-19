package com.mv.bruna.task_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mv.bruna.task_service.entity.Task;
import com.mv.bruna.task_service.enums.TaskStatus;
import com.mv.bruna.task_service.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("task_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Task task;

    @BeforeEach
    void setup() {
        taskRepository.deleteAll();
        task = Task.builder()
                .title("Tarefa Teste")
                .description("Descrição")
                .status(TaskStatus.PENDING)
                .dueDate(LocalDate.now().plusDays(3))
                .userId(1L)
                .build();
        task = taskRepository.save(task);
    }

    @Test
    @DisplayName("Deve criar uma tarefa via API")
    void shouldCreateTask() throws Exception {
        String json = "{\"title\": \"Nova Tarefa\", \"description\": \"Desc\", \"status\": \"PENDING\", \"dueDate\": \"%s\", \"userId\": 2}"
                .formatted(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Nova Tarefa"));
    }

    @Test
    @DisplayName("Deve retornar uma tarefa por ID")
    void shouldGetTaskById() throws Exception {
        mockMvc.perform(get("/api/tasks/" + task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Tarefa Teste"));
    }

    @Test
    @DisplayName("Deve atualizar uma tarefa existente")
    void shouldUpdateTask() throws Exception {
        String updateJson = "{\"title\": \"Atualizada\", \"description\": \"Nova descrição\", \"status\": \"IN_PROGRESS\", \"dueDate\": \"%s\", \"userId\": 1}"
                .formatted(LocalDate.now().plusDays(2));

        mockMvc.perform(put("/api/tasks/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Atualizada"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("Deve excluir uma tarefa")
    void shouldDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/" + task.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve listar todas as tarefas")
    void shouldListTasks() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}