import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BookService } from '../book.service';
import { Book } from '../models';

@Component({
  selector: 'app-home',
  imports: [FormsModule],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent implements OnInit {
  books: Book[] = [];
  search = '';
  difficulty = '';
  loading = true;
  error = '';

  constructor(
    private router: Router,
    private bookService: BookService,
    private changeDetector: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.bookService.getBooks().subscribe({
      next: (books) => {
        this.books = books;
        this.loading = false;
        this.changeDetector.markForCheck();
      },
      error: () => {
        this.error = 'Could not load books. Check that the backend is running and try again.';
        this.loading = false;
        this.changeDetector.markForCheck();
      }
    });
  }

  get filteredBooks(): Book[] {
    const result: Book[] = [];
    const query = this.search.trim().toLowerCase();
    for (const book of this.books) {
      const matchesSearch = book.title.toLowerCase().includes(query)
        || book.author.toLowerCase().includes(query);
      const matchesDifficulty = this.difficulty === '' || book.difficulty === this.difficulty;
      if (matchesSearch && matchesDifficulty) {
        result.push(book);
      }
    }
    return result;
  }

  hasSave(book: Book) {
    return this.bookService.readSavedGame(book.id) !== null;
  }

  resumeBook(book: Book) {
    this.router.navigate(['/game', book.id], { queryParams: { resume: 'true' } });
  }

  selectBook(book: Book) {
    if (book.valid) {
      this.router.navigate(['/game', book.id]);
    }
  }
}
