import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../services/auth.service';
import { User } from '../../shared/models';

@Component({
  selector: 'app-transferencia',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
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
  form!: FormGroup;

  private readonly gatewayUrl = 'http://localhost:8000';

  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
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

    this.form = this.fb.group({
      contaDestino: ['', Validators.required],
      valor: [0, [Validators.required, Validators.min(0.01)]],
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

    const contaDestino = this.form.get('contaDestino')?.value;
    const valor = parseFloat(this.form.get('valor')?.value) || 0;

    if (!contaDestino || valor <= 0) {
      this.erro = 'Informe a conta destino e um valor válido.';
      return;
    }

    if (valor > this.saldoDisponivel) {
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
        destinationAccountNumber: contaDestino,
        amount: valor
      },
      { headers }
    ).subscribe({
      next: (res) => {
        this.carregando = false;
        const novoSaldo = parseFloat(Number(res.balance ?? 0).toFixed(2));
        const novoLimite = Number(res.limit ?? this.limite);
        this.saldoAtual = novoSaldo;

        this.authService.atualizarSaldoSessao(novoSaldo, novoLimite);

        this.mensagem = `Transferência de R$ ${valor.toFixed(2)} realizada com sucesso!`;
        this.form.reset({ contaDestino: '', valor: 0 });
      },
      error: (err) => {
        this.carregando = false;
        try {
          this.authService.transferir(contaDestino, valor);
          this.mensagem = `Transferência de R$ ${valor.toFixed(2)} realizada com sucesso!`;
          this.form.reset({ contaDestino: '', valor: 0 });
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
