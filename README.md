# Papers, Please - Система управления УПК (Учреждений по приему и проверке)

## 📋 Описание системы

**Papers, Please** — это информационная система для автоматизации работы учреждений по приему и проверке документов мигрантов (УПК). Система предназначена для управления процессами проверки документов, обработки заявок, координации работы персонала и мониторинга деятельности УПК.

### Основные возможности:
- 👤 Управление пользователями с различными ролями и уровнями доступа
- 📄 Управление документами мигрантов с валидацией сроков действия
- 🎫 Обработка заявок (тикетов) на различные типы услуг
- 🏢 Управление учреждениями (УПК) и их персоналом
- 👥 Формирование и управление сменами сотрудников
- 🔔 Система уведомлений для пользователей
- 📊 Отслеживание событий и действий в системе
- 🔐 JWT-аутентификация и детальная система авторизации

---

## 🛠 Технологический стек

### Backend
- **Язык**: Kotlin 2.0.21
- **Фреймворк**: Spring Boot 3.3.5
  - Spring Web (REST API)
  - Spring Data JPA (ORM)
  - Spring Security (авторизация и аутентификация)
  - Spring Validation
- **База данных**: PostgreSQL
- **Миграции**: Flyway
- **Аутентификация**: JWT (JSON Web Tokens) - `io.jsonwebtoken:jjwt 0.12.6`
- **Документация API**: SpringDoc OpenAPI 2.3.0
- **Линтер**: Detekt 1.23.8
- **Сериализация**: Jackson (с поддержкой Kotlin и JSR310)
- **Сборка**: Gradle с Kotlin DSL
- **Java**: 17

---

## 👥 Иерархия ролей

Система поддерживает 5 ролей с различными уровнями доступа:

### 1. **GOD** 🔱
- **Суперадминистратор** с полным доступом ко всем ресурсам
- Может выполнять любые операции в системе
- Обход всех проверок доступа

### 2. **BOSS** 👔
- **Начальник УПК**
- Управление персоналом своего УПК
- Просмотр и управление тикетами своего УПК
- Формирование смен
- Обработка апелляций
- Доступ только к ресурсам своего УПК

### 3. **INSPECTOR** 🔍
- **Инспектор УПК**
- Обработка тикетов своего УПК
- Проверка документов мигрантов
- Просмотр информации о тикетах и документах в рамках своего УПК

### 4. **SECURITY** 🛡️
- **Сотрудник безопасности УПК**
- Просмотр документов и тикетов своего УПК
- Доступ к информации о пользователях в рамках своего УПК

### 5. **MIGRANT** 🧳
- **Мигрант** (пользователь системы)
- Создание и управление своими документами
- Создание заявок (тикетов)
- Просмотр только своих данных

---

## 🔐 Матрица доступа к API endpoints

### 🔓 Публичные endpoints (без аутентификации)

| Endpoint | Метод | Описание |
|----------|-------|----------|
| `/api/v1/auth/register` | POST | Регистрация нового пользователя |
| `/api/v1/auth/register-god` | POST | Регистрация GOD пользователя (требуется секретный ключ) |
| `/api/v1/auth/login` | POST | Авторизация в системе |
| `/api/v1/documents/active` | GET | Получение активных документов пользователя (по userId) |

### 👤 Управление пользователями (`/api/v1/users`)

| Endpoint | Метод | Роли | Описание |
|----------|-------|------|----------|
| `GET /` | GET | BOSS, GOD | Получить всех пользователей (с пагинацией) |
| `GET /me` | GET | **ВСЕ** | Получить информацию о текущем пользователе |
| `PATCH /me` | PATCH | **ВСЕ** | Обновить информацию о текущем пользователе |
| `GET /{id}` | GET | BOSS, SECURITY, GOD | Получить пользователя по ID |
| `GET /{id}/boss-details` | GET | BOSS, GOD | Получить детальную информацию о боссе (подчинённые, смены) |
| `GET /{id}/details` | GET | **ВСЕ** | Получить полную информацию о пользователе (смены, участие)* |
| `POST /` | POST | BOSS, GOD | Создать нового пользователя |
| `PATCH /{id}` | PATCH | BOSS, GOD | Обновить пользователя |
| `DELETE /{id}` | DELETE | BOSS, GOD | Удалить пользователя |

> *Пользователь может просматривать свои собственные детали. BOSS может просматривать детали пользователей только из своего УПК. GOD имеет доступ ко всем пользователям.

