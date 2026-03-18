import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout';
import { CadastroComponent } from './pages/cadastro/cadastro';
import { LoginComponent } from './pages/login/login';
import { AdminDashboardComponent } from './pages/admin/dashboard/admin-dashboard';
import { ClienteHome } from './pages/cliente/cliente-home/cliente-home';
import { DepositoComponent } from './pages/deposito/deposito';
import { SaqueComponent } from './pages/saque/saque';
import { GerenteInicioComponent } from './pages/gerente/inicio/gerente-inicio';

export const routes: Routes = [
  {
    path: 'admin/dashboard',
    component: AdminDashboardComponent
  },
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '',         redirectTo: 'login', pathMatch: 'full' },
      { path: 'cadastro', component: CadastroComponent },  // R1
      { path: 'login',    component: LoginComponent },     // R2
      { path: 'cliente-home', component: ClienteHome },    // R3
      { path: 'deposito', component: DepositoComponent },  // R5
      { path: 'saque',    component: SaqueComponent },     // R6
      { path: 'gerente/inicio', component: GerenteInicioComponent }, // R9
      // R4, R7, R8 — a implementar nas próximas sprints
    ]
  },
  { path: '**', redirectTo: '/' }
];
  