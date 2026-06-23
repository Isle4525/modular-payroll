# Payout API: инструкция для фронтенда

Документ составлен по текущему backend-коду Spring Boot. Базовый префикс всех бизнес-эндпойнтов: `/api`.

Swagger доступен без авторизации:

- `/swagger-ui.html`
- `/swagger-ui/index.html`
- `/v3/api-docs`

## 1. Авторизация и общие правила

Backend использует JWT Bearer token. После регистрации или логина нужно сохранить `accessToken` и отправлять его во все защищенные запросы:

```http
Authorization: Bearer <accessToken>
Content-Type: application/json
```

Публичные эндпойнты:

- `POST /api/auth/register`
- `POST /api/auth/register-contractor`
- `POST /api/auth/login`
- Swagger/OpenAPI URLs

Все остальные эндпойнты требуют авторизации.

Формат типовых ошибок:

```json
{ "error": "message" }
```

Формат ошибок валидации:

```json
{
  "email": "Invalid email format",
  "password": "Pass must be at least 8"
}
```

Коды, которые нужно обработать на фронте:

- `400` - ошибка бизнес-логики или валидации.
- `401` - нет или неверный токен.
- `403` - действие запрещено текущей роли.
- `404` - сущность не найдена или недоступна.

## 2. Роли и навигация

Роли пользователя:

- `ADMIN`
- `MANAGER`
- `ACCOUNTANT`
- `CONTRACTOR`

Сейчас регистрация компании создает пользователя с ролью `ADMIN`. Регистрация подрядчика создает пользователя с ролью `CONTRACTOR`. Эндпойнтов для создания `MANAGER` и `ACCOUNTANT` пока нет.

Рекомендуемые разделы фронта:

- Auth: регистрация компании, регистрация подрядчика, логин.
- Dashboard: краткая сводка по задачам и договорам.
- Tasks: список, карточка задачи, создание задачи, действия по статусам.
- Submissions: отправка результата подрядчиком, просмотр отправок компанией.
- Contracts: список договоров, карточка договора, подписание договора.
- Contract templates: список и создание шаблонов договора.
- Profile: текущий пользователь из `/api/auth/me`.

## 3. Auth API

### POST `/api/auth/register`

Регистрация компании и первого admin-пользователя.

Request:

```json
{
  "companyName": "Acme LLP",
  "bin": "123456789012",
  "email": "admin@acme.kz",
  "password": "password123"
}
```

Валидация:

- `companyName` обязателен.
- `email` обязателен и должен быть email.
- `password` обязателен, минимум 8 символов.
- `bin` сейчас фактически обязателен на уровне БД, хотя в DTO нет `@NotBlank`. На фронте лучше считать обязательным.

Response `201`:

```json
{
  "accessToken": "jwt",
  "tokenType": "Bearer",
  "user": {
    "id": 1,
    "email": "admin@acme.kz",
    "role": "ADMIN",
    "selfEmployed": false,
    "company": {
      "id": 1,
      "name": "Acme LLP",
      "bin": "123456789012"
    }
  }
}
```

### POST `/api/auth/register-contractor`

Регистрация подрядчика.

Request:

```json
{
  "userame": "ivan",
  "email": "ivan@example.com",
  "password": "password123",
  "selfEmployed": true
}
```

Важно: поле называется `userame`, не `username`. Это typo в backend DTO. При этом сущность `User` не хранит username, поэтому поле сейчас нужно только чтобы пройти валидацию и не возвращается в ответе.

Response `201`: такой же `AuthResponse`, но `role = CONTRACTOR`, `company = null`.

### POST `/api/auth/login`

Request:

```json
{
  "email": "admin@acme.kz",
  "password": "password123"
}
```

Response `200`: `AuthResponse`.

### GET `/api/auth/me`

Получить текущего пользователя по JWT.

Response `200`:

```json
{
  "id": 1,
  "email": "admin@acme.kz",
  "role": "ADMIN",
  "selfEmployed": false,
  "company": {
    "id": 1,
    "name": "Acme LLP",
    "bin": "123456789012"
  }
}
```

