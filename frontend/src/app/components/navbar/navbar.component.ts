import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { KeycloakService } from '../../auth/keycloak.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, ButtonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
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

