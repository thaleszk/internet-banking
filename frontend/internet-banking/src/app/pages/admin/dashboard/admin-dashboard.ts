import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { forkJoin } from 'rxjs';
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

  private readonly gatewayUrl = 'http://localhost:8000';

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

    forkJoin({
      managers: this.http.get<any[]>(`${this.gatewayUrl}/managers`, { headers }),
      accounts: this.http.get<any[]>(`${this.gatewayUrl}/accounts`, { headers }),
    }).subscribe({
        next: ({ managers, accounts }) => {
          this.carregando = false;
          this.gerentesResumo = this.montarResumo(managers, accounts);
        },
        error: () => {
          this.carregando = false;
          // Fallback local
          this.gerentesResumo = this.authService.obterGerentesComIndicadores();
        },
      });
  }

  private montarResumo(managers: any[], accounts: any[]): GerenteResumo[] {
    return managers
      .map((m: any) => {
        const contasDoGerente = accounts.filter(
          (account: any) => this.mesmoCpf(this.cpfGerenteDaConta(account), m.cpf)
        );
        const positivos = contasDoGerente
          .filter((account: any) => this.saldoDaConta(account) >= 0)
          .reduce((sum: number, account: any) => sum + this.saldoDaConta(account), 0);
        const negativos = contasDoGerente
          .filter((account: any) => this.saldoDaConta(account) < 0)
          .reduce((sum: number, account: any) => sum + this.saldoDaConta(account), 0);

        return {
          nome: m.name ?? m.nome,
          cpf: m.cpf,
          email: m.email,
          clienteCount: contasDoGerente.length,
          somaSaldosPositivos: parseFloat(positivos.toFixed(2)),
          somaSaldosNegativos: parseFloat(negativos.toFixed(2)),
        } as GerenteResumo;
      })
      .sort((a, b) => (b.somaSaldosPositivos ?? 0) - (a.somaSaldosPositivos ?? 0));
  }

  private cpfGerenteDaConta(account: any): string {
    return account?.cpfManager ?? account?.cpf_manager ?? account?.managerCpf ?? '';
  }

  private saldoDaConta(account: any): number {
    return Number(account?.balance ?? account?.saldo ?? 0);
  }

  private mesmoCpf(a: string, b: string): boolean {
    return this.digitos(a) === this.digitos(b);
  }

  private digitos(valor: string): string {
    return (valor || '').replace(/\D/g, '');
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
