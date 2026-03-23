import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';


@Component({
  selector: 'app-transferencia',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ] ,
  templateUrl: './transferencia.html'
})
export class TransferenciaComponent {
  contaDestino: string = '';
  valor: number = 0;

  mensagem: string = '';
  erro: string = '';

  constructor(private authService: AuthService) {}

  transferir() {
    try {
      this.authService.transferir(this.contaDestino, this.valor);
      this.mensagem = 'Transferência realizada com sucesso!';
      this.erro = '';
    } catch (e: any) {
      this.erro = e.message;
      this.mensagem = '';
    }
  }
}
