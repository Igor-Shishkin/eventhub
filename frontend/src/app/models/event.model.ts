export type EventStatus = 'DRAFT' | 'PUBLISHED' | 'CANCELLED';

export interface EventItem {
  id: string;
  title: string;
  description: string | null;
  location: string | null;
  organizerId: string;
  status: EventStatus;
  startTime: string;
  endTime: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateEventRequest {
  title: string;
  description?: string | null;
  location?: string | null;
  organizerId: string;
  startTime: string;
  endTime?: string | null;
}

export type RegistrationStatus = 'REGISTERED' | 'CANCELLED';

export interface EventRegistration {
  id: string;
  eventId: string;
  userId: string;
  status: RegistrationStatus;
  registeredAt: string;
}

export interface RegisterAttendeeRequest {
  userId: string;
}


