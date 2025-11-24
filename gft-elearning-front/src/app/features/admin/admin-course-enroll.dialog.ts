import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CourseService } from '../courses/course.service';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-admin-course-enroll-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  template: `
    <h2 mat-dialog-title>Enrolar Usuario</h2>
    <mat-dialog-content>
      <div class="flex flex-col gap-4 pt-4">
        <mat-form-field appearance="outline" class="w-full">
          <mat-label>Correo Electrónico</mat-label>
          <input matInput [(ngModel)]="email" placeholder="usuario@ejemplo.com" required email [disabled]="isLoading()">
        </mat-form-field>
      </div>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close [disabled]="isLoading()">Cancelar</button>
      <button mat-flat-button color="primary" (click)="save()" [disabled]="!email() || isLoading()">
        @if (isLoading()) {
          <mat-spinner diameter="20" class="inline-block mr-2"></mat-spinner>
        }
        Enrolar
      </button>
    </mat-dialog-actions>
  `
})
export class AdminCourseEnrollDialog {
  private courseService = inject(CourseService);
  private dialogRef = inject(MatDialogRef<AdminCourseEnrollDialog>);
  private data = inject(MAT_DIALOG_DATA); // { courseId: number }
  private snackBar = inject(MatSnackBar);

  email = signal('');
  isLoading = signal(false);

  save() {
    if (!this.email()) return;

    this.isLoading.set(true);
    this.courseService.enrollUser(this.data.courseId, this.email())
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: () => {
          this.snackBar.open('Usuario enrolado exitosamente', 'Cerrar', { duration: 3000 });
          this.dialogRef.close(true);
        },
        error: (err) => {
          console.error('Error enrolling user', err);
          this.snackBar.open('Error al enrolar usuario', 'Cerrar', { duration: 3000 });
        }
      });
  }
}
