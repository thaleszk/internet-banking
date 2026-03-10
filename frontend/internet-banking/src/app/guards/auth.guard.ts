import { Injectable } from '@angular/core';
import { CanActivate, CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = new AuthService();
  const router = new Router();

  if (authService.estaAutenticado()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};
