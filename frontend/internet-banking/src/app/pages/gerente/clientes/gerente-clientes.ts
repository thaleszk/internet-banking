import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { AuthService, ClienteRelatorio } from '../../../services/auth.service';

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
  ],
  templateUrl: './gerente-clientes.html',
  styleUrl: './gerente-clientes.css',
})
export class GerenteClientesComponent implements OnInit {
  // R12 — todos os clientes do gerente
  todosClientes: ClienteRelatorio[] = [];

  // R13 — busca individual
  termoBusca: string = '';
  clienteEncontrado: ClienteRelatorio | null = null;
  buscaRealizada = false;
  erroBusca = '';

  // R14 — top 3 melhores saldos
  top3Clientes: ClienteRelatorio[] = [];

  gerenteNome = '';
  abaSelecionada: 'todos' | 'buscar' | 'top3' = 'todos';

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    const usuario = this.authService.obterUsuarioAtual();
    if (!usuario || usuario.perfil !== 'gerente') {
      this.router.navigate(['/login']);
      return;
    }
    this.gerenteNome = usuario.nome;
    this.carregarDados();
  }

  private carregarDados(): void {
    // R12 — busca todos os clientes do gerente logado
    this.todosClientes = this.authService.obterClientesDoGerente(this.gerenteNome);
    // R14 — top 3 por maior saldo
    this.top3Clientes = [...this.todosClientes]
      .sort((a, b) => (b.saldo ?? 0) - (a.saldo ?? 0))
      .slice(0, 3);
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
    const digits = cpf.replace(/\D/g, '');
    if (digits.length === 11) {
      return digits.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
    }
    return cpf;
  }

  corSaldo(saldo: number | undefined): string {
    if (saldo === undefined) return '';
    return saldo >= 0 ? 'saldo-positivo' : 'saldo-negativo';
  }
}