### 📄 Управление документами (`/api/v1/documents`)

| Endpoint | Метод | Роли | Описание |
|----------|-------|------|----------|
| `GET /` | GET | INSPECTOR, BOSS, SECURITY, GOD, MIGRANT | Получить документы по userId |
| `GET /active` | GET | **ПУБЛИЧНЫЙ** | Получить активные (не истекшие) документы |
| `GET /{id}` | GET | INSPECTOR, BOSS, SECURITY, GOD, MIGRANT | Получить документ по ID |
| `POST /` | POST | MIGRANT, GOD | Создать новый документ |
| `PATCH /{id}` | PATCH | MIGRANT, GOD | Обновить документ |
| `DELETE /{id}` | DELETE | GOD | Удалить документ |
| `GET /ticket/{ticketId}` | GET | INSPECTOR, BOSS, SECURITY, GOD | Получить документы тикета (только своего УПК)* |

> *Доступ только для сотрудников УПК, к которому относится тикет

### 🎫 Управление тикетами (`/api/v1/tickets`)

| Endpoint | Метод | Роли | Описание |
|----------|-------|------|----------|
| `GET /` | GET | INSPECTOR, BOSS, SECURITY, MIGRANT, GOD | Получить тикеты (с фильтрацией) |
| `GET /{id}` | GET | INSPECTOR, BOSS, SECURITY, MIGRANT, GOD | Получить тикет по ID |
| `POST /` | POST | INSPECTOR, BOSS, SECURITY, MIGRANT, GOD | Создать новый тикет |
| `PATCH /{id}` | PATCH | INSPECTOR, BOSS, SECURITY, GOD | Обновить тикет |
| `DELETE /{id}` | DELETE | MIGRANT, BOSS, GOD | Удалить тикет |
| `GET /{id}/documents` | GET | INSPECTOR, BOSS, SECURITY, GOD | Получить документы тикета (только своего УПК)* |
| `POST /{id}/documents/{documentId}` | POST | INSPECTOR, BOSS, SECURITY, GOD | Прикрепить документ к тикету |
| `DELETE /{id}/documents/{documentId}` | DELETE | INSPECTOR, BOSS, SECURITY, GOD | Открепить документ от тикета |
| `GET /{id}/related` | GET | INSPECTOR, BOSS, SECURITY, GOD | Получить связанные тикеты |
| `POST /{id}/related/{relatedTicketId}` | POST | INSPECTOR, BOSS, SECURITY, GOD | Связать тикеты |
| `DELETE /{id}/related/{relatedTicketId}` | DELETE | INSPECTOR, BOSS, SECURITY, GOD | Удалить связь между тикетами |

> *Доступ только для сотрудников УПК, к которому относится тикет

### 🏢 Управление УПК (`/api/v1/upks`)

| Endpoint | Метод | Роли | Описание |
|----------|-------|------|----------|
| `GET /` | GET | BOSS, GOD | Получить все УПК |
| `GET /{id}` | GET | BOSS, GOD | Получить УПК по ID |
| `POST /` | POST | BOSS, GOD | Создать новый УПК |
| `PATCH /{id}` | PATCH | BOSS, GOD | Обновить УПК |
| `DELETE /{id}` | DELETE | GOD | Удалить УПК |
| `GET /{id}/employees` | GET | BOSS, GOD | Получить сотрудников УПК (только своего УПК для BOSS)** |

> **BOSS может видеть только сотрудников СВОЕГО УПК

### 👥 Управление сменами (`/api/v1/shifts`)

| Endpoint | Метод | Роли | Описание |
|----------|-------|------|----------|
| `GET /` | GET | INSPECTOR, BOSS, SECURITY, GOD | Получить все смены |
| `GET /{id}` | GET | INSPECTOR, BOSS, SECURITY, GOD | Получить смену по ID |
| `GET /{id}/details` | GET | INSPECTOR, BOSS, SECURITY, GOD | Получить детали смены (инспекторы, статистика) |
| `POST /` | POST | BOSS, GOD | Создать новую смену |
| `PATCH /{id}` | PATCH | BOSS, GOD | Обновить смену |
| `DELETE /{id}` | DELETE | GOD | Удалить смену |
| `POST /{id}/participants/{userId}` | POST | BOSS, GOD | Добавить участника в смену |
| `DELETE /{id}/participants/{userId}` | DELETE | BOSS, GOD | Удалить участника из смены |

