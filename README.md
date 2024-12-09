# Property Manager
A feature-rich Android application designed to streamline property maintenance management for tenants, landlords, and agency staff. 

## Modularization
```
PropertyManager/
├── app/                    # Application module for app initialization and navigation
├── core/
│   ├── common/             # Shared utilities, constants, and extensions
│   ├── firebase/           # Firebase-related integrations (shared)
│   ├── i18n/               # Localization utilities
├── feature/
│   ├── auth/               # Authentication feature
│   │   ├── data/           # Data layer for auth feature
│   │   ├── domain/         # Domain layer for auth feature
│   │   ├── presentation/   # Presentation/UI for auth feature
│   ├── tenant/             # Tenant-specific feature
│   │   ├── data/           # Data layer for tenant feature
│   │   ├── domain/         # Domain layer for tenant feature
│   │   ├── presentation/   # Presentation/UI for tenant feature
│   ├── staff/              # Staff-specific feature
│       ├── data/           # Data layer for staff feature
│       ├── domain/         # Domain layer for staff feature
│       ├── presentation/   # Presentation/UI for staff feature
```