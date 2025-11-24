import { Injectable } from '@angular/core';
import { CourseDetail } from './course-detail.model';
import { of, Observable } from 'rxjs';

/**
 * Servicio mock que devuelve el detalle del curso.
 * En producción reemplazar por HttpClient.get(...)
 */
@Injectable({ providedIn: 'root' })
export class CourseDetailService {

  // Datos mock (puedes ampliar)
  private db: Record<number, CourseDetail> = {
    1: {
      id: 1,
      title: 'Introducción a Fullstack',
      description: 'Resumen de conceptos Fullstack (frontend + backend).',
      imageUrl: '/assets/images/fullstack.jpg',
      totalSteps: 3,
      steps: [
        { stepNumber: 1, question: '¿Qué es REST?', options: ['Un estilo arquitectónico','Un DB','Un lenguaje'] , answerIndex: 0 },
        { stepNumber: 2, question: '¿Qué es JSON?', options: ['Formato de texto','DB NoSQL','Framework'], answerIndex: 0 },
        { stepNumber: 3, question: '¿Cuál es el método HTTP para crear?', options: ['GET','POST','DELETE'], answerIndex: 1 }
      ]
    },
    2: {
      id: 2,
      title: 'Buenas prácticas en APIs',
      description: 'Diseño y seguridad para APIs RESTful.',
      totalSteps: 2,
      steps: [
        { stepNumber: 1, question: '¿Qué es versionar una API?', options: ['Cambiar versión','Borrar datos','Deploy'], answerIndex: 0 },
        { stepNumber: 2, question: 'Autenticación recomendada?', options: ['API Key','OAuth2','Ninguna'], answerIndex: 1 }
      ]
    }
  };

  getCourseDetail(id: number): Observable<CourseDetail | null> {
    const item = this.db[id] ?? null;
    return of(item);
  }
}