### 🔔 Управление уведомлениями (`/api/v1/notifications`)

| Endpoint | Метод | Роли | Описание |
|----------|-------|------|----------|
| `GET /` | GET | **ВСЕ** | Получить уведомления текущего пользователя |
| `PATCH /{id}/read` | PATCH | **ВСЕ** | Пометить уведомление как прочитанное |
| `DELETE /{id}` | DELETE | **ВСЕ** | Удалить уведомление |

### 📊 Управление событиями (`/api/v1/events`)

| Endpoint | Метод | Роли | Описание |
|----------|-------|------|----------|
| `GET /` | GET | BOSS, GOD | Получить все события (с фильтрацией) |
| `GET /{id}` | GET | BOSS, GOD | Получить событие по ID |

---

## 📊 Детальные endpoints

### 👤 GET `/api/v1/users/{id}/boss-details`

**Описание**: Получить детальную информацию о боссе УПК (работает только для пользователей с ролью BOSS)

**Роли**: BOSS, GOD

**Формат ответа**:
```json
{
  "id": "uuid",
  "name": "string",
  "email": "string",
  "role": "BOSS",
  "upk": {
    "id": "uuid",
    "name": "string",
    "region": "REGION"
  },
  "subordinates": [
    {
      "id": "uuid",
      "name": "string",
      "email": "string",
      "role": "INSPECTOR | SECURITY",
      "upkId": "uuid"
    }
  ],
  "shifts": [
    {
      "id": "uuid",
      "startTime": "2024-01-01T08:00:00Z",
      "endTime": "2024-01-01T20:00:00Z",
      "createdBy": "uuid",
      "upkId": "uuid"
    }
  ]
}
```

**Примечания**:
- В `subordinates` не включаются пользователи с ролью `MIGRANT`
- Сам босс не отображается в собственном списке подчиненных
- Отображаются только сотрудники УПК: `INSPECTOR` и `SECURITY`

### 👤 GET `/api/v1/users/{id}/details`

**Описание**: Получить полную информацию о пользователе со всеми сменами и статистикой

**Роли**: **ВСЕ** аутентифицированные пользователи

**Правила доступа**:
- Любой пользователь может просматривать **свои собственные** детали
- BOSS может просматривать детали пользователей только из **своего УПК**
- GOD имеет доступ ко **всем** пользователям

**Формат ответа**:
```json
{
  "id": "uuid",
  "name": "string",
  "email": "string",
  "passwordHash": null,
  "role": "INSPECTOR | SECURITY | etc",
  "upk": {
    "id": "uuid",
    "name": "string",
    "region": "REGION"
  },
  "boss": {
    "id": "uuid",
    "name": "string",
    "email": "string",
    "role": "BOSS"
  },
  "shifts": [
    {
      "id": "uuid",
      "startTime": "2024-01-01T08:00:00Z",
      "endTime": "2024-01-01T20:00:00Z",
      "createdBy": "uuid",
      "upkId": "uuid",
      "boss": {
        "id": "uuid",
        "name": "string",
        "email": "string",
        "role": "BOSS"
      },
      "upk": {
        "id": "uuid",
        "name": "string",
        "region": "REGION"
      },
      "participation": {
        "userId": "uuid",
        "shiftId": "uuid",
        "wage": 1.5,
        "penalty": 0.2,
        "specialization": "PASSPORT | LOCALS | WORK | TRANSIT | SPECIAL",
        "resolvedTickets": 15
      }
    }
  ]
}
```

### 👥 GET `/api/v1/shifts/{id}/details`

**Описание**: Получить детальную информацию о смене с инспекторами и статистикой

**Роли**: INSPECTOR, BOSS, SECURITY, GOD

**Формат ответа**:
```json
{
  "id": "uuid",
  "startTime": "2024-01-01T08:00:00Z",
  "endTime": "2024-01-01T20:00:00Z",
  "createdBy": "uuid",
  "upk": {
    "id": "uuid",
    "name": "string",
    "region": "REGION"
  },
  "boss": {
    "id": "uuid",
    "name": "string",
    "email": "string",
    "role": "BOSS"
  },
  "inspectors": [
    {
      "userId": "uuid",
      "shiftId": "uuid",
      "wage": 1.5,
      "penalty": 0.2,
      "specialization": "PASSPORT | LOCALS | WORK | TRANSIT | SPECIAL",
      "resolvedTickets": 15,
      "passedCrossChecks": 8
    }
  ]
}
```

