import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
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
  form!: FormGroup;
  mensagem: string = '';
  erro: string = '';
  saldoAtual: number = 0;
  limite: number = 0;
  carregando: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.criarFormulario();
  }

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

  private criarFormulario(): void {
    this.form = this.fb.group({
      contaDestino: ['', [Validators.required]],
      valor: ['', [Validators.required, Validators.min(0.01)]],
    });
  }

  get saldoDisponivel(): number {
    return parseFloat((this.saldoAtual + this.limite).toFixed(2));
  }

  transferir(): void {
    if (this.form.invalid) {
      return;
    }

    this.mensagem = '';
    this.erro = '';
    this.carregando = true;

    try {
      const contaDestino = this.form.get('contaDestino')?.value;
      const valor = this.form.get('valor')?.value;

      this.authService.transferir(contaDestino, valor);
      this.mensagem = 'Transferência realizada com sucesso!';
      this.form.reset();
      this.carregando = false;
    } catch (e: any) {
      this.erro = e.message;
      this.carregando = false;
    }
  }

  voltar(): void {
    this.router.navigate(['/cliente/inicio']);
  }
}

