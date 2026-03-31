import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { AuthService, ClienteRegistro } from '../../../services/auth.service';
import { RejeicaoDialogComponent } from './rejeicao-dialog/rejeicao-dialog';

@Component({
  selector: 'app-gerente-inicio',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
  ],
  templateUrl: './gerente-inicio.html',
  styleUrl: './gerente-inicio.css',
})
export class GerenteInicioComponent implements OnInit {
  clientesPendentes: ClienteRegistro[] = [];
  mensagemSucesso: string | null = null;

  constructor(private authService: AuthService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.carregarPendentes();
  }

  private carregarPendentes(): void {
    this.clientesPendentes = this.authService.obterClientesPendentes();
  }

  // R10 — Aprovar Cliente
  aprovar(cliente: ClienteRegistro): void {
    const senhaAleatoria = Math.random().toString(36).substring(2, 8);
    this.authService.aprovarCliente(cliente.cpf, senhaAleatoria);
    this.carregarPendentes();
    this.mensagemSucesso = `Cliente ${cliente.nome} aprovado! Senha gerada: ${senhaAleatoria}`;
    setTimeout(() => (this.mensagemSucesso = null), 5000);
  }

  // R11 — Rejeitar Cliente
  rejeitar(cliente: ClienteRegistro): void {
    const ref = this.dialog.open(RejeicaoDialogComponent, {
      width: '440px',
      data: { nomeCliente: cliente.nome },
    });

    ref.afterClosed().subscribe((motivo: string | undefined) => {
      if (motivo) {
        this.authService.rejeitarCliente(cliente.cpf, motivo);
        this.carregarPendentes();
        this.mensagemSucesso = `Cadastro de ${cliente.nome} recusado.`;
        setTimeout(() => (this.mensagemSucesso = null), 4000);
      }
    });
  }
}