## 4. DTO справочник

### UserResponse

```ts
type UserResponse = {
  id: number;
  email: string;
  role: "ADMIN" | "MANAGER" | "ACCOUNTANT" | "CONTRACTOR";
  selfEmployed: boolean;
  company: CompanyResponse | null;
};
```

### CompanyResponse

```ts
type CompanyResponse = {
  id: number;
  name: string;
  bin: string;
};
```

### TaskCreateRequest

```ts
type TaskCreateRequest = {
  title: string;
  description: string;
  budget: number | string;
  deadline?: string | null;
  assignedToId?: number | null;
};
```

`deadline` должен быть ISO datetime, например `2026-06-30T18:00:00+05:00`.

### TaskResponse

```ts
type TaskResponse = {
  id: number;
  companyId: number;
  createdById: number;
  assignedToId: number | null;
  contractId: number | null;
  title: string;
  description: string;
  budget: number | string;
  deadline: string | null;
  status: TaskStatus;
  createdAt: string;
};

type TaskStatus =
  | "CREATED"
  | "ACCEPTED"
  | "IN_PROGRESS"
  | "SUBMITTED"
  | "REVIEW"
  | "APPROVED"
  | "REJECTED"
  | "COMPLETED";
```

### ContractCreateRequest

```ts
type ContractCreateRequest = {
  templateId?: number | null;
  contractorId: number;
  subject: string;
  amount: number | string;
};
```

### ContractResponse

```ts
type ContractResponse = {
  id: number;
  contractNumber: string;
  contractorId: number;
  subject: string;
  amount: number | string;
  status: ContractStatus;
  signedAt: string | null;
  fileUrl: string | null;
  createdAt: string;
};

type ContractStatus =
  | "DRAFT"
  | "SENT"
  | "SIGNED"
  | "ACTIVE"
  | "CLOSED"
  | "CANCELLED";
```

### ContractTemplateRequest

```ts
type ContractTemplateRequest = {
  name: string;
  bodyTemplate: string;
};
```

### ContractTemplateResponse

```ts
type ContractTemplateResponse = {
  id: number;
  name: string;
  bodyTemplate: string;
  createdAt: string;
};
```

### SubmissionRequest

```ts
type SubmissionRequest = {
  content: string;
  attachments?: string[] | null;
};
```

Сейчас attachments - это массив строк. Эндпойнта загрузки файлов нет, поэтому фронт может отправлять URL, имена файлов или временно пустой массив, в зависимости от договоренности.

### SubmissionResponse

```ts
type SubmissionResponse = {
  id: number;
  taskId: number;
  contractorId: number;
  content: string;
  attachments: string[] | null;
  status: SubmissionStatus;
  submittedAt: string;
};

type SubmissionStatus = "PENDING_REVIEW" | "APPROVED" | "REJECTED";
```

## 5. Tasks API

### POST `/api/tasks`

Создать задачу. Доступно компании, не подрядчику.

Request:

```json
{
  "title": "Сверстать лендинг",
  "description": "Описание задачи",
  "budget": 150000,
  "deadline": "2026-06-30T18:00:00+05:00",
  "assignedToId": 5
}
```

`assignedToId` опционален. Если не указан, задача попадает в открытый пул и видна подрядчикам.

Response `201`: `TaskResponse` со статусом `CREATED`.

### GET `/api/tasks`

Список задач.

Для компании возвращает все задачи компании. Для подрядчика возвращает:

- задачи, назначенные на него;
- открытые задачи без `assignedToId` со статусом `CREATED`.

Response `200`:

```json
[
  {
    "id": 10,
    "companyId": 1,
    "createdById": 1,
    "assignedToId": null,
    "contractId": null,
    "title": "Сверстать лендинг",
    "description": "Описание задачи",
    "budget": 150000,
    "deadline": "2026-06-30T18:00:00+05:00",
    "status": "CREATED",
    "createdAt": "2026-06-23T10:00:00+05:00"
  }
]
```

### GET `/api/tasks/{id}`

Получить задачу.

