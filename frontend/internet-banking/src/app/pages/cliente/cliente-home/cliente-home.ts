import { Component } from '@angular/core';

@Component({
  selector: 'app-cliente-home',
  imports: [],
  templateUrl: './cliente-home.html',
  styleUrl: './cliente-home.css',
})
export class ClienteHome {
  nomeCliente: string = 'Cliente';
  saldoAtual: number = -150.50;

  get saldoNegativo(): boolean {
    return this.saldoAtual < 0;
  }
}
