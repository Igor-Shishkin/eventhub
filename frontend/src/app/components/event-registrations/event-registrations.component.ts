import { Component, Input, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { TagModule } from 'primeng/tag';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { EventService } from '../../services/event.service';
import { EventRegistration, EventStatus } from '../../models/event.model';

@Component({
  selector: 'app-event-registrations',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ButtonModule,
    InputTextModule,
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

  readonly registrations = signal<EventRegistration[]>([]);
  readonly loading = signal<boolean>(false);
  readonly actionInProgress = signal<boolean>(false);
  readonly userId = signal<string>('user-attendee-1');
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  ngOnInit(): void {
    if (this.eventId) {
      this.loadRegistrations();
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

  onRegister(): void {
    const user = this.userId().trim();
    if (!user) {
      this.errorMessage.set('Please provide a valid User ID');
      return;
    }

    this.actionInProgress.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.eventService.registerForEvent(this.eventId, { userId: user }).subscribe({
      next: () => {
        this.successMessage.set(`Successfully registered '${user}' for this event!`);
        this.actionInProgress.set(false);
        this.loadRegistrations();
      },
      error: (err) => {
        this.errorMessage.set(err.error?.detail || 'Failed to register for this event.');
        this.actionInProgress.set(false);
      }
    });
  }

  onCancelRegistration(user: string): void {
    if (!confirm(`Cancel registration for '${user}'?`)) {
      return;
    }

    this.actionInProgress.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.eventService.cancelRegistration(this.eventId, user).subscribe({
      next: () => {
        this.successMessage.set(`Registration for '${user}' cancelled.`);
        this.actionInProgress.set(false);
        this.loadRegistrations();
      },
      error: (err) => {
        this.errorMessage.set(err.error?.detail || 'Failed to cancel registration.');
        this.actionInProgress.set(false);
      }
    });
  }
}



