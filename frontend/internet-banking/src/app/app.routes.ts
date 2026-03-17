import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout';
import { CadastroComponent } from './pages/cadastro/cadastro';
import { LoginComponent } from './pages/login/login';
import { ClienteInicioComponent } from './pages/cliente/inicio/cliente-inicio';
import { DepositoComponent } from './pages/deposito/deposito';

export const routes: Routes = [
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
        path: 'cliente/inicio',
        component: ClienteInicioComponent
      },
      {
        path: 'deposito',
        component: DepositoComponent
      }
    ]
  },
  {
    path: '**',
    redirectTo: '/'
  }
];