**Поля участия (participation)**:
- `wage` - коэффициент оплаты/премии (вместо старого `coeffBonus`)
- `penalty` - коэффициент штрафа (вместо старого `coeffPenalty`)
- `resolvedTickets` - количество решённых тикетов инспектором в этой смене
- `passedCrossChecks` - количество пройденных кросс-проверок

---

### 👥 GET `/api/v1/participations`

**Описание**: Получить список participations (участий в сменах) с информацией о завершенных тикетах

**Роли**: INSPECTOR, BOSS, SECURITY, GOD

**Query параметры**:
- `shiftId` (опционально) - фильтр по смене
- `userId` (опционально) - фильтр по пользователю
- `limit` (по умолчанию 10) - количество записей на страницу
- `offset` (по умолчанию 0) - смещение для пагинации

**Формат ответа**:
```json
{
  "items": [
    {
      "id": "uuid",
      "userId": "uuid",
      "shiftId": "uuid",
      "wage": 1.5,
      "penalty": 0.2,
      "specialization": "PASSPORT | LOCALS | WORK | TRANSIT | SPECIAL",
      "totalResolvedTickets": 3
    }
  ],
  "total": 10,
  "limit": 10,
  "offset": 0
}
```

**Поля**:
- `totalResolvedTickets` - количество завершенных (`CLOSED`) тикетов для данного пользователя в данной смене
- Значение вычисляется динамически и не хранится в БД
- Учитываются только тикеты где пользователь является executor'ом

---

## 🗄️ База данных: Индексы и PL/pgSQL функции

### 📊 Индексы для оптимизации запросов

Система использует индексы для ускорения критических операций:

#### Таблица `users`
- **idx_user_email** (UNIQUE) - поиск пользователей по email при авторизации

#### Таблица `documents`
- **idx_document_owner** - быстрый поиск документов по владельцу

#### Таблица `tickets`
- **idx_ticket_executor_status** - фильтрация тикетов по исполнителю и статусу
- **idx_ticket_author** - поиск тикетов по автору

#### Таблица `notifications`
- **idx_notification_user_read_time** - получение непрочитанных уведомлений пользователя

---

### ⚡ PL/pgSQL Функции

Система использует оптимизированные PostgreSQL функции для повышения производительности критических операций.

#### 1. `get_active_documents(p_owner_id UUID)`
**Назначение**: Получение всех активных (не истекших) документов пользователя

**Возвращаемые поля**:
- `id` (UUID)
- `document_type` (VARCHAR)
- `body` (TEXT)
- `issued_at` (TIMESTAMP WITH TIME ZONE)
- `expires_at` (TIMESTAMP WITH TIME ZONE)
- `uploaded_at` (TIMESTAMP WITH TIME ZONE)
- `owner_id` (UUID)

**Логика**:
```sql
SELECT документы WHERE owner_id = p_owner_id 
  AND (expires_at IS NULL OR expires_at > NOW())
```

**Применение**: Просмотр документов мигранта, проверка документов инспектором

---

#### 2. `get_ticket_documents(p_ticket_id UUID)`
**Назначение**: Получение всех документов, прикрепленных к тикету

**Возвращаемые поля**:
- `id` (UUID)
- `document_type` (VARCHAR)
- `body` (TEXT)
- `issued_at` (TIMESTAMP WITH TIME ZONE)
- `expires_at` (TIMESTAMP WITH TIME ZONE)
- `owner_name` (VARCHAR) - имя владельца документа

**Логика**:
```sql
SELECT документы 
FROM documents d
JOIN ticket_documents td ON d.id = td.document_id
JOIN users u ON d.owner_id = u.id
WHERE td.ticket_id = p_ticket_id
ORDER BY d.document_type
```

**Применение**: Проверка документов инспектором при обработке заявки

---

#### 3. `get_users_by_upk(p_upk_id UUID)`
**Назначение**: Получение списка всех сотрудников УПК

**Возвращаемые поля**:
- `id` (UUID)
- `email` (VARCHAR)
- `name` (VARCHAR)
- `password_hash` (VARCHAR)
- `role` (VARCHAR)
- `upk_id` (UUID)

**Логика**:
```sql
SELECT пользователи 
FROM users u
WHERE u.upk_id = p_upk_id
ORDER BY u.role, u.name
```

