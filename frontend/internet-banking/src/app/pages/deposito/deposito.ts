import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-deposito',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    FormsModule
  ],
  templateUrl: './deposito.html',
  styleUrls: ['./deposito.css']
})
export class DepositoComponent {
  valor: number = 0;

  constructor(private authService: AuthService) {}

  depositar() {
    if (this.valor > 0) {
      // Lógica para depósito
      alert(`Depósito de R$ ${this.valor} realizado com sucesso!`);
      this.valor = 0;
    } else {
      alert('Valor inválido!');
    }
  }
}
