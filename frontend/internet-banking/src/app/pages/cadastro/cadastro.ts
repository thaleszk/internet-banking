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
import { ClienteRegistro} from '../../shared/models';

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
    MatIconModule
  ],
  templateUrl: './cadastro.html',
  styleUrl: './cadastro.css'
})
export class CadastroComponent implements OnInit {
  form;
  carregando = false;
  sucessoMensagem: string | null = null;
  erroMensagem: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      nome: ['', Validators.required],
      cpf: ['', [Validators.required, Validators.minLength(11)]],
      email: ['', [Validators.required, Validators.email]],
      telefone: ['', Validators.required],
      salario: ['', [Validators.required, Validators.min(0)]],
      cep: ['', Validators.required],
      logradouro: ['', Validators.required],
      numero: ['', Validators.required],
      complemento: [''],
      cidade: ['', Validators.required],
      estado: ['', Validators.required]
    });
  }

  ngOnInit() {
    if (this.authService.estaAutenticado()) {
      const usuario = this.authService.obterUsuarioAtual();
      if (usuario) {
        this.router.navigate([`/${usuario.perfil}/dashboard`]);
      }
    }
  }

  solicitarCadastro(){
    if(this.form.valid){
      this.carregando = true;
      this.erroMensagem = null;

      const formValue = this.form.value;
      const dados: ClienteRegistro = {
        nome: formValue.nome || '',
        cpf: formValue.cpf || '',
        email: formValue.email || '',
        telefone: formValue.telefone || '',
        salario: parseFloat(String(formValue.salario || 0)),
        cep: formValue.cep || '',
        logradouro: formValue.logradouro || '',
        numero: formValue.numero || '',
        complemento: formValue.complemento || '',
        cidade: formValue.cidade || '',
        estado: formValue.estado || ''
      };

      this.authService.autocadastro(dados).subscribe({
        next: (resposta) => {
          this.carregando = false;
          this.sucessoMensagem = 'Sua solicitação foi enviada! Um gerente  analisará em breve e entrará em contato por e-mail.';
          
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 3000);
        },
        error: (erro) => {
          this.carregando = false;
          this.erroMensagem = erro.message || 'Erro ao processar a solicitação. Tente novamente.';
        }
      });
    }
  }

}
