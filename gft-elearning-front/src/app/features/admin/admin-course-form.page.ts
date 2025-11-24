import { Component, inject, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Course, Category } from '../courses/course.model';
import { CourseService } from '../courses/course.service';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-admin-course-form',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  template: `
    <div class="max-w-2xl mx-auto">
      <mat-card class="p-6">
        <h2 class="text-2xl font-bold mb-6">{{ isEditMode() ? 'Editar' : 'Nuevo' }} Curso</h2>

        <form (ngSubmit)="save()" class="flex flex-col gap-4">
          
          <mat-form-field appearance="outline">
            <mat-label>Título</mat-label>
            <input matInput [ngModel]="title()" (ngModelChange)="title.set($event)" name="title" required>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Categoría</mat-label>
            <mat-select [ngModel]="selectedCategoryId()" (ngModelChange)="selectedCategoryId.set($event)" name="category" required>
              @for (c of categories(); track c.id) {
                <mat-option [value]="c.id">{{ c.name }}</mat-option>
              }
            </mat-select>
          </mat-form-field>

          <mat-form-field appearance="outline">
            <mat-label>Descripción</mat-label>
            <textarea matInput [ngModel]="description()" (ngModelChange)="description.set($event)" name="description" rows="4" required></textarea>
          </mat-form-field>

          <div class="flex justify-end gap-2 mt-4">
            <button mat-button type="button" (click)="cancel()" [disabled]="isLoading()">Cancelar</button>
            <button mat-flat-button color="primary" type="submit" [disabled]="!isValid() || isLoading()">
              @if (isLoading()) {
                <mat-spinner diameter="20" class="inline-block mr-2"></mat-spinner>
              }
              Guardar
            </button>
          </div>

        </form>
      </mat-card>
    </div>
  `
})
export class AdminCourseFormPage {
  private courseService = inject(CourseService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private snackBar = inject(MatSnackBar);

  categories = signal<Category[]>([]);
  isEditMode = signal(false);
  isLoading = signal(false);

  // Form Signals
  title = signal('');
  description = signal('');
  selectedCategoryId = signal<number | null>(null);

  courseId: number | null = null;

  constructor() {
    this.loadCategories();

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode.set(true);
      this.courseId = Number(id);
      this.loadCourse(this.courseId);
    }
  }

  loadCategories() {
    this.courseService.getCategories().subscribe(cats => this.categories.set(cats));
  }

  loadCourse(id: number) {
    this.courseService.getCourseById(id).subscribe({
      next: (course) => {
        console.log('Course loaded:', course);
        // Update signals
        this.title.set(course.title);
        this.description.set(course.description);

        if (course.category) {
          this.selectedCategoryId.set(course.category.id);
        }
      },
      error: (err) => console.error('Error loading course:', err)
    });
  }

  isValid() {
    return this.title() && this.selectedCategoryId() && this.description();
  }

  save() {
    if (!this.isValid()) return;

    this.isLoading.set(true);
    const payload = {
      title: this.title(),
      description: this.description(),
      categoryId: this.selectedCategoryId()!
    };

    const request = (this.isEditMode() && this.courseId)
      ? this.courseService.updateCourse(this.courseId, payload)
      : this.courseService.addCourse(payload);

    request.pipe(
      finalize(() => this.isLoading.set(false))
    ).subscribe({
      next: () => {
        this.snackBar.open('Curso guardado exitosamente', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/admin']);
      },
      error: (err) => {
        console.error('Error saving course', err);
        this.snackBar.open('Error al guardar el curso', 'Cerrar', { duration: 3000 });
      }
    });
  }

  cancel() {
    this.router.navigate(['/admin']);
  }
}
