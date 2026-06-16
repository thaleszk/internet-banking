package com.internet.banking.customer.microservice.service.impl;

import com.internet.banking.customer.microservice.dao.CustomerRepository;
import com.internet.banking.customer.microservice.data.CustomerData;
import com.internet.banking.customer.microservice.exception.CustomerAlreadyExistsException;
import com.internet.banking.customer.microservice.exception.CustomerNotFoundException;
import com.internet.banking.customer.microservice.exception.ProcessingException;
import com.internet.banking.customer.microservice.mapper.AddressMapper;
import com.internet.banking.customer.microservice.mapper.CustomerMapper;
import com.internet.banking.customer.microservice.model.CustomerModel;
import com.internet.banking.customer.microservice.model.RegistrationStatus;
import com.internet.banking.customer.microservice.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// @Service avisa o Spring que essa classe é um serviço gerenciado automaticamente pelo framework
// "implements CustomerService" significa que essa classe assina um contrato e é obrigada a ter todos os métodos definidos nele
@Service
public class DefaultCustomerService implements CustomerService {

    // repository é o objeto responsável por falar com o banco de dados
    private final CustomerRepository repository;

    // Construtor: quando o Spring criar essa classe, vai entregar o repository automaticamente (injeção de dependência)
    public DefaultCustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    // @Override confirma que esse método existe porque o contrato CustomerService exigiu
    // Usado pelo GERENTE para cadastrar um cliente diretamente (já aprovado, sem precisar de aprovação)
    // Fluxo: valida → verifica duplicidade → converte → define status ACTIVE → salva → retorna
    @Override
    public CustomerData createCustomer(final CustomerData customerData) {
        // valida se os dados obrigatórios (CPF e endereço) foram informados
        validateCustomer(customerData);

        // verifica no banco se já existe um cliente com esse CPF antes de salvar
        if (repository.existsByCpf(customerData.getCpf())) {
            throw new CustomerAlreadyExistsException(
                    "Cliente ja cadastrado para o CPF: " + customerData.getCpf()
            );
        }

        // converte CustomerData (formato do código) para CustomerModel (formato do banco)
        CustomerModel customerModel = CustomerMapper.toModel(customerData);
        // proteção: se a conversão falhou e retornou nulo, lança exceção
        if (Objects.isNull(customerModel)) {
            throw new ProcessingException("Erro ao criar os dados do cliente");
        }
        // gerente cadastrou diretamente: cliente já nasce como ATIVO, sem precisar de aprovação
        customerModel.setRegistrationStatus(RegistrationStatus.ACTIVE);
        customerModel.setPendingManagerCpf(null);
        // salva no banco (Spring Data executa o INSERT automaticamente, sem precisar escrever SQL)
        CustomerModel savedCustomer = repository.save(customerModel);

        // converte de volta para CustomerData e retorna
        return CustomerMapper.toData(savedCustomer);
    }

    // Busca um único cliente no banco pelo CPF
    // Fluxo: busca no banco → se não achar lança exceção → se achar converte e retorna
    @Override
    public CustomerData getCustomerByCpf(final String cpf) {
        // findByCpf retorna um Optional (envelope que pode estar vazio)
        // orElseThrow: se estiver vazio lança exceção — nunca retorna nulo
        CustomerModel customerModel = repository.findByCpf(cpf)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente nao encontrado para o CPF: " + cpf));

        // converte CustomerModel para CustomerData e retorna
        return CustomerMapper.toData(customerModel);
    }

    // Retorna todos os clientes do banco convertidos para CustomerData
    // Fluxo: busca todos → passa pela esteira de conversão → devolve lista
    @Override
    public List<CustomerData> getAllCustomers() {
        return repository.findAll()            // busca todos os registros do banco
                .stream()                      // transforma em fluxo para processar item a item (como uma esteira)
                .map(CustomerMapper::toData)   // converte cada CustomerModel para CustomerData
                .collect(Collectors.toList()); // junta tudo em uma lista e retorna
    }

    // Atualiza os dados de um cliente existente — o CPF nunca é alterado, é só o identificador
    // Fluxo: valida → busca existente no banco → sobrescreve campos → salva → retorna
    @Override
    public CustomerData updateCustomer(final String cpf, final CustomerData customerData) {
        // valida os novos dados antes de qualquer coisa
        validateCustomer(customerData);

        // busca o cliente atual no banco — se não existir, lança exceção
        CustomerModel existingCustomer = repository.findByCpf(cpf)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente nao encontrado para o CPF: " + cpf));

        // sobrescreve campo a campo com os novos valores (CPF não está aqui pois nunca muda)
        existingCustomer.setName(customerData.getName());
        existingCustomer.setEmail(customerData.getEmail());
        existingCustomer.setPhone(customerData.getPhone());
        existingCustomer.setSalary(customerData.getSalary());
        existingCustomer.setAddress(AddressMapper.toModel(customerData.getAddress()));

        // save() com objeto que já existe no banco executa UPDATE (não INSERT)
        CustomerModel savedCustomer = repository.save(existingCustomer);

        return CustomerMapper.toData(savedCustomer);
    }

