import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, MatIconModule, MatButtonModule],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.css'
})
export class MainLayoutComponent {

  // TODO: substituir pela leitura real do AuthService / localStorage após R2
  isLoggedIn = false;
  perfil: 'CLIENTE' | 'GERENTE' | 'ADMIN' | null = null;
  nomeUsuario = '';

  logout(): void {
    this.isLoggedIn = false;
    this.perfil = null;
    this.nomeUsuario = '';
    // TODO: chamar AuthService.logout() e redirecionar para /login
  }
}

