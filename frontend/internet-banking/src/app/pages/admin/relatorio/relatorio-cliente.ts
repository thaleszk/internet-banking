import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../../services/auth.service';
import { ClienteRelatorio } from '../../../shared/models';

@Component({
  selector: 'app-relatorio-cliente',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatProgressSpinnerModule],
  templateUrl: './relatorio-cliente.html',
  styleUrls: ['./relatorio-cliente.css'],
})
export class RelatorioClienteComponent implements OnInit {
  clientes: ClienteRelatorio[] = [];
  carregando = false;

  private readonly gatewayUrl = 'http://localhost:8000';

  constructor(
    private authService: AuthService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.carregarRelatorio();
  }

  private carregarRelatorio(): void {
    this.carregando = true;
    const token = this.authService.obterToken();
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    // Busca todos os clientes + gerentes para montar o relatório (API Composition)
    this.http
      .get<any[]>(`${this.gatewayUrl}/customers`, { headers })
      .subscribe({
        next: (customers) => {
          this.http
            .get<any[]>(`${this.gatewayUrl}/managers`, { headers })
            .subscribe({
              next: (managers) => {
                this.carregando = false;
                this.clientes = this.montarRelatorio(customers, managers);
              },
              error: () => {
                this.carregando = false;
                this.clientes = this.mapearClientes(customers, []);
              },
            });
        },
        error: () => {
          this.carregando = false;
          // Fallback local
          this.clientes = this.authService.obterRelatorioClientes();
          this.clientes.sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'));
        },
      });
  }

  private montarRelatorio(customers: any[], managers: any[]): ClienteRelatorio[] {
    return this.mapearClientes(customers, managers)
      .sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'));
  }

  private mapearClientes(customers: any[], managers: any[]): ClienteRelatorio[] {
    return customers.map((c: any) => {
      const gerente = managers.find(
        (m: any) => m.cpf === (c.cpfManager ?? c.managerCpf ?? c.pendingManagerCpf)
      );
      return {
        cpf:         c.cpf,
        nome:        c.name ?? c.nome,
        email:       c.email,
        salario:     Number(c.salary ?? c.salario ?? 0),
        numeroConta: c.accountNumber ?? c.numeroConta ?? '—',
        saldo:       Number(c.balance ?? c.saldo ?? 0),
        limite:      Number(c.accountLimit ?? c.limite ?? 0),
        gerenteNome: gerente?.name ?? gerente?.nome ?? c.managerName ?? '—',
        gerenteCpf:  gerente?.cpf ?? c.cpfManager ?? '—',
      } as ClienteRelatorio;
    });
  }
}