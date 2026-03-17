import { Routes } from '@angular/router';
import { ItemsComponent } from './features/items/items.component';

export const routes: Routes = [
  { path: '',       redirectTo: 'items', pathMatch: 'full' },
  { path: 'items',  component: ItemsComponent },
  { path: '**',     redirectTo: 'items' }
];
