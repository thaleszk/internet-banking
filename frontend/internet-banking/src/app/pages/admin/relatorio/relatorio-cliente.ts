import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { ClienteRelatorio} from '../../../shared/models';


@Component({
  selector: 'app-relatorio-cliente',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './relatorio-cliente.html',
  styleUrls: ['./relatorio-cliente.css']
})
export class RelatorioClienteComponent implements OnInit {
  clientes: ClienteRelatorio[] = [];

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.clientes = this.authService.obterRelatorioClientes();
  }
}
