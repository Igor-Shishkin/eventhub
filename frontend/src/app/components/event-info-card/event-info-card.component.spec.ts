import { TestBed } from '@angular/core/testing';
import { EventInfoCardComponent } from './event-info-card.component';
import { EventItem } from '../../models/event.model';

describe('EventInfoCardComponent', () => {
  let component: EventInfoCardComponent;

  const mockEvent: EventItem = {
    id: 'evt-1',
    title: 'Demo Event',
    description: 'Description',
    location: 'Warsaw',
    organizerId: 'org-1',
    status: 'PUBLISHED',
    startTime: '2026-06-01T10:00:00Z',
    endTime: '2026-06-01T12:00:00Z',
    createdAt: '2026-01-01T10:00:00Z',
    updatedAt: '2026-01-01T10:00:00Z'
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventInfoCardComponent]
    }).compileComponents();

    const fixture = TestBed.createComponent(EventInfoCardComponent);
    component = fixture.componentInstance;
    component.event = mockEvent;
    fixture.detectChanges();
  });

  it('should create and display location and organizer', () => {
    expect(component).toBeTruthy();
    expect(component.event.location).toBe('Warsaw');
    expect(component.event.organizerId).toBe('org-1');
  });
});