Доступ:

- компания видит задачи своей компании;
- подрядчик видит назначенные на него задачи;
- подрядчик видит открытые задачи без исполнителя, если статус `CREATED`.

### POST `/api/tasks/{id}/accept`

Принять задачу. Доступно только `CONTRACTOR`.

Правила:

- задача должна быть в статусе `CREATED`;
- если `assignedToId` уже задан, принять может только этот подрядчик;
- при принятии автоматически создается договор и сразу ставится `ContractStatus.SIGNED`;
- задача получает `assignedToId`, `contractId`, статус `ACCEPTED`.

Фронт: показывать кнопку `Accept` подрядчику на задачах `CREATED`, если задача открытая или назначена на него.

### POST `/api/tasks/{id}/approve`

Одобрить задачу. Доступно компании, не подрядчику.

Правила:

- допустимые исходные статусы: `REVIEW`, `SUBMITTED`;
- результат: `APPROVED`.

Фронт: показывать компании кнопку `Approve` на задачах `SUBMITTED` или `REVIEW`.

### POST `/api/tasks/{id}/reject`

Отклонить задачу.

Текущая реализация backend требует роль `CONTRACTOR`, но затем проверяет `task.company.id == user.company.id`. У подрядчика `company = null`, поэтому этот эндпойнт выглядит сломанным для реального подрядчика. По бизнес-логике отклонение результата обычно должна делать компания.

До исправления backend фронту лучше не строить критичный workflow на этом эндпойнте.

## 6. Submissions API

### POST `/api/tasks/{taskId}/submissions`

Подрядчик отправляет результат работы.

Request:

```json
{
  "content": "Работа выполнена, ссылка на результат: https://example.com",
  "attachments": ["https://example.com/file.pdf"]
}
```

Правила:

- доступно только `CONTRACTOR`;
- задача должна быть назначена на текущего подрядчика;
- задача должна быть в статусе `ACCEPTED` или `IN_PROGRESS`;
- после отправки задача становится `SUBMITTED`;
- submission получает статус `PENDING_REVIEW`.

Response `201`: `SubmissionResponse`.

Фронт: показывать форму отправки результата подрядчику на задачах `ACCEPTED` или `IN_PROGRESS`, назначенных на него.

### GET `/api/tasks/{taskId}/submissions`

Получить отправки по задаче.

Для компании должна работать проверка по `companyId`. Для подрядчика текущая реализация выглядит ошибочной: сравнивает `task.assignedTo.id` с `user.company.id`, а у подрядчика `company = null`. Вероятен `500` или `403`.

До исправления backend фронту надежнее использовать этот эндпойнт только в интерфейсе компании.

## 7. Contracts API

### POST `/api/contracts`

Создать договор вручную.

Request:

```json
{
  "templateId": 1,
  "contractorId": 5,
  "subject": "Разработка лендинга",
  "amount": 150000
}
```

Валидация:

- `contractorId` обязателен и должен указывать на пользователя с ролью `CONTRACTOR`;
- `subject` обязателен;
- `amount` обязателен и должен быть больше `0`;
- `templateId` опционален.

Response `201`: `ContractResponse` со статусом `DRAFT`.

Важно: в сервисе нет явной проверки роли создателя. Фронт должен показывать создание договора только пользователям компании, но backend лучше дополнительно защитить.

### GET `/api/contracts`

Список договоров.

- подрядчик видит свои договоры;
- компания видит договоры своей компании.

### GET `/api/contracts/{id}`

Получить договор по id.

### POST `/api/contracts/{id}/sign`

Подписать договор.

Правила:

- подписать может только подрядчик, которому принадлежит договор;
- допустимые исходные статусы: `DRAFT`, `SENT`;
- результат: `SIGNED`, заполняется `signedAt`.

Фронт: показывать кнопку `Sign` подрядчику только на своих договорах в статусах `DRAFT` или `SENT`.

## 8. Contract Templates API

### POST `/api/contracts/templates`

Создать шаблон договора.

Request:

```json
{
  "name": "Стандартный договор ГПХ",
  "bodyTemplate": "Текст шаблона договора..."
}
```

