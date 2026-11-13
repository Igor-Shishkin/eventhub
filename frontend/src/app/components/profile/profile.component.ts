import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { TextareaModule } from 'primeng/textarea';
import { ProfileService } from '../../services/profile.service';
import { UserProfile } from '../../models/profile.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    ButtonModule,
    CheckboxModule,
    InputTextModule,
    MessageModule,
    TextareaModule
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly profileService = inject(ProfileService);

  readonly profile = signal<UserProfile | null>(null);
  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    displayName: ['', [Validators.maxLength(120)]],
    bio: ['', [Validators.maxLength(2000)]],
    locale: ['', [Validators.maxLength(32)]],
    timezone: ['', [Validators.maxLength(64)]],
    emailNotificationsEnabled: [true]
  });

  ngOnInit(): void {
    this.profileService.getMyProfile().subscribe({
      next: profile => {
        this.profile.set(profile);
        this.form.patchValue({
          displayName: profile.displayName ?? '',
          bio: profile.bio ?? '',
          locale: profile.locale ?? '',
          timezone: profile.timezone ?? '',
          emailNotificationsEnabled: profile.emailNotificationsEnabled
        });
        this.loading.set(false);
      },
      error: err => {
        console.error('Failed to load profile', err);
        this.errorMessage.set('Failed to load your profile.');
        this.loading.set(false);
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    this.saving.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.profileService.updateMyProfile({
      displayName: value.displayName.trim() || null,
      bio: value.bio.trim() || null,
      locale: value.locale.trim() || null,
      timezone: value.timezone.trim() || null,
      emailNotificationsEnabled: value.emailNotificationsEnabled
    }).subscribe({
      next: profile => {
        this.profile.set(profile);
        this.saving.set(false);
        this.successMessage.set('Profile saved.');
      },
      error: err => {
        console.error('Failed to update profile', err);
        this.errorMessage.set(err.error?.detail || 'Failed to save your profile.');
        this.saving.set(false);
      }
    });
  }
}
