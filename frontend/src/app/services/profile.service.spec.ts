import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ProfileService } from './profile.service';
import { UserProfile } from '../models/profile.model';

describe('ProfileService', () => {
  let service: ProfileService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ProfileService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ProfileService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('should get the current user profile', () => {
    const profile: UserProfile = {
      userId: 'user-1',
      displayName: 'Ada Lovelace',
      bio: null,
      locale: 'en-GB',
      timezone: 'Europe/London',
      emailNotificationsEnabled: true,
      createdAt: '2026-01-01T10:00:00Z',
      updatedAt: '2026-01-01T10:00:00Z'
    };

    service.getMyProfile().subscribe(result => expect(result).toEqual(profile));

    const request = httpTesting.expectOne('/api/v1/me');
    expect(request.request.method).toBe('GET');
    request.flush(profile);
  });

  it('should update the current user profile', () => {
    service.updateMyProfile({
      displayName: 'Ada Lovelace',
      bio: 'Organizer',
      locale: 'en-GB',
      timezone: 'Europe/London',
      emailNotificationsEnabled: false
    }).subscribe();

    const request = httpTesting.expectOne('/api/v1/me');
    expect(request.request.method).toBe('PUT');
    expect(request.request.body.displayName).toBe('Ada Lovelace');
  });
});
