import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-saque',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './saque.html',
  styleUrl: './saque.css',
})
export class SaqueComponent implements OnInit {
  form;
  carregando = false;
  erro: string | null = null;
  sucesso: string | null = null;
  saldoAtual: number = 0;
  limite: number = 0;
  nomeUsuario: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      valor: ['', [Validators.required, Validators.min(0.01)]],
    });
  }

  ngOnInit(): void {
    const usuario = this.authService.obterUsuarioAtual();
    if (!usuario) {
      this.router.navigate(['/login']);
      return;
    }
    this.saldoAtual = usuario.saldo ?? 0;
    this.limite = usuario.limite ?? 0;
    this.nomeUsuario = usuario.nome;
    this.authService.usuario$.subscribe((u) => {
      if (u) {
        this.saldoAtual = u.saldo ?? 0;
        this.limite = u.limite ?? 0;
      }
    });
  }

  get saldoNegativo(): boolean {
    return this.saldoAtual < 0;
  }

  get saldoDisponivel(): number {
    return parseFloat((this.saldoAtual + this.limite).toFixed(2));
  }

  sacar(): void {
    if (this.form.invalid) return;
    const valor = parseFloat(this.form.value.valor as string);
    this.carregando = true;
    this.erro = null;
    this.sucesso = null;
    this.authService.sacar(valor).subscribe({
      next: (res) => {
        this.carregando = false;
        this.sucesso = `Saque de R$ ${valor.toFixed(2)} realizado com sucesso!`;
        this.saldoAtual = res.novoSaldo;
        this.form.reset();
      },
      error: (err) => {
        this.carregando = false;
        this.erro = err.message ?? 'Erro ao realizar saque.';
      },
    });
  }

  voltar(): void {
    this.router.navigate(['/cliente-home']);
  }
}
