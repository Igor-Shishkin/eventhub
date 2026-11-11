import { Component, Input, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { MessageModule } from 'primeng/message';
import { TagModule } from 'primeng/tag';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { EventService } from '../../services/event.service';
import { EventRegistration, EventStatus } from '../../models/event.model';
import { KeycloakService } from '../../auth/keycloak.service';

@Component({
  selector: 'app-event-registrations',
  standalone: true,
  imports: [
    CommonModule,
    ButtonModule,
    MessageModule,
    TagModule,
    ProgressSpinnerModule
  ],
  templateUrl: './event-registrations.component.html',
  styleUrl: './event-registrations.component.scss'
})
export class EventRegistrationsComponent implements OnInit {
  @Input({ required: true }) eventId!: string;
  @Input({ required: true }) eventStatus!: EventStatus;

  private readonly eventService = inject(EventService);
  readonly keycloak = inject(KeycloakService);

  readonly registrations = signal<EventRegistration[]>([]);
  readonly myRegistration = signal<EventRegistration | null>(null);
  readonly loading = signal<boolean>(false);
  readonly actionInProgress = signal<boolean>(false);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  ngOnInit(): void {
    if (this.eventId) {
      if (this.keycloak.isOrganizer()) {
        this.loadRegistrations();
      }
      if (this.keycloak.authenticated()) {
        this.loadMyRegistration();
      }
    }
  }

  loadRegistrations(): void {
    this.loading.set(true);
    this.eventService.getRegistrations(this.eventId).subscribe({
      next: (data) => {
        this.registrations.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load registrations', err);
        this.loading.set(false);
      }
    });
  }

  loadMyRegistration(): void {
    this.eventService.getMyRegistration(this.eventId).subscribe({
      next: registration => this.myRegistration.set(registration),
      error: err => {
        if (err.status === 404) {
          this.myRegistration.set(null);
          return;
        }
        this.errorMessage.set('Failed to load your registration.');
      }
    });
  }

  onRegister(): void {
    this.actionInProgress.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.eventService.registerForEvent(this.eventId).subscribe({
      next: registration => {
        this.myRegistration.set(registration);
        this.successMessage.set('You are registered for this event.');
        this.actionInProgress.set(false);
        if (this.keycloak.isOrganizer()) {
          this.loadRegistrations();
        }
      },
      error: (err) => {
        this.errorMessage.set(err.error?.detail || 'Failed to register for this event.');
        this.actionInProgress.set(false);
      }
    });
  }

  onCancelRegistration(): void {
    if (!confirm('Cancel your registration for this event?')) {
      return;
    }

    this.actionInProgress.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.eventService.cancelMyRegistration(this.eventId).subscribe({
      next: registration => {
        this.myRegistration.set(registration);
        this.successMessage.set('Your registration was cancelled.');
        this.actionInProgress.set(false);
        if (this.keycloak.isOrganizer()) {
          this.loadRegistrations();
        }
      },
      error: (err) => {
        this.errorMessage.set(err.error?.detail || 'Failed to cancel registration.');
        this.actionInProgress.set(false);
      }
    });
  }
}


