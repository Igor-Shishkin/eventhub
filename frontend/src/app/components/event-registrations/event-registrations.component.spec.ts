import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { EventRegistrationsComponent } from './event-registrations.component';

describe('EventRegistrationsComponent', () => {
  let component: EventRegistrationsComponent;
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EventRegistrationsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    const fixture = TestBed.createComponent(EventRegistrationsComponent);
    component = fixture.componentInstance;
    component.eventId = 'evt-123';
    component.eventStatus = 'PUBLISHED';
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should create without loading private registrations for an anonymous user', () => {
    component.ngOnInit();

    expect(component).toBeTruthy();
    expect(component.registrations()).toEqual([]);
  });
});