**Применение**: Формирование смены начальником УПК, управление персоналом УПК

---

#### 4. `countByExecutor_IdAndStatusAndShift_Id(executor_id UUID, status VARCHAR, shift_id UUID)`
**Назначение**: Подсчет количества тикетов с определенным статусом для конкретного исполнителя в рамках смены

**Возвращает**: `BIGINT` - количество тикетов

**Логика**:
```sql
SELECT COUNT(*) 
FROM tickets
WHERE executor_id = p_executor_id
  AND status = p_status
  AND shift_id = p_shift_id
```

**Применение**: Расчет статистики производительности сотрудника в конкретной смене (поле `totalResolvedTickets` в Participation API)

---

## 🏗️ Архитектура системы

Проект построен по классической многослойной архитектуре:

```mermaid
graph TB
    Client[👤 Client<br/>Web/Mobile/API consumers]
    
    subgraph Presentation["📱 Presentation Layer"]
        Controllers[Controllers<br/>REST endpoints<br/>@GetMapping, @PostMapping<br/>Request validation]
        JWT[JWT Authentication<br/>Token generation<br/>Token validation]
    end
    
    subgraph Business["⚙️ Business Logic Layer"]
        Services[Services<br/>Business rules<br/>Access control<br/>Transactions]
    end
    
    subgraph Data["💾 Data Layer"]
        Repositories[Repositories<br/>JPA operations<br/>Custom queries<br/>PL/pgSQL functions]
        DTO[DTO<br/>Request/Response objects]
        Entities[Entities<br/>JPA models<br/>Database mappings]
        Mappers[Mappers<br/>Entity ↔ DTO conversions]
    end
    
    DB[(🗄️ PostgreSQL<br/>Tables + Indexes<br/>PL/pgSQL Functions)]
    
    Client -->|HTTP Request| JWT
    JWT -->|Authenticated| Controllers
    Controllers -->|Response| Client
    
    Controllers -->|DTO| Services
    Services -->|DTO| Controllers
    
    Services -->|Entity| Repositories
    Repositories -->|Entity| Services
    
    Repositories -->|SQL/JPA| DB
    DB -->|Result Set| Repositories
    
    Controllers -.->|Use| DTO
    Services -.->|Use| DTO
    Services -.->|Use| Entities
    Repositories -.->|Use| Entities
    
    Services -.->|Use| Mappers
    Mappers -.->|Transform| DTO
    Mappers -.->|Transform| Entities
    
    Services -.->|Check permissions| JWT
    
    style Client fill:#e1f5ff,stroke:#0066cc,stroke-width:3px
    style Presentation fill:#fff4e1,stroke:#ff9900,stroke-width:3px
    style Business fill:#ffe1f5,stroke:#cc0066,stroke-width:3px
    style Data fill:#e1ffe1,stroke:#00cc00,stroke-width:3px
    style DB fill:#f0f0f0,stroke:#333333,stroke-width:3px
```

### Описание архитектурных слоев:

#### 📱 **Presentation Layer** (`controller/`, `security/`, `config/`)
Слой представления, отвечающий за взаимодействие с клиентом и безопасность:
- **Controllers**: обработка HTTP запросов/ответов, маршрутизация endpoints
- **JWT Authentication**: аутентификация, генерация и валидация токенов
- Первичная валидация данных (`@Valid`)
- Авторизация доступа (`@PreAuthorize`)

#### ⚙️ **Business Logic Layer** (`service/`)
Слой бизнес-логики, содержащий основную логику приложения:
- Реализация бизнес-правил
- Проверка прав доступа к ресурсам
- Управление транзакциями (`@Transactional`)
- Координация работы между репозиториями
- Обработка бизнес-логики документов, тикетов, УПК

#### 💾 **Data Layer** (`repository/`, `model/`, `dto/`, `mapper/`)
Слой данных, включающий работу с базой данных и структуры данных:
- **Repositories**: абстракция работы с БД, CRUD операции, кастомные SQL запросы, PL/pgSQL функции
- **Entities**: JPA сущности, отображающие таблицы базы данных
- **DTO**: объекты передачи данных между слоями
- **Mappers**: преобразование Entity ↔ DTO

---

## 📁 Структура проекта

