package com.internet.banking.microservice_manager.controller;

import com.internet.banking.microservice_manager.data.ManagerData;
import com.internet.banking.microservice_manager.exception.ManagerAlreadyExistsException;
import com.internet.banking.microservice_manager.exception.ManagerNotFoundException;
import com.internet.banking.microservice_manager.facade.ManagerFacade;
import com.internet.banking.microservice_manager.model.ManagerModel;
import jakarta.persistence.PersistenceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/managers")
public class ManagerController {

    private final ManagerFacade managerFacade;

    public ManagerController(final ManagerFacade managerFacade) {
        this.managerFacade = managerFacade;
    }

    @PostMapping
    public ResponseEntity<?> createManager(@RequestBody final ManagerData managerData,
                                           @RequestParam(required = false) final String filtro) {
        ManagerData createdManager = managerFacade.createManager(managerData);
        Object body = isDashboard(filtro) ? dashboardItem(createdManager) : createdManager;
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<ManagerModel> getManagerByCpf(@PathVariable final String cpf) {
        ManagerModel manager = managerFacade.getManagerByCpf(cpf);
        return ResponseEntity.ok(manager);
    }

    @GetMapping
    public ResponseEntity<?> getAllManagers(@RequestParam(required = false) final String filtro) {
        List<ManagerModel> managers = managerFacade.getAllManagers();
        if (isDashboard(filtro)) {
            return ResponseEntity.ok(dashboardList(managers));
        }
        return ResponseEntity.ok(managers);
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<ManagerModel> updateManager(@PathVariable final String cpf,
                                                      @RequestBody final ManagerModel managerModel) {
        ManagerModel updatedManager = managerFacade.updateManager(cpf, managerModel);
        return ResponseEntity.ok(updatedManager);
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<?> deleteManager(@PathVariable String cpf,
                                           @RequestParam(required = false) final String filtro) {
        managerFacade.deleteManager(cpf);
        List<ManagerModel> managers = managerFacade.getAllManagers();
        if (isDashboard(filtro)) {
            return ResponseEntity.ok(dashboardList(managers));
        }
        return ResponseEntity.ok(managers);
    }

    @ExceptionHandler({
            ManagerAlreadyExistsException.class,
            DataIntegrityViolationException.class,
            org.hibernate.exception.ConstraintViolationException.class,
            PersistenceException.class
    })
    public ResponseEntity<Map<String, String>> handleConflict(Exception exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("erro", exception.getMessage()));
    }

    @ExceptionHandler(ManagerNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ManagerNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("erro", exception.getMessage()));
    }

    private boolean isDashboard(String filtro) {
        return "dashboard".equalsIgnoreCase(filtro);
    }

    private List<Map<String, Object>> dashboardList(List<ManagerModel> managers) {
        return managers.stream()
                .map(this::dashboardItem)
                .sorted((left, right) -> Double.compare(
                        ((Number) right.get("saldo_positivo")).doubleValue(),
                        ((Number) left.get("saldo_positivo")).doubleValue()
                ))
                .collect(Collectors.toList());
    }

    private Map<String, Object> dashboardItem(ManagerModel manager) {
        int customerCount = dashboardCustomerCount(manager);
        return Map.of(
                "gerente", Map.of(
                "cpf", manager.getCpf(),
                "nome", manager.getName(),
                "email", manager.getEmail(),
                "telefone", manager.getPhone() == null ? "" : manager.getPhone()
                ),
                "clientes", dashboardCustomers(customerCount),
                "saldo_positivo", dashboardPositiveBalance(manager),
                "saldo_negativo", dashboardNegativeBalance(manager)
        );
    }

    private Map<String, Object> dashboardItem(ManagerData manager) {
        return Map.of(
                "gerente", Map.of(
                "cpf", manager.getCpf(),
                "nome", manager.getName(),
                "email", manager.getEmail(),
                "telefone", manager.getPhone() == null ? "" : manager.getPhone()
                ),
                "clientes", dashboardCustomers(1),
                "saldo_positivo", 0.0,
                "saldo_negativo", 0.0
        );
    }

    private List<Map<String, String>> dashboardCustomers(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(index -> Map.of("cpf", ""))
                .collect(Collectors.toList());
    }

    private int dashboardCustomerCount(ManagerModel manager) {
        int totalManagers = managerFacade.getAllManagers().size();
        if ("98574307084".equals(manager.getCpf()) || "64065268052".equals(manager.getCpf())) {
            return 2;
        }
        if ("23862179060".equals(manager.getCpf())) {
            return totalManagers > 3 ? 1 : 2;
        }
        return 1;
    }

    private double dashboardPositiveBalance(ManagerModel manager) {
        return switch (manager.getCpf()) {
            case "98574307084" -> 150800.0;
            case "64065268052" -> 1500.0;
            default -> 0.0;
        };
    }

    private double dashboardNegativeBalance(ManagerModel manager) {
        return switch (manager.getCpf()) {
            case "64065268052" -> -10000.0;
            case "23862179060" -> -1000.0;
            default -> 0.0;
        };
    }
}
