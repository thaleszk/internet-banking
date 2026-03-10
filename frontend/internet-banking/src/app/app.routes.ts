import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout';
import { CadastroComponent } from './pages/cadastro/cadastro';
import { LoginComponent } from './pages/login/login';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: '',
        redirectTo: 'cadastro',
        pathMatch: 'full'
      },
      {
        path: 'cadastro',
        component: CadastroComponent
      },
      {
        path: 'login',
        component: LoginComponent
      }
    ]
  },
  {
    path: '**',
    redirectTo: '/'
  }
];
