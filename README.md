# Property Manager
A feature-rich Android application designed to streamline property maintenance management for tenants, landlords, and agency staff. 

## Modularization
```
PropertyManager/
├── app/                    # Application module for app initialization and navigation
├── core/
│   ├── common/             # Shared utilities, constants, and extensions
│   ├── data/               # Data layer for features
│   ├── domain/             # Domain layer for features
│   ├── firebase/           # Firebase-related integrations (shared)
│   ├── i18n/               # Localization utilities
├── feature/
│   ├── auth/               # Authentication feature
│   │   ├── presentation/   # Presentation/UI for auth feature
│   ├── tenant/             # Tenant-specific feature
│   │   ├── presentation/   # Presentation/UI for tenant feature
│   ├── staff/              # Staff-specific feature
│       ├── presentation/   # Presentation/UI for staff feature
```
