# NEXA — Jetpack Components and Libraries

## Overview
This document describes the Jetpack components and libraries selected for the NEXA Android application, including their purposes and justifications.

## Jetpack Components

| Component | Purpose | Justification |
|---|---|---|
| Jetpack Compose | Build application screens such as Login, Dashboard, Projects, Tasks, and Team Members. | Supports reusable UI components and reactive interface updates. |
| ViewModel | Manage screen state and user interactions. | Separates UI logic from composables and preserves state across configuration changes. |
| Room Database | Cache project and task information locally. | Supports local data access, including previously cached information when offline. |
| DataStore | Store user preferences and application settings. | Provides asynchronous and persistent storage for small settings. |

## Firebase Services

| Service | Purpose | Justification |
|---|---|---|
| Firebase Authentication | Manage registration, login, logout, and password reset. | Provides managed authentication services. |
| Cloud Firestore | Store projects, tasks, memberships, deadlines, and comments. | Supports real-time data synchronization. |
| Cloud Storage for Firebase | Store project documents and attachments. | Supports file storage and access control. |
| Firebase Cloud Messaging (FCM) | Deliver task notifications and reminders. | Enables push notifications through backend integration. |

## Architecture Integration
NEXA follows a layered architecture consisting of UI, Domain, and Data layers.

- UI Layer: Jetpack Compose and ViewModels.
- Domain Layer: Business models and use cases.
- Data Layer: Repositories, Firebase services, Room, and DataStore.

## Related Issue
Closes #9

## Contributor
Fatimah Emad Alarfaj — Stage 2, Jetpack Components and Libraries Documentation.
