export interface UserProfile {
  userId: string;
  displayName: string | null;
  bio: string | null;
  locale: string | null;
  timezone: string | null;
  emailNotificationsEnabled: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface UpdateUserProfileRequest {
  displayName: string | null;
  bio: string | null;
  locale: string | null;
  timezone: string | null;
  emailNotificationsEnabled: boolean;
}
