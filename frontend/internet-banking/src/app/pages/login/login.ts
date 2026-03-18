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

@Component({
  selector: 'app-login',
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
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent implements OnInit {
  form;
  mostrarSenha = false;
  carregando = false;
  erro: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      senha: ['', Validators.required]
    });
  }

  ngOnInit() {
    if (this.authService.estaAutenticado()) {
      const usuario = this.authService.obterUsuarioAtual();
      if (usuario) {
        this.redirecionarPorPerfil(usuario.perfil);
      }
    }
  }

  fazerLogin() {
    if (this.form.valid) {
      this.carregando = true;
      this.erro = null;

      const formValue = this.form.value;
      const email = formValue.email || '';
      const senha = formValue.senha || '';

      this.authService.login(email, senha).subscribe({
        next: (usuario) => {
          this.carregando = false;
          this.redirecionarPorPerfil(usuario.perfil);
        },
        error: (erro) => {
          this.carregando = false;
          this.erro = erro.message || 'Erro ao fazer login';
        }
      });
    }
  }

  private redirecionarPorPerfil(perfil: string) {
    switch (perfil) {
      case 'admin':
        this.router.navigate(['/admin/dashboard']);
        break;
      case 'gerente':
        this.router.navigate(['/gerente/inicio']);
        break;
      case 'cliente':
        this.router.navigate(['/cliente-home']);
        break;
      default:
        this.router.navigate(['/']);
    }
  }
}
