import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-gerente-inicio',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <mat-card style="padding:40px; border-radius:16px;">
      <h2>Área do Gerente</h2>
      <p style="color: var(--bantads-text-muted)">
        <!-- R9: Tela inicial do gerente — aprovações pendentes (a implementar) -->
        Em construção...
      </p>
    </mat-card>
  `,
})
export class GerenteInicioComponent {}
