import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface User {
  id?: string;
  email: string;
  nome: string;
  cpf: string;
  perfil: 'cliente' | 'gerente' | 'admin';
  saldo?: number;
  limite?: number;
  numeroConta?: string;
  gerente?: string;
}

export type TipoMovimentacao = 'deposito' | 'saque' | 'transferencia_enviada' | 'transferencia_recebida';

export interface Movimentacao {
  dataHora: string;
  tipo: TipoMovimentacao;
  valor: number;
  contaOrigem?: string;
  contaDestino?: string;
  nomeOrigem?: string;
  nomeDestino?: string;
}

export interface ClienteRegistro {
  nome: string;
  cpf: string;
  email: string;
  telefone: string;
  salario: number;
  cep: string;
  logradouro: string;
  numero: string;
  complemento?: string;
  cidade: string;
  estado: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private usuarioAtual = new BehaviorSubject<User | null>(null);
  private tokenAtual = new BehaviorSubject<string | null>(null);

  public usuario$ = this.usuarioAtual.asObservable();
  public token$ = this.tokenAtual.asObservable();

  // Dados simulados de usuários cadastrados
  private usuariosCadastrados: Map<string, any> = new Map();
  private clientesPendentes: Map<string, ClienteRegistro> = new Map();

  constructor() {
    this.carregarDadosLocais();
  }

  private carregarDadosLocais() {
    const usuarioSalvo = localStorage.getItem('usuario');
    const tokenSalvo = localStorage.getItem('token');
    
    if (usuarioSalvo && tokenSalvo) {
      this.usuarioAtual.next(JSON.parse(usuarioSalvo));
      this.tokenAtual.next(tokenSalvo);
    }

    // Carregar dados simulados
    const dadosSimulados = localStorage.getItem('usuariosCadastrados');
    if (dadosSimulados) {
      const usuarios = JSON.parse(dadosSimulados);
      this.usuariosCadastrados = new Map(Object.entries(usuarios));
    }

    const clientesPendSimulados = localStorage.getItem('clientesPendentes');
    if (clientesPendSimulados) {
      const clientes = JSON.parse(clientesPendSimulados);
      this.clientesPendentes = new Map(Object.entries(clientes));
    }
  }

  login(email: string, senha: string): Observable<User> {
    return new Observable((observer) => {
      setTimeout(() => {
        const usuario = this.usuariosCadastrados.get(email);
        
        if (usuario && usuario.senha === senha) {
          const userData: User = {
            id: usuario.id,
            email: usuario.email,
            nome: usuario.nome,
            cpf: usuario.cpf,
            perfil: usuario.perfil,
            saldo: usuario.saldo,
            limite: usuario.limite,
            numeroConta: usuario.numeroConta,
            gerente: usuario.gerente
          };

          const token = this.gerarToken();
          this.usuarioAtual.next(userData);
          this.tokenAtual.next(token);

          localStorage.setItem('usuario', JSON.stringify(userData));
          localStorage.setItem('token', token);

          observer.next(userData);
          observer.complete();
        } else {
          observer.error(new Error('Email ou senha incorretos'));
        }
      }, 500);
    });
  }

  logout(): void {
    this.usuarioAtual.next(null);
    this.tokenAtual.next(null);
    localStorage.removeItem('usuario');
    localStorage.removeItem('token');
  }

  autocadastro(dados: ClienteRegistro): Observable<{ sucesso: boolean; mensagem: string }> {
    return new Observable((observer) => {
      setTimeout(() => {
        if (this.cpfJaExiste(dados.cpf)) {
          observer.error(new Error('CPF já cadastrado no sistema'));
          return;
        }

        this.clientesPendentes.set(dados.cpf, dados);
        this.salvarClientesPendentes();

        observer.next({ sucesso: true, mensagem: 'Solicitação enviada para aprovação' });
        observer.complete();
      }, 500);
    });
  }

  private cpfJaExiste(cpf: string): boolean {
    for (const usuario of this.usuariosCadastrados.values()) {
      if (usuario.cpf === cpf) {
        return true;
      }
    }
    for (const cliente of this.clientesPendentes.values()) {
      if (cliente.cpf === cpf) {
        return true;
      }
    }
    return false;
  }

  private gerarToken(): string {
    return Math.random().toString(36).substr(2) + Date.now().toString(36);
  }

  obterUsuarioAtual(): User | null {
    return this.usuarioAtual.value;
  }

  estaAutenticado(): boolean {
    return this.tokenAtual.value !== null;
  }

  obterClientesPendentes(): ClienteRegistro[] {
    return Array.from(this.clientesPendentes.values());
  }

  aprovarCliente(cpf: string, senhaAleatoria: string): void {
    const cliente = this.clientesPendentes.get(cpf);
    if (cliente) {
      const usuario = {
        id: this.gerarId(),
        email: cliente.email,
        nome: cliente.nome,
        cpf: cliente.cpf,
        perfil: 'cliente',
        senha: senhaAleatoria,
        saldo: 0,
        limite: cliente.salario >= 2000 ? cliente.salario / 2 : 0,
        numeroConta: this.gerarNumeroConta(),
        telefone: cliente.telefone,
        salario: cliente.salario,
        cep: cliente.cep,
        logradouro: cliente.logradouro,
        numero: cliente.numero,
        complemento: cliente.complemento,
        cidade: cliente.cidade,
        estado: cliente.estado,
        gerente: 'Gerente Padrão',
        dataCriacao: new Date(),
        aprovado: true
      };

      this.usuariosCadastrados.set(cliente.email, usuario);
      this.clientesPendentes.delete(cpf);
      
      this.salvarUsuariosCadastrados();
      this.salvarClientesPendentes();
    }
  }

