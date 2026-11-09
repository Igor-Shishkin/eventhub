import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <nav class="navbar">
      <div class="nav-container">
        <a routerLink="/events" class="brand">
          <span class="brand-badge">⚡</span>
          <span class="brand-name">EventHub</span>
        </a>
        <div class="nav-links">
          <a routerLink="/events" routerLinkActive="active" [routerLinkActiveOptions]="{ exact: true }" class="nav-link">
            Events
          </a>
          <a routerLink="/events/new" routerLinkActive="active" class="btn-create">
            + New Event
          </a>
        </div>
      </div>
    </nav>
  `,
  styles: [`
    .navbar {
      background: #1e293b;
      color: #f8fafc;
      padding: 0.85rem 1.5rem;
      border-bottom: 1px solid #334155;
      position: sticky;
      top: 0;
      z-index: 100;
    }
    .nav-container {
      max-width: 1200px;
      margin: 0 auto;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .brand {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      text-decoration: none;
      color: #ffffff;
      font-weight: 700;
      font-size: 1.25rem;
    }
    .brand-badge {
      font-size: 1.3rem;
    }
    .nav-links {
      display: flex;
      align-items: center;
      gap: 1.25rem;
    }
    .nav-link {
      color: #94a3b8;
      text-decoration: none;
      font-weight: 500;
      padding: 0.4rem 0.75rem;
      border-radius: 6px;
      transition: all 0.2s ease;
    }
    .nav-link:hover, .nav-link.active {
      color: #ffffff;
      background: #334155;
    }
    .btn-create {
      background: #3b82f6;
      color: #ffffff;
      padding: 0.5rem 1rem;
      border-radius: 6px;
      text-decoration: none;
      font-weight: 600;
      transition: background 0.2s ease;
    }
    .btn-create:hover {
      background: #2563eb;
    }
  `]
})
export class NavbarComponent {}

