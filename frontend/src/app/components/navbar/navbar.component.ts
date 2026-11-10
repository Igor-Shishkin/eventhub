import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { KeycloakService } from '../../auth/keycloak.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, ButtonModule],
  template: `
    <nav class="navbar">
      <div class="nav-container">
        <a routerLink="/events" class="brand">
          <i class="pi pi-bolt brand-icon"></i>
          <span class="brand-name">EventHub</span>
        </a>
        <div class="nav-links">
          <p-button
            routerLink="/events"
            label="Events"
            icon="pi pi-calendar"
            [text]="true"
            severity="contrast"
            size="small">
          </p-button>
          @if (keycloak.authenticated()) {
            <span class="user-name">{{ keycloak.username() }}</span>
            @if (keycloak.isOrganizer()) {
              <p-button
                routerLink="/events/new"
                label="New Event"
                icon="pi pi-plus"
                severity="primary"
                size="small">
              </p-button>
            }
            <p-button
              label="Logout"
              icon="pi pi-sign-out"
              [text]="true"
              severity="contrast"
              size="small"
              (onClick)="logout()">
            </p-button>
          } @else {
            <p-button
              label="Login"
              icon="pi pi-sign-in"
              severity="primary"
              size="small"
              (onClick)="login()">
            </p-button>
          }
        </div>
      </div>
    </nav>
  `,
  styles: [`
    .navbar {
      background: #0f172a;
      padding: 0.75rem 1.5rem;
      border-bottom: 1px solid #1e293b;
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
    .brand-icon {
      font-size: 1.35rem;
      color: #3b82f6;
    }
    .nav-links {
      display: flex;
      align-items: center;
      gap: 0.75rem;
    }
    .user-name {
      color: #cbd5e1;
      font-size: 0.875rem;
    }
  `]
})
export class NavbarComponent {
  readonly keycloak = inject(KeycloakService);

  login(): void {
    void this.keycloak.login();
  }

  logout(): void {
    void this.keycloak.logout();
  }
}


