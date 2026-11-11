import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { EventService } from './event.service';
import { EventItem, EventRegistration } from '../models/event.model';

describe('EventService', () => {
  let service: EventService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        EventService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(EventService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get events from /api/v1/events', () => {
    const mockEvents: EventItem[] = [
      {
        id: '1',
        title: 'Event 1',
        description: 'Desc',
        location: 'Warsaw',
        organizerId: 'org1',
        status: 'PUBLISHED',
        startTime: '2026-06-01T10:00:00Z',
        endTime: null,
        createdAt: '2026-01-01T10:00:00Z',
        updatedAt: '2026-01-01T10:00:00Z'
      }
    ];

    service.getEvents().subscribe(events => {
      expect(events.length).toBe(1);
      expect(events[0].title).toBe('Event 1');
    });

    const req = httpTesting.expectOne('/api/v1/events');
    expect(req.request.method).toBe('GET');
    req.flush(mockEvents);
  });

  it('should register attendee for event', () => {
    const mockRegistration: EventRegistration = {
      id: 'reg-1',
      eventId: 'evt-1',
      userId: 'user-1',
      status: 'REGISTERED',
      registeredAt: '2026-06-01T10:00:00Z'
    };

    service.registerForEvent('evt-1').subscribe(reg => {
      expect(reg.id).toBe('reg-1');
      expect(reg.userId).toBe('user-1');
      expect(reg.status).toBe('REGISTERED');
    });

    const req = httpTesting.expectOne('/api/v1/events/evt-1/registrations');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({});
    req.flush(mockRegistration);
  });

  it('should get the current user registration', () => {
    const mockRegistration: EventRegistration = {
      id: 'reg-1',
      eventId: 'evt-1',
      userId: 'user-1',
      status: 'REGISTERED',
      registeredAt: '2026-06-01T10:00:00Z'
    };

    service.getMyRegistration('evt-1').subscribe(reg => {
      expect(reg.id).toBe('reg-1');
    });

    const req = httpTesting.expectOne('/api/v1/events/evt-1/registrations/me');
    expect(req.request.method).toBe('GET');
    req.flush(mockRegistration);
  });

  it('should cancel the current user registration', () => {
    const mockRegistration: EventRegistration = {
      id: 'reg-1',
      eventId: 'evt-1',
      userId: 'user-1',
      status: 'CANCELLED',
      registeredAt: '2026-06-01T10:00:00Z'
    };

    service.cancelMyRegistration('evt-1').subscribe(reg => {
      expect(reg.status).toBe('CANCELLED');
    });

    const req = httpTesting.expectOne('/api/v1/events/evt-1/registrations/me');
    expect(req.request.method).toBe('DELETE');
    req.flush(mockRegistration);
  });
});

