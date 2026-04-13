import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

// ── Guard genérico: só precisa estar autenticado ──────────────────────────────
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.estaAutenticado()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};

// ── Guard de perfil CLIENTE ───────────────────────────────────────────────────
export const clienteGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const usuario = authService.obterUsuarioAtual();

  if (usuario?.perfil === 'cliente') {
    return true;
  }

  if (!usuario) {
    router.navigate(['/login']);
    return false;
  }

  // Redireciona para a tela correta se perfil errado
  redirecionarPorPerfil(usuario.perfil, router);
  return false;
};

// ── Guard de perfil GERENTE ───────────────────────────────────────────────────
export const gerenteGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const usuario = authService.obterUsuarioAtual();

  if (usuario?.perfil === 'gerente') {
    return true;
  }

  if (!usuario) {
    router.navigate(['/login']);
    return false;
  }

  redirecionarPorPerfil(usuario.perfil, router);
  return false;
};

// ── Guard de perfil ADMIN ─────────────────────────────────────────────────────
export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const usuario = authService.obterUsuarioAtual();

  if (usuario?.perfil === 'admin') {
    return true;
  }

  if (!usuario) {
    router.navigate(['/login']);
    return false;
  }

  redirecionarPorPerfil(usuario.perfil, router);
  return false;
};

// ── Helper interno ────────────────────────────────────────────────────────────
function redirecionarPorPerfil(perfil: string, router: Router): void {
  switch (perfil) {
    case 'cliente':
      router.navigate(['/cliente/inicio']);
      break;
    case 'gerente':
      router.navigate(['/gerente/inicio']);
      break;
    case 'admin':
      router.navigate(['/admin/inicio']);
      break;
    default:
      router.navigate(['/login']);
  }
}
