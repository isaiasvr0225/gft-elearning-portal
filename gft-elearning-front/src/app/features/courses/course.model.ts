export interface Category {
  id: number;
  name: string;
}

export interface ModuleRequest {
  title: string;
  content: string;
}

export interface Module {
  id: number;
  title: string;
  orderIndex: number;
  completed?: boolean;
}

export interface ModuleDetail extends Module {
  content: string;
  courseId: number;
}

export interface CourseRequest {
  title: string;
  description: string;
  categoryId: number;
}

export interface Course {
  id: number;
  title: string;
  description: string;
  module?: string; // Legacy
  category?: Category;
  isCompleted?: boolean;
  progressPercentage?: number;
  modules?: Module[];
}
