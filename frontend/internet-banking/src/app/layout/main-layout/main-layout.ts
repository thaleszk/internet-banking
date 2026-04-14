import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from '../../services/auth.service';
import { User } from '../../shared/models';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, MatIconModule, MatButtonModule],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.css'
})
export class MainLayoutComponent implements OnInit {

  isLoggedIn = false;
  perfil: 'CLIENTE' | 'GERENTE' | 'ADMIN' | null = null;
  nomeUsuario = '';

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.authService.usuario$.subscribe((user: User | null) => {
      if (user) {
        this.isLoggedIn = true;
        this.perfil = user.perfil.toUpperCase() as 'CLIENTE' | 'GERENTE' | 'ADMIN';
        this.nomeUsuario = user.nome;
      } else {
        this.isLoggedIn = false;
        this.perfil = null;
        this.nomeUsuario = '';
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}

