import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { EventService } from '../../services/event.service';
import { EventItem, EventStatus } from '../../models/event.model';

@Component({
  selector: 'app-event-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './event-list.component.html',
  styleUrl: './event-list.component.scss'
})
export class EventListComponent implements OnInit {
  private readonly eventService = inject(EventService);

  readonly events = signal<EventItem[]>([]);
  readonly loading = signal<boolean>(true);
  readonly errorMessage = signal<string | null>(null);
  readonly actionInProgress = signal<string | null>(null);

  readonly searchTerm = signal<string>('');
  readonly statusFilter = signal<'ALL' | EventStatus>('ALL');

  readonly filteredEvents = computed(() => {
    const term = this.searchTerm().trim().toLowerCase();
    const filter = this.statusFilter();

    return this.events().filter(event => {
      const matchesFilter = filter === 'ALL' || event.status === filter;
      const matchesTerm = !term ||
        event.title.toLowerCase().includes(term) ||
        (event.location && event.location.toLowerCase().includes(term)) ||
        (event.description && event.description.toLowerCase().includes(term));
      return matchesFilter && matchesTerm;
    });
  });

  ngOnInit(): void {
    this.loadEvents();
  }

  loadEvents(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.eventService.getEvents().subscribe({
      next: (data) => {
        this.events.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load events', err);
        this.errorMessage.set('Failed to load events. Please verify backend is running.');
        this.loading.set(false);
      }
    });
  }

  setStatusFilter(status: 'ALL' | EventStatus): void {
    this.statusFilter.set(status);
  }

  onPublish(event: EventItem, mouseEvt: MouseEvent): void {
    mouseEvt.stopPropagation();
    if (!confirm(`Are you sure you want to publish event "${event.title}"?`)) {
      return;
    }

    this.actionInProgress.set(event.id);
    this.eventService.publishEvent(event.id).subscribe({
      next: (updated) => {
        this.updateEventInList(updated);
        this.actionInProgress.set(null);
      },
      error: (err) => {
        alert(err.error?.detail || 'Failed to publish event.');
        this.actionInProgress.set(null);
      }
    });
  }

  onCancel(event: EventItem, mouseEvt: MouseEvent): void {
    mouseEvt.stopPropagation();
    if (!confirm(`Are you sure you want to cancel event "${event.title}"?`)) {
      return;
    }

    this.actionInProgress.set(event.id);
    this.eventService.cancelEvent(event.id).subscribe({
      next: (updated) => {
        this.updateEventInList(updated);
        this.actionInProgress.set(null);
      },
      error: (err) => {
        alert(err.error?.detail || 'Failed to cancel event.');
        this.actionInProgress.set(null);
      }
    });
  }

  private updateEventInList(updated: EventItem): void {
    this.events.update(list => list.map(item => item.id === updated.id ? updated : item));
  }
}
