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

// @RestController indica que essa classe recebe requisições HTTP e devolve respostas (é a porta de entrada do microserviço)
// @RequestMapping define que todas as rotas desta classe começam com /customers
@RestController
@RequestMapping("/customers")
public class CustomerController {

    // logger: registra mensagens no console do servidor enquanto o sistema roda
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    // customerService: contém as regras de negócio — o Controller delega toda lógica para ele
    private final CustomerService customerService;
    // restTemplate: ferramenta para fazer chamadas HTTP para outros microserviços
    private final RestTemplate restTemplate = new RestTemplate();

    // @Value lê variáveis de ambiente ou usa o valor padrão (após o ":") caso a variável não exista
    // permite configurar URLs diferentes para Docker (produção) e localhost (desenvolvimento)
    @Value("${AUTH_SERVICE_URL:http://localhost:8081}")
    private String authServiceUrl;

    @Value("${ACCOUNT_SERVICE_URL:http://localhost:8083}")
    private String accountServiceUrl;

    @Value("${MANAGER_SERVICE_URL:http://localhost:8084}")
    private String managerServiceUrl;

    // Construtor: Spring injeta o CustomerService automaticamente (injeção de dependência)
    public CustomerController(final CustomerService customerService) {
        this.customerService = customerService;
    }

    // Rota GET /customers/registration/pending
    // Retorna lista de clientes aguardando aprovação do gerente
    @GetMapping("/registration/pending")
    public ResponseEntity<List<CustomerResponse>> listPendingRegistrations() {
        // ResponseEntity.ok() = empacota a resposta com status HTTP 200 OK
        return ResponseEntity.ok(CustomerDtoMapper.toResponseList(customerService.listPendingRegistration()));
    }

