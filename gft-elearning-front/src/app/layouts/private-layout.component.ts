// src/layouts/private-layout.component.ts
import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';

@Component({
  selector: 'app-private-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  template: `
    <div class="min-h-screen flex flex-col">
      <nav class="w-full bg-white shadow p-4 flex justify-between">
        <a routerLink="/courses" class="font-semibold">Capacitaciones</a>

        <button
          (click)="logout()"
          class="text-red-600 hover:underline">
          Cerrar sesión
        </button>
      </nav>

      <main class="flex-1 p-4 max-w-6xl mx-auto w-full">
        <router-outlet></router-outlet>
      </main>
    </div>
  `
})
export class PrivateLayoutComponent {
  constructor(private auth: AuthService) {}

  logout() {
    this.auth.logout();
  }
}
