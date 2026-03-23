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

// 🔐 Tipo interno com senha
type UsuarioInterno = User & { senha: string };

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private usuarioAtual = new BehaviorSubject<User | null>(null);
  private tokenAtual = new BehaviorSubject<string | null>(null);

  public usuario$ = this.usuarioAtual.asObservable();
  public token$ = this.tokenAtual.asObservable();

  private usuariosCadastrados: Map<string, UsuarioInterno> = new Map();
  private clientesPendentes: Map<string, ClienteRegistro> = new Map();

  constructor() {
    this.carregarDadosLocais();

    // 🔥 garante dados para login funcionar
    if (this.usuariosCadastrados.size === 0) {
      this.inicializarDadosExemplo();
    }
  }

  private carregarDadosLocais() {
    const usuarioSalvo = localStorage.getItem('usuario');
    const tokenSalvo = localStorage.getItem('token');

    if (usuarioSalvo && tokenSalvo) {
      this.usuarioAtual.next(JSON.parse(usuarioSalvo));
      this.tokenAtual.next(tokenSalvo);
    }

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
            gerente: usuario.gerente,
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
      if (usuario.cpf === cpf) return true;
    }
    for (const cliente of this.clientesPendentes.values()) {
      if (cliente.cpf === cpf) return true;
    }
    return false;
  }

  private gerarToken(): string {
    return Math.random().toString(36).substring(2) + Date.now().toString(36);
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
      const usuario: UsuarioInterno = {
        id: this.gerarId(),
        email: cliente.email,
        nome: cliente.nome,
        cpf: cliente.cpf,
        perfil: 'cliente',
        senha: senhaAleatoria,
        saldo: 0,
        limite: cliente.salario >= 2000 ? cliente.salario / 2 : 0,
        numeroConta: this.gerarNumeroConta(),
        gerente: 'Gerente Padrão',
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
    return 'id_' + Math.random().toString(36).substring(2, 9);
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

  inicializarDadosExemplo(): void {
    const usuariosExemplo: { [key: string]: UsuarioInterno } = {
      'admin@bantads.com': {
        id: 'admin_001',
        email: 'admin@bantads.com',
        nome: 'Admin Sistema',
        cpf: '123456',
        perfil: 'admin',
        senha: 'admin123',
      },
      'gerente@bantads.com': {
        id: 'gerente_001',
        email: 'gerente@bantads.com',
        nome: 'Jão Gerente',
        cpf: '98765432101',
        perfil: 'gerente',
        senha: 'gerente123',
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
      },
    };

    this.usuariosCadastrados = new Map(Object.entries(usuariosExemplo));
    this.salvarUsuariosCadastrados();
  }

  transferir(numeroContaDestino: string, valor: number): void {
    const usuario = this.usuarioAtual.value;

    if (!usuario) throw new Error('Usuário não autenticado');

    if (!valor || valor <= 0) throw new Error('Valor inválido');

    const valorFinal = parseFloat(valor.toFixed(2));

    if (usuario.numeroConta === numeroContaDestino) {
      throw new Error('Não pode transferir para a própria conta');
    }

    const usuarioCompleto = this.usuariosCadastrados.get(usuario.email);
    if (!usuarioCompleto) throw new Error('Usuário não encontrado');

    let usuarioDestino: UsuarioInterno | null = null;

    for (const u of this.usuariosCadastrados.values()) {
      if (u.numeroConta === numeroContaDestino) {
        usuarioDestino = u;
        break;
      }
    }

    if (!usuarioDestino) throw new Error('Conta destino não encontrada');

    const saldoDisponivel =
      (usuarioCompleto.saldo || 0) + (usuarioCompleto.limite || 0);

    if (valorFinal > saldoDisponivel) {
      throw new Error('Saldo insuficiente');
    }

    usuarioCompleto.saldo = (usuarioCompleto.saldo || 0) - valorFinal;
    usuarioDestino.saldo = (usuarioDestino.saldo || 0) + valorFinal;

    this.usuariosCadastrados.set(usuario.email, usuarioCompleto);
    this.usuariosCadastrados.set(usuarioDestino.email, usuarioDestino);

    this.salvarUsuariosCadastrados();

    this.usuarioAtual.next({
      ...usuario,
      saldo: usuarioCompleto.saldo,
    });
  }
}
