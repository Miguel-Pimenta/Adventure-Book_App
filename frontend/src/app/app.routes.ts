import { Routes } from '@angular/router';
import { GameComponent } from './game/game';
import { HomeComponent } from './home/home';

export const routes: Routes = [
  { path: 'game/:bookId', component: GameComponent },
  { path: '', component: HomeComponent },
  { path: '**', redirectTo: '' }
];
