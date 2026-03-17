import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-cliente-home',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  templateUrl: './cliente-home.html',
  styleUrl: './cliente-home.css',
})
export class ClienteHome implements OnInit {
  nomeCliente: string = '';
  numeroConta: string = '';
  nomeGerente: string = '';
  saldoAtual: number = 0;
  limite: number = 0;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    const usuario = this.authService.obterUsuarioAtual();
    if (!usuario) {
      this.router.navigate(['/login']);
      return;
    }
    this.nomeCliente = usuario.nome;
    this.numeroConta = usuario.numeroConta ?? '----';
    this.nomeGerente = usuario.gerente ?? 'Não atribuído';
    this.saldoAtual = usuario.saldo ?? 0;
    this.limite = usuario.limite ?? 0;

    this.authService.usuario$.subscribe((u) => {
      if (u) this.saldoAtual = u.saldo ?? 0;
    });
  }

  get saldoNegativo(): boolean {
    return this.saldoAtual < 0;
  }

  navegarPara(rota: string): void {
    this.router.navigate([rota]);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
