import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../services/auth.service';
import { User } from '../../shared/models';

@Component({
  selector: 'app-perfil',
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
    MatSnackBarModule,
  ],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class PerfilComponent implements OnInit {
  form!: FormGroup;
  carregando = false;
  enviando = false;
  usuarioAtual: User | null = null;
  saldoAtual = 0;
  nomeGerente = '';
  alteracaoConcluida = false;
  novoLimite = 0;
  Math = Math;

  private readonly gatewayUrl = 'http://localhost:8000';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private http: HttpClient,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.criarForm();
  }

  private criarForm(): void {
    this.form = this.fb.group({
      nome:        ['', [Validators.required, Validators.minLength(3)]],
      email:       ['', [Validators.required, Validators.email]],
      telefone:    ['', [Validators.required, Validators.pattern(/^\(\d{2}\)\s\d{4,5}-\d{4}$/)]],
      salario:     [0,  [Validators.required, Validators.min(0)]],
      logradouro:  ['', Validators.required],
      numero:      ['', Validators.required],
      complemento: [''],
      cep:         ['', [Validators.required, Validators.pattern(/^\d{5}-\d{3}$/)]],
      cidade:      ['', Validators.required],
      estado:      ['', [Validators.required, Validators.pattern(/^[A-Z]{2}$/)]],
    });

    this.form.get('salario')?.valueChanges.subscribe(() => this.calcularLimite());
  }

  ngOnInit(): void {
    const usuario = this.authService.obterUsuarioAtual();
    if (!usuario) { this.router.navigate(['/login']); return; }
    if (usuario.perfil !== 'cliente') {
      this.snackBar.open('Apenas clientes podem acessar essa página', 'Fechar', { duration: 3000 });
      this.router.navigate([`/${usuario.perfil}/inicio`]);
      return;
    }

    this.usuarioAtual = usuario;
    this.saldoAtual   = usuario.saldo ?? 0;
    this.nomeGerente  = usuario.gerente ?? 'Não atribuído';

    if (usuario.cpf) {
      this.carregando = true;
      const token = this.authService.obterToken();
      const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

      this.http
        .get<any>(`${this.gatewayUrl}/customers/${usuario.cpf}`, { headers })
        .subscribe({
          next: (dados) => {
            this.carregando = false;
            this.form.patchValue({
              nome:        dados.name        ?? usuario.nome,
              email:       dados.email       ?? usuario.email,
              telefone:    this.formatarTelefoneValor(dados.phone ?? usuario.telefone ?? ''),
              salario:     dados.salary      ?? usuario.salario ?? 0,
              logradouro:  dados.address?.streetName   ?? usuario.logradouro ?? '',
              numero:      dados.address?.streetNumber ?? usuario.numero ?? '',
              complemento: dados.address?.complement   ?? usuario.complemento ?? '',
              cep:         this.formatarCepValor(dados.address?.zipCode ?? usuario.cep ?? ''),
              cidade:      dados.address?.city         ?? usuario.cidade ?? '',
              estado:      this.formatarEstadoValor(dados.address?.state ?? usuario.estado ?? ''),
            });
            this.calcularLimite();
          },
          error: () => {
            this.carregando = false;
            this.preencherComDadosLocais();
          },
        });
    } else {
      this.preencherComDadosLocais();
    }
  }

  private preencherComDadosLocais(): void {
    if (!this.usuarioAtual) return;
    const u = this.usuarioAtual;
    this.form.patchValue({
      nome: u.nome ?? '', email: u.email ?? '',
      telefone: this.formatarTelefoneValor(u.telefone ?? ''), salario: u.salario ?? 0,
      logradouro: u.logradouro ?? '', numero: u.numero ?? '',
      complemento: u.complemento ?? '', cep: this.formatarCepValor(u.cep ?? ''),
      cidade: u.cidade ?? '', estado: this.formatarEstadoValor(u.estado ?? ''),
    });
    this.calcularLimite();
  }

  calcularLimite(): void {
    const salario = Number(this.form.get('salario')?.value) || 0;
    this.novoLimite = this.calcularLimiteParaSalario(salario);
  }

  private calcularLimiteParaSalario(salario: number): number {
    const limiteBase = salario >= 2000 ? Math.round((salario / 2) * 100) / 100 : 0;
    if (this.saldoAtual < 0 && limiteBase < Math.abs(this.saldoAtual)) {
      return Math.abs(this.saldoAtual);
    }
    return limiteBase;
  }

  deveMostrarNovoLimite(): boolean {
    const salario = Number(this.form.get('salario')?.value) || 0;
    return salario >= 2000 || this.saldoAtual < 0;
  }

  obterHintLimite(): string {
    if (this.saldoAtual < 0 && this.novoLimite === Math.abs(this.saldoAtual)) {
      return 'Limite ajustado ao saldo negativo atual';
    }
    return '50% do salário';
  }

  atualizarPerfil(): void {
    if (this.form.invalid || !this.usuarioAtual) {
      this.snackBar.open('Preencha todos os campos corretamente', 'Fechar', { duration: 3000 });
      return;
    }

    this.enviando = true;
    const v = this.form.getRawValue();
    const salarioNovo = Number(v.salario) || 0;
    const novoLimite = this.calcularLimiteParaSalario(salarioNovo);
    const token = this.authService.obterToken();
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    const body = {
      cpf:    this.usuarioAtual.cpf,
      name:   v.nome,
      email:  v.email,
      phone:  v.telefone,
      salary: salarioNovo,
      address: {
        streetName:   v.logradouro,
        streetNumber: v.numero,
        complement:   v.complemento || undefined,
        zipCode:      v.cep.replace(/\D/g, ''),
        city:         v.cidade,
        state:        v.estado,
      },
    };

    this.http
      .put<any>(
        `${this.gatewayUrl}/customers/${this.usuarioAtual.cpf}`,
        body,
        { headers }
      )
      .subscribe({
        next: () => {
          this.enviando = false;
          this.finalizarAtualizacao(v, salarioNovo, novoLimite);
        },
        error: () => {
          try {
            this.authService.atualizarPerfilCliente(this.usuarioAtual!.cpf, {
              nome: v.nome, email: v.email, telefone: v.telefone,
              salario: salarioNovo, logradouro: v.logradouro, numero: v.numero,
              complemento: v.complemento, cep: v.cep, cidade: v.cidade,
              estado: v.estado, limite: novoLimite,
            });
            this.enviando = false;
            this.finalizarAtualizacao(v, salarioNovo, novoLimite);
          } catch (e) {
            this.enviando = false;
            this.snackBar.open('Erro ao atualizar perfil', 'Fechar', { duration: 3000 });
          }
        },
      });
  }

  private finalizarAtualizacao(v: any, salario: number, limite: number): void {
    this.novoLimite = limite;
    this.alteracaoConcluida = true;
    this.snackBar.open('Perfil atualizado com sucesso!', 'Fechar', { duration: 3000 });
    setTimeout(() => this.router.navigate(['/cliente/inicio']), 3000);
  }

  formatarTelefone(event: Event): void {
    const input = event.target as HTMLInputElement;
    const d = input.value.replace(/\D/g, '').slice(0, 11);
    let f = '';
    if (d.length <= 2) f = d ? `(${d}` : '';
    else if (d.length <= 6) f = `(${d.slice(0,2)}) ${d.slice(2)}`;
    else if (d.length <= 10) f = `(${d.slice(0,2)}) ${d.slice(2,6)}-${d.slice(6)}`;
    else f = `(${d.slice(0,2)}) ${d.slice(2,7)}-${d.slice(7)}`;
    input.value = f;
    this.form.get('telefone')?.setValue(f, { emitEvent: false });
  }

  formatarCEP(event: Event): void {
    const input = event.target as HTMLInputElement;
    let v = input.value.replace(/\D/g, '').substring(0, 8);
    if (v.length >= 5) v = `${v.substring(0,5)}-${v.substring(5)}`;
    input.value = v;
    this.form.get('cep')?.setValue(v, { emitEvent: false });
  }

  formatarEstado(event: Event): void {
    const input = event.target as HTMLInputElement;
    let v = input.value.toUpperCase().replace(/[^A-Z]/g, '').substring(0, 2);
    input.value = v;
    this.form.get('estado')?.setValue(v, { emitEvent: false });
  }

  private formatarTelefoneValor(valor: string): string {
    const d = (valor || '').replace(/\D/g, '').slice(0, 11);
    if (d.length <= 2) return d ? `(${d}` : '';
    if (d.length <= 6) return `(${d.slice(0,2)}) ${d.slice(2)}`;
    if (d.length <= 10) return `(${d.slice(0,2)}) ${d.slice(2,6)}-${d.slice(6)}`;
    return `(${d.slice(0,2)}) ${d.slice(2,7)}-${d.slice(7)}`;
  }

  private formatarCepValor(valor: string): string {
    const d = (valor || '').replace(/\D/g, '').slice(0, 8);
    return d.length > 5 ? `${d.slice(0, 5)}-${d.slice(5)}` : d;
  }

  private formatarEstadoValor(valor: string): string {
    return (valor || '').toUpperCase().replace(/[^A-Z]/g, '').slice(0, 2);
  }

  cancelar(): void {
    this.router.navigate(['/cliente/inicio']);
  }
}
