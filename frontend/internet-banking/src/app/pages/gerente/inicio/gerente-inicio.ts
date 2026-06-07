import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../../services/auth.service';
import { CustomerApiService } from '../../../services/customer-api.service';
import { RejeicaoDialogComponent } from './rejeicao-dialog/rejeicao-dialog';
import { ClienteRegistro } from '../../../shared/models';

@Component({
  selector: 'app-gerente-inicio',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './gerente-inicio.html',
  styleUrl: './gerente-inicio.css',
})
export class GerenteInicioComponent implements OnInit {
  clientesPendentes: ClienteRegistro[] = [];
  mensagemSucesso: string | null = null;
  erroMensagem: string | null = null;
  carregando = false;

  private readonly gatewayUrl = 'http://localhost:8000';

  constructor(
    private authService: AuthService,
    private customerApi: CustomerApiService,
    private http: HttpClient,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.carregarPendentes();
  }

  // Carrega pendentes do gateway, fallback local
  carregarPendentes(): void {
    this.carregando = true;
    this.erroMensagem = null;

    this.customerApi.listarAutocadastrosPendentes().subscribe({
      next: (lista) => {
        this.carregando = false;
        this.clientesPendentes = lista;
      },
      error: () => {
        this.carregando = false;
        // Fallback local
        this.clientesPendentes = this.authService.obterClientesPendentes();
      }
    });
  }

  // R10 — Aprovar Cliente
  aprovar(cliente: ClienteRegistro): void {
    const token = this.authService.obterToken();
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    this.http.post(
      `${this.gatewayUrl}/customers/registration/${cliente.cpf}/approve`,
      {},
      { headers }
    ).subscribe({
      next: () => {
        this.mensagemSucesso = `Cliente ${cliente.nome} aprovado com sucesso!`;
        setTimeout(() => (this.mensagemSucesso = null), 5000);
        this.carregarPendentes();
      },
      error: () => {
        // Fallback local
        const senhaAleatoria = Math.random().toString(36).substring(2, 8);
        this.authService.aprovarCliente(cliente.cpf, senhaAleatoria);
        this.clientesPendentes = this.authService.obterClientesPendentes();
        this.mensagemSucesso = `Cliente ${cliente.nome} aprovado! Senha: ${senhaAleatoria}`;
        setTimeout(() => (this.mensagemSucesso = null), 5000);
      }
    });
  }

  // R11 — Rejeitar Cliente
  rejeitar(cliente: ClienteRegistro): void {
    const ref = this.dialog.open(RejeicaoDialogComponent, {
      width: '440px',
      data: { nomeCliente: cliente.nome },
    });

    ref.afterClosed().subscribe((motivo: string | undefined) => {
      if (!motivo) return;

      const token = this.authService.obterToken();
      const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

      this.http.post(
        `${this.gatewayUrl}/customers/registration/${cliente.cpf}/reject`,
        { reason: motivo },
        { headers }
      ).subscribe({
        next: () => {
          this.mensagemSucesso = `Cadastro de ${cliente.nome} recusado.`;
          setTimeout(() => (this.mensagemSucesso = null), 4000);
          this.carregarPendentes();
        },
        error: () => {
          // Fallback local
          this.authService.rejeitarCliente(cliente.cpf, motivo);
          this.clientesPendentes = this.authService.obterClientesPendentes();
          this.mensagemSucesso = `Cadastro de ${cliente.nome} recusado.`;
          setTimeout(() => (this.mensagemSucesso = null), 4000);
        }
      });
    });
  }
}
