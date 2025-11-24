import { Component, signal, computed, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormControl } from '@angular/forms';

import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatRadioModule } from '@angular/material/radio';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';

import { CourseDetailService } from './course-detail.service';
import { CourseDetail } from './course-detail.model';
import { ProgressService } from '../../shared/course-progress/progress.service';
import { ProfileService } from '../profile/profile.service';
import { userSignal } from '../../core/auth/auth.service';

@Component({
  selector: 'app-course-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule,
    MatProgressBarModule,
    MatCardModule,
    MatButtonModule,
    MatRadioModule,
    MatIconModule,
    MatDividerModule
  ],
  template: `
  <div class="max-w-3xl mx-auto">

  @if (isLoading()) {
    <div class="py-20 flex justify-center">
      <!-- <mat-spinner></mat-spinner> -->
    </div>
  } @else {
    <mat-card class="p-6">

      <div class="mb-4">
        @if (course()?.imageUrl) {
          <img [src]="course()?.imageUrl" class="w-full rounded-lg mb-4" />
        }

        <h2 class="text-2xl font-bold">{{ course()?.title }}</h2>
        <p class="text-sm text-gray-600">{{ course()?.description }}</p>
      </div>

      <!-- Barra de progreso -->
      <div class="mt-4 mb-6">
        <mat-progress-bar
          mode="determinate"
          [value]="progressValue()">
        </mat-progress-bar>

        <div class="text-sm text-right text-gray-600 mt-1">
          Paso {{ currentStep() }} / {{ course()?.totalSteps }}
        </div>
      </div>

      <!-- Pregunta actual -->
      <div class="bg-white p-4 rounded-md border">
        <div class="mb-3">
          <h3 class="font-medium text-lg">Pregunta</h3>
          <p class="text-gray-800 mt-2">{{ currentQuestion()?.question }}</p>
        </div>

        <mat-radio-group [formControl]="answerControl" class="flex flex-col gap-2">
          @for (opt of currentQuestion()?.options ?? []; track trackByIndex($index)) {
            <mat-radio-button [value]="opt" class="py-2">
              {{ opt }}
            </mat-radio-button>
          }
        </mat-radio-group>
      </div>

      <!-- Acciones -->
      <div class="flex justify-between items-center mt-6">

        <button
          mat-stroked-button
          color="primary"
          (click)="prev()"
          [disabled]="currentStep() === 1">
          <mat-icon>arrow_back</mat-icon>
          Volver
        </button>

        <div class="flex items-center gap-3">

          @if (isLastStep()) {
            <button mat-flat-button color="accent" (click)="finish()">
              <mat-icon>check_circle</mat-icon>
              Finalizar
            </button>
          } @else {
            <button mat-flat-button color="primary" (click)="next()">
              Siguiente
              <mat-icon>arrow_forward</mat-icon>
            </button>
          }

        </div>

      </div>

    </mat-card>
  }

</div>
  `
})
export class CourseDetailComponent {
  course = signal<CourseDetail | null>(null);
  isLoading = signal(true);

  // step management
  currentStep = signal(1);

  // respuestas guardadas localmente (map stepNumber -> selected option)
  answers = signal<Record<number, string | null>>({});

  // reactive control for radio group (bind to current question)
  answerControl = new FormControl<string | null>(null);

  // usuario desde userSignal (importado)
  user = userSignal; // es una signal; usar user() para obtener

  constructor(
    private route: ActivatedRoute,
    private service: CourseDetailService,
    private profile: ProfileService,
  ) {
    const id = Number(this.route.snapshot.paramMap.get('id') ?? 0);
    if (!id) {
      this.isLoading.set(false);
      return;
    }

    // cargar curso (mock service devuelve Observable)
    this.service.getCourseDetail(id).subscribe((c) => {
      this.course.set(c);
      this.isLoading.set(false);

      // restaurar progreso si existe (por usuario)
      const username = this.user() ? this.user()!.username : 'anon';
      const state = ProgressService.get(username, id);
      if (state === 'started') {
        this.currentStep.set(this.restoreStep(username, id) || 1);
      } else {
        // marcar started la primera vez que lo abre
        ProgressService.markStarted(username, id);
      }
    });

    // cuando cambie currentStep, sincronizar answerControl con la respuesta guardada
    effect(() => {
      const step = this.currentStep();
      const a = this.answers()[step];
      this.answerControl.setValue(a ?? null, { emitEvent: false });
    });

    // cuando el usuario selecciona una opción, la guardamos en `answers`
    this.answerControl.valueChanges.subscribe((val) => {
      const step = this.currentStep();
      this.answers.set({ ...this.answers(), [step]: val });
      // persistir el paso actual para restaurar si vuelve
      const username = this.user() ? this.user()!.username : 'anon';
      const courseId = Number(this.route.snapshot.paramMap.get('id'));
      localStorage.setItem(this.persistKey(username, courseId), JSON.stringify({
        currentStep: this.currentStep(),
        answers: this.answers()
      }));
    });
  }

  // util para key localStorage por curso+usuario
  private persistKey(username: string, courseId: number) {
    return `course_detail_${username}_${courseId}`;
  }

  private restoreStep(username: string, courseId: number): number | null {
    const raw = localStorage.getItem(this.persistKey(username, courseId));
    if (!raw) return null;
    try {
      const parsed = JSON.parse(raw);
      if (parsed?.currentStep) {
        // restaurar respuestas también
        this.answers.set(parsed.answers ?? {});
        return parsed.currentStep;
      }
      return null;
    } catch {
      return null;
    }
  }

  // helpers
  currentQuestion() {
    const c = this.course();
    if (!c) return null;
    return c.steps.find(s => s.stepNumber === this.currentStep());
  }

  progressValue() {
    const c = this.course();
    if (!c) return 0;
    return (this.currentStep() / c.totalSteps) * 100;
  }

  isLastStep() {
    const c = this.course();
    if (!c) return false;
    return this.currentStep() === c.totalSteps;
  }

  next() {
    const c = this.course();
    if (!c) return;
    if (this.currentStep() < c.totalSteps) {
      this.currentStep.set(this.currentStep() + 1);
      // persistir step
      const username = this.user() ? this.user()!.username : 'anon';
      const courseId = c.id;
      localStorage.setItem(this.persistKey(username, courseId), JSON.stringify({
        currentStep: this.currentStep(),
        answers: this.answers()
      }));
      // marcar started
      ProgressService.markStarted(this.user() ? this.user()!.username : 'anon', c.id);
    }
  }

  prev() {
    if (this.currentStep() > 1) {
      this.currentStep.set(this.currentStep() - 1);
    }
  }

  finish() {
    const c = this.course();
    if (!c) return;
    // marca completado en ProgressService
    const username = this.user() ? this.user()!.username : 'anon';
    ProgressService.markCompleted(username, c.id);

    // opcional: asignar insignia (si tienes lógica de badges, agrégala aquí)
    // limpiar persistencia
    localStorage.removeItem(this.persistKey(username, c.id));

    if (!this.profile.hasBadge(username, c.id)) {
      this.profile.addBadge(username, {
        id: Date.now(),
        courseId: c.id,
        icon: '🏅',
        title: `Curso completado: ${c.title}`,
        earnedAt: new Date().toISOString()
      });
    }

    // navegar o mostrar toast; por simplicidad, volvemos a /courses
    window.location.href = '/courses';
  }

  // trackBy auxiliar
  trackByIndex(i: number) { return i; }
}
