import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { map, Observable } from 'rxjs';
import { User } from '../shared/models';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => validarAcesso();

export const clienteGuard: CanActivateFn = () => validarAcesso(['cliente']);

export const gerenteGuard: CanActivateFn = () => validarAcesso(['gerente']);

export const adminGuard: CanActivateFn = () => validarAcesso(['admin']);

function validarAcesso(perfisPermitidos?: User['perfil'][]): Observable<boolean | UrlTree> | UrlTree {
  const authService = inject(AuthService);
  const router = inject(Router);
  const usuario = authService.obterUsuarioAtual();

  if (!usuario || !authService.obterToken()) {
    return router.createUrlTree(['/login']);
  }

  return authService.validarToken().pipe(
    map((tokenValido) => {
      if (!tokenValido) {
        authService.logout();
        return router.createUrlTree(['/login']);
      }

      if (!perfisPermitidos || perfisPermitidos.includes(usuario.perfil)) {
        return true;
      }

      return rotaPorPerfil(usuario.perfil, router);
    })
  );
}

function rotaPorPerfil(perfil: User['perfil'], router: Router): UrlTree {
  switch (perfil) {
    case 'cliente':
      return router.createUrlTree(['/cliente/inicio']);
    case 'gerente':
      return router.createUrlTree(['/gerente/inicio']);
    case 'admin':
      return router.createUrlTree(['/admin/inicio']);
    default:
      return router.createUrlTree(['/login']);
  }
}
