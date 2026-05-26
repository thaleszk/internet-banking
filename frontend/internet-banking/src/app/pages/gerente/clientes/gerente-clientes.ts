import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../../services/auth.service';
import { ClienteRelatorio } from '../../../shared/models';

@Component({
  selector: 'app-gerente-clientes',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    MatFormFieldModule,
    MatTooltipModule,
    MatChipsModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './gerente-clientes.html',
  styleUrl: './gerente-clientes.css',
})
export class GerenteClientesComponent implements OnInit {
  todosClientes: ClienteRelatorio[] = [];
  termoBusca: string = '';
  clienteEncontrado: ClienteRelatorio | null = null;
  buscaRealizada = false;
  erroBusca = '';
  top3Clientes: ClienteRelatorio[] = [];
  gerenteNome = '';
  gerenteCpf = '';
  abaSelecionada: 'todos' | 'buscar' | 'top3' = 'todos';
  carregando = false;

  private readonly gatewayUrl = 'http://localhost:8080';

  constructor(
    private authService: AuthService,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    const usuario = this.authService.obterUsuarioAtual();
    if (!usuario || usuario.perfil !== 'gerente') {
      this.router.navigate(['/login']);
      return;
    }
    this.gerenteNome = usuario.nome;
    this.gerenteCpf  = usuario.cpf ?? '';
    this.carregarDados();
  }

  carregarDados(): void {
    this.carregando = true;
    const token = this.authService.obterToken();
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    // R12 — busca clientes do gerente via gateway
    this.http
      .get<any[]>(`${this.gatewayUrl}/managers/${this.gerenteCpf}/customers`, { headers })
      .subscribe({
        next: (lista) => {
          this.carregando = false;
          this.todosClientes = this.mapear(lista);
          this.calcularTop3();
        },
        error: () => {
          this.carregando = false;
          // Fallback local
          this.todosClientes = this.authService.obterClientesDoGerente(this.gerenteNome);
          this.calcularTop3();
        },
      });
  }

  private calcularTop3(): void {
    // R14 — 3 maiores saldos (de qualquer gerente conforme enunciado)
    const token = this.authService.obterToken();
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    this.http
      .get<any[]>(`${this.gatewayUrl}/customers`, { headers })
      .subscribe({
        next: (todos) => {
          this.top3Clientes = this.mapear(todos)
            .sort((a, b) => (b.saldo ?? 0) - (a.saldo ?? 0))
            .slice(0, 3);
        },
        error: () => {
          this.top3Clientes = [...this.todosClientes]
            .sort((a, b) => (b.saldo ?? 0) - (a.saldo ?? 0))
            .slice(0, 3);
        },
      });
  }

  private mapear(lista: any[]): ClienteRelatorio[] {
    return lista.map((c: any) => ({
      cpf:         c.cpf,
      nome:        c.name ?? c.nome,
      email:       c.email,
      salario:     Number(c.salary ?? c.salario ?? 0),
      numeroConta: c.accountNumber ?? c.numeroConta ?? '—',
      saldo:       Number(c.balance ?? c.saldo ?? 0),
      limite:      Number(c.accountLimit ?? c.limite ?? 0),
      gerenteNome: this.gerenteNome,
      gerenteCpf:  this.gerenteCpf,
      cidade:      c.address?.city ?? c.cidade ?? '',
      estado:      c.address?.state ?? c.estado ?? '',
    }));
  }

  // R13 — busca por CPF ou nome
  buscarCliente(): void {
    const termo = this.termoBusca.trim();
    if (!termo) {
      this.erroBusca = 'Digite um CPF ou nome para buscar.';
      this.clienteEncontrado = null;
      this.buscaRealizada = false;
      return;
    }
    this.buscaRealizada = true;
    this.erroBusca = '';
    const resultado = this.todosClientes.find(
      (c) =>
        c.cpf.replace(/\D/g, '').includes(termo.replace(/\D/g, '')) ||
        c.nome.toLowerCase().includes(termo.toLowerCase())
    );
    if (resultado) {
      this.clienteEncontrado = resultado;
    } else {
      this.clienteEncontrado = null;
      this.erroBusca = 'Nenhum cliente encontrado com esse CPF ou nome.';
    }
  }

  limparBusca(): void {
    this.termoBusca = '';
    this.clienteEncontrado = null;
    this.buscaRealizada = false;
    this.erroBusca = '';
  }

  selecionarAba(aba: 'todos' | 'buscar' | 'top3'): void {
    this.abaSelecionada = aba;
    this.limparBusca();
  }

  formatarCpf(cpf: string): string {
    const d = cpf.replace(/\D/g, '');
    if (d.length === 11) return d.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
    return cpf;
  }

  corSaldo(saldo: number | undefined): string {
    if (saldo === undefined) return '';
    return saldo >= 0 ? 'saldo-positivo' : 'saldo-negativo';
  }
}