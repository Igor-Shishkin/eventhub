import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { TextareaModule } from 'primeng/textarea';
import { MessageModule } from 'primeng/message';
import { EventService } from '../../services/event.service';
import { CreateEventRequest } from '../../models/event.model';

@Component({
  selector: 'app-event-create',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    ButtonModule,
    InputTextModule,
    TextareaModule,
    MessageModule
  ],
  templateUrl: './event-create.component.html',
  styleUrl: './event-create.component.scss'
})
export class EventCreateComponent {
  private readonly fb = inject(FormBuilder);
  private readonly eventService = inject(EventService);
  private readonly router = inject(Router);

  readonly submitting = signal<boolean>(false);
  readonly errorMessage = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    title: ['', [Validators.required, Validators.maxLength(255)]],
    location: [''],
    startTime: ['', [Validators.required]],
    endTime: [''],
    description: ['']
  });

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const val = this.form.getRawValue();

    const startIso = new Date(val.startTime).toISOString();
    let endIso: string | null = null;
    if (val.endTime) {
      endIso = new Date(val.endTime).toISOString();
      if (new Date(endIso).getTime() <= new Date(startIso).getTime()) {
        this.errorMessage.set('End time must be after start time.');
        return;
      }
    }

    const payload: CreateEventRequest = {
      title: val.title.trim(),
      location: val.location?.trim() || null,
      description: val.description?.trim() || null,
      startTime: startIso,
      endTime: endIso
    };

    this.submitting.set(true);
    this.errorMessage.set(null);

    this.eventService.createEvent(payload).subscribe({
      next: (created) => {
        this.submitting.set(false);
        this.router.navigate(['/events', created.id]);
      },
      error: (err) => {
        console.error('Failed to create event', err);
        const detail = err.error?.detail || err.error?.title || 'An error occurred while creating the event.';
        this.errorMessage.set(detail);
        this.submitting.set(false);
      }
    });
  }
}
