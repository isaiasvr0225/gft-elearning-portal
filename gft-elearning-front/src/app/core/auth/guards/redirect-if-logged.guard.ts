// src/auth/redirect-if-logged.guard.ts
import { inject } from '@angular/core';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';

export const RedirectIfLoggedGuard = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isLoggedIn()) {
    router.navigate(['/courses']);
    return false;
  }
  return true;
};

