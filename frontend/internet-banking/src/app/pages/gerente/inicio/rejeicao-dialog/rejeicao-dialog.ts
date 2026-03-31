import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-rejeicao-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatIconModule],
  templateUrl: './rejeicao-dialog.html',
  styleUrl: './rejeicao-dialog.css',
})
export class RejeicaoDialogComponent {
  motivo: string = '';

  constructor(
    public dialogRef: MatDialogRef<RejeicaoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { nomeCliente: string }
  ) {}

  confirmar(): void {
    if (this.motivo.trim()) this.dialogRef.close(this.motivo.trim());
  }

  cancelar(): void {
    this.dialogRef.close();
  }
}
