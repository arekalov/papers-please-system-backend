# Реализация Backend системы Papers Please

## Этап 0: Настройка Detekt и CI/CD

### Файлы для создания:
- `build.gradle.kts` - добавить плагин Detekt
- `detekt.yml` - конфигурация линтера
- `.github/workflows/lint.yml` - проверка линтера на main (блокирует merge при ошибках)
- `.github/workflows/swagger-deploy.yml` - деплой Swagger UI на github-pages

---

## Этап 1: Базовая конфигурация

### Зависимости в `build.gradle.kts`:
- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `jackson-module-kotlin`

### Файлы для создания:
- `src/main/resources/application.yml` - базовые настройки (port, context-path, logging)

---

## Этап 2: Модели данных

### Зависимости в `build.gradle.kts`:
- `spring-boot-starter-data-jpa`
- `postgresql` драйвер

### Обновить:
- `application.yml` - настройки PostgreSQL (se.ifmo.ru) и JPA

### Enum классы (`com.arekalov.papersplease.entity.enums`):
1. `RoleType`
2. `Region`
3. `Specialization`
4. `Priority`
5. `TicketType`
6. `Status`
7. `DocumentType`
8. `NotificationType`
9. `Verdict`

### Entity классы (`com.arekalov.papersplease.entity`):
1. `User`
2. `Upk`
3. `Shift`
4. `Participation`
5. `Event`
6. `Ticket`
7. `Document`
8. `Notification`
9. `Appeal`

---

## Этап 3: Repositories

### Repository интерфейсы (`com.arekalov.papersplease.repository`):
1. `UserRepository`
2. `UpkRepository`
3. `ShiftRepository`
4. `ParticipationRepository`
5. `EventRepository`
6. `TicketRepository`
7. `DocumentRepository`
8. `NotificationRepository`
9. `AppealRepository`

*Каждый extends JpaRepository, содержит методы фильтрации и @Query для вызова pl/pgsql функций*

---

## Этап 4: DTO и маппинг

### Request DTO (`com.arekalov.papersplease.dto.request`):
1. `UserRequest`
2. `UserRequestPartial`
3. `UpkRequest`
4. `UpkRequestPartial`
5. `ShiftRequest`
6. `ShiftRequestPartial`
7. `ParticipationRequest`
8. `ParticipationRequestPartial`
9. `EventRequest`
10. `EventRequestPartial`
11. `TicketRequest`
12. `TicketRequestPartial`
13. `DocumentRequest`
14. `DocumentRequestPartial`
15. `NotificationRequest`
16. `NotificationRequestPartial`
17. `AppealRequest`
18. `AppealRequestPartial`
19. `RegisterRequest`
20. `LoginRequest`
21. `RefreshRequest`
22. `ResetPasswordRequest`

### Response DTO (`com.arekalov.papersplease.dto.response`):
1. `UserResponse`
2. `UsersListResponse`
3. `UpkResponse`
4. `UpksListResponse`
5. `ShiftResponse`
6. `ShiftsListResponse`
7. `ParticipationResponse`
8. `ParticipationsListResponse`
9. `EventResponse`
10. `EventsListResponse`
11. `TicketResponse`
12. `TicketsListResponse`
13. `DocumentResponse`
14. `DocumentsListResponse`
15. `NotificationResponse`
16. `NotificationsListResponse`
17. `AppealResponse`
18. `AppealsListResponse`
19. `AuthResponse`
20. `ReportResponse`
21. `ErrorResponse`

### Mapper (`com.arekalov.papersplease.dto.mapper`):
1. `EntityMappers.kt` - extension functions для всех Entity ↔ DTO преобразований

---

## Этап 5: Security и JWT

### Зависимости в `build.gradle.kts`:
- `spring-boot-starter-security`
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson`

### Обновить:
- `application.yml` - JWT секреты и expiration time

### Security классы (`com.arekalov.papersplease.security`):
1. `JwtTokenProvider`
2. `JwtAuthenticationFilter`
3. `SecurityConfig`
4. `UserDetailsServiceImpl`

---

## Этап 6: Обработка ошибок и базовые сервисы

### Exception классы (`com.arekalov.papersplease.exception`):
1. `ResourceNotFoundException`
2. `ResourceAlreadyExistsException`
3. `ValidationException`
4. `UnauthorizedException`
5. `ForbiddenException`
6. `GlobalExceptionHandler`

### Service классы (`com.arekalov.papersplease.service`):
1. `AuthService`
2. `UserService`

### Controller классы (`com.arekalov.papersplease.controller`):
1. `AuthController` (все эндпоинты из `/api/v1/auth`)
2. `UserController` (все эндпоинты из `/api/v1/users`)

---

## Этап 7: Остальные сервисы и контроллеры

### Service классы (`com.arekalov.papersplease.service`):
3. `UpkService`
4. `ShiftService`
5. `ParticipationService`
6. `EventService`
7. `TicketService`
8. `DocumentService`
9. `NotificationService`
10. `AppealService`
11. `ReportService`

### Controller классы (`com.arekalov.papersplease.controller`):
3. `UpkController` (все эндпоинты из `/api/v1/upks`)
4. `ShiftController` (все эндпоинты из `/api/v1/shifts`)
5. `ParticipationController` (все эндпоинты из `/api/v1/participations`)
6. `EventController` (все эндпоинты из `/api/v1/events`)
7. `TicketController` (все эндпоинты из `/api/v1/tickets`)
8. `DocumentController` (все эндпоинты из `/api/v1/documents`)
9. `NotificationController` (все эндпоинты из `/api/v1/notifications`)
10. `AppealController` (все эндпоинты из `/api/v1/appeals`)
11. `ReportController` (все эндпоинты из `/api/v1/reports`)

---

## Этап 8: Бизнес-процессы с корутинами

### Зависимости в `build.gradle.kts`:
- `kotlinx-coroutines-core`
- `kotlinx-coroutines-reactor`

### Реализация бизнес-процессов:
- Добавить в `ShiftService` - управление стадиями смены
- Добавить в `TicketService` - процесс подачи заявки мигранта
- Добавить в `DocumentService` - кросс-проверка документов
- Добавить в `AppealService` - процесс апелляций

---

## Итоговая структура проекта

```
src/main/kotlin/com/arekalov/papersplease/
├── PaperspleaseApplication.kt
├── entity/
│   ├── enums/ [9 классов]
│   └── [9 Entity классов]
├── repository/ [9 Repository интерфейсов]
├── dto/
│   ├── request/ [22 Request класса]
│   ├── response/ [21 Response класс]
│   └── mapper/ [1 файл с extension functions]
├── service/ [11 Service классов]
├── controller/ [11 Controller классов]
├── security/ [4 класса]
└── exception/ [6 классов]

.github/workflows/
├── lint.yml
└── swagger-deploy.yml

detekt.yml
application.yml
```

**Всего файлов для создания: ~100 классов + конфигурационные файлы**

## Технологии
Spring Boot 4.0.0, Kotlin 2.2.21, Spring Data JPA, PostgreSQL, Spring Security, JWT, Kotlin Coroutines, Detekt, GitHub Actions