Response `201`: `ContractTemplateResponse`.

Важно: в backend нет явной проверки роли. Фронт должен показывать раздел шаблонов только пользователям компании. Подрядчик, вероятно, получит ошибку из-за `company = null`.

### GET `/api/contracts/templates`

Список шаблонов компании текущего пользователя.

## 9. Рекомендуемый UX по статусам

### Задачи

`CREATED`

- Компания: видит задачу, может ждать исполнителя.
- Подрядчик: может принять, если задача открытая или назначена на него.

`ACCEPTED`

- Подрядчик: видит форму отправки результата.
- Компания: видит исполнителя и связанный `contractId`.

`IN_PROGRESS`

- В enum есть, но backend сейчас не переводит задачу в этот статус.
- Фронт может поддержать отображение, но отдельного действия для перехода нет.

`SUBMITTED`

- Компания: видит результат, может `Approve`.
- Подрядчик: видит, что работа отправлена.

`REVIEW`

- В enum есть, но backend сейчас не переводит задачу в этот статус.
- Компания может `Approve`, если задача уже оказалась в `REVIEW`.

`APPROVED`

- Финальный успешный статус с точки зрения текущего API.

`REJECTED`

- В enum есть, но текущий endpoint reject выглядит некорректным.

`COMPLETED`

- В enum есть, но backend сейчас не переводит задачу в этот статус.

### Договоры

`DRAFT`

- Создается через `POST /api/contracts`.
- Подрядчик может подписать.

`SENT`

- В enum есть, но endpoint отправки договора отсутствует.
- Подрядчик может подписать, если статус вручную/миграцией стал `SENT`.

`SIGNED`

- Договор подписан.
- При принятии задачи договор создается сразу в `SIGNED`.

`ACTIVE`, `CLOSED`, `CANCELLED`

- В enum есть, но endpoint переходов отсутствует.

### Submissions

`PENDING_REVIEW`

- Создается при отправке результата.

`APPROVED`, `REJECTED`

- В enum есть, но отдельных endpoint для изменения submission-статуса нет.
- Сейчас approval/rejection реализованы на уровне `TaskStatus`, не `SubmissionStatus`.

## 10. Что фронтенду нужно реализовать

Минимальный рабочий функционал:

1. Auth flow:
   - регистрация компании;
   - регистрация подрядчика;
   - логин;
   - хранение JWT;
   - восстановление сессии через `/api/auth/me`;
   - logout на клиенте.

2. Role-based layout:
   - меню компании: задачи, договоры, шаблоны, профиль;
   - меню подрядчика: доступные задачи, мои задачи, договоры, профиль.

3. Tasks:
   - список с фильтрами по `status`, `assignedToId`, `deadline`;
   - карточка задачи;
   - создание задачи для компании;
   - принятие задачи подрядчиком;
   - отправка результата подрядчиком;
   - одобрение результата компанией.

4. Contracts:
   - список договоров;
   - карточка договора;
   - ручное создание договора компанией;
   - подписание договора подрядчиком;
   - переход из задачи в связанный договор по `contractId`.

5. Contract templates:
   - список шаблонов;
   - создание шаблона;
   - выбор шаблона при ручном создании договора.

6. Ошибки и состояния:
   - loading states;
   - empty states;
   - toast/banner для `400/403/404`;
   - redirect на login при `401`;
   - disabled actions, если статус не позволяет действие.

## 11. Чего API пока не хватает для полноценного фронта

Нужно добавить или уточнить на backend:

1. Endpoint списка подрядчиков для компании.
   Сейчас создание задачи/договора требует `assignedToId` или `contractorId`, но API не дает получить список подрядчиков.
   Предложение: `GET /api/contractors` или `GET /api/users?role=CONTRACTOR`.

2. Исправить `RegisterContractorRequest.userame`.
   Нужно переименовать в `username` или удалить поле из DTO, если username не нужен.

3. Добавить username/displayName в `User`.
   Сейчас фронт может показывать только email и id подрядчика.

