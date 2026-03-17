import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout';
import { CadastroComponent } from './pages/cadastro/cadastro';
import { LoginComponent } from './pages/login/login';
import { AdminDashboardComponent } from './pages/admin/dashboard/admin-dashboard';
import { ClienteHome } from './pages/cliente/cliente-home/cliente-home';

export const routes: Routes = [
  {
    path: 'admin/dashboard',
    component: AdminDashboardComponent
  },
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
      },
      {
        path: 'cadastro',
        component: CadastroComponent
      },
      {
        path: 'login',
        component: LoginComponent
      },
      {
        path: 'cliente-home',
        component: ClienteHome
      }
    ]
  },
  {
    path: '**',
    redirectTo: '/'
  }
];

