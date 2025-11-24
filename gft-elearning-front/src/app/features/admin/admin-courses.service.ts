import { Injectable, signal } from '@angular/core';
import { Course } from '../courses/course.model';

@Injectable({ providedIn: 'root' })
export class AdminCoursesService {

  private key = 'admin_courses';
  private list = signal<Course[]>(this.load());

  get courses() {
    return this.list;
  }

  private load(): Course[] {
    const raw = localStorage.getItem(this.key);
    return raw ? JSON.parse(raw) : [];
  }

  private save(data: Course[]) {
    localStorage.setItem(this.key, JSON.stringify(data));
  }

  add(course: Course) {
    const next = [...this.list(), course];
    this.list.set(next);
    this.save(next);
  }

  update(course: Course) {
    const next = this.list().map(c => c.id === course.id ? course : c);
    this.list.set(next);
    this.save(next);
  }

  delete(id: number) {
    const next = this.list().filter(c => c.id !== id);
    this.list.set(next);
    this.save(next);
  }

  getById(id: number) {
    return this.list().find(c => c.id === id) ?? null;
  }
}
