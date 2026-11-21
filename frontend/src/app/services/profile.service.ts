import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UpdateUserProfileRequest, UserProfile } from '../models/profile.model';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = '/api/v1/me';

  getMyProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(this.endpoint);
  }

  updateMyProfile(request: UpdateUserProfileRequest): Observable<UserProfile> {
    return this.http.put<UserProfile>(this.endpoint, request);
  }
}
