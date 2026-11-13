import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { BookService } from '../book.service';
import { Book, Game } from '../models';

@Component({
  selector: 'app-game',
  imports: [RouterLink],
  templateUrl: './game.html',
  styleUrl: './game.css'
})
export class GameComponent implements OnInit {
  game: Game | null = null;
  loading = false;
  paused = false;
  error = '';
  saveMessage = '';

  constructor(
    private route: ActivatedRoute,
    private bookService: BookService,
    private changeDetector: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const bookId = this.route.snapshot.paramMap.get('bookId');
    const resume = this.route.snapshot.queryParamMap.get('resume') === 'true';

    if (bookId && resume) {
      this.resumeSavedGame(bookId);
    } else {
      this.startGame();
    }
  }

  private resumeSavedGame(bookId: string) {
    const saved = this.bookService.readSavedGame(bookId);
    if (!saved) {
      this.startGame();
      return;
    }

    this.loading = true;
    this.error = '';
    this.bookService.getBooks().subscribe({
      next: (books) => {
        this.restoreSavedGame(books, saved);
        this.loading = false;
        this.changeDetector.markForCheck();
      },
      error: () => {
        this.error = 'Could not restore this adventure. Check the backend and reload the page.';
        this.loading = false;
        this.changeDetector.markForCheck();
      }
    });
  }

  private restoreSavedGame(books: Book[], saved: Game) {
    for (const book of books) {
      if (book.id !== saved.bookId || !book.valid) {
        continue;
      }
      for (const section of book.sections) {
        if (section.id === saved.currentSection.id && section.type !== 'END') {
          this.game = {
            bookId: book.id,
            bookTitle: book.title,
            currentSection: section,
            health: saved.health,
            ended: false,
            consequenceText: null
          };
          this.saveMessage = 'Saved progress restored.';
          return;
        }
      }
    }
    this.error = 'This saved adventure is no longer available. Start a new game.';
  }

  startGame() {
    const bookId = this.route.snapshot.paramMap.get('bookId');
    if (!bookId || this.loading) {
      return;
    }
    this.loading = true;
    this.error = '';
    this.saveMessage = '';
    this.bookService.startGame(bookId).subscribe({
      next: (game) => {
        this.game = game;
        this.paused = false;
        this.loading = false;
        this.changeDetector.markForCheck();
      },
      error: () => {
        this.error = 'Could not start this adventure. The book may be invalid or unavailable.';
        this.loading = false;
        this.changeDetector.markForCheck();
      }
    });
  }

  chooseOption(index: number) {
    if (!this.game || this.game.ended || this.paused || this.loading) {
      return;
    }
    this.loading = true;
    this.error = '';
    this.saveMessage = '';
    this.bookService.chooseOption(this.game, index).subscribe({
      next: (game) => {
        this.game = game;
        if (game.ended || game.health <= 0) {
          try {
            this.bookService.removeSavedGame(game.bookId);
          } catch {
            this.saveMessage = 'Could not clear saved progress. Browser storage may be unavailable.';
          }
        }
        this.loading = false;
        this.changeDetector.markForCheck();
      },
      error: () => {
        this.error = 'Could not make that choice. Your current section is unchanged; please try again.';
        this.loading = false;
        this.changeDetector.markForCheck();
      }
    });
  }

  saveProgress() {
    if (!this.game || this.game.ended || this.loading) {
      return;
    }
    try {
      this.bookService.saveGame(this.game);
      this.saveMessage = 'Progress saved in this browser. Resume it from the library.';
    } catch {
      this.saveMessage = 'Could not save progress. Browser storage may be unavailable or full.';
    }
  }

  togglePause() {
    this.paused = !this.paused;
  }
}