import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { AuthService } from '../../services/auth.service';
import { User, Movimentacao } from '../../shared/models';

interface SaldoDiario {
  data: Date;
  saldo: number;
}

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
  saldosDiarios: SaldoDiario[] = [];
  carregando = false;
  erro: string | null = null;
  nomeUsuario: string = '';

  private readonly gatewayUrl = 'http://localhost:8000';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private http: HttpClient,
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
    this.consultarExtrato();
  }

  consultarExtrato(): void {
    if (this.form.invalid) return;

    const usuario = this.authService.obterUsuarioAtual();
    if (!usuario?.numeroConta) {
      this.carregarExtratLocal();
      return;
    }

    const dataInicio = this.formatarData(this.form.value.dataInicio as Date);
    const dataFim = this.formatarData(this.form.value.dataFim as Date);
    const token = this.authService.obterToken();
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });
    const params = new HttpParams()
      .set('startDate', dataInicio)
      .set('endDate', dataFim);

    this.carregando = true;
    this.erro = null;

    this.http.get<any[]>(
      `${this.gatewayUrl}/accounts/${usuario.numeroConta}/statement`,
      { headers, params }
    ).subscribe({
      next: (data) => {
        this.carregando = false;
        this.operacoes = (data || []).map((d: any) => ({
          dataHora: d.dateTime ?? d.dataHora ?? new Date().toISOString(),
          tipo: (d.type ?? '').toString().toLowerCase(),
          valor: Number(d.amount ?? d.value ?? d.valor ?? 0),
          contaOrigem: d.cpfOrigin ?? d.contaOrigem,
          contaDestino: d.cpfDest ?? d.contaDestino,
          nomeOrigem: d.nomeOrigem,
          nomeDestino: d.nomeDestino,
        }));
        this.aplicarFiltroData();
      },
      error: () => {
        this.carregando = false;
        // Fallback local
        this.carregarExtratLocal();
      }
    });
  }

  private carregarExtratLocal(): void {
    const usuario = this.authService.obterUsuarioAtual();
    if (!usuario) return;
    const movimentacoes: Movimentacao[] = this.authService.obterMovimentacoes();
    const dataInicio = new Date(this.form.value.dataInicio as Date);
    const dataFim = new Date(this.form.value.dataFim as Date);
    dataInicio.setHours(0, 0, 0, 0);
    dataFim.setHours(23, 59, 59, 999);

    this.operacoes = movimentacoes.filter((op: Movimentacao) => {
      const d = new Date(op.dataHora);
      return d >= dataInicio && d <= dataFim;
    });
    this.aplicarFiltroData();
  }

  filtrarOperacoes(): void {
    this.consultarExtrato();
  }

  private aplicarFiltroData(): void {
    const dataInicio = new Date(this.form.value.dataInicio as Date);
    const dataFim = new Date(this.form.value.dataFim as Date);
    dataInicio.setHours(0, 0, 0, 0);
    dataFim.setHours(23, 59, 59, 999);

    this.operacoesFiltradas = (this.operacoes || []).filter((op) => {
      const d = new Date(op.dataHora);
      return d >= dataInicio && d <= dataFim;
    });

    this.calcularSaldosDiarios(dataInicio, dataFim);
  }

  private formatarData(data: Date): string {
    const ano = data.getFullYear();
    const mes = String(data.getMonth() + 1).padStart(2, '0');
    const dia = String(data.getDate()).padStart(2, '0');
    return `${ano}-${mes}-${dia}`;
  }

  obterIcone(tipo: string): string {
    const t = (tipo || '').toString().toLowerCase();
    if (t.includes('deposito')) return 'arrow_downward';
    if (t.includes('saque')) return 'arrow_upward';
    if (t.includes('transferencia')) return 'swap_horiz';
    return 'receipt';
  }

  obterDescricao(op: Movimentacao): string {
    const t = (op.tipo || '').toString().toLowerCase();
    if (t.includes('deposito')) return 'Depósito';
    if (t.includes('saque')) return 'Saque';
    if (t.includes('transferencia')) {
      return op.contaDestino ? `Transferência → Conta ${op.contaDestino}` : 'Transferência';
    }
    return (op as any).tipo || '';
  }

  ehEntrada(op: Movimentacao): boolean {
    const tipo = (op.tipo || '').toString().toLowerCase();
    return tipo.includes('deposito') || this.ehTransferenciaRecebida(op);
  }

  ehSaida(op: Movimentacao): boolean {
    const tipo = (op.tipo || '').toString().toLowerCase();
    return tipo.includes('saque') || (tipo.includes('transferencia') && !this.ehTransferenciaRecebida(op));
  }

  private calcularSaldosDiarios(dataInicio: Date, dataFim: Date): void {
    const usuario = this.authService.obterUsuarioAtual();
    const saldoAtual = usuario?.saldo ?? 0;
    const movimentacaoPeriodo = this.operacoesFiltradas.reduce(
      (total, op) => total + this.obterImpactoNoSaldo(op),
      0
    );
    let saldoDia = parseFloat((saldoAtual - movimentacaoPeriodo).toFixed(2));
    const saldos: SaldoDiario[] = [];

    for (let data = new Date(dataInicio); data <= dataFim; data = this.proximoDia(data)) {
      const movimentacaoDoDia = this.operacoesFiltradas
        .filter((op) => this.mesmoDia(new Date(op.dataHora), data))
        .reduce((total, op) => total + this.obterImpactoNoSaldo(op), 0);

      saldoDia = parseFloat((saldoDia + movimentacaoDoDia).toFixed(2));
      saldos.push({ data: new Date(data), saldo: saldoDia });
    }

    this.saldosDiarios = saldos;
  }

  private obterImpactoNoSaldo(op: Movimentacao): number {
    if (this.ehEntrada(op)) return Number(op.valor) || 0;
    if (this.ehSaida(op)) return -(Number(op.valor) || 0);
    return 0;
  }

  private mesmoDia(a: Date, b: Date): boolean {
    return a.getFullYear() === b.getFullYear()
      && a.getMonth() === b.getMonth()
      && a.getDate() === b.getDate();
  }

  private proximoDia(data: Date): Date {
    const proxima = new Date(data);
    proxima.setDate(proxima.getDate() + 1);
    return proxima;
  }

  private ehTransferenciaRecebida(op: Movimentacao): boolean {
    const usuario = this.authService.obterUsuarioAtual();
    const tipo = (op.tipo || '').toString().toLowerCase();
    return tipo.includes('transferencia') && (tipo.includes('recebida') || (!!usuario?.cpf && op.contaDestino === usuario.cpf));
  }

  voltar(): void {
    this.router.navigate(['/cliente/inicio']);
  }
}
