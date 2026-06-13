package com.internet.banking.microservice_manager.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.internet.banking.microservice_manager.data.ManagerData;
import com.internet.banking.microservice_manager.exception.ManagerAlreadyExistsException;
import com.internet.banking.microservice_manager.exception.ManagerNotFoundException;
import com.internet.banking.microservice_manager.facade.ManagerFacade;
import com.internet.banking.microservice_manager.model.ManagerModel;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/managers")
public class ManagerController {

    private final ManagerFacade managerFacade;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${AUTH_SERVICE_URL:http://localhost:8081}")
    private String authServiceUrl;

    @Value("${ACCOUNT_SERVICE_URL:http://localhost:8083}")
    private String accountServiceUrl;

    public ManagerController(final ManagerFacade managerFacade) {
        this.managerFacade = managerFacade;
    }

    @PostMapping
    public ResponseEntity<?> createManager(@RequestBody final ManagerData managerData,
                                           @RequestParam(required = false) final String filtro) {
        validateManagerPassword(managerData.getPassword());
        ManagerData createdManager = managerFacade.createManager(managerData);
        createManagerCredential(createdManager, managerData.getPassword());
        redistributeAccountTo(createdManager.getCpf());
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
                                                      @RequestBody final UpdateManagerRequest request) {
        ManagerModel managerModel = new ManagerModel();
        managerModel.setName(firstFilled(request.name(), request.nome()));
        managerModel.setEmail(request.email());
        managerModel.setPhone(firstFilled(request.phone(), request.telefone()));

        ManagerModel updatedManager = managerFacade.updateManager(cpf, managerModel);
        updateManagerCredential(cpf, updatedManager, firstFilled(request.password(), request.senha()));
        return ResponseEntity.ok(updatedManager);
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<?> deleteManager(@PathVariable String cpf,
                                           @RequestParam(required = false) final String filtro) {
        deleteManagerCredential(cpf);
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
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
        List<Map<String, Object>> accounts = accountsByManager(manager.getCpf());
        return Map.of(
                "gerente", Map.of(
                "cpf", manager.getCpf(),
                "nome", manager.getName(),
                "email", manager.getEmail(),
                "telefone", manager.getPhone() == null ? "" : manager.getPhone()
                ),
                "clientes", dashboardCustomers(accounts),
                "saldo_positivo", dashboardPositiveBalance(accounts),
                "saldo_negativo", dashboardNegativeBalance(accounts)
        );
    }

    private Map<String, Object> dashboardItem(ManagerData manager) {
        List<Map<String, Object>> accounts = accountsByManager(manager.getCpf());
        return Map.of(
                "gerente", Map.of(
                "cpf", manager.getCpf(),
                "nome", manager.getName(),
                "email", manager.getEmail(),
                "telefone", manager.getPhone() == null ? "" : manager.getPhone()
                ),
                "clientes", dashboardCustomers(accounts),
                "saldo_positivo", dashboardPositiveBalance(accounts),
                "saldo_negativo", dashboardNegativeBalance(accounts)
        );
    }

    private List<Map<String, String>> dashboardCustomers(List<Map<String, Object>> accounts) {
        return accounts.stream()
                .map(account -> Map.of("cpf", valueAsString(account.get("cpfCustomer"))))
                .collect(Collectors.toList());
    }

    private double dashboardPositiveBalance(List<Map<String, Object>> accounts) {
        return accounts.stream()
                .map(this::balanceFromAccount)
                .filter(balance -> balance.compareTo(BigDecimal.ZERO) >= 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();
    }

    private double dashboardNegativeBalance(List<Map<String, Object>> accounts) {
        return accounts.stream()
                .map(this::balanceFromAccount)
                .filter(balance -> balance.compareTo(BigDecimal.ZERO) < 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> accountsByManager(String cpf) {
        Map<String, Object>[] accounts = restTemplate.getForObject(
                accountServiceUrl + "/accounts/manager/" + cpf,
                Map[].class
        );
        if (accounts == null) {
            return List.of();
        }
        return Arrays.stream(accounts)
                .map(account -> (Map<String, Object>) account)
                .collect(Collectors.toList());
    }

    private BigDecimal balanceFromAccount(Map<String, Object> account) {
        Object balance = account.get("balance");
        if (balance instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        if (balance != null) {
            return new BigDecimal(balance.toString());
        }
        return BigDecimal.ZERO;
    }

    private String valueAsString(Object value) {
        return value == null ? "" : value.toString();
    }

    private void redistributeAccountTo(String newManagerCpf) {
        List<ManagerModel> managers = managerFacade.getAllManagers().stream()
                .filter(manager -> !newManagerCpf.equals(manager.getCpf()))
                .toList();
        if (managers.isEmpty()) {
            return;
        }

        managers.stream()
                .max(managerRedistributionOrder())
                .ifPresent(manager -> transferFirstAccount(manager.getCpf(), newManagerCpf));
    }

    private Comparator<ManagerModel> managerRedistributionOrder() {
        return (left, right) -> {
            List<Map<String, Object>> leftAccounts = accountsByManager(left.getCpf());
            List<Map<String, Object>> rightAccounts = accountsByManager(right.getCpf());

            int countComparison = Integer.compare(leftAccounts.size(), rightAccounts.size());
            if (countComparison != 0) {
                return countComparison;
            }

            return -Double.compare(dashboardPositiveBalance(leftAccounts), dashboardPositiveBalance(rightAccounts));
        };
    }

    private void transferFirstAccount(String currentManagerCpf, String newManagerCpf) {
        List<Map<String, Object>> accounts = accountsByManager(currentManagerCpf);
        if (accounts.isEmpty()) {
            return;
        }

        String accountNumber = valueAsString(accounts.get(0).get("number"));
        if (accountNumber.isBlank()) {
            return;
        }

        restTemplate.put(
                accountServiceUrl + "/accounts/" + accountNumber + "/manager",
                Map.of("cpfManager", newManagerCpf)
        );
    }

    private void validateManagerPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("A senha do gerente e obrigatoria");
        }
    }

    private void createManagerCredential(ManagerData manager, String password) {
        CreateManagerUserRequest request = new CreateManagerUserRequest(
                manager.getCpf(),
                manager.getEmail(),
                password,
                manager.getName()
        );
        restTemplate.postForEntity(authServiceUrl + "/auth/users/gerentes", request, Void.class);
    }

    private void updateManagerCredential(String cpf, ManagerModel manager, String password) {
        CreateManagerUserRequest request = new CreateManagerUserRequest(
                cpf,
                manager.getEmail(),
                password,
                manager.getName()
        );
        restTemplate.put(authServiceUrl + "/auth/users/gerentes/" + cpf, request);
    }

    private void deleteManagerCredential(String cpf) {
        try {
            restTemplate.exchange(
                    authServiceUrl + "/auth/users/gerentes/" + cpf,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
        } catch (HttpClientErrorException.NotFound exception) {
            // Credencial ja removida: o cadastro do gerente ainda pode ser excluido.
        }
    }

    private String firstFilled(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        return second;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record UpdateManagerRequest(
            String name,
            String nome,
            String email,
            String phone,
            String telefone,
            String password,
            String senha
    ) {
    }

    private record CreateManagerUserRequest(
            String cpf,
            String email,
            String senha,
            String nome
    ) {
    }
}
