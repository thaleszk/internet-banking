import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../services/auth.service';
import { User } from '../../shared/models';

@Component({
  selector: 'app-transferencia',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './transferencia.html',
  styleUrl: './transferencia.css',
})
export class TransferenciaComponent implements OnInit {
  contaDestino: string = '';
  valor: number = 0;
  mensagem: string = '';
  erro: string = '';
  carregando = false;
  saldoAtual: number = 0;
  limite: number = 0;

  private readonly gatewayUrl = 'http://localhost:8080';

  constructor(
    private authService: AuthService,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    const usuario: User | null = this.authService.obterUsuarioAtual();
    if (!usuario) {
      this.router.navigate(['/login']);
      return;
    }
    this.saldoAtual = usuario.saldo ?? 0;
    this.limite = usuario.limite ?? 0;

    this.authService.usuario$.subscribe((u: User | null) => {
      if (u) {
        this.saldoAtual = u.saldo ?? 0;
        this.limite = u.limite ?? 0;
      }
    });
  }

  get saldoDisponivel(): number {
    return parseFloat((this.saldoAtual + this.limite).toFixed(2));
  }

  transferir(): void {
    this.mensagem = '';
    this.erro = '';

    const usuario = this.authService.obterUsuarioAtual();
    if (!usuario?.numeroConta) {
      this.erro = 'Conta não encontrada.';
      return;
    }

    if (!this.contaDestino || this.valor <= 0) {
      this.erro = 'Informe a conta destino e um valor válido.';
      return;
    }

    if (this.valor > this.saldoDisponivel) {
      this.erro = 'Saldo insuficiente.';
      return;
    }

    this.carregando = true;
    const token = this.authService.obterToken();
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    this.http.post<any>(
      `${this.gatewayUrl}/accounts/transfer`,
      {
        sourceAccountNumber: usuario.numeroConta,
        destinationAccountNumber: this.contaDestino,
        amount: this.valor
      },
      { headers }
    ).subscribe({
      next: (res) => {
        this.carregando = false;
        const novoSaldo = parseFloat(Number(res.balance ?? 0).toFixed(2));
        const novoLimite = Number(res.limit ?? this.limite);
        this.saldoAtual = novoSaldo;

        // Atualiza sessão
        this.authService.atualizarSaldoSessao(novoSaldo, novoLimite);

        this.mensagem = `Transferência de R$ ${this.valor.toFixed(2)} realizada com sucesso!`;
        this.contaDestino = '';
        this.valor = 0;
      },
      error: (err) => {
        this.carregando = false;
        // Fallback local se gateway indisponível
        try {
          this.authService.transferir(this.contaDestino, this.valor);
          this.mensagem = `Transferência de R$ ${this.valor.toFixed(2)} realizada com sucesso!`;
          this.contaDestino = '';
          this.valor = 0;
        } catch (e: any) {
          this.erro = e.message || 'Erro ao realizar transferência.';
        }
      }
    });
  }

  voltar(): void {
    this.router.navigate(['/cliente/inicio']);
  }
}