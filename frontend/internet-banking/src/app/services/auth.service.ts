import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface User {
  id?: string;
  email: string;
  nome: string;
  cpf: string;
  perfil: 'cliente' | 'gerente' | 'admin';
  salario?: number;
  saldo?: number;
  limite?: number;
  numeroConta?: string;
  gerente?: string;
  telefone?: string;
  logradouro?: string;
  numero?: string;
  complemento?: string;
  cep?: string;
  cidade?: string;
  estado?: string;
}

export type TipoMovimentacao =
  | 'deposito'
  | 'saque'
  | 'transferencia_enviada'
  | 'transferencia_recebida';

export interface Movimentacao {
  dataHora: string;
  tipo: TipoMovimentacao;
  valor: number;
  contaOrigem?: string;
  contaDestino?: string;
  nomeOrigem?: string;
  nomeDestino?: string;
}

export interface GerenteResumo {
  nome: string;
  email: string;
  clienteCount: number;
  somaSaldosPositivos: number;
  somaSaldosNegativos: number;
}

export interface ClienteRelatorio {
  cpf: string;
  nome: string;
  email: string;
  salario?: number;
  numeroConta?: string;
  saldo?: number;
  limite?: number;
  gerenteCpf?: string;
  gerenteNome?: string;
}

export interface GerenteListagem {
  cpf: string;
  nome: string;
  email: string;
  telefone?: string;
  clienteCount: number;
  somaSaldosPositivos: number;
  somaSaldosNegativos: number;
}

export interface AtualizacaoGerente {
  nome: string;
  email: string;
  senha?: string;
}

export interface NovoGerente {
  nome: string;
  cpf: string;
  email: string;
  telefone: string;
  senha: string;
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
          const userData = this.mapearUsuarioPublico(usuario);
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