    // Rota POST /customers/registration/request
    // Usado pelo PRÓPRIO CLIENTE para se auto-cadastrar — gera registro com status PENDING_APPROVAL
    // @Valid aciona validações do CustomerRequest antes de entrar no método
    // @RequestBody converte o JSON enviado pelo frontend para o objeto CustomerRequest
    @PostMapping("/registration/request")
    public ResponseEntity<CustomerResponse> requestSelfRegistration(@Valid @RequestBody final CustomerRequest request) {
        CustomerData created = customerService.createPendingRegistration(CustomerDtoMapper.toData(request));
        // HttpStatus.CREATED = status HTTP 201, indica que um recurso foi criado (diferente do 200)
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerDtoMapper.toResponse(created));
    }

    // Rota POST /customers/register
    // Usado pelo GERENTE para cadastrar um cliente diretamente — gera registro com status ACTIVE
    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody final CustomerRequest request) {
        CustomerData createdCustomer = customerService.createCustomer(CustomerDtoMapper.toData(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerDtoMapper.toResponse(createdCustomer));
    }

    // Rota POST /customers/{cpf}/aprovar
    // Método mais importante do Controller — orquestra 3 microserviços em uma única operação
    // @PathVariable captura o CPF diretamente da URL: /customers/12345678900/aprovar
    // @RequestHeader captura o token JWT do cabeçalho para identificar o gerente responsável
    @PostMapping("/{cpf}/aprovar")
    public ResponseEntity<CustomerResponse> approveCustomer(@PathVariable final String cpf,
                                                            @RequestHeader(value = "Authorization", required = false) final String authorization) {
        // bloqueia clientes de aprovar cadastros — apenas gerentes podem
        if (isCustomerToken(authorization)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // busca dados do cliente para usar nas próximas etapas
        CustomerData customer = customerService.getCustomerByCpf(cpf);
        // gera senha temporária aleatória de 12 caracteres
        String senha = generateTemporaryPassword();
        // 1º: cria credencial de login no microserviço de autenticação
        createClientCredential(customer, senha);
        // 2º: cria conta bancária no microserviço de contas
        createClientAccount(customer, authorization);
        // 3º: só depois muda o status para ACTIVE no banco — garante que conta e credencial existem antes
        CustomerData approvedCustomer = customerService.approveRegistration(cpf);
        // em produção real, aqui dispararia um e-mail com a senha; no projeto, aparece só no log
        logger.info(">>>> SENHA ENVIADA POR E-MAIL PARA {}: {}", approvedCustomer.getEmail(), senha);
        return ResponseEntity.ok(CustomerDtoMapper.toResponse(approvedCustomer));
    }

    // Rota POST /customers/{cpf}/rejeitar
    // Gerente rejeita um cadastro pendente — deleta o registro do banco
    @PostMapping("/{cpf}/rejeitar")
    public ResponseEntity<Void> rejectCustomer(@PathVariable final String cpf,
                                               @RequestHeader(value = "Authorization", required = false) final String authorization) {
        // mesma proteção: apenas gerentes podem rejeitar
        if (isCustomerToken(authorization)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        customerService.rejectRegistration(cpf);
        // ResponseEntity.ok().build() = status 200 sem corpo, pois não há dado para devolver
        return ResponseEntity.ok().build();
    }

    // Rota GET /customers/{cpf}
    // Retorna dados do cliente enriquecidos com saldo e gerente (buscados em outros microserviços)
    // Retorna Map ao invés de CustomerResponse porque carrega campos extras que o DTO padrão não tem
    @GetMapping("/{cpf}")
    public ResponseEntity<Map<String, Object>> getCustomerByCpf(@PathVariable final String cpf) {
        CustomerData customer = customerService.getCustomerByCpf(cpf);
        // customerResponseWithBalance() busca saldo e gerente antes de montar a resposta
        return ResponseEntity.ok(customerResponseWithBalance(customer));
    }

    // Rota GET /customers (com filtros opcionais via ?filtro=...)
    // Comportamento muda conforme o filtro e o papel do usuário logado
    // ResponseEntity<?> — o "?" indica que o tipo do retorno pode variar conforme o filtro
    @GetMapping
    public ResponseEntity<?> getAllCustomers(@RequestParam(required = false) String filtro,
                                             @RequestHeader(value = "Authorization", required = false) String authorization) {
        // filtro "para_aprovar": retorna apenas clientes com status PENDING_APPROVAL
        if ("para_aprovar".equalsIgnoreCase(filtro)) {
            return ResponseEntity.ok(CustomerDtoMapper.toResponseList(customerService.listPendingRegistration()));
        }

        // filtro "melhores_clientes": retorna os 3 clientes com maior saldo
        if ("melhores_clientes".equalsIgnoreCase(filtro)) {
            return ResponseEntity.ok(bestCustomersByBalance());
        }

        List<CustomerData> customers = customerService.getAllCustomers();

        // filtro "adm_relatorio_clientes": retorna todos os clientes ordenados por nome A→Z
        if ("adm_relatorio_clientes".equalsIgnoreCase(filtro)) {
            customers = customers.stream()
                    // nullsLast: clientes sem nome não causam erro, ficam no final
                    // compareToIgnoreCase: ordenação ignora maiúsculas/minúsculas
                    .sorted(Comparator.comparing(CustomerData::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
                    .toList();
        } else if ("GERENTE".equals(roleFromAuthorization(authorization))) {
            // sem filtro especial e usuário é gerente: exibe apenas os clientes vinculados a ele
            Set<String> customerCpfs = customerCpfsByManager(managerCpfFromAuthorization(authorization));
            customers = customers.stream()
                    .filter(customer -> customerCpfs.contains(customer.getCpf()))
                    .sorted(Comparator.comparing(CustomerData::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
                    .toList();
        }
        return ResponseEntity.ok(CustomerDtoMapper.toResponseList(customers));
    }

    // Rota PUT /customers/{cpf}
    // Atualiza dados de um cliente existente — CPF é só o identificador, nunca é alterado
    @PutMapping("/{cpf}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable final String cpf,
                                                           @Valid @RequestBody final CustomerRequest request) {
        CustomerData updatedCustomer = customerService.updateCustomer(cpf, CustomerDtoMapper.toData(request));
        return ResponseEntity.ok(CustomerDtoMapper.toResponse(updatedCustomer));
    }

    // Rota DELETE /customers/{cpf}
    // Remove o cliente do banco — status 204 NO CONTENT indica sucesso sem dado para devolver
    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable final String cpf) {
        customerService.deleteCustomer(cpf);
        // 204 NO CONTENT: diferente do 200, comunica explicitamente que não há corpo na resposta
        return ResponseEntity.noContent().build();
    }

    // Verifica se o token JWT pertence a um CLIENTE — usado para bloquear aprovação/rejeição por clientes
    // Retorna false (não bloqueia) quando o token está ausente ou inválido — só bloqueia com certeza de CLIENTE
    private boolean isCustomerToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return false;
        }

        // limpa o token: remove "Bearer ", aspas e espaços extras
        String token = authorization.trim()
                .replaceFirst("(?i)^Bearer\\s+", "")
                .replace("\"", "")
                .trim();
        if (token.isBlank()) {
            return false;
        }

        try {
            String payload = decodeToken(token);
            // o payload decodificado tem formato "algo:PAPEL" — a segunda parte é o papel do usuário
            String[] parts = payload.split(":");
            return parts.length >= 2 && "CLIENTE".equals(parts[1]);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    // Gera senha temporária aleatória de 12 caracteres para o cliente usar no primeiro login
    // UUID garante aleatoriedade praticamente impossível de repetir
    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    // Chama o microserviço de autenticação para criar o login do cliente aprovado
    private void createClientCredential(CustomerData customer, String senha) {
        CreateClientUserRequest request = new CreateClientUserRequest(
                customer.getCpf(),
                customer.getEmail(),
                senha,
                customer.getName()
        );
        // restTemplate.postForEntity: faz requisição POST para outro microserviço
        // Void.class: não espera nenhum dado de volta, só confirmação de sucesso
        restTemplate.postForEntity(authServiceUrl + "/auth/users/clientes", request, Void.class);
    }

    // Chama o microserviço de contas para criar a conta bancária do cliente aprovado
    private void createClientAccount(CustomerData customer, String authorization) {
        AccountRequest request = new AccountRequest(
                customer.getCpf(),
                accountNumber(customer.getCpf()),  // número gerado a partir do CPF
                LocalDate.now(),                    // data de criação é hoje
                BigDecimal.ZERO,                    // saldo inicial sempre zero
                accountLimit(customer.getSalary()), // limite calculado com base no salário
                managerCpfFromAuthorization(authorization) // gerente responsável extraído do token
        );
        restTemplate.postForEntity(accountServiceUrl + "/accounts", request, Void.class);
    }

    // Regra de negócio: limite é metade do salário, mas zero se salário for nulo ou menor que R$2.000
    // compareTo é usado porque BigDecimal não suporta operadores < > diretamente
    private BigDecimal accountLimit(BigDecimal salary) {
        if (salary == null || salary.compareTo(BigDecimal.valueOf(2000)) < 0) {
            return BigDecimal.ZERO;
        }
        return salary.divide(BigDecimal.valueOf(2));
    }

    // Gera número da conta usando os 4 primeiros dígitos do CPF (sem formatação)
    // Ex: CPF "123.456.789-00" → dígitos "12345678900" → número "1234"
    private String accountNumber(String cpf) {
        // \\D em regex significa "qualquer caractere que não seja dígito"
        String digits = cpf == null ? "" : cpf.replaceAll("\\D", "");
        if (digits.length() >= 4) {
            return digits.substring(0, 4);
        }
        return digits;
    }

    // Extrai o CPF do gerente logado cruzando o email do token com a lista de gerentes do MS de gerentes
    // Necessário porque o token armazena email, mas a conta precisa do CPF do gerente
    private String managerCpfFromAuthorization(String authorization) {
        String email = emailFromAuthorization(authorization);
        if (email.isBlank()) {
            throw new IllegalStateException("Gerente responsavel nao identificado");
        }

        // busca todos os gerentes no microserviço de gerentes
        Map[] managers = restTemplate.getForObject(managerServiceUrl + "/managers", Map[].class);
        if (managers != null) {
            // percorre até encontrar o gerente cujo email bate com o email do token
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

    // Busca no MS de contas todos os CPFs de clientes vinculados a um gerente específico
    // Retorna Set (sem duplicatas) para evitar que um cliente apareça duas vezes se tiver múltiplas contas
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

    // Monta resposta enriquecida com saldo e gerente — buscados no MS de contas em tempo real
    // Usa LinkedHashMap para garantir que os campos apareçam sempre na mesma ordem no JSON
    // Campos em português e inglês garantem compatibilidade com diferentes consumidores da API
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
        // saldo e gerente não existem no banco deste MS — são buscados no MS de contas
        body.put("saldo", customerBalance(response.getConta()));
        body.put("gerente", customerManager(response.getConta()));
        body.put("registrationStatus", response.getRegistrationStatus());
        body.put("pendingManagerCpf", response.getPendingManagerCpf());
        return body;
    }

    // Busca o CPF do gerente responsável pela conta no MS de contas
    // try/catch garante resiliência: se o MS de contas estiver fora, retorna "" ao invés de quebrar
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
            // MS de contas indisponível: retorna vazio para não impedir exibição do cliente
            return "";
        }
    }

    // Busca o saldo atual da conta no MS de contas
    // try/catch: se o MS de contas estiver fora, retorna zero ao invés de quebrar a aplicação
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
            // pattern matching do Java moderno: instanceof verifica e já declara a variável em uma linha
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

    // Retorna os 3 clientes com maior saldo — usado pelo filtro "melhores_clientes"
    // Cruza dados do MS de contas (saldos) com dados locais (informações dos clientes)
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> bestCustomersByBalance() {
        // monta mapa de CPF → CustomerData para busca rápida ao cruzar com as contas
        Map<String, CustomerData> customersByCpf = customerService.getAllCustomers()
                .stream()
                .collect(java.util.stream.Collectors.toMap(CustomerData::getCpf, customer -> customer, (first, second) -> first));

        // busca todas as contas no MS de contas
        Map<String, Object>[] accounts = restTemplate.getForObject(accountServiceUrl + "/accounts", Map[].class);
        if (accounts == null) {
            return List.of();
        }

        return Arrays.stream(accounts)
                .map(account -> (Map<String, Object>) account)
                .sorted((left, right) -> balanceFromAccount(right).compareTo(balanceFromAccount(left))) // ordena por saldo decrescente
                .map(account -> customerWithBalance(customersByCpf.get(valueAsString(account.get("cpfCustomer"))), account))
                .filter(customer -> customer != null) // remove contas cujo cliente não foi encontrado
                .limit(3) // pega só os 3 primeiros (maior saldo)
                .toList();
    }

    // Monta o objeto de resposta de um cliente com seu saldo incluído
    // Retorna null se o cliente não foi encontrado — filtrado no bestCustomersByBalance
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

    // Extrai o saldo de um Map de conta retornado pelo MS de contas
    // Trata tanto Number quanto String para robustez contra variações no formato da resposta
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

    // Utilitário: converte qualquer Object para String com segurança (retorna "" se nulo)
    private String valueAsString(Object value) {
        return value == null ? "" : value.toString();
    }

    // Extrai o papel do usuário (GERENTE ou CLIENTE) do token JWT
    // Usado em getAllCustomers para decidir se filtra por gerente ou exibe todos
    private String roleFromAuthorization(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return "";
        }

        // limpa o token removendo "Bearer ", aspas e espaços
        String token = authorization.trim()
                .replaceFirst("(?i)^Bearer\\s+", "")
                .replace("\"", "")
                .trim();
        if (token.isBlank()) {
            return "";
        }

        try {
            // o payload tem formato "algo:PAPEL" — a segunda parte é o papel
            String[] parts = decodeToken(token).split(":");
            return parts.length >= 2 ? parts[1] : "";
        } catch (IllegalArgumentException exception) {
            return "";
        }
    }

    // Extrai o email do usuário logado do token JWT
    // Usado em managerCpfFromAuthorization para identificar o gerente pelo email
    private String emailFromAuthorization(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return "";
        }

        // limpa o token removendo "Bearer ", aspas e espaços
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
            // faz parse do payload como JSON e extrai o campo "email"
            Map<String, Object> payloadMap = new com.fasterxml.jackson.databind.ObjectMapper().readValue(payload, Map.class);
            Object email = payloadMap.get("email");
            return email == null ? "" : email.toString();
        } catch (Exception e) {
            return "";
        }
    }

    // Decodifica o payload de um token JWT (a parte do meio, entre os dois pontos)
    // JWT tem 3 partes separadas por ".": header.payload.signature — apenas o payload nos interessa
    // O payload é codificado em Base64URL e contém as informações do usuário logado
    private String decodeToken(String token) {
        if (token == null || token.isBlank()) {
            return "";
        }

        // divide o token nas 3 partes — JWT sempre tem exatamente 3
        String[] parts = token.trim().split("\\.");
        if (parts.length != 3) {
            return "";
        }

        // a segunda parte (índice 1) é o payload
        String payload = parts[1];
        // Base64 exige que o tamanho seja múltiplo de 4 — adiciona "=" de padding se necessário
        int padding = payload.length() % 4;
        if (padding > 0) {
            payload = payload + "=".repeat(4 - padding);
        }

        try {
            // decodifica de Base64URL para texto legível (UTF-8)
            return new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            return "";
        }
    }

    // Record: estrutura de dados imutável e compacta do Java moderno
    // Usado como DTO interno para montar o corpo da requisição ao MS de autenticação
    private record CreateClientUserRequest(
            String cpf,
            String email,
            String senha,
            String nome
    ) {
    }

    // Record interno para montar o corpo da requisição ao MS de contas ao criar uma conta bancária
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
