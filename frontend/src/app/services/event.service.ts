import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateEventRequest, EventItem, EventRegistration, RegisterAttendeeRequest } from '../models/event.model';

@Injectable({
  providedIn: 'root'
})
export class EventService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1/events';

  getEvents(): Observable<EventItem[]> {
    return this.http.get<EventItem[]>(this.baseUrl);
  }

  getEventById(id: string): Observable<EventItem> {
    return this.http.get<EventItem>(`${this.baseUrl}/${id}`);
  }

  createEvent(request: CreateEventRequest): Observable<EventItem> {
    return this.http.post<EventItem>(this.baseUrl, request);
  }

  publishEvent(id: string): Observable<EventItem> {
    return this.http.post<EventItem>(`${this.baseUrl}/${id}/publish`, {});
  }

  cancelEvent(id: string): Observable<EventItem> {
    return this.http.post<EventItem>(`${this.baseUrl}/${id}/cancel`, {});
  }

  getRegistrations(eventId: string): Observable<EventRegistration[]> {
    return this.http.get<EventRegistration[]>(`${this.baseUrl}/${eventId}/registrations`);
  }

  getRegistration(eventId: string, userId: string): Observable<EventRegistration> {
    return this.http.get<EventRegistration>(`${this.baseUrl}/${eventId}/registrations/${userId}`);
  }

  registerForEvent(eventId: string, request: RegisterAttendeeRequest): Observable<EventRegistration> {
    return this.http.post<EventRegistration>(`${this.baseUrl}/${eventId}/registrations`, request);
  }

  cancelRegistration(eventId: string, userId: string): Observable<EventRegistration> {
    return this.http.post<EventRegistration>(`${this.baseUrl}/${eventId}/registrations/${userId}/cancel`, {});
  }
}


