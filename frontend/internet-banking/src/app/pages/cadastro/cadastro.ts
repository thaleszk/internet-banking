import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';

import { AuthService } from '../../services/auth.service';
import { CustomerApiService } from '../../services/customer-api.service';
import { ClienteRegistro } from '../../shared/models';
import { NgxMaskDirective } from 'ngx-mask';
import { CepService } from '../../services/cep.service';


@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    NgxMaskDirective
  ],
  templateUrl: './cadastro.html',
  styleUrl: './cadastro.css'
})
export class CadastroComponent implements OnInit {
  form;
  carregando = false;
  sucessoMensagem: string | null = null;
  erroMensagem: string | null = null;
  isLoadingCep = false;
  cepErrorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private customerApi: CustomerApiService,
    private router: Router,
    private readonly cepService: CepService
  ) {
    this.form = this.fb.group({
      nome:        ['', Validators.required],
      cpf:         ['', [Validators.required, Validators.minLength(11)]],
      email:       ['', [Validators.required, Validators.email]],
      telefone:    ['', Validators.required],
      salario:     ['', [Validators.required, Validators.min(0)]],
      cep:         ['', Validators.required],
      logradouro:  ['', Validators.required],
      numero:      ['', Validators.required],
      complemento: [''],
      cidade:      ['', Validators.required],
      estado:      ['', Validators.required]
    });
  }

  ngOnInit() {
    // Se já estiver logado, redireciona para a tela correta
    if (this.authService.estaAutenticado()) {
      const usuario = this.authService.obterUsuarioAtual();
      if (usuario) {
        this.router.navigate([`/${usuario.perfil}/inicio`]);
      }
    }
  }

  
  searchCep(): void {
    const zipCodeControl = this.form.get('cep');
    const zipCode = zipCodeControl?.value || '';
    const cleanZipCode = zipCode.replace(/\D/g, '');

    this.cepErrorMessage = '';

    if (cleanZipCode.length !== 8) {
      return;
    }

    this.isLoadingCep = true;

    this.cepService.getAddressByCep(cleanZipCode).subscribe({
      next: (response) => {
        this.isLoadingCep = false;

        if (response.erro) {
          this.cepErrorMessage = 'CEP não encontrado.';
          return;
        }

        this.form.patchValue({
          logradouro: response.logradouro,
          complemento: response.complemento,
          cep: response.cep,
          cidade: response.localidade,
          estado: response.uf
        });
      },
      error: () => {
        this.isLoadingCep = false;
        this.cepErrorMessage = 'Erro ao consultar o CEP.';
      }
    });
  }

  solicitarCadastro() {
    if (this.form.invalid) return;

    this.carregando = true;
    this.erroMensagem = null;

    const f = this.form.value;
    const dados: ClienteRegistro = {
      nome:        f.nome        || '',
      cpf:         f.cpf         || '',
      email:       f.email       || '',
      telefone:    f.telefone    || '',
      salario:     parseFloat(String(f.salario || 0)),
      cep:         f.cep         || '',
      logradouro:  f.logradouro  || '',
      numero:      f.numero      || '',
      complemento: f.complemento || '',
      cidade:      f.cidade      || '',
      estado:      f.estado      || ''
    };

    // ✅ Usa CustomerApiService (integrado com gateway) em vez de AuthService
    this.customerApi.solicitarAutocadastro(dados).subscribe({
      next: () => {
        this.carregando = false;
        this.sucessoMensagem =
          'Sua solicitação foi enviada! Um gerente analisará em breve e entrará em contato por e-mail.';
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: (erro) => {
        this.carregando = false;
        this.erroMensagem = erro.message || 'Erro ao processar a solicitação. Tente novamente.';
      }
    });
  }
}