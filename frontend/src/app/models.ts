export interface Book {
  id: string;
  title: string;
  author: string;
  difficulty: string;
  valid: boolean;
  validationErrors: string[];
  sections: Section[];
}

export interface Section {
  id: string;
  text: string;
  type: string;
  options: Option[];
}

export interface Option {
  description: string;
  gotoId: string;
  consequence: Consequence | null;
}

export interface Consequence {
  type: string;
  value: number;
  text: string;
}

export interface Game {
  bookId: string;
  bookTitle: string;
  currentSection: Section;
  health: number;
  ended: boolean;
  consequenceText: string | null;
}
