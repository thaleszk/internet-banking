import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-admin-inicio',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <mat-card style="padding:40px; border-radius:16px;">
      <h2>Área do Administrador</h2>
      <p style="color: var(--bantads-text-muted)">
        <!-- R15: Dashboard do administrador (a implementar) -->
        Em construção...
      </p>
    </mat-card>
  `,
})
export class AdminInicioComponent {}
