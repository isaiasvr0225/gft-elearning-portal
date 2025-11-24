import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  template: `
    <div class="flex items-center justify-center min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 p-4">
      <mat-card class="w-full max-w-md !rounded-xl !shadow-xl overflow-hidden">
        <div class="bg-primary h-2 w-full"></div>
        <mat-card-header class="p-6 pb-2">
          <mat-card-title class="text-3xl font-bold text-gray-800 mb-4">Bienvenido</mat-card-title>
          <mat-card-subtitle class="text-gray-600 mt-8">Ingresa tus credenciales para continuar</mat-card-subtitle>
        </mat-card-header>

        <mat-card-content class="p-6 pt-4">
          <form (ngSubmit)="login()" class="flex flex-col gap-5">
            <mat-form-field appearance="outline" class="w-full">
              <mat-label>Usuario</mat-label>
              <input matInput [(ngModel)]="username" name="username" required placeholder="Ej. usuario123">
              <mat-error>El usuario es requerido</mat-error>
            </mat-form-field>

            <mat-form-field appearance="outline" class="w-full">
              <mat-label>Contraseña</mat-label>
              <input matInput type="password" [(ngModel)]="password" name="password" required placeholder="••••••••">
              <mat-error>La contraseña es requerida</mat-error>
            </mat-form-field>

            <button mat-flat-button color="primary" type="submit" 
                    class="!w-full !py-6 !text-lg !rounded-lg mt-2 transition-transform active:scale-95"
                    [disabled]="!username || !password || isLoading()">
              @if (isLoading()) {
                <mat-spinner diameter="24" class="mr-2 inline-block align-middle"></mat-spinner>
              }
              <span class="align-middle">{{ isLoading() ? 'Ingresando...' : 'Ingresar' }}</span>
            </button>
          </form>
        </mat-card-content>
      </mat-card>
    </div>
  `
})
export class LoginComponent {
  private authService = inject(AuthService);
  private snackBar = inject(MatSnackBar);

  username = '';
  password = '';
  isLoading = signal(false);

  login() {
    if (this.username && this.password) {
      this.isLoading.set(true);
      this.authService.login(this.username, this.password).subscribe({
        next: () => {
          // Navigation is handled in AuthService
          // isLoading remains true until navigation happens or we could reset it
        },
        error: (err) => {
          console.error('Login failed', err);
          this.isLoading.set(false);
          this.snackBar.open('Error al iniciar sesión. Verifique sus credenciales.', 'Cerrar', {
            duration: 3000,
            horizontalPosition: 'center',
            verticalPosition: 'bottom',
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }
}