  rejeitarCliente(cpf: string): void {
    this.clientesPendentes.delete(cpf);
    this.salvarClientesPendentes();
  }

  private gerarId(): string {
    return 'id_' + Math.random().toString(36).substr(2, 9);
  }

  private gerarNumeroConta(): string {
    return Math.floor(1000 + Math.random() * 9000).toString();
  }

  private salvarUsuariosCadastrados(): void {
    const dados = Object.fromEntries(this.usuariosCadastrados);
    localStorage.setItem('usuariosCadastrados', JSON.stringify(dados));
  }

  private salvarClientesPendentes(): void {
    const dados = Object.fromEntries(this.clientesPendentes);
    localStorage.setItem('clientesPendentes', JSON.stringify(dados));
  }

  // ─── Movimentações ────────────────────────────────────────────────────────

  depositar(valor: number): Observable<{ sucesso: boolean; novoSaldo: number }> {
    return new Observable((observer) => {
      setTimeout(() => {
        const usuario = this.obterUsuarioAtual();
        if (!usuario || usuario.perfil !== 'cliente') {
          observer.error(new Error('Usuário não autenticado'));
          return;
        }
        const saldoAtual = usuario.saldo ?? 0;
        const novoSaldo = parseFloat((saldoAtual + valor).toFixed(2));
        this.atualizarSaldoUsuario(usuario, novoSaldo);
        this.registrarMovimentacao(usuario, { dataHora: new Date().toISOString(), tipo: 'deposito', valor });
        observer.next({ sucesso: true, novoSaldo });
        observer.complete();
      }, 400);
    });
  }

  sacar(valor: number): Observable<{ sucesso: boolean; novoSaldo: number }> {
    return new Observable((observer) => {
      setTimeout(() => {
        const usuario = this.obterUsuarioAtual();
        if (!usuario || usuario.perfil !== 'cliente') {
          observer.error(new Error('Usuário não autenticado'));
          return;
        }
        const saldoAtual = usuario.saldo ?? 0;
        const limite = usuario.limite ?? 0;
        if (valor > saldoAtual + limite) {
          observer.error(new Error('Saldo insuficiente (incluindo limite)'));
          return;
        }
        const novoSaldo = parseFloat((saldoAtual - valor).toFixed(2));
        this.atualizarSaldoUsuario(usuario, novoSaldo);
        this.registrarMovimentacao(usuario, { dataHora: new Date().toISOString(), tipo: 'saque', valor });
        observer.next({ sucesso: true, novoSaldo });
        observer.complete();
      }, 400);
    });
  }

  obterMovimentacoes(): Movimentacao[] {
    const usuario = this.obterUsuarioAtual();
    if (!usuario) return [];
    const chave = 'movimentacoes_' + (usuario.numeroConta ?? usuario.cpf);
    const dados = localStorage.getItem(chave);
    return dados ? JSON.parse(dados) : [];
  }

  private atualizarSaldoUsuario(usuario: User, novoSaldo: number): void {
    const usuarioAtualizado = { ...usuario, saldo: novoSaldo };
    this.usuarioAtual.next(usuarioAtualizado);
    localStorage.setItem('usuario', JSON.stringify(usuarioAtualizado));
    // Atualiza também no mapa de usuários cadastrados
    const dadosCompletos = this.usuariosCadastrados.get(usuario.email);
    if (dadosCompletos) {
      dadosCompletos.saldo = novoSaldo;
      this.usuariosCadastrados.set(usuario.email, dadosCompletos);
      this.salvarUsuariosCadastrados();
    }
  }

  private registrarMovimentacao(usuario: User, mov: Movimentacao): void {
    const chave = 'movimentacoes_' + (usuario.numeroConta ?? usuario.cpf);
    const historico = this.obterMovimentacoes();
    historico.unshift(mov); // mais recente primeiro
    localStorage.setItem(chave, JSON.stringify(historico));
  }

  inicializarDadosExemplo(): void {
    const usuariosExemplo = {
      'admin@bantads.com': {
        id: 'admin_001',
        email: 'admin@bantads.com',
        nome: 'Admin Sistema',
        cpf: '123456',
        perfil: 'admin',
        senha: 'admin123',
        dataCriacao: new Date()
      },
      'gerente@bantads.com': {
        id: 'gerente_001',
        email: 'gerente@bantads.com',
        nome: 'João Gerente',
        cpf: '98765432101',
        perfil: 'gerente',
        senha: 'gerente123',
        dataCriacao: new Date()
      },
      'cliente@example.com': {
        id: 'cliente_001',
        email: 'cliente@example.com',
        nome: 'Maria Cliente',
        cpf: '11122233344',
        perfil: 'cliente',
        senha: 'cliente123',
        saldo: 1500,
        limite: 1000,
        numeroConta: '1234',
        gerente: 'Jão Gerente',
        dataCriacao: new Date()
      }
    };

    this.usuariosCadastrados = new Map(Object.entries(usuariosExemplo));
    this.salvarUsuariosCadastrados();
  }
}
