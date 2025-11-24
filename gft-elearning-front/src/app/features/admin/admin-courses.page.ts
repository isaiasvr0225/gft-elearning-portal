import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CourseService } from '../courses/course.service';
import { AdminCourseModulesDialog } from './admin-course-modules.dialog';
import { AdminCourseEnrollDialog } from './admin-course-enroll.dialog';
import { ConfirmationDialog } from '../../shared/components/confirmation.dialog';

@Component({
  selector: 'app-admin-courses',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, RouterLink, MatDialogModule, MatSnackBarModule],
  template: `
    <div class="container mx-auto p-6">
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-3xl font-bold">Administrar Cursos</h1>
        <a mat-flat-button color="primary" routerLink="/admin/new">
          <mat-icon>add</mat-icon> Nuevo Curso
        </a>
      </div>

      <table mat-table [dataSource]="courses()" class="mat-elevation-z8">
        
        <ng-container matColumnDef="id">
          <th mat-header-cell *matHeaderCellDef> ID </th>
          <td mat-cell *matCellDef="let element"> {{element.id}} </td>
        </ng-container>

        <ng-container matColumnDef="title">
          <th mat-header-cell *matHeaderCellDef> Título </th>
          <td mat-cell *matCellDef="let element"> {{element.title}} </td>
        </ng-container>

        <ng-container matColumnDef="category">
          <th mat-header-cell *matHeaderCellDef> Categoría </th>
          <td mat-cell *matCellDef="let element"> {{element.category?.name}} </td>
        </ng-container>

        <ng-container matColumnDef="actions">
          <th mat-header-cell *matHeaderCellDef> Acciones </th>
          <td mat-cell *matCellDef="let element">
            <button mat-icon-button color="primary" [routerLink]="['/admin/edit', element.id]" title="Editar">
              <mat-icon>edit</mat-icon>
            </button>
            <button mat-icon-button color="accent" (click)="openModulesDialog(element.id)" title="Agregar Módulos">
              <mat-icon>add_circle</mat-icon>
            </button>
            <button mat-icon-button class="text-green-600" (click)="openEnrollDialog(element.id)" title="Enrolar Usuario">
              <mat-icon>person_add</mat-icon>
            </button>
            <button mat-icon-button color="warn" (click)="deleteCourse(element.id)" title="Eliminar">
              <mat-icon>delete</mat-icon>
            </button>
          </td>
        </ng-container>

        <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
        <tr mat-row *matRowDef="let row; columns: displayedColumns;"></tr>
      </table>
    </div>
  `
})
export class AdminCoursesPage {
  private courseService = inject(CourseService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  courses = this.courseService.courses;
  displayedColumns: string[] = ['id', 'title', 'category', 'actions'];

  constructor() {
    this.courseService.getAdminCourses().subscribe();
  }

  deleteCourse(id: number) {
    this.dialog.open(ConfirmationDialog, {
      width: '400px',
      data: {
        title: 'Eliminar Curso',
        message: '¿Estás seguro de que deseas eliminar este curso? Esta acción no se puede deshacer.',
        action: () => this.courseService.deleteCourse(id)
      }
    }).afterClosed().subscribe(result => {
      if (result) {
        this.snackBar.open('Curso eliminado exitosamente', 'Cerrar', { duration: 3000 });
      }
    });
  }

  openModulesDialog(courseId: number) {
    this.dialog.open(AdminCourseModulesDialog, {
      width: '600px',
      data: { courseId }
    });
  }

  openEnrollDialog(courseId: number) {
    this.dialog.open(AdminCourseEnrollDialog, {
      width: '400px',
      data: { courseId }
    });
  }
}
