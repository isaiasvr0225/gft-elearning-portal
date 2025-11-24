import { Injectable } from '@angular/core';
import { Badge } from './badge.model';

@Injectable({ providedIn: 'root' })
export class ProfileService {

  private key(username: string) {
    return `badges_${username}`;
  }

  getBadges(username: string): Badge[] {
    const raw = localStorage.getItem(this.key(username));
    return raw ? JSON.parse(raw) : [];
  }

  addBadge(username: string, badge: Badge) {
    const all = this.getBadges(username);
    all.push(badge);
    localStorage.setItem(this.key(username), JSON.stringify(all));
  }

  hasBadge(username: string, courseId: number): boolean {
    return this.getBadges(username).some(b => b.courseId === courseId);
  }
}
