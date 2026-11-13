-- Local development demo data for the Event Service.
-- This script runs only when PostgreSQL initializes a fresh data volume.
-- Organizer and registration user IDs match the fixed subject IDs in
-- infrastructure/keycloak/eventhub-realm.json for the local demo only.

INSERT INTO events (
    id,
    title,
    description,
    location,
    organizer_id,
    status,
    start_time,
    end_time
)
VALUES
    (
        '11111111-1111-4111-8111-111111111111',
        'Spring Community Meetup',
        'An informal evening for local organizers, volunteers, and people interested in community events.',
        'Krakow Community Hub',
        '00000000-0000-4000-8000-000000000001',
        'PUBLISHED',
        '2027-03-20T17:00:00Z',
        '2027-03-20T20:00:00Z'
    ),
    (
        '22222222-2222-4222-8222-222222222222',
        'Volunteer Coordination Workshop',
        'Hands-on workshop about planning volunteer shifts, assigning responsibilities, and managing event-day changes.',
        'Poznan Innovation Center',
        '00000000-0000-4000-8000-000000000002',
        'PUBLISHED',
        '2027-04-10T09:00:00Z',
        '2027-04-10T13:00:00Z'
    ),
    (
        '33333333-3333-4333-8333-333333333333',
        'Kotlin and Coroutines Lab',
        'A draft technical session for developers who want to practice Kotlin coroutines and reactive service design.',
        'Online',
        '00000000-0000-4000-8000-000000000001',
        'DRAFT',
        '2027-05-15T10:00:00Z',
        '2027-05-15T12:30:00Z'
    ),
    (
        '44444444-4444-4444-8444-444444444444',
        'Local Charity Run',
        'A cancelled charity run kept in the demo dataset to make the event lifecycle visible in the UI.',
        'Wroclaw Riverside Park',
        '00000000-0000-4000-8000-000000000002',
        'CANCELLED',
        '2027-06-05T08:00:00Z',
        '2027-06-05T11:00:00Z'
    )
ON CONFLICT DO NOTHING;

INSERT INTO event_registrations (
    id,
    event_id,
    user_id,
    status,
    registered_at
)
VALUES
    (
        'aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa',
        '11111111-1111-4111-8111-111111111111',
        '00000000-0000-4000-8000-000000000004',
        'REGISTERED',
        '2026-11-01T10:00:00Z'
    ),
    (
        'bbbbbbbb-bbbb-4bbb-8bbb-bbbbbbbbbbbb',
        '11111111-1111-4111-8111-111111111111',
        '00000000-0000-4000-8000-000000000005',
        'REGISTERED',
        '2026-11-01T12:30:00Z'
    ),
    (
        'cccccccc-cccc-4ccc-8ccc-cccccccccccc',
        '11111111-1111-4111-8111-111111111111',
        '00000000-0000-4000-8000-000000000006',
        'REGISTERED',
        '2026-11-02T09:15:00Z'
    ),
    (
        'dddddddd-dddd-4ddd-8ddd-dddddddddddd',
        '11111111-1111-4111-8111-111111111111',
        '00000000-0000-4000-8000-000000000007',
        'CANCELLED',
        '2026-11-02T09:30:00Z'
    ),
    (
        'eeeeeeee-eeee-4eee-8eee-eeeeeeeeeeee',
        '22222222-2222-4222-8222-222222222222',
        '00000000-0000-4000-8000-000000000008',
        'REGISTERED',
        '2026-11-03T08:00:00Z'
    ),
    (
        'ffffffff-ffff-4fff-8fff-ffffffffffff',
        '22222222-2222-4222-8222-222222222222',
        '00000000-0000-4000-8000-000000000003',
        'REGISTERED',
        '2026-11-03T09:15:00Z'
    ),
    (
        '12121212-1212-4121-8121-121212121212',
        '22222222-2222-4222-8222-222222222222',
        '00000000-0000-4000-8000-000000000004',
        'REGISTERED',
        '2026-11-04T11:45:00Z'
    ),
    (
        '13131313-1313-4131-8131-131313131313',
        '22222222-2222-4222-8222-222222222222',
        '00000000-0000-4000-8000-000000000005',
        'CANCELLED',
        '2026-11-04T12:00:00Z'
    )
ON CONFLICT DO NOTHING;
