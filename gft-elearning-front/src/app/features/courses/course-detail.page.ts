import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CourseService } from './course.service';
import { Course } from './course.model';

@Component({
  selector: 'app-course-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatListModule,
    MatProgressBarModule,
    MatProgressSpinnerModule
  ],
  template: `
    <div class="max-w-4xl mx-auto p-6">
      <button mat-button routerLink="/courses" class="mb-4">
        <mat-icon>arrow_back</mat-icon> Volver
      </button>

      @if (course(); as c) {
        <mat-card class="mb-6">
          <mat-card-header>
            <mat-card-title class="text-2xl">{{ c.title }}</mat-card-title>
            <mat-card-subtitle class="mt-1">{{ c.category?.name }}</mat-card-subtitle>
          </mat-card-header>
          <mat-card-content class="pt-4">
            <p class="text-gray-700 mb-6">{{ c.description }}</p>

            @if (c.progressPercentage !== undefined) {
              <div class="mb-4">
                <div class="flex justify-between text-sm mb-1">
                  <span>Tu Progreso</span>
                  <span>{{ c.progressPercentage }}%</span>
                </div>
                <mat-progress-bar mode="determinate" [value]="c.progressPercentage"></mat-progress-bar>
              </div>
            }
          </mat-card-content>
        </mat-card>

        <h3 class="text-xl font-bold mb-4">Contenido del Curso</h3>
        
        @if (c.modules && c.modules.length > 0) {
          <mat-card>
            <mat-nav-list>
              @for (module of c.modules; track module.id) {
                <a mat-list-item [routerLink]="['/courses', c.id, 'modules', module.id]" class="hover:bg-gray-50">
                  <mat-icon matListItemIcon [class.text-green-600]="module.completed">
                    {{ module.completed ? 'check_circle' : 'article' }}
                  </mat-icon>
                  <div matListItemTitle [class.text-green-600]="module.completed" [class.font-medium]="module.completed">
                    {{ module.title }}
                  </div>
                  <div matListItemLine class="text-xs text-gray-500">Módulo {{ module.orderIndex }}</div>
                </a>
              }
            </mat-nav-list>
          </mat-card>
        } @else {
          <div class="text-center py-8 text-gray-500 bg-gray-50 rounded-lg border border-dashed">
            No hay módulos disponibles para este curso aún.
          </div>
        }

      } @else {
        <div class="flex justify-center py-12">
          <mat-spinner diameter="40"></mat-spinner>
        </div>
      }
    </div>
  `
})
export class CourseDetailPage {
  private route = inject(ActivatedRoute);
  private courseService = inject(CourseService);

  course = signal<Course | null>(null);

  constructor() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.courseService.getCourseDetails(Number(id)).subscribe({
        next: (data) => this.course.set(data),
        error: (err) => console.error('Error loading course details', err)
      });
    }
  }
}
