import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Course, Category, ModuleDetail } from './course.model';
import { tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class CourseService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1';

  // State signal to hold the courses
  private _courses = signal<Course[]>([]);
  readonly courses = this._courses.asReadonly();

  getAdminCourses() {
    return this.http.get<Course[]>(`${this.apiUrl}/admin/courses`).pipe(
      tap(courses => this._courses.set(courses))
    );
  }

  getCourses() {
    return this.http.get<Course[]>(`${this.apiUrl}/me/courses`);
  }

  getCoursesByCategory(categoryName: string) {
    return this.http.get<Course[]>(`${this.apiUrl}/me/courses/by-category/${categoryName}`);
  }

  getCourseDetails(id: number) {
    return this.http.get<Course>(`${this.apiUrl}/courses/${id}`);
  }

  getModule(id: number) {
    return this.http.get<ModuleDetail>(`${this.apiUrl}/modules/${id}`);
  }

  completeModule(id: number) {
    return this.http.put(`${this.apiUrl}/modules/${id}/complete`, {});
  }

  getCategories() {
    return this.http.get<Category[]>(`${this.apiUrl}/categories`);
  }

  getCourseById(id: number) {
    return this.http.get<Course>(`${this.apiUrl}/admin/courses/${id}`);
  }

  addCourse(course: any) {
    return this.http.post<Course>(`${this.apiUrl}/admin/courses`, course).pipe(
      tap(() => this.getAdminCourses().subscribe()) // Refresh list
    );
  }

  updateCourse(id: number, course: any) {
    return this.http.put<Course>(`${this.apiUrl}/admin/courses/${id}`, course).pipe(
      tap(() => this.getAdminCourses().subscribe()) // Refresh list
    );
  }

  addModules(courseId: number, modules: any[]) {
    return this.http.post(`${this.apiUrl}/admin/courses/${courseId}/modules`, modules);
  }

  deleteCourse(id: number) {
    return this.http.delete(`${this.apiUrl}/admin/courses/${id}`).pipe(
      tap(() => this.getAdminCourses().subscribe())
    );
  }


  enrollUser(courseId: number, email: string) {
    return this.http.post(`${this.apiUrl}/admin/courses/${courseId}/enroll`, { email });
  }

  getAll() {
    return this.courses();
  }

  filterByModule(module: string | null) {
    const list = this.courses();
    if (!module) return list;
    // Note: API uses 'category', but legacy code uses 'module'. 
    // We might need to map or check both if we want to support both temporarily.
    // For now, let's try to match category name if module matches.
    return list.filter(c => c.category?.name === module || c.module === module);
  }


}

