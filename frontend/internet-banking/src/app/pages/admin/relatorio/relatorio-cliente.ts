import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { forkJoin } from 'rxjs';
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

    forkJoin({
      customers: this.http.get<any[]>(`${this.gatewayUrl}/customers`, { headers }),
      managers: this.http.get<any[]>(`${this.gatewayUrl}/managers`, { headers }),
      accounts: this.http.get<any[]>(`${this.gatewayUrl}/accounts`, { headers }),
    }).subscribe({
      next: ({ customers, managers, accounts }) => {
        this.carregando = false;
        this.clientes = this.montarRelatorio(customers, managers, accounts);
      },
      error: () => {
        this.carregando = false;
        this.clientes = this.authService.obterRelatorioClientes();
        this.clientes.sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'));
      },
    });
  }

  private montarRelatorio(customers: any[], managers: any[], accounts: any[]): ClienteRelatorio[] {
    return this.mapearClientes(customers, managers, accounts)
      .sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'));
  }

  private mapearClientes(customers: any[], managers: any[], accounts: any[] = []): ClienteRelatorio[] {
    return customers.map((customer: any) => {
      const conta = accounts.find((account: any) =>
        this.mesmoCpf(this.cpfClienteDaConta(account), customer.cpf)
      );
      const cpfGerente = this.cpfGerenteDaConta(conta)
        || customer.cpfManager
        || customer.managerCpf
        || customer.gerente
        || customer.pendingManagerCpf
        || '';
      const gerente = managers.find((manager: any) =>
        this.mesmoCpf(manager.cpf, cpfGerente)
      );

      return {
        cpf: customer.cpf,
        nome: customer.name ?? customer.nome,
        email: customer.email,
        salario: Number(customer.salary ?? customer.salario ?? 0),
        numeroConta: this.numeroDaConta(conta) || customer.accountNumber || customer.numeroConta || customer.conta || '—',
        saldo: this.saldoDaConta(conta, customer),
        limite: this.limiteDaConta(conta, customer),
        gerenteNome: gerente?.name ?? gerente?.nome ?? customer.managerName ?? '—',
        gerenteCpf: (gerente?.cpf ?? cpfGerente) || '—',
      } as ClienteRelatorio;
    });
  }

  private cpfClienteDaConta(account: any): string {
    return account?.cpfCustomer ?? account?.cpf_customer ?? account?.customerCpf ?? '';
  }

  private cpfGerenteDaConta(account: any): string {
    return account?.cpfManager ?? account?.cpf_manager ?? account?.managerCpf ?? '';
  }

  private numeroDaConta(account: any): string {
    return account?.number ?? account?.numeroConta ?? account?.accountNumber ?? '';
  }

  private saldoDaConta(account: any, customer: any): number {
    return Number(account?.balance ?? account?.saldo ?? customer?.balance ?? customer?.saldo ?? 0);
  }

  private limiteDaConta(account: any, customer: any): number {
    return Number(account?.limit ?? account?.accountLimit ?? customer?.accountLimit ?? customer?.limite ?? 0);
  }

  private mesmoCpf(a: string, b: string): boolean {
    return this.digitos(a) === this.digitos(b);
  }

  private digitos(valor: string): string {
    return (valor || '').replace(/\D/g, '');
  }
}