```
papersplease/
├── src/main/kotlin/com/arekalov/papersplease/
│   ├── config/           # Конфигурация Spring (Security, JWT)
│   ├── controller/       # REST API контроллеры
│   │   ├── AuthController.kt
│   │   ├── UserController.kt
│   │   ├── DocumentController.kt
│   │   ├── TicketController.kt
│   │   ├── UpkController.kt
│   │   ├── ShiftController.kt
│   │   ├── NotificationController.kt
│   │   ├── EventController.kt
│   │   └── ParticipationController.kt
│   ├── service/          # Бизнес-логика
│   ├── repository/       # JPA репозитории
│   ├── model/            # Entities и Enums
│   ├── dto/              # Data Transfer Objects
│   ├── mapper/           # Mappers (Entity ↔ DTO)
│   ├── security/         # JWT и Security
│   ├── validation/       # Custom validators
│   └── exception/        # Exception handlers
├── src/main/resources/
│   ├── application.yaml  # Конфигурация приложения
│   └── db/migration/     # Flyway миграции
├── docs/                 # Документация
│   ├── openapi.yaml      # OpenAPI спецификация
│   └── wiki/             # Wiki документация
├── scripts/              # SQL скрипты (seed, clear)
│   ├── seed-data-prod.sql
│   ├── clear-data-prod.sql
│   └── fix-functions.sql
├── deployment/           # Скрипты для деплоя
│   ├── deploy.sh
│   ├── start.sh
│   ├── cleanup.sh
│   └── application-prod.yaml
└── build.gradle.kts      # Gradle конфигурация
```

---

## 🚀 Запуск проекта

### Требования
- Java 17+
- PostgreSQL 14+
- Gradle 9.2+

### Настройка переменных окружения

```bash
export DB_URL="jdbc:postgresql://localhost:5432/papersplease"
export DB_USERNAME="your_username"
export DB_PASSWORD="your_password"
export JWT_SECRET="your-secret-key-at-least-256-bits"
export GOD_SECRET_KEY="super-secret-god-key"
```

### Запуск

```bash
# Сборка проекта
./gradlew build

# Запуск приложения
./gradlew bootRun

# Или через JAR
java -jar build/libs/papersplease-0.0.1-SNAPSHOT.jar
```

Приложение будет доступно по адресу: `http://localhost:8080`


Если функции еще не созданы, выполните:

```bash
psql -U your_username -d papersplease -f scripts/fix-functions.sql
```

### Загрузка тестовых данных

Для загрузки тестовых данных используйте:

```bash
psql -U your_username -d papersplease -f scripts/seed-data-prod.sql
```

Для очистки данных:

```bash
psql -U your_username -d papersplease -f scripts/clear-data-prod.sql
```

---

## 📝 Изменения в API

### Обновления v1.2.0

В версии 1.2.0 добавлены следующие улучшения:

#### 1. **Добавлено поле `totalResolvedTickets` в Participation API**

Теперь при получении информации об участии в смене (participation) возвращается количество завершенных тикетов для конкретной смены.

**Endpoints с обновленным форматом**:
- `GET /api/v1/participations`
- `GET /api/v1/participations/{id}`
- `GET /api/v1/participations?shiftId={id}`
- `GET /api/v1/participations?userId={id}`
- `POST /api/v1/participations`
- `PATCH /api/v1/participations/{id}`

**Формат ответа**:
```json
{
  "id": "uuid",
  "userId": "uuid",
  "shiftId": "uuid",
  "wage": 1.5,
  "penalty": 0.2,
  "specialization": "PASSPORT",
  "totalResolvedTickets": 3
}
```

**Важно**: 
- `totalResolvedTickets` подсчитывается динамически и не хранится в БД
- Считаются только тикеты со статусом `CLOSED` для конкретной смены
- Подсчет привязан к executor'у (пользователю participation) и конкретной смене

#### 2. **Фильтрация subordinates в Boss Details**

При получении информации о боссе (`GET /api/v1/users/{id}/boss-details`) из списка подчиненных теперь исключаются:
- **Сам босс** (не отображается в собственном списке подчиненных)
- **Мигранты** (роль `MIGRANT`)

В списке `subordinates` теперь отображаются только:
- Инспекторы (`INSPECTOR`)
- Сотрудники безопасности (`SECURITY`)

**Пример ответа**:
```json
{
  "id": "boss-uuid",
  "name": "Boss Name",
  "role": "BOSS",
  "subordinates": [
    {
      "id": "uuid",
      "name": "Inspector 1",
      "role": "INSPECTOR",
      "upkId": "upk-uuid"
    },
    {
      "id": "uuid",
      "name": "Security 1",
      "role": "SECURITY",
      "upkId": "upk-uuid"
    }
  ]
}
```

