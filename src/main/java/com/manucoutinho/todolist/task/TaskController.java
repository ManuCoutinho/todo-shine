package com.manucoutinho.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping("/")
  public ResponseEntity<Object> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    taskModel.setIdUser((UUID) request.getAttribute("idUser"));

    var currentData = LocalDateTime.now();
    if (currentData.isAfter(taskModel.getStartAt()) || currentData.isAfter(taskModel.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("The start date and/or end date must be greater than the current date");
    }
    if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The end date must be greater than the start date");
    }
    var task = this.taskRepository.save(taskModel);

    return ResponseEntity.status(HttpStatus.OK).body(task);
  }

  @GetMapping("/")
  public List<TaskModel> list(HttpServletRequest request){
    var tasks = this.taskRepository.findByIdUser((UUID) request.getAttribute("idUser"));
    return tasks;
  }

  @PutMapping("/{id}")
  public void update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id){
    taskModel.setIdUser((UUID) request.getAttribute("idUser"));
    taskModel.setId(id);
    this.taskRepository.save(taskModel);
  }
}