// Servicio simple para persistir el progreso por usuario en localStorage
// Key: progress_<username>

export type CourseState = 'not' | 'started' | 'completed';

export const ProgressService = {
  privateKey(username: string) {
    return `progress_${username}`;
  },

  getAll(username: string): Record<number, CourseState> {
    const raw = localStorage.getItem(this.privateKey(username));
    return raw ? JSON.parse(raw) : {};
  },

  get(username: string, courseId: number): CourseState {
    const all = this.getAll(username);
    return (all[courseId] as CourseState) ?? 'not';
  },

  set(username: string, courseId: number, state: CourseState) {
    const all = this.getAll(username);
    all[courseId] = state;
    localStorage.setItem(this.privateKey(username), JSON.stringify(all));
  },

  markStarted(username: string, courseId: number) {
    this.set(username, courseId, 'started');
  },

  markCompleted(username: string, courseId: number) {
    this.set(username, courseId, 'completed');
  }
};
