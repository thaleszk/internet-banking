import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../services/auth.service';
import { User } from '../../shared/models';
 
@Component({
  selector: 'app-transferencia',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './transferencia.html',
  styleUrl: './transferencia.css',
})
export class TransferenciaComponent implements OnInit {
  contaDestino: string = '';
  valor: number = 0;
  mensagem: string = '';
  erro: string = '';
  saldoAtual: number = 0;
  limite: number = 0;
 
  constructor(private authService: AuthService, private router: Router) {}
 
  ngOnInit(): void {
    const usuario: User | null = this.authService.obterUsuarioAtual();
    if (!usuario) {
      this.router.navigate(['/login']);
      return;
    }
    this.saldoAtual = usuario.saldo ?? 0;
    this.limite = usuario.limite ?? 0;
 
    this.authService.usuario$.subscribe((u: User | null) => {
      if (u) {
        this.saldoAtual = u.saldo ?? 0;
        this.limite = u.limite ?? 0;
      }
    });
  }
 
  get saldoDisponivel(): number {
    return parseFloat((this.saldoAtual + this.limite).toFixed(2));
  }
 
  transferir(): void {
    this.mensagem = '';
    this.erro = '';
 
    try {
      this.authService.transferir(this.contaDestino, this.valor);
      this.mensagem = 'Transferência realizada com sucesso!';
      this.contaDestino = '';
      this.valor = 0;
    } catch (e: any) {
      this.erro = e.message;
    }
  }
 
  voltar(): void {
    this.router.navigate(['/cliente/inicio']); // corrigido: era ausente
  }
}
 