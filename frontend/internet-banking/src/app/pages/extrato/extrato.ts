import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { AuthService } from '../../services/auth.service';
import { User, Movimentacao } from '../../shared/models';
 
@Component({
  selector: 'app-extrato',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatDatepickerModule,
    MatNativeDateModule,
  ],
  templateUrl: './extrato.html',
  styleUrl: './extrato.css',
})
export class ExtratoComponent implements OnInit {
  form;
  operacoes: Movimentacao[] = [];
  operacoesFiltradas: Movimentacao[] = [];
  nomeUsuario: string = '';
 
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    const dataInicio = new Date();
    dataInicio.setDate(dataInicio.getDate() - 30);
 
    this.form = this.fb.group({
      dataInicio: [dataInicio, Validators.required],
      dataFim: [new Date(), Validators.required],
    });
  }
 
  ngOnInit(): void {
    const usuario: User | null = this.authService.obterUsuarioAtual();
    if (!usuario) {
      this.router.navigate(['/login']);
      return;
    }
    this.nomeUsuario = usuario.nome;
    this.carregarOperacoes();
  }
 
  carregarOperacoes(): void {
    const usuario: User | null = this.authService.obterUsuarioAtual();
    if (!usuario) return;
 
    const chave = 'movimentacoes_' + (usuario.numeroConta ?? usuario.cpf);
    const dados = localStorage.getItem(chave);
    const movimentacoes: Movimentacao[] = dados ? JSON.parse(dados) : [];
 
    this.operacoes = movimentacoes;
    this.filtrarOperacoes();
  }
 
  filtrarOperacoes(): void {
    if (this.form.invalid) {
      this.operacoesFiltradas = [];
      return;
    }
 
    const dataInicio = this.form.value.dataInicio
      ? new Date(this.form.value.dataInicio)
      : new Date();
    const dataFim = this.form.value.dataFim
      ? new Date(this.form.value.dataFim)
      : new Date();
 
    dataInicio.setHours(0, 0, 0, 0);
    dataFim.setHours(23, 59, 59, 999);
 
    this.operacoesFiltradas = this.operacoes.filter((op: Movimentacao) => {
      const dataOp = new Date(op.dataHora);
      return dataOp >= dataInicio && dataOp <= dataFim;
    });
  }
 
  obterIcone(tipo: string): string {
    switch (tipo) {
      case 'deposito': return 'arrow_downward';
      case 'saque': return 'arrow_upward';
      case 'transferencia_enviada': return 'arrow_upward';
      case 'transferencia_recebida': return 'arrow_downward';
      default: return 'receipt';
    }
  }
 
  obterDescricao(op: Movimentacao): string {
    switch (op.tipo) {
      case 'deposito': return 'Depósito';
      case 'saque': return 'Saque';
      case 'transferencia_enviada': return `Transferência para ${op.nomeDestino ?? op.contaDestino}`;
      case 'transferencia_recebida': return `Transferência de ${op.nomeOrigem ?? op.contaOrigem}`;
      default: return 'Operação';
    }
  }
 
  ehEntrada(tipo: string): boolean {
    return tipo === 'deposito' || tipo === 'transferencia_recebida';
  }
 
  ehSaida(tipo: string): boolean {
    return tipo === 'saque' || tipo === 'transferencia_enviada';
  }
 
  voltar(): void {
    this.router.navigate(['/cliente/inicio']); // corrigido: era /cliente-home
  }
}
 