4. Исправить `POST /api/tasks/{id}/reject`.
   Сейчас endpoint доступен подрядчику, но проверка использует `user.company.id`, которого у подрядчика нет. Нужно решить бизнес-правило: отклоняет компания результат или подрядчик отказывается от задачи.

5. Исправить contractor-доступ в `GET /api/tasks/{taskId}/submissions`.
   Нужно сравнивать `task.assignedTo.id` с `user.id`, а не с `user.company.id`.

6. Добавить endpoint изменения статуса задачи в `IN_PROGRESS`, если этот статус нужен.
   Сейчас `IN_PROGRESS` есть в enum, но нет действия.

7. Добавить endpoint завершения задачи в `COMPLETED`, если нужен отдельный финальный статус после оплаты/закрытия.

8. Добавить управление `SubmissionStatus`.
   Сейчас `SubmissionStatus.APPROVED/REJECTED` есть, но API меняет только `TaskStatus`.

9. Добавить file upload API.
   Сейчас `attachments` - массив строк, без загрузки файлов.

10. Добавить CRUD для contract templates.
    Сейчас есть только create/list. Нет update/delete/get by id.

11. Добавить переходы договоров.
    Сейчас можно создать `DRAFT` и подписать `DRAFT/SENT`, но нет отправки `SENT`, активации, закрытия, отмены.

12. Добавить роли/пользователей компании.
    В enum есть `MANAGER`, `ACCOUNTANT`, но нет API для приглашения или создания сотрудников компании.

## 12. Клиентские модели TypeScript

Можно начать с таких типов:

```ts
export type UserRole = "ADMIN" | "MANAGER" | "ACCOUNTANT" | "CONTRACTOR";

export type TaskStatus =
  | "CREATED"
  | "ACCEPTED"
  | "IN_PROGRESS"
  | "SUBMITTED"
  | "REVIEW"
  | "APPROVED"
  | "REJECTED"
  | "COMPLETED";

export type ContractStatus =
  | "DRAFT"
  | "SENT"
  | "SIGNED"
  | "ACTIVE"
  | "CLOSED"
  | "CANCELLED";

export type SubmissionStatus = "PENDING_REVIEW" | "APPROVED" | "REJECTED";

export type CompanyResponse = {
  id: number;
  name: string;
  bin: string;
};

export type UserResponse = {
  id: number;
  email: string;
  role: UserRole;
  selfEmployed: boolean;
  company: CompanyResponse | null;
};

export type AuthResponse = {
  accessToken: string;
  tokenType: "Bearer";
  user: UserResponse;
};

export type TaskResponse = {
  id: number;
  companyId: number;
  createdById: number;
  assignedToId: number | null;
  contractId: number | null;
  title: string;
  description: string;
  budget: string | number;
  deadline: string | null;
  status: TaskStatus;
  createdAt: string;
};

export type ContractResponse = {
  id: number;
  contractNumber: string;
  contractorId: number;
  subject: string;
  amount: string | number;
  status: ContractStatus;
  signedAt: string | null;
  fileUrl: string | null;
  createdAt: string;
};

export type ContractTemplateResponse = {
  id: number;
  name: string;
  bodyTemplate: string;
  createdAt: string;
};

export type SubmissionResponse = {
  id: number;
  taskId: number;
  contractorId: number;
  content: string;
  attachments: string[] | null;
  status: SubmissionStatus;
  submittedAt: string;
};
```

## 13. Рекомендуемый порядок интеграции

1. Сделать auth client и interceptor для `Authorization`.
2. Реализовать `/api/auth/me` и role-based routing.
3. Реализовать список задач для обеих ролей.
4. Реализовать create task для компании без выбора подрядчика или с ручным вводом `assignedToId`.
5. Реализовать accept task для подрядчика.
6. Реализовать submit work для подрядчика.
7. Реализовать approve task для компании.
8. Реализовать contracts list/detail/sign.
9. Реализовать contract templates create/list.
10. После backend-доработок добавить выбор подрядчика, upload attachments, reject flow и управление полным lifecycle статусов.
