import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

export interface ConfirmationDialogData {
    title: string;
    message: string;
    action: () => Observable<any>;
}

@Component({
    selector: 'app-confirmation-dialog',
    standalone: true,
    imports: [CommonModule, MatDialogModule, MatButtonModule, MatProgressSpinnerModule],
    template: `
    <h2 mat-dialog-title>{{ data.title }}</h2>
    <mat-dialog-content>
      <p>{{ data.message }}</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close [disabled]="isLoading()">Cancelar</button>
      <button mat-flat-button color="warn" (click)="confirm()" [disabled]="isLoading()">
        @if (isLoading()) {
          <mat-spinner diameter="20" class="inline-block mr-2"></mat-spinner>
        }
        Confirmar
      </button>
    </mat-dialog-actions>
  `
})
export class ConfirmationDialog {
    private dialogRef = inject(MatDialogRef<ConfirmationDialog>);
    data = inject<ConfirmationDialogData>(MAT_DIALOG_DATA);

    isLoading = signal(false);

    confirm() {
        this.isLoading.set(true);
        this.data.action()
            .pipe(finalize(() => this.isLoading.set(false)))
            .subscribe({
                next: (result) => this.dialogRef.close(result || true),
                error: () => this.dialogRef.close(false) // Or handle error here
            });
    }
}
