import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';

export interface User {
  username: string;
  role: 'ADMIN' | 'EMPLOYEE';
  token: string;
}

interface LoginResponse {
  documentNumber: number;
  documentType: string;
  city: string;
  address: string | null;
  fullName: string;
  phoneNumber: string;
  email: string;
  role: 'ADMIN' | 'EMPLOYEE';
  profileImageLink: string;
  jwtToken: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private router = inject(Router);
  private http = inject(HttpClient);
  
  // Initialize from localStorage if available
  private _user = signal<User | null>(this.getUserFromStorage());

  user = this._user.asReadonly();

  constructor() {}

  private getUserFromStorage(): User | null {
    const stored = localStorage.getItem('session_user');
    return stored ? JSON.parse(stored) : null;
  }

  login(username: string, password: string) {
    return this.http.post<LoginResponse>('http://localhost:8080/api/v1/users/login', {
      user: username,
      password: password
    }).pipe(
      tap(response => {
        const newUser: User = {
          username: response.fullName,
          role: response.role,
          token: response.jwtToken
        };
        this._user.set(newUser);
        localStorage.setItem('session_user', JSON.stringify(newUser));
        
        if (newUser.role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/courses']);
        }
      })
    );
  }

  logout() {
    this._user.set(null);
    localStorage.removeItem('session_user');
    this.router.navigate(['/login']);
  }

  isLoggedIn() {
    return this._user() !== null;
  }

  hasRole(role: 'ADMIN' | 'EMPLOYEE') {
    const user = this._user();
    return user ? user.role === role : false;
  }
}// We will keep this for now to fix the immediate error in ProfilePage, but we should refactor ProfilePage to use the service.
export const userSignal = signal<User | null>(null);
