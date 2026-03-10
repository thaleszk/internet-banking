import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-cliente-inicio',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  template: `
    <mat-card style="padding:40px; border-radius:16px;">
      <h2>Área do Cliente</h2>
      <p style="color: var(--bantads-text-muted)">
        <!-- R3: Tela inicial do cliente — saldo e menu de operações (a implementar) -->
        Em construção...
      </p>
    </mat-card>
  `,
})
export class ClienteInicioComponent {}
