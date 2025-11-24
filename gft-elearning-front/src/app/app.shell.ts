import { Component, inject } from '@angular/core';
import { RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from './core/auth/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterModule, MatToolbarModule, MatButtonModule],
  template: `
    <mat-toolbar color="primary" class="px-6">
      <span class="font-semibold">Portal Capacitaciones</span>
      <span class="flex-1"></span>
      @if (authService.isLoggedIn()) {
        @if (authService.hasRole('ADMIN')) {
          <button mat-button (click)="logout()">Salir</button>
        } @else {
          <button mat-button routerLink="/courses">Cursos</button>
          <button mat-button routerLink="/profile">Perfil</button>
          <button mat-button (click)="logout()">Salir</button>
        }
      }
    </mat-toolbar>

    <main class="p-6">
      <router-outlet></router-outlet>
    </main>
  `
})
export class AppShell {
  authService = inject(AuthService);

  logout() {
    this.authService.logout();
  }
}
