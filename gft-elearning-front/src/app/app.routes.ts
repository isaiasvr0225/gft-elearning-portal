import { Routes } from '@angular/router';
import { LoginComponent } from './core/auth/login.component';
import { CoursesPage } from './features/courses/courses.page';
import { RedirectIfLoggedGuard } from './core/auth/guards/redirect-if-logged.guard';
import { AuthGuard } from './core/auth/guards/auth.guard';

export const routes: Routes = [
  // Redirect
  { path: '', redirectTo: 'courses', pathMatch: 'full' },

  // PUBLIC
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [RedirectIfLoggedGuard] // si ya está logueado → courses
  },

  // PRIVATE
  {
    path: 'courses',
    component: CoursesPage,
    canActivate: [AuthGuard]
  },
  {
    path: 'courses/:id',
    loadComponent: () => import('./features/courses/course-detail.page').then(m => m.CourseDetailPage),
    canActivate: [AuthGuard]
  },
  {
    path: 'courses/:courseId/modules/:moduleId',
    loadComponent: () => import('./features/courses/module-detail.page').then(m => m.ModuleDetailPage),
    canActivate: [AuthGuard]
  },

  {
    path: 'profile',
    loadComponent: () =>
      import('./features/profile/profile.page').then(m => m.ProfilePage),
    canActivate: [AuthGuard]
  },

  {
    path: 'admin',
    canActivate: [AuthGuard],
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./features/admin/admin-courses.page').then(
            m => m.AdminCoursesPage
          )
      },

      {
        path: 'new',
        loadComponent: () =>
          import('./features/admin/admin-course-form.page').then(
            m => m.AdminCourseFormPage
          )
      },

      {
        path: 'edit/:id',
        loadComponent: () =>
          import('./features/admin/admin-course-form.page').then(
            m => m.AdminCourseFormPage
          )
      }
    ]
  },

  // Wildcard SIEMPRE DE ÚLTIMO
  { path: '**', redirectTo: 'courses' }
];
