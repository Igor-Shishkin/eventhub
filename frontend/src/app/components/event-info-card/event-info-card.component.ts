import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EventItem } from '../../models/event.model';

@Component({
  selector: 'app-event-info-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './event-info-card.component.html',
  styleUrl: './event-info-card.component.scss'
})
export class EventInfoCardComponent {
  @Input({ required: true }) event!: EventItem;
}

