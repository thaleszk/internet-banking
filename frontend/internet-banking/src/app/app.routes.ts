import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout';
import { CadastroComponent } from './pages/cadastro/cadastro';

export const routes: Routes = [

  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: '',
        component: CadastroComponent
      }
    ]
  }

];
