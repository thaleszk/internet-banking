import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import {
  AtualizacaoGerente,
  AuthService,
  GerenteListagem,
  NovoGerente,
  User,
} from '../../../services/auth.service';

@Component({
  selector: 'app-admin-gerentes',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSnackBarModule,
  ],
  templateUrl: './admin-gerentes.html',
  styleUrl: './admin-gerentes.css',
})
export class AdminGerentesComponent implements OnInit {
  usuarioAtual: User | null = null;
  gerentes: GerenteListagem[] = [];
  gerenteSelecionado: GerenteListagem | null = null;
  formCriacao!: FormGroup;
  formEdicao!: FormGroup;

  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router,
    private snackBar: MatSnackBar
  ) {
    this.criarFormularios();
  }

  ngOnInit(): void {
    this.usuarioAtual = this.authService.obterUsuarioAtual();

    if (!this.usuarioAtual || this.usuarioAtual.perfil !== 'admin') {
      this.router.navigate(['/login']);
      return;
    }

    this.carregarGerentes();
  }

  private criarFormularios(): void {
    this.formCriacao = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      cpf: ['', [Validators.required, Validators.pattern(/^\d{3}\.\d{3}\.\d{3}-\d{2}$/)]],
      email: ['', [Validators.required, Validators.email]],
      telefone: ['', Validators.required],
      senha: ['', [Validators.required, Validators.minLength(4)]],
    });

    this.formEdicao = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.minLength(4)]],
    });
  }

  private carregarGerentes(): void {
    this.gerentes = this.authService
      .obterGerentes()
      .filter((gerente) => gerente.nome?.trim() && gerente.email?.trim() && gerente.cpf?.trim());

    if (this.gerenteSelecionado) {
      const gerenteAtualizado = this.gerentes.find(
        (gerente) => gerente.cpf === this.gerenteSelecionado?.cpf
      );

      if (gerenteAtualizado) {
        this.selecionarGerente(gerenteAtualizado);
        return;
      }
    }

    if (this.gerentes.length > 0) {
      this.selecionarGerente(this.gerentes[0]);
      return;
    }

    this.gerenteSelecionado = null;
    this.formEdicao.reset();
  }

  selecionarGerente(gerente: GerenteListagem): void {
    this.gerenteSelecionado = gerente;
    this.formEdicao.reset({
      nome: gerente.nome,
      email: gerente.email,
      senha: '',
    });
  }

  cadastrarGerente(): void {
    if (this.formCriacao.invalid) {
      this.formCriacao.markAllAsTouched();
      this.snackBar.open('Preencha os campos corretamente.', 'Fechar', {
        duration: 3000,
      });
      return;
    }

    const valorFormulario = this.formCriacao.getRawValue();
    const payload: NovoGerente = {
      nome: valorFormulario.nome,
      cpf: this.obterDigitos(valorFormulario.cpf),
      email: valorFormulario.email,
      telefone: valorFormulario.telefone,
      senha: valorFormulario.senha,
    };

    try {
      const gerenteCriado = this.authService.criarGerente(payload);
      this.carregarGerentes();
      const gerenteSelecionado = this.gerentes.find(
        (gerente) => gerente.cpf === gerenteCriado.cpf
      );

      if (gerenteSelecionado) {
        this.selecionarGerente(gerenteSelecionado);
      }

      this.formCriacao.reset();
      this.snackBar.open('Gerente cadastrado com sucesso.', 'Fechar', {
        duration: 3000,
      });
    } catch (erro) {
      console.error('Erro ao cadastrar gerente', erro);
      this.snackBar.open(this.obterMensagemErro(erro, 'Não foi possível cadastrar o gerente.'), 'Fechar', {
        duration: 3000,
      });
    }
  }

  salvarAlteracoes(): void {
    if (!this.gerenteSelecionado) {
      return;
    }

    if (this.formEdicao.invalid) {
      this.formEdicao.markAllAsTouched();
      this.snackBar.open('Preencha os campos corretamente.', 'Fechar', {
        duration: 3000,
      });
      return;
    }

    const valorFormulario = this.formEdicao.getRawValue();
    const payload: AtualizacaoGerente = {
      nome: valorFormulario.nome,
      email: valorFormulario.email,
      senha: valorFormulario.senha,
    };

    try {
      const gerenteAtualizado = this.authService.atualizarGerente(
        this.gerenteSelecionado.cpf,
        payload
      );
      this.carregarGerentes();
      this.selecionarGerente(gerenteAtualizado);
      this.snackBar.open('Gerente atualizado com sucesso.', 'Fechar', {
        duration: 3000,
      });
    } catch (erro) {
      console.error('Erro ao atualizar gerente', erro);
      this.snackBar.open(this.obterMensagemErro(erro, 'Não foi possível atualizar o gerente.'), 'Fechar', {
        duration: 3000,
      });
    }
  }

  removerGerente(): void {
    if (!this.gerenteSelecionado) {
      return;
    }

    const confirmarRemocao = window.confirm(
      `Deseja remover o gerente ${this.gerenteSelecionado.nome}?`
    );

    if (!confirmarRemocao) {
      return;
    }

    try {
      this.authService.removerGerente(this.gerenteSelecionado.cpf);
      this.carregarGerentes();

      if (this.gerentes.length > 0) {
        this.selecionarGerente(this.gerentes[0]);
      }

      this.snackBar.open('Gerente removido com sucesso.', 'Fechar', {
        duration: 3000,
      });
    } catch (erro) {
      console.error('Erro ao remover gerente', erro);
      this.snackBar.open(this.obterMensagemErro(erro, 'Não foi possível remover o gerente.'), 'Fechar', {
        duration: 3000,
      });
    }
  }

  formatarTelefone(event: Event, formulario: FormGroup): void {
    const input = event.target as HTMLInputElement;
    const digitos = this.obterDigitos(input.value).slice(0, 11);
    let valorFormatado = '';

    if (digitos.length <= 2) {
      valorFormatado = digitos ? `(${digitos}` : '';
    } else if (digitos.length <= 6) {
      valorFormatado = `(${digitos.slice(0, 2)}) ${digitos.slice(2)}`;
    } else if (digitos.length <= 10) {
      valorFormatado = `(${digitos.slice(0, 2)}) ${digitos.slice(2, 6)}-${digitos.slice(6)}`;
    } else {
      valorFormatado = `(${digitos.slice(0, 2)}) ${digitos.slice(2, 7)}-${digitos.slice(7)}`;
    }

    input.value = valorFormatado;
    formulario.get('telefone')?.setValue(valorFormatado, { emitEvent: false });
  }

  formatarCpf(event: Event): void {
    const input = event.target as HTMLInputElement;
    const digitos = this.obterDigitos(input.value).slice(0, 11);
    let valorFormatado = digitos;

    if (digitos.length > 9) {
      valorFormatado = `${digitos.slice(0, 3)}.${digitos.slice(3, 6)}.${digitos.slice(6, 9)}-${digitos.slice(9)}`;
    } else if (digitos.length > 6) {
      valorFormatado = `${digitos.slice(0, 3)}.${digitos.slice(3, 6)}.${digitos.slice(6)}`;
    } else if (digitos.length > 3) {
      valorFormatado = `${digitos.slice(0, 3)}.${digitos.slice(3)}`;
    }

    input.value = valorFormatado;
    this.formCriacao.get('cpf')?.setValue(valorFormatado, { emitEvent: false });
  }

  voltar(): void {
    this.router.navigate(['/admin/inicio']);
  }

  private obterDigitos(valor: string): string {
    return (valor || '').replace(/\D/g, '');
  }

  private obterMensagemErro(erro: unknown, mensagemPadrao: string): string {
    return erro instanceof Error ? erro.message : mensagemPadrao;
  }
}
