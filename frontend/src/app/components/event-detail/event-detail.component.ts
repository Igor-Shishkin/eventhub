import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { EventService } from '../../services/event.service';
import { EventItem } from '../../models/event.model';

@Component({
  selector: 'app-event-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './event-detail.component.html',
  styleUrl: './event-detail.component.scss'
})
export class EventDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly eventService = inject(EventService);

  readonly event = signal<EventItem | null>(null);
  readonly loading = signal<boolean>(true);
  readonly errorMessage = signal<string | null>(null);
  readonly actionInProgress = signal<boolean>(false);

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.errorMessage.set('Missing event ID.');
      this.loading.set(false);
      return;
    }
    this.loadEvent(id);
  }

  loadEvent(id: string): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.eventService.getEventById(id).subscribe({
      next: (data) => {
        this.event.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load event', err);
        if (err.status === 404) {
          this.errorMessage.set('Event not found.');
        } else {
          this.errorMessage.set('Failed to load event details.');
        }
        this.loading.set(false);
      }
    });
  }

  onPublish(): void {
    const current = this.event();
    if (!current) return;

    if (!confirm(`Are you sure you want to publish event "${current.title}"?`)) {
      return;
    }

    this.actionInProgress.set(true);
    this.eventService.publishEvent(current.id).subscribe({
      next: (updated) => {
        this.event.set(updated);
        this.actionInProgress.set(false);
      },
      error: (err) => {
        alert(err.error?.detail || 'Failed to publish event.');
        this.actionInProgress.set(false);
      }
    });
  }

  onCancel(): void {
    const current = this.event();
    if (!current) return;

    if (!confirm(`Are you sure you want to cancel event "${current.title}"?`)) {
      return;
    }

    this.actionInProgress.set(true);
    this.eventService.cancelEvent(current.id).subscribe({
      next: (updated) => {
        this.event.set(updated);
        this.actionInProgress.set(false);
      },
      error: (err) => {
        alert(err.error?.detail || 'Failed to cancel event.');
        this.actionInProgress.set(false);
      }
    });
  }
}
