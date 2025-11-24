// Modelo para el detalle del curso (steps / preguntas)
export type CourseStep = {
  stepNumber: number;
  question: string;
  options: string[];
  // índice (0-based) de la respuesta correcta (opcional)
  answerIndex?: number;
};

export type CourseDetail = {
  id: number;
  title: string;
  description?: string;
  imageUrl?: string;
  totalSteps: number;
  steps: CourseStep[];
};
