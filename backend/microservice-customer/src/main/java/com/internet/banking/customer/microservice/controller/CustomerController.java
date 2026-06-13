package com.internet.banking.customer.microservice.controller;

import com.internet.banking.customer.microservice.data.CustomerData;
import com.internet.banking.customer.microservice.dto.request.CustomerRequest;
import com.internet.banking.customer.microservice.dto.response.CustomerResponse;
import com.internet.banking.customer.microservice.mapper.CustomerDtoMapper;
import com.internet.banking.customer.microservice.service.CustomerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${AUTH_SERVICE_URL:http://localhost:8081}")
    private String authServiceUrl;

    @Value("${ACCOUNT_SERVICE_URL:http://localhost:8083}")
    private String accountServiceUrl;

    @Value("${MANAGER_SERVICE_URL:http://localhost:8084}")
    private String managerServiceUrl;

    public CustomerController(final CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/registration/pending")
    public ResponseEntity<List<CustomerResponse>> listPendingRegistrations() {
        return ResponseEntity.ok(CustomerDtoMapper.toResponseList(customerService.listPendingRegistration()));
    }

    @PostMapping("/registration/request")
    public ResponseEntity<CustomerResponse> requestSelfRegistration(@Valid @RequestBody final CustomerRequest request) {
        CustomerData created = customerService.createPendingRegistration(CustomerDtoMapper.toData(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerDtoMapper.toResponse(created));
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody final CustomerRequest request) {
        CustomerData createdCustomer = customerService.createCustomer(CustomerDtoMapper.toData(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerDtoMapper.toResponse(createdCustomer));
    }

    @PostMapping("/{cpf}/aprovar")
    public ResponseEntity<CustomerResponse> approveCustomer(@PathVariable final String cpf,
                                                            @RequestHeader(value = "Authorization", required = false) final String authorization) {
        if (isCustomerToken(authorization)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        CustomerData customer = customerService.getCustomerByCpf(cpf);
        String senha = generateTemporaryPassword();
        createClientCredential(customer, senha);
        createClientAccount(customer, authorization);
        CustomerData approvedCustomer = customerService.approveRegistration(cpf);
        logger.info(">>>> SENHA ENVIADA POR E-MAIL PARA {}: {}", approvedCustomer.getEmail(), senha);
        return ResponseEntity.ok(CustomerDtoMapper.toResponse(approvedCustomer));
    }

    @PostMapping("/{cpf}/rejeitar")
    public ResponseEntity<Void> rejectCustomer(@PathVariable final String cpf,
                                               @RequestHeader(value = "Authorization", required = false) final String authorization) {
        if (isCustomerToken(authorization)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        customerService.rejectRegistration(cpf);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<Map<String, Object>> getCustomerByCpf(@PathVariable final String cpf) {
        CustomerData customer = customerService.getCustomerByCpf(cpf);
        return ResponseEntity.ok(customerResponseWithBalance(customer));
    }

    @GetMapping
    public ResponseEntity<?> getAllCustomers(@RequestParam(required = false) String filtro,
                                             @RequestHeader(value = "Authorization", required = false) String authorization) {
        if ("para_aprovar".equalsIgnoreCase(filtro)) {
            return ResponseEntity.ok(CustomerDtoMapper.toResponseList(customerService.listPendingRegistration()));
        }

        if ("melhores_clientes".equalsIgnoreCase(filtro)) {
            return ResponseEntity.ok(bestCustomersByBalance());
        }

        List<CustomerData> customers = customerService.getAllCustomers();
        if ("adm_relatorio_clientes".equalsIgnoreCase(filtro)) {
            customers = customers.stream()
                    .sorted(Comparator.comparing(CustomerData::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
                    .toList();
        } else if ("GERENTE".equals(roleFromAuthorization(authorization))) {
            Set<String> customerCpfs = customerCpfsByManager(managerCpfFromAuthorization(authorization));
            customers = customers.stream()
                    .filter(customer -> customerCpfs.contains(customer.getCpf()))
                    .sorted(Comparator.comparing(CustomerData::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
                    .toList();
        }
        return ResponseEntity.ok(CustomerDtoMapper.toResponseList(customers));
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable final String cpf,
                                                           @Valid @RequestBody final CustomerRequest request) {
        CustomerData updatedCustomer = customerService.updateCustomer(cpf, CustomerDtoMapper.toData(request));
        return ResponseEntity.ok(CustomerDtoMapper.toResponse(updatedCustomer));
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable final String cpf) {
        customerService.deleteCustomer(cpf);
        return ResponseEntity.noContent().build();
    }

    private boolean isCustomerToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return false;
        }

        String token = authorization.trim()
                .replaceFirst("(?i)^Bearer\\s+", "")
                .replace("\"", "")
                .trim();
        if (token.isBlank()) {
            return false;
        }

        try {
            String payload = decodeToken(token);
            String[] parts = payload.split(":");
            return parts.length >= 2 && "CLIENTE".equals(parts[1]);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    private void createClientCredential(CustomerData customer, String senha) {
        CreateClientUserRequest request = new CreateClientUserRequest(
                customer.getCpf(),
                customer.getEmail(),
                senha,
                customer.getName()
        );
        restTemplate.postForEntity(authServiceUrl + "/auth/users/clientes", request, Void.class);
    }

    private void createClientAccount(CustomerData customer, String authorization) {
        AccountRequest request = new AccountRequest(
                customer.getCpf(),
                accountNumber(customer.getCpf()),
                LocalDate.now(),
                BigDecimal.ZERO,
                accountLimit(customer.getSalary()),
                managerCpfFromAuthorization(authorization)
        );
        restTemplate.postForEntity(accountServiceUrl + "/accounts", request, Void.class);
    }

    private BigDecimal accountLimit(BigDecimal salary) {
        if (salary == null || salary.compareTo(BigDecimal.valueOf(2000)) < 0) {
            return BigDecimal.ZERO;
        }
        return salary.divide(BigDecimal.valueOf(2));
    }

    private String accountNumber(String cpf) {
        String digits = cpf == null ? "" : cpf.replaceAll("\\D", "");
        if (digits.length() >= 4) {
            return digits.substring(0, 4);
        }
        return digits;
    }

    private String managerCpfFromAuthorization(String authorization) {
        String email = emailFromAuthorization(authorization);
        if (email.isBlank()) {
            throw new IllegalStateException("Gerente responsavel nao identificado");
        }

        Map[] managers = restTemplate.getForObject(managerServiceUrl + "/managers", Map[].class);
        if (managers != null) {
            for (Map manager : managers) {
                Object managerEmail = manager.get("email");
                Object managerCpf = manager.get("cpf");
                if (email.equals(managerEmail) && managerCpf != null) {
                    return managerCpf.toString();
                }
            }
        }
        throw new IllegalStateException("Gerente responsavel nao encontrado");
    }

    private Set<String> customerCpfsByManager(String cpfManager) {
        Set<String> customerCpfs = new HashSet<>();
        Map[] accounts = restTemplate.getForObject(accountServiceUrl + "/accounts/manager/" + cpfManager, Map[].class);
        if (accounts != null) {
            for (Map account : accounts) {
                Object cpfCustomer = account.get("cpfCustomer");
                if (cpfCustomer != null) {
                    customerCpfs.add(cpfCustomer.toString());
                }
            }
        }
        return customerCpfs;
    }

    private Map<String, Object> customerResponseWithBalance(CustomerData customer) {
        CustomerResponse response = CustomerDtoMapper.toResponse(customer);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("cpf", response.getCpf());
        body.put("nome", response.getNome());
        body.put("name", response.getName());
        body.put("email", response.getEmail());
        body.put("telefone", response.getTelefone());
        body.put("phone", response.getPhone());
        body.put("salario", response.getSalario());
        body.put("salary", response.getSalary());
        body.put("endereco", response.getAddress());
        body.put("address", response.getAddress());
        body.put("conta", response.getConta());
        body.put("limite", response.getLimite());
        body.put("saldo", customerBalance(response.getConta()));
        body.put("gerente", customerManager(response.getConta()));
        body.put("registrationStatus", response.getRegistrationStatus());
        body.put("pendingManagerCpf", response.getPendingManagerCpf());
        return body;
    }

    private String customerManager(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            return "";
        }
        try {
            Map account = restTemplate.getForObject(accountServiceUrl + "/accounts/" + accountNumber, Map.class);
            if (account == null) {
                return "";
            }
            Object managerCpf = account.get("cpfManager");
            return managerCpf == null ? "" : managerCpf.toString();
        } catch (RuntimeException exception) {
            return "";
        }
    }

    private BigDecimal customerBalance(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            Map balance = restTemplate.getForObject(accountServiceUrl + "/accounts/" + accountNumber + "/saldo", Map.class);
            if (balance == null) {
                return BigDecimal.ZERO;
            }
            Object value = balance.get("saldo");
            if (value instanceof Number number) {
                return BigDecimal.valueOf(number.doubleValue());
            }
            if (value != null) {
                return new BigDecimal(value.toString());
            }
        } catch (RuntimeException exception) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> bestCustomersByBalance() {
        Map<String, CustomerData> customersByCpf = customerService.getAllCustomers()
                .stream()
                .collect(java.util.stream.Collectors.toMap(CustomerData::getCpf, customer -> customer, (first, second) -> first));

        Map<String, Object>[] accounts = restTemplate.getForObject(accountServiceUrl + "/accounts", Map[].class);
        if (accounts == null) {
            return List.of();
        }

        return Arrays.stream(accounts)
                .map(account -> (Map<String, Object>) account)
                .sorted((left, right) -> balanceFromAccount(right).compareTo(balanceFromAccount(left)))
                .map(account -> customerWithBalance(customersByCpf.get(valueAsString(account.get("cpfCustomer"))), account))
                .filter(customer -> customer != null)
                .limit(3)
                .toList();
    }

    private Map<String, Object> customerWithBalance(CustomerData customer, Map<String, Object> account) {
        if (customer == null) {
            return null;
        }
        return Map.of(
                "cpf", customer.getCpf(),
                "nome", customer.getName(),
                "name", customer.getName(),
                "email", customer.getEmail(),
                "telefone", customer.getPhone() == null ? "" : customer.getPhone(),
                "salario", customer.getSalary() == null ? BigDecimal.ZERO : customer.getSalary(),
                "salary", customer.getSalary() == null ? BigDecimal.ZERO : customer.getSalary(),
                "saldo", balanceFromAccount(account)
        );
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

    private String roleFromAuthorization(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return "";
        }

        String token = authorization.trim()
                .replaceFirst("(?i)^Bearer\\s+", "")
                .replace("\"", "")
                .trim();
        if (token.isBlank()) {
            return "";
        }

        try {
            String[] parts = decodeToken(token).split(":");
            return parts.length >= 2 ? parts[1] : "";
        } catch (IllegalArgumentException exception) {
            return "";
        }
    }

    private String emailFromAuthorization(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return "";
        }

        String token = authorization.trim()
                .replaceFirst("(?i)^Bearer\\s+", "")
                .replace("\"", "")
                .trim();
        if (token.isBlank()) {
            return "";
        }

        String payload = decodeToken(token);
        if (payload.isBlank()) {
            return "";
        }

        try {
            Map<String, Object> payloadMap = new com.fasterxml.jackson.databind.ObjectMapper().readValue(payload, Map.class);
            Object email = payloadMap.get("email");
            return email == null ? "" : email.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String decodeToken(String token) {
        if (token == null || token.isBlank()) {
            return "";
        }

        String[] parts = token.trim().split("\\.");
        if (parts.length != 3) {
            return "";
        }

        String payload = parts[1];
        int padding = payload.length() % 4;
        if (padding > 0) {
            payload = payload + "=".repeat(4 - padding);
        }

        try {
            return new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            return "";
        }
    }

    private record CreateClientUserRequest(
            String cpf,
            String email,
            String senha,
            String nome
    ) {
    }

    private record AccountRequest(
            String cpfCustomer,
            String number,
            LocalDate creationDate,
            BigDecimal balance,
            BigDecimal limit,
            String cpfManager
    ) {
    }
}