  autocadastro(
    dados: ClienteRegistro
  ): Observable<{ sucesso: boolean; mensagem: string }> {
    return new Observable((observer) => {
      setTimeout(() => {
        if (this.cpfJaExiste(dados.cpf)) {
          observer.error(new Error('CPF já cadastrado no sistema'));
          return;
        }

        this.clientesPendentes.set(dados.cpf, dados);
        this.salvarClientesPendentes();

        observer.next({
          sucesso: true,
          mensagem: 'Solicitação enviada para aprovação',
        });
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
    return Math.random().toString(36).substring(2) + Date.now().toString(36);
  }

  obterUsuarioAtual(): User | null {
    return this.usuarioAtual.value;
  }

  obterGerentesComIndicadores(): GerenteResumo[] {
    const usuarios = Array.from(this.usuariosCadastrados.values());
    const clientes = usuarios.filter((u) => u.perfil === 'cliente');
    const gerentes = usuarios.filter((u) => u.perfil === 'gerente');

    const resumo = gerentes.map((gerente) => {
      const clientesDoGerente = clientes.filter(
        (cliente) => cliente.gerente === gerente.nome
      );
      const somaSaldosPositivos = clientesDoGerente
        .map((cliente) => cliente.saldo ?? 0)
        .filter((saldo) => saldo >= 0)
        .reduce((acc, saldo) => acc + saldo, 0);
      const somaSaldosNegativos = clientesDoGerente
        .map((cliente) => cliente.saldo ?? 0)
        .filter((saldo) => saldo < 0)
        .reduce((acc, saldo) => acc + saldo, 0);

      return {
        nome: gerente.nome,
        email: gerente.email,
        clienteCount: clientesDoGerente.length,
        somaSaldosPositivos: parseFloat(somaSaldosPositivos.toFixed(2)),
        somaSaldosNegativos: parseFloat(somaSaldosNegativos.toFixed(2)),
      };
    });

    return resumo.sort((a, b) => b.somaSaldosPositivos - a.somaSaldosPositivos);
  }

  obterRelatorioClientes(): ClienteRelatorio[] {
    const usuarios = Array.from(this.usuariosCadastrados.values());
    const clientes = usuarios.filter((u) => u.perfil === 'cliente');
    const gerentes = usuarios.filter((u) => u.perfil === 'gerente');

    return clientes
      .map((cliente) => {
        const gerente = gerentes.find((g) => g.nome === cliente.gerente);
        return {
          cpf: cliente.cpf,
          nome: cliente.nome,
          email: cliente.email,
          salario: cliente.salario,
          numeroConta: cliente.numeroConta,
          saldo: cliente.saldo,
          limite: cliente.limite,
          gerenteCpf: gerente?.cpf ?? '',
          gerenteNome: gerente?.nome ?? cliente.gerente ?? '',
        };
      })
      .sort((a, b) =>
        a.nome.localeCompare(b.nome, 'pt-BR', { sensitivity: 'base' })
      );
  }

  obterGerentes(): GerenteListagem[] {
    return Array.from(this.usuariosCadastrados.values())
      .filter(
        (usuario) =>
          usuario.perfil === 'gerente' &&
          !!usuario.cpf?.trim() &&
          !!usuario.nome?.trim() &&
          !!usuario.email?.trim()
      )
      .map((gerente) => {
        const clientes = this.obterClientesInternosDoGerente(gerente.nome);
        const somaSaldosPositivos = clientes
          .map((cliente) => cliente.saldo ?? 0)
          .filter((saldo) => saldo >= 0)
          .reduce((total, saldo) => total + saldo, 0);
        const somaSaldosNegativos = clientes
          .map((cliente) => cliente.saldo ?? 0)
          .filter((saldo) => saldo < 0)
          .reduce((total, saldo) => total + saldo, 0);

        return {
          cpf: gerente.cpf,
          nome: gerente.nome,
          email: gerente.email,
          telefone: gerente.telefone ?? '',
          clienteCount: clientes.length,
          somaSaldosPositivos: parseFloat(somaSaldosPositivos.toFixed(2)),
          somaSaldosNegativos: parseFloat(somaSaldosNegativos.toFixed(2)),
        };
      })
      .sort((a, b) =>
        a.nome.localeCompare(b.nome, 'pt-BR', { sensitivity: 'base' })
      );
  }

  criarGerente(dadosNovoGerente: NovoGerente): GerenteListagem {
    if (this.cpfJaExiste(dadosNovoGerente.cpf)) {
      throw new Error('CPF já cadastrado no sistema');
    }

    const emailJaExiste = Array.from(this.usuariosCadastrados.values()).some(
      (usuario) =>
        usuario.email.toLowerCase() === dadosNovoGerente.email.trim().toLowerCase()
    );

    if (emailJaExiste) {
      throw new Error('E-mail já cadastrado no sistema');
    }

    const novoGerente: UsuarioInterno = {
      id: this.gerarId(),
      nome: dadosNovoGerente.nome.trim(),
      cpf: dadosNovoGerente.cpf.trim(),
      email: dadosNovoGerente.email.trim(),
      telefone: dadosNovoGerente.telefone.trim(),
      perfil: 'gerente',
      senha: dadosNovoGerente.senha.trim(),
    };

    this.usuariosCadastrados.set(novoGerente.email, novoGerente);
    this.redistribuirContaNaInsercaoDeGerente(novoGerente);
    this.salvarUsuariosCadastrados();

    return this.obterGerentes().find((gerente) => gerente.cpf === novoGerente.cpf)!;
  }

  atualizarGerente(cpf: string, dadosAtualizados: AtualizacaoGerente): GerenteListagem {
    const registroAtual = Array.from(this.usuariosCadastrados.entries()).find(
      ([, usuario]) => usuario.cpf === cpf && usuario.perfil === 'gerente'
    );

    if (!registroAtual) {
      throw new Error('Gerente não encontrado');
    }

    const [emailAtual, gerenteAtual] = registroAtual;
    const nomeAnterior = gerenteAtual.nome;
    const emailNovo = dadosAtualizados.email.trim();

    const emailJaCadastrado = Array.from(this.usuariosCadastrados.values()).some(
      (usuario) =>
        usuario.email.toLowerCase() === emailNovo.toLowerCase() && usuario.cpf !== cpf
    );

    if (emailJaCadastrado) {
      throw new Error('E-mail já cadastrado no sistema');
    }

    const gerenteAtualizado: UsuarioInterno = {
      ...gerenteAtual,
      nome: dadosAtualizados.nome.trim(),
      email: emailNovo,
      senha: dadosAtualizados.senha?.trim() ? dadosAtualizados.senha.trim() : gerenteAtual.senha,
    };

    if (emailNovo !== emailAtual) {
      this.usuariosCadastrados.delete(emailAtual);
    }

    this.usuariosCadastrados.set(emailNovo, gerenteAtualizado);

    if (nomeAnterior !== gerenteAtualizado.nome) {
      const clientesDoGerente = this.obterClientesInternosDoGerente(nomeAnterior);
      for (const cliente of clientesDoGerente) {
        cliente.gerente = gerenteAtualizado.nome;
        this.usuariosCadastrados.set(cliente.email, cliente);
      }
    }

    this.salvarUsuariosCadastrados();

    const usuarioEmSessao = this.usuarioAtual.value;
    if (usuarioEmSessao?.cpf === cpf) {
      const usuarioPublico = this.mapearUsuarioPublico(gerenteAtualizado);
      this.usuarioAtual.next(usuarioPublico);
      localStorage.setItem('usuario', JSON.stringify(usuarioPublico));
    }

    return {
      cpf: gerenteAtualizado.cpf,
      nome: gerenteAtualizado.nome,
      email: gerenteAtualizado.email,
      telefone: gerenteAtualizado.telefone ?? '',
      clienteCount: this.obterClientesInternosDoGerente(gerenteAtualizado.nome).length,
      somaSaldosPositivos: this.calcularSaldoPositivoTotalGerente(gerenteAtualizado.nome),
      somaSaldosNegativos: this.calcularSaldoNegativoTotalGerente(gerenteAtualizado.nome),
    };
  }

  removerGerente(cpf: string): void {
    const gerentes = Array.from(this.usuariosCadastrados.entries()).filter(
      ([, usuario]) => usuario.perfil === 'gerente'
    );

    if (gerentes.length <= 1) {
      throw new Error('Não é permitido remover o último gerente do banco');
    }

    const registroGerente = gerentes.find(([, usuario]) => usuario.cpf === cpf);

    if (!registroGerente) {
      throw new Error('Gerente não encontrado');
    }

    const [emailGerenteRemovido, gerenteRemovido] = registroGerente;
    const clientesDoGerente = this.obterClientesInternosDoGerente(gerenteRemovido.nome);
    const gerentesRestantes = gerentes
      .map(([, usuario]) => usuario)
      .filter((usuario) => usuario.cpf !== cpf);

    for (const cliente of clientesDoGerente) {
      const gerenteDestino = this.encontrarGerenteComMenosClientes(gerentesRestantes);
      cliente.gerente = gerenteDestino.nome;
      this.usuariosCadastrados.set(cliente.email, cliente);
    }

    this.usuariosCadastrados.delete(emailGerenteRemovido);
    this.salvarUsuariosCadastrados();

    const usuarioEmSessao = this.usuarioAtual.value;
    if (usuarioEmSessao?.cpf === cpf) {
      this.logout();
    }
  }

  obterClientesDoGerente(nomeGerente: string): ClienteRelatorio[] {
    const usuarios = Array.from(this.usuariosCadastrados.values());
    return usuarios
      .filter((u) => u.perfil === 'cliente' && u.gerente === nomeGerente)
      .map((cliente) => ({
        cpf: cliente.cpf,
        nome: cliente.nome,
        email: cliente.email,
        salario: cliente.salario,
        numeroConta: cliente.numeroConta,
        saldo: cliente.saldo,
        limite: cliente.limite,
        gerenteNome: nomeGerente,
      }))
      .sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR', { sensitivity: 'base' }));
  }

  estaAutenticado(): boolean {
    return this.tokenAtual.value !== null;
  }

  obterClientesPendentes(): ClienteRegistro[] {
    return Array.from(this.clientesPendentes.values());
  }

  atualizarPerfilCliente(cpf: string, dadosPerfil: Partial<User>): User {
    const registroAtual = Array.from(this.usuariosCadastrados.entries()).find(
      ([, usuario]) => usuario.cpf === cpf
    );

    if (!registroAtual) {
      throw new Error('Usuário não encontrado');
    }

    const [emailAtual, usuarioAtual] = registroAtual;
    const emailAtualizado = (dadosPerfil.email || usuarioAtual.email).trim();

    const usuarioAtualizado: UsuarioInterno = {
      ...usuarioAtual,
      ...dadosPerfil,
      cpf: usuarioAtual.cpf,
      perfil: usuarioAtual.perfil,
      email: emailAtualizado,
    };

    if (emailAtualizado !== emailAtual) {
      this.usuariosCadastrados.delete(emailAtual);
    }

    this.usuariosCadastrados.set(emailAtualizado, usuarioAtualizado);
    this.salvarUsuariosCadastrados();

    const usuarioPublico = this.mapearUsuarioPublico(usuarioAtualizado);
    const usuarioEmSessao = this.usuarioAtual.value;

    if (usuarioEmSessao?.cpf === cpf) {
      this.usuarioAtual.next(usuarioPublico);
      localStorage.setItem('usuario', JSON.stringify(usuarioPublico));
    }

    return usuarioPublico;
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
        salario: cliente.salario,
        saldo: 0,
        limite: cliente.salario >= 2000 ? cliente.salario / 2 : 0,
        numeroConta: this.gerarNumeroConta(),
        gerente: 'Gerente Padrão',
        telefone: cliente.telefone,
        logradouro: cliente.logradouro,
        numero: cliente.numero,
        complemento: cliente.complemento,
        cep: cliente.cep,
        cidade: cliente.cidade,
        estado: cliente.estado,
      };

      this.usuariosCadastrados.set(cliente.email, usuario);
      this.clientesPendentes.delete(cpf);

      this.salvarUsuariosCadastrados();
      this.salvarClientesPendentes();
    }
  }

  rejeitarCliente(cpf: string, motivo: string = ''): void {
    const cliente = this.clientesPendentes.get(cpf);
    if (cliente) {
      const rejeicoes = JSON.parse(localStorage.getItem('rejeicoes') || '[]');
      rejeicoes.push({ cpf, nome: cliente.nome, email: cliente.email, motivo, dataHora: new Date().toISOString() });
      localStorage.setItem('rejeicoes', JSON.stringify(rejeicoes));
    }
    this.clientesPendentes.delete(cpf);
    this.salvarClientesPendentes();
  }

  private gerarId(): string {
    return 'id_' + Math.random().toString(36).substring(2, 9);
  }

  private gerarNumeroConta(): string {
    return Math.floor(1000 + Math.random() * 9000).toString();
  }

  private redistribuirContaNaInsercaoDeGerente(novoGerente: UsuarioInterno): void {
    const gerentesExistentes = Array.from(this.usuariosCadastrados.values()).filter(
      (usuario) => usuario.perfil === 'gerente' && usuario.cpf !== novoGerente.cpf
    );

    if (gerentesExistentes.length === 0) {
      return;
    }

    if (gerentesExistentes.length === 1) {
      const clientesDoUnicoGerente = this.obterClientesInternosDoGerente(
        gerentesExistentes[0].nome
      );

      if (clientesDoUnicoGerente.length <= 1) {
        return;
      }
    }

    const maxClientes = Math.max(
      ...gerentesExistentes.map((gerente) => this.obterClientesInternosDoGerente(gerente.nome).length)
    );

    if (maxClientes <= 0) {
      return;
    }

    const candidatos = gerentesExistentes.filter(
      (gerente) => this.obterClientesInternosDoGerente(gerente.nome).length === maxClientes
    );

    const gerenteOrigem = candidatos.reduce((escolhido, atual) =>
      this.calcularSaldoPositivoTotalGerente(atual.nome) <
      this.calcularSaldoPositivoTotalGerente(escolhido.nome)
        ? atual
        : escolhido
    );

    const clientesDoGerenteOrigem = this.obterClientesInternosDoGerente(gerenteOrigem.nome);
    const clienteTransferido = this.selecionarClienteParaRedistribuicao(clientesDoGerenteOrigem);

    if (!clienteTransferido) {
      return;
    }

    clienteTransferido.gerente = novoGerente.nome;
    this.usuariosCadastrados.set(clienteTransferido.email, clienteTransferido);
  }

  private selecionarClienteParaRedistribuicao(clientes: UsuarioInterno[]): UsuarioInterno | null {
    if (clientes.length === 0) {
      return null;
    }

    const clientesComSaldoPositivo = clientes
      .filter((cliente) => (cliente.saldo ?? 0) > 0)
      .sort((a, b) => (a.saldo ?? 0) - (b.saldo ?? 0));

    if (clientesComSaldoPositivo.length > 0) {
      return clientesComSaldoPositivo[0];
    }

    return [...clientes].sort((a, b) =>
      a.nome.localeCompare(b.nome, 'pt-BR', { sensitivity: 'base' })
    )[0];
  }

  private encontrarGerenteComMenosClientes(gerentes: UsuarioInterno[]): UsuarioInterno {
    return gerentes.reduce((escolhido, atual) =>
      this.obterClientesInternosDoGerente(atual.nome).length <
      this.obterClientesInternosDoGerente(escolhido.nome).length
        ? atual
        : escolhido
    );
  }

  private obterClientesInternosDoGerente(nomeGerente: string): UsuarioInterno[] {
    return Array.from(this.usuariosCadastrados.values()).filter(
      (usuario) => usuario.perfil === 'cliente' && usuario.gerente === nomeGerente
    );
  }

  private calcularSaldoPositivoTotalGerente(nomeGerente: string): number {
    return parseFloat(
      this.obterClientesInternosDoGerente(nomeGerente)
        .map((cliente) => cliente.saldo ?? 0)
        .filter((saldo) => saldo >= 0)
        .reduce((total, saldo) => total + saldo, 0)
        .toFixed(2)
    );
  }

  private calcularSaldoNegativoTotalGerente(nomeGerente: string): number {
    return parseFloat(
      this.obterClientesInternosDoGerente(nomeGerente)
        .map((cliente) => cliente.saldo ?? 0)
        .filter((saldo) => saldo < 0)
        .reduce((total, saldo) => total + saldo, 0)
        .toFixed(2)
    );
  }

  private salvarUsuariosCadastrados(): void {
    const dados = Object.fromEntries(this.usuariosCadastrados);
    localStorage.setItem('usuariosCadastrados', JSON.stringify(dados));
  }

  private salvarClientesPendentes(): void {
    const dados = Object.fromEntries(this.clientesPendentes);
    localStorage.setItem('clientesPendentes', JSON.stringify(dados));
  }

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
        this.registrarMovimentacao(usuario, {
          dataHora: new Date().toISOString(),
          tipo: 'deposito',
          valor,
        });

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
        this.registrarMovimentacao(usuario, {
          dataHora: new Date().toISOString(),
          tipo: 'saque',
          valor,
        });

        observer.next({ sucesso: true, novoSaldo });
        observer.complete();
      }, 400);
    });
  }

  obterMovimentacoes(): Movimentacao[] {
    const usuario = this.obterUsuarioAtual();
    if (!usuario) {
      return [];
    }

    const chave = 'movimentacoes_' + (usuario.numeroConta ?? usuario.cpf);
    const dados = localStorage.getItem(chave);
    return dados ? JSON.parse(dados) : [];
  }

  private atualizarSaldoUsuario(usuario: User, novoSaldo: number): void {
    const usuarioAtualizado = { ...usuario, saldo: novoSaldo };
    this.usuarioAtual.next(usuarioAtualizado);
    localStorage.setItem('usuario', JSON.stringify(usuarioAtualizado));

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
    historico.unshift(mov);
    localStorage.setItem(chave, JSON.stringify(historico));
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
        nome: 'João Gerente',
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
        salario: 2500,
        saldo: 1500,
        limite: 1000,
        numeroConta: '1234',
        gerente: 'João Gerente',
      },
    };

    this.usuariosCadastrados = new Map(Object.entries(usuariosExemplo));
    this.salvarUsuariosCadastrados();
  }

  private mapearUsuarioPublico(usuario: UsuarioInterno): User {
    return {
      id: usuario.id,
      email: usuario.email,
      nome: usuario.nome,
      cpf: usuario.cpf,
      perfil: usuario.perfil,
      saldo: usuario.saldo,
      limite: usuario.limite,
      numeroConta: usuario.numeroConta,
      gerente: usuario.gerente,
      salario: usuario.salario,
      telefone: usuario.telefone,
      logradouro: usuario.logradouro,
      numero: usuario.numero,
      complemento: usuario.complemento,
      cep: usuario.cep,
      cidade: usuario.cidade,
      estado: usuario.estado,
    };
  }

  transferir(numeroContaDestino: string, valor: number): void {
    const usuario = this.usuarioAtual.value;

    if (!usuario) {
      throw new Error('Usuário não autenticado');
    }

    if (!valor || valor <= 0) {
      throw new Error('Valor inválido');
    }

    const valorFinal = parseFloat(valor.toFixed(2));

    if (usuario.numeroConta === numeroContaDestino) {
      throw new Error('Não pode transferir para a própria conta');
    }

    const usuarioCompleto = this.usuariosCadastrados.get(usuario.email);
    if (!usuarioCompleto) {
      throw new Error('Usuário não encontrado');
    }

    let usuarioDestino: UsuarioInterno | null = null;

    for (const usuarioCadastrado of this.usuariosCadastrados.values()) {
      if (usuarioCadastrado.numeroConta === numeroContaDestino) {
        usuarioDestino = usuarioCadastrado;
        break;
      }
    }

    if (!usuarioDestino) {
      throw new Error('Conta destino não encontrada');
    }

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

    // Registra no histórico — ORIGEM
    this.registrarMovimentacao(usuario, {
    dataHora: new Date().toISOString(),
    tipo: 'transferencia_enviada',
    valor: valorFinal,
    contaDestino: numeroContaDestino,
    nomeDestino: usuarioDestino.nome,
    });

    // Registra no histórico — DESTINO
    this.registrarMovimentacao(
    { ...usuarioDestino },
    {
    dataHora: new Date().toISOString(),
    tipo: 'transferencia_recebida',
    valor: valorFinal,
    contaOrigem: usuario.numeroConta,
    nomeOrigem: usuario.nome,
    });

  }
}
