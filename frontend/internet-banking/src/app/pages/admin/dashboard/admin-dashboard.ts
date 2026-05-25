import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../../services/auth.service';
import { User, GerenteResumo } from '../../../shared/models';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatDividerModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboardComponent implements OnInit {
  usuarioAtual: User | null = null;
  gerentesResumo: GerenteResumo[] = [];
  carregando = false;

  private readonly gatewayUrl = 'http://localhost:8080';

  constructor(
    private authService: AuthService,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.usuarioAtual = this.authService.obterUsuarioAtual();
    if (!this.usuarioAtual || this.usuarioAtual.perfil !== 'admin') {
      this.router.navigate(['/login']);
      return;
    }
    this.carregarDashboard();
  }

  private carregarDashboard(): void {
    this.carregando = true;
    const token = this.authService.obterToken();
    const headers = new HttpHeaders({ Authorization: `Bearer ${token}` });

    // Busca gerentes no gateway
    this.http
      .get<any[]>(`${this.gatewayUrl}/managers`, { headers })
      .subscribe({
        next: (managers) => {
          // Para cada gerente, busca clientes via /customers
          this.http
            .get<any[]>(`${this.gatewayUrl}/customers`, { headers })
            .subscribe({
              next: (customers) => {
                this.carregando = false;
                this.gerentesResumo = this.montarResumo(managers, customers);
              },
              error: () => {
                this.carregando = false;
                // Fallback: usa somente os gerentes sem dados de saldo
                this.gerentesResumo = managers.map((m: any) => ({
                  nome: m.name ?? m.nome,
                  cpf: m.cpf,
                  email: m.email,
                  clienteCount: 0,
                  somaSaldosPositivos: 0,
                  somaSaldosNegativos: 0,
                }));
              },
            });
        },
        error: () => {
          this.carregando = false;
          // Fallback local
          this.gerentesResumo = this.authService.obterGerentesComIndicadores();
        },
      });
  }

  private montarResumo(managers: any[], customers: any[]): GerenteResumo[] {
    return managers
      .map((m: any) => {
        const clientesDoGerente = customers.filter(
          (c: any) => c.cpfManager === m.cpf || c.managerCpf === m.cpf
        );
        const positivos = clientesDoGerente
          .filter((c: any) => (c.balance ?? c.saldo ?? 0) >= 0)
          .reduce((sum: number, c: any) => sum + Number(c.balance ?? c.saldo ?? 0), 0);
        const negativos = clientesDoGerente
          .filter((c: any) => (c.balance ?? c.saldo ?? 0) < 0)
          .reduce((sum: number, c: any) => sum + Number(c.balance ?? c.saldo ?? 0), 0);

        return {
          nome: m.name ?? m.nome,
          cpf: m.cpf,
          email: m.email,
          clienteCount: clientesDoGerente.length,
          somaSaldosPositivos: parseFloat(positivos.toFixed(2)),
          somaSaldosNegativos: parseFloat(negativos.toFixed(2)),
        } as GerenteResumo;
      })
      .sort((a, b) => (b.somaSaldosPositivos ?? 0) - (a.somaSaldosPositivos ?? 0));
  }

  getTotalSaldosPositivos(): number {
    return this.gerentesResumo.reduce((t, g) => t + (g.somaSaldosPositivos ?? 0), 0);
  }

  getTotalSaldosNegativos(): number {
    return this.gerentesResumo.reduce((t, g) => t + (g.somaSaldosNegativos ?? 0), 0);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  navegarPara(rota: string): void {
    this.router.navigate([rota]);
  }
}