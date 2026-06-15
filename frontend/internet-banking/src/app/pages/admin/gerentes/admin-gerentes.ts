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
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../../services/auth.service';
import { ManagerApiService } from '../../../services/manager-api.service';
import { User, AtualizacaoGerente, GerenteListagem, NovoGerente } from '../../../shared/models';

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
    MatProgressSpinnerModule,
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
  carregando = false;

  constructor(
    private authService: AuthService,
    private managerApi: ManagerApiService,
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
      nome:     ['', [Validators.required, Validators.minLength(3)]],
      cpf:      ['', [Validators.required, Validators.pattern(/^\d{3}\.\d{3}\.\d{3}-\d{2}$/)]],
      email:    ['', [Validators.required, Validators.email]],
      telefone: ['', Validators.required],
      senha:    ['', [Validators.required, Validators.minLength(4)]],
    });

    this.formEdicao = this.fb.group({
      nome:  ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.minLength(4)]],
    });
  }

  carregarGerentes(): void {
    this.carregando = true;

    this.managerApi.listarGerentes().subscribe({
      next: (lista) => {
        this.carregando = false;
        this.gerentes = lista.filter(g => g.nome?.trim() && g.email?.trim() && g.cpf?.trim());
        this.selecionarPrimeiro();
      },
      error: () => {
        this.carregando = false;
        this.gerentes = this.authService.obterGerentes()
          .filter(g => g.nome?.trim() && g.email?.trim() && g.cpf?.trim());
        this.selecionarPrimeiro();
      }
    });
  }

  private selecionarPrimeiro(): void {
    if (this.gerenteSelecionado) {
      const atualizado = this.gerentes.find(g => g.cpf === this.gerenteSelecionado?.cpf);
      if (atualizado) { this.selecionarGerente(atualizado); return; }
    }
    if (this.gerentes.length > 0) this.selecionarGerente(this.gerentes[0]);
    else { this.gerenteSelecionado = null; this.formEdicao.reset(); }
  }

  selecionarGerente(gerente: GerenteListagem): void {
    this.gerenteSelecionado = gerente;
    this.formEdicao.reset({ nome: gerente.nome, email: gerente.email, senha: '' });
  }

  cadastrarGerente(): void {
    if (this.formCriacao.invalid) {
      this.formCriacao.markAllAsTouched();
      this.snackBar.open('Preencha os campos corretamente.', 'Fechar', { duration: 3000 });
      return;
    }

    const v = this.formCriacao.getRawValue();
    const payload: NovoGerente = {
      nome: v.nome,
      cpf: this.digitos(v.cpf),
      email: v.email,
      telefone: v.telefone,
      senha: v.senha,
    };

    this.managerApi.inserirGerente(payload).subscribe({
      next: () => {
        this.formCriacao.reset();
        this.snackBar.open('Gerente cadastrado com sucesso.', 'Fechar', { duration: 3000 });
        this.carregarGerentes();
      },
      error: (err) => {
        try {
          this.authService.criarGerente(payload);
          this.formCriacao.reset();
          this.snackBar.open('Gerente cadastrado com sucesso.', 'Fechar', { duration: 3000 });
          this.carregarGerentes();
        } catch (e) {
          this.snackBar.open(err.message ?? 'Não foi possível cadastrar o gerente.', 'Fechar', { duration: 3000 });
        }
      }
    });
  }

  salvarAlteracoes(): void {
    if (!this.gerenteSelecionado || this.formEdicao.invalid) {
      this.formEdicao.markAllAsTouched();
      this.snackBar.open('Preencha os campos corretamente.', 'Fechar', { duration: 3000 });
      return;
    }

    const v = this.formEdicao.getRawValue();
    const payload: AtualizacaoGerente = { nome: v.nome, email: v.email, senha: v.senha };

    this.managerApi.alterarGerente(this.gerenteSelecionado.cpf, payload).subscribe({
      next: (atualizado) => {
        this.snackBar.open('Gerente atualizado com sucesso.', 'Fechar', { duration: 3000 });
        this.carregarGerentes();
      },
      error: (err) => {
        try {
          const atualizado = this.authService.atualizarGerente(this.gerenteSelecionado!.cpf, payload);
          this.selecionarGerente(atualizado);
          this.snackBar.open('Gerente atualizado com sucesso.', 'Fechar', { duration: 3000 });
          this.carregarGerentes();
        } catch (e) {
          this.snackBar.open(err.message ?? 'Não foi possível atualizar.', 'Fechar', { duration: 3000 });
        }
      }
    });
  }

  removerGerente(): void {
    if (!this.gerenteSelecionado) return;
    if (!window.confirm(`Deseja remover o gerente ${this.gerenteSelecionado.nome}?`)) return;

    this.managerApi.removerGerente(this.gerenteSelecionado.cpf).subscribe({
      next: () => {
        this.snackBar.open('Gerente removido com sucesso.', 'Fechar', { duration: 3000 });
        this.carregarGerentes();
      },
      error: (err) => {
        this.snackBar.open(err.message ?? 'Não foi possível remover.', 'Fechar', { duration: 3000 });
      }
    });
  }

  formatarTelefone(event: Event, formulario: FormGroup): void {
    const input = event.target as HTMLInputElement;
    const d = this.digitos(input.value).slice(0, 11);
    let f = '';
    if (d.length <= 2) f = d ? `(${d}` : '';
    else if (d.length <= 6) f = `(${d.slice(0,2)}) ${d.slice(2)}`;
    else if (d.length <= 10) f = `(${d.slice(0,2)}) ${d.slice(2,6)}-${d.slice(6)}`;
    else f = `(${d.slice(0,2)}) ${d.slice(2,7)}-${d.slice(7)}`;
    input.value = f;
    formulario.get('telefone')?.setValue(f, { emitEvent: false });
  }

  formatarCpf(event: Event): void {
    const input = event.target as HTMLInputElement;
    const d = this.digitos(input.value).slice(0, 11);
    let f = d;
    if (d.length > 9) f = `${d.slice(0,3)}.${d.slice(3,6)}.${d.slice(6,9)}-${d.slice(9)}`;
    else if (d.length > 6) f = `${d.slice(0,3)}.${d.slice(3,6)}.${d.slice(6)}`;
    else if (d.length > 3) f = `${d.slice(0,3)}.${d.slice(3)}`;
    input.value = f;
    this.formCriacao.get('cpf')?.setValue(f, { emitEvent: false });
  }

  voltar(): void { this.router.navigate(['/admin/inicio']); }

  private digitos(v: string): string { return (v || '').replace(/\D/g, ''); }
}
