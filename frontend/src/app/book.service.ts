import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Book, Game } from './models';

@Injectable({ providedIn: 'root' })
export class BookService {
  constructor(private http: HttpClient) {}

  startGame(bookId: string) {
    return this.http.post<Game>(`/api/games/${encodeURIComponent(bookId)}/start`, {});
  }

  chooseOption(game: Game, optionIndex: number) {
    const bookId = encodeURIComponent(game.bookId);
    const sectionId = encodeURIComponent(game.currentSection.id);
    return this.http.post<Game>(`/api/games/${bookId}/sections/${sectionId}/choices/${optionIndex}`, {
      health: game.health,
      ended: game.ended
    });
  }

  saveGame(game: Game) {
    if (game.ended || game.health <= 0 || game.currentSection.type === 'END') {
      return;
    }
    localStorage.setItem('adventure-save-' + game.bookId, JSON.stringify(game));
  }

  removeSavedGame(bookId: string) {
    localStorage.removeItem('adventure-save-' + bookId);
  }

  readSavedGame(bookId: string): Game | null {
    try {
      const saved = JSON.parse(localStorage.getItem('adventure-save-' + bookId) || 'null');
      if (!saved || saved.bookId !== bookId) {
        return null;
      }
      if (saved.health <= 0) {
        return null;
      }
      if (saved.ended !== false) {
        return null;
      }
      if (!saved.currentSection || saved.currentSection.type === 'END') {
        return null;
      }
      return saved;
    } catch {
      //Error reading from localStorage
    }
    return null;
  }

  getBooks() {
    return this.http.get<Book[]>('/api/books');
  }
}