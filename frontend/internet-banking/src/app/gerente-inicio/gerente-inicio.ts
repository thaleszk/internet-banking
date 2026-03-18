import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthService, ClienteRegistro } from '../../../services/auth.service';

@Component({
  selector: 'app-gerente-inicio',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  template: `
    <mat-card class="gerente-card">
      <h2>Pedidos de Autocadastro Pendentes</h2>

      <p class="descricao">
        Confira abaixo os clientes aguardando aprovação. Você pode
        <strong>Aprovar</strong> ou <strong>Recusar</strong> cada solicitação.
      </p>

      <div *ngIf="clientesPendentes.length === 0" class="sem-pendencias">
        <mat-icon>check_circle</mat-icon>
        <span>Nenhum pedido de autocadastro pendente no momento.</span>
      </div>

      <div *ngIf="clientesPendentes.length > 0" class="tabela-wrapper">
        <table class="tabela-pendentes">
          <thead>
            <tr>
              <th>CPF</th>
              <th>Nome</th>
              <th>Salário</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let cliente of clientesPendentes">
              <td>{{ cliente.cpf }}</td>
              <td>{{ cliente.nome }}</td>
              <td>R$ {{ cliente.salario | number:'1.2-2':'pt-BR' }}</td>
              <td class="acoes">
                <button
                  mat-stroked-button
                  color="primary"
                  (click)="aprovar(cliente)"
                >
                  <mat-icon>check</mat-icon>
                  Aprovar
                </button>
                <button
                  mat-stroked-button
                  color="warn"
                  (click)="rejeitar(cliente)"
                >
                  <mat-icon>close</mat-icon>
                  Recusar
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </mat-card>
  `,
})
export class GerenteInicioComponent implements OnInit {
  clientesPendentes: ClienteRegistro[] = [];

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.carregarPendentes();
  }

  private carregarPendentes(): void {
    this.clientesPendentes = this.authService.obterClientesPendentes();
  }

  aprovar(cliente: ClienteRegistro): void {
    const senhaAleatoria = Math.random().toString(36).substring(2, 8);
    this.authService.aprovarCliente(cliente.cpf, senhaAleatoria);
    this.carregarPendentes();
  }

  rejeitar(cliente: ClienteRegistro): void {
    this.authService.rejeitarCliente(cliente.cpf);
    this.carregarPendentes();
  }
}
