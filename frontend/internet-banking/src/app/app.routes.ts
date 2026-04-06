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

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full',
      },
      {
        path: 'cadastro',
        component: CadastroComponent,
      },
      {
        path: 'login',
        component: LoginComponent,
      },
      {
        path: 'cliente/inicio',
        component: ClienteHome,
      },
      {
        path: 'cliente/perfil',
        component: PerfilComponent,
      },
      {
        path: 'deposito',
        component: DepositoComponent,
      },
      {
        path: 'saque',
        component: SaqueComponent,
      },
      {
        path: 'extrato',
        component: ExtratoComponent,
      },
      {
        path: 'cliente/transferencia',
        component: TransferenciaComponent,
      },
      {
        path: 'gerente/inicio',
        component: GerenteInicioComponent,
      },
      {
        path: 'gerente/clientes',
        component: GerenteClientesComponent,
      },
      {
        path: 'gerentes/novo',
        redirectTo: 'admin/gerentes',
        pathMatch: 'full',
      },
      {
        path: 'admin/inicio',
        component: AdminDashboardComponent,
      },
      {
        path: 'admin/gerentes',
        component: AdminGerentesComponent,
      },
      {
        path: 'admin/relatorio',
        component: RelatorioClienteComponent,
      },
    ],
  },
  {
    path: 'admin/dashboard',
    redirectTo: 'admin/inicio',
    pathMatch: 'full',
  },
  {
    path: '**',
    redirectTo: '/',
  },
];
