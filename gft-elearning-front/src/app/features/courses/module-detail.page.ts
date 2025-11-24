import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CourseService } from './course.service';
import { ModuleDetail } from './course.model';

@Component({
  selector: 'app-module-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  template: `
    <div class="max-w-3xl mx-auto p-6">
      @if (module(); as m) {
        <div class="mb-8">
          <button mat-button [routerLink]="['/courses', m.courseId]" class="mb-4 text-gray-600">
            <mat-icon>arrow_back</mat-icon> Volver
          </button>
          
          <h1 class="text-3xl font-bold text-gray-900 mb-2">{{ m.title }}</h1>
          <div class="text-sm text-gray-500 mb-8">Módulo {{ m.orderIndex }}</div>
        </div>

        <div class="prose prose-lg max-w-none text-gray-800 leading-relaxed bg-white p-8 rounded-lg shadow-sm border border-gray-100 mb-8">
          {{ m.content }}
        </div>

        @if (!m.completed) {
          <div class="flex justify-end">
            <button 
              mat-flat-button 
              color="primary" 
              class="px-8 py-6 text-lg"
              [disabled]="isCompleting()"
              (click)="completeModule(m)"
            >
              @if (isCompleting()) {
                <mat-spinner diameter="20" class="mr-2 inline-block"></mat-spinner>
              }
              Completar y Continuar
              <mat-icon class="ml-2">check_circle</mat-icon>
            </button>
          </div>
        }

      } @else {
        <div class="flex justify-center py-20">
          <mat-spinner diameter="40"></mat-spinner>
        </div>
      }
    </div>
  `
})
export class ModuleDetailPage {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private courseService = inject(CourseService);
  private snackBar = inject(MatSnackBar);

  module = signal<ModuleDetail | null>(null);
  isCompleting = signal(false);

  constructor() {
    const moduleId = this.route.snapshot.paramMap.get('moduleId');
    if (moduleId) {
      this.courseService.getModule(Number(moduleId)).subscribe({
        next: (data) => this.module.set(data),
        error: (err) => console.error('Error loading module details', err)
      });
    }
  }

  completeModule(module: ModuleDetail) {
    this.isCompleting.set(true);
    this.courseService.completeModule(module.id).subscribe({
      next: () => {
        this.snackBar.open('¡Módulo completado!', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/courses', module.courseId]);
      },
      error: (err) => {
        console.error('Error completing module', err);
        this.snackBar.open('Error al completar el módulo', 'Cerrar', { duration: 3000 });
        this.isCompleting.set(false);
      }
    });
  }
}