    // Remove um cliente do banco — retorno void porque não há dado para devolver
    // Fluxo: busca no banco → se não achar lança exceção → deleta
    @Override
    public void deleteCustomer(final String cpf) {
        // busca antes de deletar para garantir mensagem de erro clara se não existir
        CustomerModel existingCustomer = repository.findByCpf(cpf)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente nao encontrado para o CPF: " + cpf));

        // Spring Data executa o DELETE automaticamente
        repository.delete(existingCustomer);
    }

    // Lista apenas os clientes com status PENDING_APPROVAL, ordenados por nome A→Z
    // Alimenta a tela de "cadastros pendentes" que o gerente visualiza
    @Override
    public List<CustomerData> listPendingRegistration() {
        // Spring Data gera o SQL automaticamente pelo nome do método (findBy + OrderBy)
        // RegistrationStatus.PENDING_APPROVAL é um enum — valor fixo, evita erro de digitação
        return repository.findByRegistrationStatusOrderByNameAsc(RegistrationStatus.PENDING_APPROVAL)
                .stream()
                .map(CustomerMapper::toData)
                .collect(Collectors.toList());
    }

    // Usado pelo PRÓPRIO CLIENTE ao se auto-cadastrar — nasce com status PENDENTE, aguarda aprovação do gerente
    // Diferença do createCustomer: status é PENDING_APPROVAL ao invés de ACTIVE
    // Fluxo: valida → verifica duplicidade → converte → define status PENDENTE → salva → retorna
    @Override
    public CustomerData createPendingRegistration(final CustomerData customerData) {
        validateCustomer(customerData);
        // mensagem diferente: avisa que pode ser cadastro ativo OU solicitação pendente já existente
        if (repository.existsByCpf(customerData.getCpf())) {
            throw new CustomerAlreadyExistsException(
                    "CPF ja cadastrado ou ja existe solicitacao pendente para: " + customerData.getCpf()
            );
        }
        CustomerModel model = CustomerMapper.toModel(customerData);
        if (Objects.isNull(model)) {
            throw new ProcessingException("Erro ao criar os dados do cliente");
        }
        // cliente se auto-cadastrou: fica pendente até o gerente aprovar ou rejeitar
        model.setRegistrationStatus(RegistrationStatus.PENDING_APPROVAL);
        model.setPendingManagerCpf(null);
        CustomerModel saved = repository.save(model);
        return CustomerMapper.toData(saved);
    }

    // Gerente aprova um cadastro pendente — apenas muda o status para ACTIVE no banco
    // Quem cria conta, senha e credenciais é o CustomerController, antes de chamar esse método
    // Fluxo: busca → muda status para ACTIVE → limpa gerente pendente → salva e retorna
    @Override
    public CustomerData approveRegistration(final String cpf) {
        CustomerModel customerModel = repository.findByCpf(cpf)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente nao encontrado para o CPF: " + cpf));

        // aprovação de fato: muda de PENDING_APPROVAL para ACTIVE
        customerModel.setRegistrationStatus(RegistrationStatus.ACTIVE);
        // limpa o gerente pendente, pois o processo de aprovação foi concluído
        customerModel.setPendingManagerCpf(null);

        // save() e toData() encadeados: mais compacto, mesmo resultado que duas linhas separadas
        return CustomerMapper.toData(repository.save(customerModel));
    }

    // Gerente rejeita um cadastro pendente — deleta o registro do banco
    // Decisão de design: rejeitar deleta (não cria status REJECTED) para liberar o CPF para nova tentativa
    // Fluxo: busca → deleta
    @Override
    public void rejectRegistration(final String cpf) {
        CustomerModel customerModel = repository.findByCpf(cpf)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente nao encontrado para o CPF: " + cpf));

        repository.delete(customerModel);
    }

    // Método auxiliar privado — só pode ser chamado dentro desta classe
    // Princípio DRY (Don't Repeat Yourself): centraliza validações usadas por createCustomer, createPendingRegistration e updateCustomer
    private void validateCustomer(final CustomerData customerData) {
        // verifica se o objeto inteiro foi passado
        if (customerData == null) {
            throw new IllegalArgumentException("Cliente nao pode ser nulo");
        }

        // || significa "ou": qualquer uma das condições já é suficiente para lançar a exceção
        // isBlank() captura tanto campo vazio quanto campo com apenas espaços em branco
        if (customerData.getCpf() == null || customerData.getCpf().isBlank()) {
            throw new IllegalArgumentException("CPF do cliente e obrigatorio");
        }

        if (customerData.getAddress() == null) {
            throw new IllegalArgumentException("Endereco do cliente e obrigatorio");
        }
    }
}
