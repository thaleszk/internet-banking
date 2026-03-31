import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService, User } from '../../services/auth.service';

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

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.criarForm();
  }

  private criarForm(): void {
    this.form = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      telefone: [
        '',
        [Validators.required, Validators.pattern(/^\(\d{2}\)\s\d{4,5}-\d{4}$/)],
      ],
      salario: [0, [Validators.required, Validators.min(0)]],
      logradouro: ['', Validators.required],
      numero: ['', Validators.required],
      complemento: [''],
      cep: ['', [Validators.required, Validators.pattern(/^\d{5}-\d{3}$/)]],
      cidade: ['', Validators.required],
      estado: ['', [Validators.required, Validators.pattern(/^[A-Z]{2}$/)]],
    });

    this.form.get('salario')?.valueChanges.subscribe(() => this.calcularLimite());
  }

  ngOnInit(): void {
    const usuario = this.authService.obterUsuarioAtual();

    if (!usuario) {
      this.router.navigate(['/login']);
      return;
    }

    if (usuario.perfil !== 'cliente') {
      this.snackBar.open('Apenas clientes podem acessar essa página', 'Fechar', {
        duration: 3000,
      });
      this.redirecionarPorPerfil(usuario.perfil);
      return;
    }

    this.usuarioAtual = usuario;
    this.saldoAtual = usuario.saldo ?? 0;
    this.nomeGerente = usuario.gerente ?? 'Não atribuído';
    this.carregando = true;

    setTimeout(() => {
      this.carregarDadosPerfil();
      this.carregando = false;
    }, 500);
  }

  private redirecionarPorPerfil(perfil: User['perfil']): void {
    if (perfil === 'admin') {
      this.router.navigate(['/admin/inicio']);
      return;
    }

    if (perfil === 'gerente') {
      this.router.navigate(['/gerente/inicio']);
      return;
    }

    this.router.navigate(['/cliente-home']);
  }

  private carregarDadosPerfil(): void {
    if (!this.usuarioAtual) {
      return;
    }

    const chavePerfil = `perfil_${this.usuarioAtual.cpf}`;
    const dadosPerfil = localStorage.getItem(chavePerfil);

    if (dadosPerfil) {
      try {
        this.form.patchValue(JSON.parse(dadosPerfil));
      } catch {
        this.preencherComDadosUsuario();
      }
    } else {
      this.preencherComDadosUsuario();
    }

    this.calcularLimite();
  }

  private preencherComDadosUsuario(): void {
    if (!this.usuarioAtual) {
      return;
    }

    this.form.patchValue({
      nome: this.usuarioAtual.nome ?? '',
      email: this.usuarioAtual.email ?? '',
      salario: this.usuarioAtual.salario ?? 0,
      telefone: this.usuarioAtual.telefone ?? '',
      logradouro: this.usuarioAtual.logradouro ?? '',
      numero: this.usuarioAtual.numero ?? '',
      complemento: this.usuarioAtual.complemento ?? '',
      cep: this.usuarioAtual.cep ?? '',
      cidade: this.usuarioAtual.cidade ?? '',
      estado: this.usuarioAtual.estado ?? '',
    });
  }

  private calcularLimiteParaSalario(salario: number): number {
    const limiteBase = salario >= 2000 ? Math.round((salario / 2) * 100) / 100 : 0;

    if (this.saldoAtual < 0 && limiteBase < Math.abs(this.saldoAtual)) {
      return Math.abs(this.saldoAtual);
    }

    return limiteBase;
  }

  calcularLimite(): void {
    const salario = Number(this.form.get('salario')?.value) || 0;
    this.novoLimite = this.calcularLimiteParaSalario(salario);
  }

  deveMostrarNovoLimite(): boolean {
    const salario = Number(this.form.get('salario')?.value) || 0;
    return salario >= 2000 || this.saldoAtual < 0;
  }

  obterHintLimite(): string {
    const salario = Number(this.form.get('salario')?.value) || 0;

    if (this.saldoAtual < 0 && salario < 2000) {
      return 'Limite ajustado ao saldo negativo atual';
    }

    if (this.saldoAtual < 0 && this.novoLimite === Math.abs(this.saldoAtual)) {
      return 'Limite ajustado ao saldo negativo atual';
    }

    return '50% do salário';
  }

  formatarTelefone(event: Event): void {
    const input = event.target as HTMLInputElement;
    const value = this.aplicarMascaraTelefone(input.value);

    input.value = value;
    this.form.get('telefone')?.setValue(value, { emitEvent: false });
  }

  private aplicarMascaraTelefone(valor: string): string {
    const digitos = valor.replace(/\D/g, '').slice(0, 11);

    if (digitos.length === 0) {
      return '';
    }

    if (digitos.length <= 2) {
      return `(${digitos}`;
    }

    if (digitos.length <= 6) {
      return `(${digitos.slice(0, 2)}) ${digitos.slice(2)}`;
    }

    if (digitos.length <= 10) {
      return `(${digitos.slice(0, 2)}) ${digitos.slice(2, 6)}-${digitos.slice(6)}`;
    }

    return `(${digitos.slice(0, 2)}) ${digitos.slice(2, 7)}-${digitos.slice(7)}`;
  }

  formatarCEP(event: Event): void {
    const input = event.target as HTMLInputElement;
    let value = input.value.replace(/\D/g, '');

    if (value.length > 8) {
      value = value.substring(0, 8);
    }

    if (value.length >= 5) {
      value = `${value.substring(0, 5)}-${value.substring(5)}`;
    }

    input.value = value;
    this.form.get('cep')?.setValue(value, { emitEvent: false });
  }

  formatarEstado(event: Event): void {
    const input = event.target as HTMLInputElement;
    let value = input.value.toUpperCase().replace(/[^A-Z]/g, '');

    if (value.length > 2) {
      value = value.substring(0, 2);
    }

    input.value = value;
    this.form.get('estado')?.setValue(value, { emitEvent: false });
  }

  atualizarPerfil(): void {
    const usuarioAtual = this.usuarioAtual;

    if (this.form.invalid || !usuarioAtual) {
      this.snackBar.open('Preencha todos os campos corretamente', 'Fechar', {
        duration: 3000,
      });
      return;
    }

    this.enviando = true;

    setTimeout(() => {
      try {
        const dadosAtualizados = this.form.getRawValue();
        const salarioNovo = Number(dadosAtualizados.salario) || 0;
        const novoLimite = this.calcularLimiteParaSalario(salarioNovo);

        const usuarioAtualizado = this.authService.atualizarPerfilCliente(
          usuarioAtual.cpf,
          {
            nome: dadosAtualizados.nome || usuarioAtual.nome,
            email: dadosAtualizados.email || usuarioAtual.email,
            telefone: dadosAtualizados.telefone,
            salario: salarioNovo,
            logradouro: dadosAtualizados.logradouro,
            numero: dadosAtualizados.numero,
            complemento: dadosAtualizados.complemento,
            cep: dadosAtualizados.cep,
            cidade: dadosAtualizados.cidade,
            estado: dadosAtualizados.estado,
            limite: novoLimite,
          }
        );

        const chavePerfil = `perfil_${usuarioAtual.cpf}`;
        localStorage.setItem(
          chavePerfil,
          JSON.stringify({
            ...dadosAtualizados,
            salario: salarioNovo,
          })
        );

        this.usuarioAtual = usuarioAtualizado;
        this.saldoAtual = usuarioAtualizado.saldo ?? this.saldoAtual;
        this.nomeGerente = usuarioAtualizado.gerente ?? this.nomeGerente;
        this.novoLimite = usuarioAtualizado.limite ?? novoLimite;
        this.alteracaoConcluida = true;

        this.snackBar.open('Perfil atualizado com sucesso!', 'Fechar', {
          duration: 3000,
        });

        setTimeout(() => {
          this.router.navigate(['/cliente-home']);
        }, 3000);
      } catch (erro) {
        console.error('Erro ao atualizar perfil', erro);
        this.snackBar.open('Erro ao atualizar perfil', 'Fechar', {
          duration: 3000,
        });
      } finally {
        this.enviando = false;
      }
    }, 1000);
  }

  cancelar(): void {
    this.router.navigate(['/cliente-home']);
  }
}
