import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout';
import { CadastroComponent } from './pages/cadastro/cadastro';
import { LoginComponent } from './pages/login/login';
import { ClienteInicioComponent } from './pages/cliente/inicio/cliente-inicio';
import { GerenteInicioComponent } from './pages/gerente/inicio/gerente-inicio';
import { AdminInicioComponent } from './pages/admin/inicio/admin-inicio';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      // Área pública
      { path: '',      component: CadastroComponent }, // R1 - Autocadastro
      { path: 'login', component: LoginComponent },    // R2 - Login

      // Área do cliente (R3–R8)
      // TODO: adicionar AuthGuard com perfil CLIENTE após R2
      { path: 'cliente/inicio',   component: ClienteInicioComponent },
      { path: 'cliente/extrato',  redirectTo: 'cliente/inicio' }, // placeholder R8
      { path: 'cliente/deposito', redirectTo: 'cliente/inicio' }, // placeholder R5
      { path: 'cliente/saque',    redirectTo: 'cliente/inicio' }, // placeholder R6
      { path: 'cliente/perfil',   redirectTo: 'cliente/inicio' }, // placeholder R4

      // Área do gerente (R9–R14)
      // TODO: adicionar AuthGuard com perfil GERENTE após R2
      { path: 'gerente/inicio',   component: GerenteInicioComponent },
      { path: 'gerente/clientes', redirectTo: 'gerente/inicio' }, // placeholder R12

      // Área do administrador (R15–R20)
      // TODO: adicionar AuthGuard com perfil ADMIN após R2
      { path: 'admin/inicio',     component: AdminInicioComponent },
      { path: 'admin/gerentes',   redirectTo: 'admin/inicio' },   // placeholder R17–R20
      { path: 'admin/relatorio',  redirectTo: 'admin/inicio' },   // placeholder R16

      // Fallback
      { path: '**', redirectTo: '' },
    ],
  },
];

