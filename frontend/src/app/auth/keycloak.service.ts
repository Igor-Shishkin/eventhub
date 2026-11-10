import { Injectable, signal } from '@angular/core';
import Keycloak from 'keycloak-js';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {
  private readonly client = new Keycloak({
    url: 'http://localhost:8080',
    realm: 'eventhub',
    clientId: 'eventhub-frontend'
  });

  readonly authenticated = signal(false);
  readonly username = signal<string | null>(null);

  async init(): Promise<boolean> {
    try {
      const authenticated = await this.client.init({
        onLoad: 'check-sso',
        pkceMethod: 'S256',
        checkLoginIframe: false
      });

      this.updateAuthenticationState(authenticated);
      return authenticated;
    } catch (error) {
      console.warn('Keycloak is not available. Continuing as an anonymous user.', error);
      this.updateAuthenticationState(false);
      return false;
    }
  }

  async login(): Promise<void> {
    await this.client.login({
      redirectUri: window.location.href
    });
  }

  async logout(): Promise<void> {
    await this.client.logout({
      redirectUri: window.location.origin
    });
  }

  async getToken(): Promise<string | null> {
    if (!this.authenticated()) {
      return null;
    }

    try {
      await this.client.updateToken(30);
      return this.client.token ?? null;
    } catch (error) {
      console.warn('Unable to refresh the Keycloak token.', error);
      this.client.clearToken();
      this.updateAuthenticationState(false);
      return null;
    }
  }

  getUserId(): string | null {
    return this.client.tokenParsed?.sub ?? null;
  }

  isOrganizer(): boolean {
    return this.client.hasRealmRole('ORGANIZER') || this.client.hasRealmRole('ADMIN');
  }

  private updateAuthenticationState(authenticated: boolean): void {
    this.authenticated.set(authenticated);
    this.username.set(
      authenticated
        ? (this.client.tokenParsed?.['preferred_username'] as string | undefined) ?? null
        : null
    );
  }
}
