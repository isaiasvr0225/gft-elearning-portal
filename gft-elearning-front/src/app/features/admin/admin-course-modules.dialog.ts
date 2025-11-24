import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CourseService } from '../courses/course.service';
import { ModuleRequest } from '../courses/course.model';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-admin-course-modules-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  template: `
    <h2 mat-dialog-title>Agregar Módulos</h2>
    <mat-dialog-content>
      <div class="flex flex-col gap-4">
        @for (module of modules(); track $index) {
          <div class="border p-4 rounded-lg relative">
            <button mat-icon-button color="warn" class="absolute top-2 right-2" (click)="removeModule($index)" [disabled]="isLoading()">
              <mat-icon>delete</mat-icon>
            </button>
            
            <mat-form-field appearance="outline" class="w-full">
              <mat-label>Título del Módulo</mat-label>
              <input matInput [(ngModel)]="module.title" placeholder="Ej. Introducción" [disabled]="isLoading()">
            </mat-form-field>

            <mat-form-field appearance="outline" class="w-full">
              <mat-label>Contenido</mat-label>
              <textarea matInput [(ngModel)]="module.content" rows="3" placeholder="Contenido del módulo..." [disabled]="isLoading()"></textarea>
            </mat-form-field>
          </div>
        }

        <button mat-stroked-button color="primary" (click)="addModule()" [disabled]="isLoading()">
          <mat-icon>add</mat-icon> Agregar otro módulo
        </button>
      </div>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close [disabled]="isLoading()">Cancelar</button>
      <button mat-flat-button color="primary" (click)="save()" [disabled]="!isValid() || isLoading()">
        @if (isLoading()) {
          <mat-spinner diameter="20" class="inline-block mr-2"></mat-spinner>
        }
        Guardar
      </button>
    </mat-dialog-actions>
  `
})
export class AdminCourseModulesDialog {
  private courseService = inject(CourseService);
  private dialogRef = inject(MatDialogRef<AdminCourseModulesDialog>);
  private data = inject(MAT_DIALOG_DATA); // { courseId: number }
  private snackBar = inject(MatSnackBar);

  modules = signal<ModuleRequest[]>([{ title: '', content: '' }]);
  isLoading = signal(false);

  addModule() {
    this.modules.update(current => [...current, { title: '', content: '' }]);
  }

  removeModule(index: number) {
    this.modules.update(current => current.filter((_, i) => i !== index));
  }

  isValid() {
    return this.modules().every(m => m.title && m.content);
  }

  save() {
    if (!this.isValid()) return;

    this.isLoading.set(true);
    this.courseService.addModules(this.data.courseId, this.modules())
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: () => {
          this.snackBar.open('Módulos agregados exitosamente', 'Cerrar', { duration: 3000 });
          this.dialogRef.close(true);
        },
        error: (err) => {
          console.error('Error adding modules', err);
          this.snackBar.open('Error al agregar módulos', 'Cerrar', { duration: 3000 });
        }
      });
  }
}