### Переименованные поля (v1.1.0)

В версии 1.1.0 были переименованы поля для лучшей читаемости:

**Таблица `participations`**:
- ~~`bonus_coefficient`~~ → **`wage`** (коэффициент оплаты/премии)
- ~~`penalty_coefficient`~~ → **`penalty`** (коэффициент штрафа)

**API endpoints** (Request/Response):
- ~~`coeffBonus`~~ → **`wage`**
- ~~`coeffPenalty`~~ → **`penalty`**

**Примеры**:

Старый формат (deprecated):
```json
{
  "coeffBonus": 1.5,
  "coeffPenalty": 0.2
}
```

Новый формат:
```json
{
  "wage": 1.5,
  "penalty": 0.2
}
```

---

## 🚀 Деплой на сервер

Проект содержит скрипты для автоматического деплоя на удаленный сервер (IFMO).

### Настройка SSH

Убедитесь, что у вас настроен SSH доступ к серверу. В файле `~/.ssh/config` добавьте:

```
Host ifmo
    HostName se.ifmo.ru
    User sXXXXXX
    Port 2222
```

### Скрипты деплоя

#### 1. `deployment/deploy.sh` - Полный деплой

Собирает JAR, останавливает старое приложение и загружает новую версию на сервер.

```bash
./deployment/deploy.sh
```

**Что делает скрипт:**
1. Собирает JAR файл (`./gradlew clean bootJar`)
2. Подготавливает директорию на сервере
3. **Останавливает все Java процессы** (включая старое приложение)
4. Загружает новые файлы на сервер

**⚠️ Внимание:** Скрипт убивает ВСЕ Java процессы пользователя на сервере командой `pkill -9 -u $USER java`

#### 2. `deployment/start.sh` - Запуск приложения

Запускает приложение на сервере с пробросом порта на локальную машину.

```bash
./deployment/start.sh
```

**Параметры:**
- Удаленный порт: `23561`
- Локальный порт: `8080`
- Приложение будет доступно на: `http://localhost:8080`

**Остановка:** Нажмите `Ctrl+C` - приложение автоматически остановится на сервере.

#### 3. `deployment/stop.sh` - Остановка приложения

Останавливает приложение на сервере без деплоя.

```bash
./deployment/stop.sh
```

**Что делает скрипт:**
1. Показывает все запущенные Java процессы
2. Пытается мягко остановить приложение (`SIGTERM`)
3. При необходимости принудительно останавливает (`SIGKILL`)
4. Проверяет, что все процессы остановлены

### Пример workflow деплоя

```bash
# 1. Деплой новой версии
./deployment/deploy.sh

# 2. Запуск приложения с пробросом порта
./deployment/start.sh

# Приложение работает, доступно на http://localhost:8080
# Нажмите Ctrl+C для остановки

# 3. Или остановка без запуска
./deployment/stop.sh
```

### Конфигурация для production

Файл `deployment/application-prod.yaml` содержит настройки для production среды:
- Подключение к базе данных
- JWT секреты
- Настройки логирования
- И другие параметры

**⚠️ Важно:** Не коммитьте `application-prod.yaml` с реальными секретами в git!

---

## 🔧 Линтинг

Проект использует Detekt для статического анализа кода:

```bash
# Запуск линтера
./gradlew detekt

# Автоматическое исправление проблем (где возможно)
./gradlew detektFormat
```

---

## 📚 Документация API

OpenAPI спецификация доступна в файле `docs/openapi.yaml` или по адресу:
```
http://localhost:8080/v3/api-docs
http://localhost:8080/v3/api-docs.yaml
```

---

## 🤝 Вклад в проект

1. Fork проекта
2. Создайте feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit изменения (`git commit -m 'Add some AmazingFeature'`)
4. Push в branch (`git push origin feature/AmazingFeature`)
5. Откройте Pull Request

---

## 📄 Лицензия

Этот проект является учебным и не имеет лицензии.

---

## 👨‍💻 Автор

**Papers, Please System** - Система управления УПК

Разработано как учебный проект для демонстрации:
- Spring Boot + Kotlin
- REST API design
- JWT authentication
- PostgreSQL + JPA
- Multi-role authorization
- Clean Architecture
