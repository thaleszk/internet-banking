import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout';
import { CadastroComponent } from './pages/cadastro/cadastro';
import { LoginComponent } from './pages/login/login';
import { AdminDashboardComponent } from './pages/admin/dashboard/admin-dashboard';
import { AdminGerentesComponent } from './pages/admin/gerentes/admin-gerentes';
import { RelatorioClienteComponent } from './pages/admin/relatorio/relatorio-cliente';
import { ClienteHome } from './pages/cliente/cliente-home/cliente-home';
import { PerfilComponent } from './pages/perfil/perfil';
import { DepositoComponent } from './pages/deposito/deposito';
import { SaqueComponent } from './pages/saque/saque';
import { ExtratoComponent } from './pages/extrato/extrato';
import { GerenteClientesComponent } from './pages/gerente/clientes/gerente-clientes';
import { GerenteInicioComponent } from './pages/gerente/inicio/gerente-inicio';
import { TransferenciaComponent } from './pages/transferencia/transferencia';
import { clienteGuard, gerenteGuard, adminGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [

      // ── Rotas públicas ───────────────────────────────────────────────────────
      { path: '', redirectTo: 'login', pathMatch: 'full' },
      { path: 'login', component: LoginComponent },
      { path: 'cadastro', component: CadastroComponent },

      // ── Cliente ──────────────────────────────────────────────────────────────
      { path: 'cliente/inicio',        component: ClienteHome,          canActivate: [clienteGuard] },
      { path: 'cliente/perfil',        component: PerfilComponent,      canActivate: [clienteGuard] },
      { path: 'cliente/deposito',      component: DepositoComponent,    canActivate: [clienteGuard] },
      { path: 'cliente/saque',         component: SaqueComponent,       canActivate: [clienteGuard] },
      { path: 'cliente/extrato',       component: ExtratoComponent,     canActivate: [clienteGuard] },
      { path: 'cliente/transferencia', component: TransferenciaComponent, canActivate: [clienteGuard] },

      // ── Gerente ──────────────────────────────────────────────────────────────
      { path: 'gerente/inicio',   component: GerenteInicioComponent,   canActivate: [gerenteGuard] },
      { path: 'gerente/clientes', component: GerenteClientesComponent, canActivate: [gerenteGuard] },

      // ── Admin ────────────────────────────────────────────────────────────────
      { path: 'admin/inicio',    component: AdminDashboardComponent, canActivate: [adminGuard] },
      { path: 'admin/gerentes',  component: AdminGerentesComponent,  canActivate: [adminGuard] },
      { path: 'admin/relatorio', component: RelatorioClienteComponent, canActivate: [adminGuard] },
    ],
  },

  // ── Redirects legados ─────────────────────────────────────────────────────
  { path: 'deposito',        redirectTo: 'cliente/deposito',    pathMatch: 'full' },
  { path: 'saque',           redirectTo: 'cliente/saque',       pathMatch: 'full' },
  { path: 'extrato',         redirectTo: 'cliente/extrato',     pathMatch: 'full' },
  { path: 'admin/dashboard', redirectTo: 'admin/inicio',        pathMatch: 'full' },
  { path: 'gerentes/novo',   redirectTo: 'admin/gerentes',      pathMatch: 'full' },
  { path: '**',              redirectTo: 'login' },
];