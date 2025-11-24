import { Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';

import { ProfileService } from './profile.service';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatDividerModule
  ],
  template: `
  <div class="max-w-3xl mx-auto px-4">

    <h2 class="text-3xl font-bold mb-6">Mi Perfil</h2>

    <!-- User card -->
    <mat-card class="p-4 mb-6">
      <p class="text-gray-600 text-sm">Usuario: {{ user()?.username }}</p>
    </mat-card>

    <!-- Insignias -->
    <mat-card class="p-4 mb-6">
      <h3 class="text-xl font-semibold mb-4">🏅 Mis Insignias</h3>

      <div *ngIf="badges().length === 0" class="text-gray-500 text-sm">
        Aún no tienes insignias. Completa cursos para obtenerlas.
      </div>

      <div class="flex flex-wrap gap-4">
        <div *ngFor="let b of badges()" class="flex flex-col items-center">
          <div class="text-5xl">{{ b.icon }}</div>
          <div class="text-sm text-center mt-1">{{ b.title }}</div>
          <div class="text-xs text-gray-500">{{ b.earnedAt | date }}</div>
        </div>
      </div>
    </mat-card>

    <!-- Histórico -->
    <mat-card class="p-4">
      <h3 class="text-xl font-semibold mb-4">📘 Histórico de Cursos</h3>

      <div *ngFor="let item of history()" class="mb-3 p-3 border rounded-md">
        <div class="font-medium">
          Curso {{ item.courseId }} — {{ item.state }}
        </div>
        <div class="text-xs text-gray-500">
          Última actualización: {{ item.updatedAt | date:'short' }}
        </div>
      </div>

    </mat-card>

  </div>
  `
})
export class ProfilePage {
  private authService = inject(AuthService);
  private profileService = inject(ProfileService);

  user = this.authService.user;

  badges = computed(() => {
    const u = this.user();
    if (!u) return [];
    return this.profileService.getBadges(u.username);
  });

  history = computed(() => {
    const u = this.user();
    if (!u) return [];
    const username = u.username;

    const raw = localStorage.getItem(`progress_${username}`);
    if (!raw) return [];

    const parsed = JSON.parse(raw) as Record<number,string>;

    return Object.entries(parsed).map(([courseId, state]) => ({
      courseId,
      state,
      updatedAt: new Date()
    }));
  });
}
