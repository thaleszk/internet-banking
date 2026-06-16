import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../../services/auth.service';
import { User } from '../../../shared/models';

@Component({
  selector: 'app-cliente-home',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './cliente-home.html',
  styleUrl: './cliente-home.css',
})
export class ClienteHome implements OnInit {
  nomeCliente: string = '';
  numeroConta: string = '';
  nomeGerente: string = '';
  saldoAtual: number = 0;
  limite: number = 0;
  ultimoLogin: string = '';
  carregando = false;

  private readonly gatewayUrl = 'http://localhost:8000';

  constructor(
    private authService: AuthService,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    const usuario = this.authService.obterUsuarioAtual();
    if (!usuario) {
      this.router.navigate(['/login']);
      return;
    }

    this.aplicarUsuario(usuario);
    this.ultimoLogin = this.formatarDataHora(new Date());

    this.authService.sincronizarClienteAtual().subscribe((clienteAtualizado) => {
      if (!clienteAtualizado) {
        return;
      }

      this.aplicarUsuario(clienteAtualizado);

      if (clienteAtualizado.numeroConta) {
        this.buscarSaldoAtualizado(clienteAtualizado.numeroConta);
      }
    });

    if (usuario.numeroConta) {
      this.buscarSaldoAtualizado(usuario.numeroConta);
    }

    this.authService.usuario$.subscribe((u) => {
      if (u) {
        this.saldoAtual = u.saldo ?? 0;
        this.limite = u.limite ?? 0;
      }
    });
  }

  private aplicarUsuario(usuario: User): void {
    this.nomeCliente = usuario.nome;
    this.numeroConta = usuario.numeroConta ?? '----';
    this.nomeGerente = usuario.gerente ?? 'NÃ£o atribuÃ­do';
    this.saldoAtual = usuario.saldo ?? 0;
    this.limite = usuario.limite ?? 0;
  }

  private buscarSaldoAtualizado(numeroConta: string): void {
    this.carregando = true;
    const token = this.authService.obterToken();
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    this.http
      .get<{ balance: number; limit: number }>(
        `${this.gatewayUrl}/accounts/${numeroConta}`,
        { headers }
      )
      .subscribe({
        next: (conta) => {
          this.carregando = false;
          this.saldoAtual = parseFloat(Number(conta.balance ?? 0).toFixed(2));
          this.limite = Number(conta.limit ?? 0);
          this.authService.atualizarSaldoSessao(this.saldoAtual, this.limite);
        },
        error: () => {
          this.carregando = false;
        },
      });
  }

  get saldoNegativo(): boolean {
    return this.saldoAtual < 0;
  }

  navegarPara(rota: string): void {
    this.router.navigate([rota]);
  }

  private formatarDataHora(data: Date): string {
    return new Intl.DateTimeFormat('pt-BR', {
      dateStyle: 'short',
      timeStyle: 'short',
    }).format(data);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
