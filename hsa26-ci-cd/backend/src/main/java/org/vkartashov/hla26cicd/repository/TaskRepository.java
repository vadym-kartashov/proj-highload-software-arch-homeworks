package org.vkartashov.hla26cicd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vkartashov.hla26cicd.domain.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {}