import { Component, signal, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatIconModule } from '@angular/material/icon';

import { CourseService } from './course.service';
import { Course, Category } from './course.model';

@Component({
  selector: 'app-courses',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatChipsModule, MatButtonModule, MatProgressBarModule, MatIconModule],
  template: `
  <div class="max-w-4xl mx-auto">

    <h2 class="text-2xl font-semibold mb-4">Capacitaciones</h2>

    <!-- Chips -->
    <mat-chip-listbox class="mb-6 flex flex-wrap gap-2">
      <mat-chip-option
        [selected]="selectedCategory() === null"
        (click)="selectCategory(null)">
        Todos
      </mat-chip-option>

      @for (c of categories(); track c.id) {
        <mat-chip-option
          [selected]="selectedCategory() === c.name"
          (click)="selectCategory(c.name)">
          {{ c.name }}
        </mat-chip-option>
      }
    </mat-chip-listbox>

    <!-- Grid -->
    @if (courses().length > 0) {
      <div class="grid gap-4 md:grid-cols-2">
        @for (course of courses(); track course.id) {
          <mat-card class="relative">
            @if (course.isCompleted) {
              <div class="absolute top-2 right-2 bg-green-100 text-green-800 text-xs font-medium px-2.5 py-0.5 rounded border border-green-400 z-10">
                Completado
              </div>
            }
            <mat-card-header>
              <mat-card-title>{{ course.title }}</mat-card-title>
              <mat-card-subtitle>{{ course.category?.name || course.module }}</mat-card-subtitle>
            </mat-card-header>

            <mat-card-content>
              <p class="text-sm mt-2 mb-4">{{ course.description }}</p>
              
              @if (course.progressPercentage !== undefined) {
                <div class="mb-2">
                  <div class="flex justify-between text-xs mb-1">
                    <span>Progreso</span>
                    <span>{{ course.progressPercentage }}%</span>
                  </div>
                  <mat-progress-bar mode="determinate" [value]="course.progressPercentage"></mat-progress-bar>
                </div>
              }
            </mat-card-content>

            <mat-card-actions align="end">
              <button
                mat-flat-button
                [color]="course.isCompleted ? 'accent' : 'primary'"
                (click)="viewCourse(course.id)"
              >
                Ver Curso
              </button>
            </mat-card-actions>
          </mat-card>
        }
      </div>
    } @else {
      <div class="flex flex-col items-center justify-center py-12 text-center">
        <mat-icon class="text-gray-400 text-6xl w-16 h-16 mb-4">import_contacts</mat-icon>
        <h3 class="text-xl font-semibold text-gray-800 mb-2">No hay cursos disponibles</h3>
        <p class="text-gray-500 max-w-md">
          No se encontraron cursos en esta categoría o no tienes asignaciones en este momento.
        </p>
      </div>
    }

  </div>
  `
})
export class CoursesPage {
  private courseService = inject(CourseService);
  private router = inject(Router);

  categories = signal<Category[]>([]);
  courses = signal<Course[]>([]);
  selectedCategory = signal<string | null>(null);

  constructor() {
    this.loadCategories();
    this.loadCourses();
  }

  loadCategories() {
    this.courseService.getCategories().subscribe(cats => this.categories.set(cats));
  }

  loadCourses(categoryName: string | null = null) {
    const request = categoryName
      ? this.courseService.getCoursesByCategory(categoryName)
      : this.courseService.getCourses();

    request.subscribe(courses => {
      console.log('Loaded courses:', courses);
      this.courses.set(courses);
    });
  }

  selectCategory(categoryName: string | null) {
    this.selectedCategory.set(categoryName);
    this.loadCourses(categoryName);
  }

  viewCourse(id: number) {
    console.log('Navigating to course id:', id);
    if (id === undefined || id === null) {
      console.error('Course ID is undefined!');
      return;
    }
    this.router.navigate(['/courses', id]);
  }
}
