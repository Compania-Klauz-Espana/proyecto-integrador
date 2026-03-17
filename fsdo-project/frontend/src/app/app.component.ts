import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { LoadingService } from './core/services/loading.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule],
  template: `
    <div class="app-shell">
      <header class="topbar">
        <span class="logo">FSDO Project</span>
        <span class="env-badge">{{ env }}</span>
      </header>
      <div class="spinner-bar" *ngIf="loading.isLoading$ | async"></div>
      <main>
        <router-outlet/>
      </main>
    </div>
  `,
  styles: [`
    .app-shell { min-height: 100vh; background: #f9fafb; }
    .topbar { background: #1e40af; color: white; padding: .75rem 2rem;
              display: flex; align-items: center; gap: 1rem; }
    .logo { font-weight: 700; font-size: 1.1rem; }
    .env-badge { background: rgba(255,255,255,.2); padding: .2rem .7rem;
                 border-radius: 12px; font-size: .75rem; }
    .spinner-bar { height: 3px; background: #60a5fa;
                   animation: slide 1s ease-in-out infinite; }
    @keyframes slide { 0%{width:0%} 50%{width:70%} 100%{width:100%} }
    main { padding: 1.5rem 2rem; }
  `]
})
export class AppComponent {
  env = 'dev';
  constructor(public loading: LoadingService) {}
}
