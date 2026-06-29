# Payout MVP

Backend MVP для платформы выплат подрядчикам. Сервис покрывает базовый цикл: компания регистрируется, создает задачу, подрядчик принимает ее, отправляет результат, компания одобряет работу и инициирует выплату.

## Что уже реализовано

- Регистрация и вход по JWT.
- Роли пользователей: `ADMIN`, `MANAGER`, `ACCOUNTANT`, `CONTRACTOR`.
- Регистрация компании с первым пользователем `ADMIN`.
- Регистрация подрядчика с ролью `CONTRACTOR`.
- Создание и просмотр задач.
- Принятие задачи подрядчиком.
- Автоматическое создание договора при принятии задачи.
- Ручное создание договоров и шаблонов договоров.
- Подписание договора подрядчиком.
- Отправка результата работы подрядчиком.
- Одобрение задачи компанией.
- MVP-выплата по одобренной задаче через mock payment provider.
- Swagger/OpenAPI документация.

## Стек

- Java 21
- Spring Boot 3.4.6
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- Redis Cache
- JWT
- Lombok
- springdoc-openapi
- Maven Wrapper

## Архитектура модулей

```text
src/main/java/com/payout/app
├── iam          # пользователи, компании, JWT auth
├── tasks        # задачи и жизненный цикл задач
├── contracts    # договоры и шаблоны договоров
├── submissions  # отправка результатов работы
├── payments     # MVP-выплаты
└── config       # security, cors, swagger, errors, cache
```

Миграции базы находятся в `src/main/resources/db/migration`.

## Требования

- JDK 21
- Docker и Docker Compose
- Maven не обязателен, в проекте есть `./mvnw`

## Быстрый запуск

1. Поднять PostgreSQL:

```bash
docker compose up -d
```

2. Запустить приложение:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/payoutxx1 ./mvnw spring-boot:run
```

По умолчанию приложение стартует на `http://localhost:8080`.

Важно: в `docker-compose.yml` база называется `payoutxx1`, а дефолтное значение в `application.yml` указывает на `payout`. Поэтому для запуска с текущим compose нужно передать `SPRING_DATASOURCE_URL`, как в примере выше, либо переименовать базу в compose.

## Переменные окружения

| Переменная | Значение по умолчанию | Назначение |
| --- | --- | --- |
| `PORT` | `8080` | HTTP-порт приложения |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/payout` | JDBC URL PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `admin` | Пользователь PostgreSQL |
| `SPRING_DATASOURCE_PASSWORD` | `1234` | Пароль PostgreSQL |
| `REDISHOST` | `localhost` | Redis host |
| `REDISPORT` | `6379` | Redis port |
| `REDISPASSWORD` | пусто | Redis password |
| `JWT_SECRET` | dev-secret из `application.yml` | Секрет для JWT |
| `FRONTEND_URL` | `http://localhost:5173` | CORS origin фронтенда |

Для production нужно обязательно заменить `JWT_SECRET`.

## Swagger

Swagger доступен без авторизации:

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/swagger-ui/index.html`
- `http://localhost:8080/v3/api-docs`

В Swagger настроена Bearer JWT авторизация.

## Основной MVP workflow

1. Компания регистрируется через `POST /api/auth/register`.
2. Подрядчик регистрируется через `POST /api/auth/register-contractor`.
3. Компания создает задачу через `POST /api/tasks`.
4. Подрядчик видит доступные задачи через `GET /api/tasks`.
5. Подрядчик принимает задачу через `POST /api/tasks/{id}/accept`.
6. Backend автоматически создает договор и связывает его с задачей.
7. Подрядчик отправляет результат через `POST /api/tasks/{taskId}/submissions`.
8. Компания одобряет задачу через `POST /api/tasks/{id}/approve`.
9. Компания инициирует выплату через `POST /api/payments/tasks/{id}`.
10. Backend создает mock-платеж со статусом `PAID` и переводит задачу в `COMPLETED`.

## Auth API

Публичные endpoint'ы:

- `POST /api/auth/register`
- `POST /api/auth/register-contractor`
- `POST /api/auth/login`

Защищенный endpoint:

- `GET /api/auth/me`

После регистрации или логина нужно отправлять JWT во все защищенные запросы:

```http
Authorization: Bearer <accessToken>
Content-Type: application/json
```

Пример регистрации компании:

```json
{
  "companyName": "Acme LLP",
  "bin": "123456789012",
  "email": "admin@acme.kz",
  "password": "password123"
}
```

Пример регистрации подрядчика:

```json
{
  "userame": "ivan",
  "email": "ivan@example.com",
  "password": "password123",
  "selfEmployed": true
}
```

Поле `userame` написано именно так в текущем DTO. Это известная опечатка backend API.

## Tasks API

- `POST /api/tasks` - создать задачу, доступно пользователям компании.
- `GET /api/tasks` - список задач текущего пользователя.
- `GET /api/tasks/{id}` - получить задачу.
- `POST /api/tasks/{id}/accept` - принять задачу, доступно подрядчику.
- `POST /api/tasks/{id}/approve` - одобрить задачу, доступно компании.
- `POST /api/tasks/{id}/reject` - отклонить задачу, текущая реализация требует доработки.

Пример создания задачи:

```json
{
  "title": "Сверстать лендинг",
  "description": "Описание задачи",
  "budget": 150000,
  "deadline": "2026-06-30T18:00:00+05:00",
  "assignedToId": null
}
```

Статусы задач:

- `CREATED`
- `ACCEPTED`
- `IN_PROGRESS`
- `SUBMITTED`
- `REVIEW`
- `APPROVED`
- `REJECTED`
- `COMPLETED`

## Submissions API

- `POST /api/tasks/{taskId}/submissions` - подрядчик отправляет результат работы.
- `GET /api/tasks/{taskId}/submissions` - список отправок по задаче.

Пример отправки результата:

```json
{
  "content": "Работа выполнена, ссылка на результат: https://example.com",
  "attachments": ["https://example.com/file.pdf"]
}
```

`attachments` сейчас хранится как массив строк. Загрузки файлов в MVP нет.

## Contracts API

- `POST /api/contracts` - создать договор вручную.
- `GET /api/contracts` - список договоров текущего пользователя.
- `GET /api/contracts/{id}` - получить договор.
- `POST /api/contracts/{id}/sign` - подписать договор подрядчиком.

Пример создания договора:

```json
{
  "templateId": 1,
  "contractorId": 5,
  "subject": "Разработка лендинга",
  "amount": 150000
}
```

Статусы договоров:

- `DRAFT`
- `SENT`
- `SIGNED`
- `ACTIVE`
- `CLOSED`
- `CANCELLED`

## Contract Templates API

- `POST /api/contracts/templates` - создать шаблон договора.
- `GET /api/contracts/templates` - получить шаблоны компании.

Пример:

```json
{
  "name": "Стандартный договор ГПХ",
  "bodyTemplate": "Текст шаблона договора..."
}
```

## Payments API

- `POST /api/payments/tasks/{id}` - создать выплату по задаче.
- `GET /api/payments` - список выплат текущего пользователя.

В MVP платежный провайдер замокан: backend генерирует `providerTxId`, ставит платежу статус `PAID`, а задаче статус `COMPLETED`.

Платеж можно создать только если:

- текущий пользователь не `CONTRACTOR`;
- задача принадлежит компании пользователя;
- задача в статусе `APPROVED`;
- у задачи есть связанный договор.

Статусы платежей:

- `PENDING`
- `PROCESSING`
- `PAID`
- `FAILED`

## Формат ошибок

Типовая ошибка:

```json
{
  "error": "message"
}
```

Ошибки валидации:

```json
{
  "email": "Invalid email format",
  "password": "Pass must be at least 8"
}
```

Основные HTTP-коды:

- `400` - ошибка бизнес-логики или валидации.
- `401` - нет или неверный JWT.
- `403` - действие запрещено текущей роли.
- `404` - сущность не найдена или недоступна.

## Тесты

Запуск тестов:

```bash
./mvnw test
```

Текущий интеграционный тест поднимает Spring context и может требовать доступные PostgreSQL/Redis настройки, потому что приложение использует реальные datasource/cache конфигурации.

## Известные ограничения MVP

- Нет frontend-приложения в этом репозитории.
- Нет загрузки файлов, `attachments` передаются строками.
- Нет реальной интеграции с платежным провайдером.
- Нет endpoint'ов для создания `MANAGER` и `ACCOUNTANT`.
- `POST /api/tasks/{id}/reject` выглядит некорректно: требует роль `CONTRACTOR`, но проверяет компанию пользователя.
- `GET /api/tasks/{taskId}/submissions` для подрядчика выглядит некорректно: сравнивает `assignedTo.id` с `user.company.id`.
- Поле `userame` в регистрации подрядчика содержит опечатку, но сейчас является частью API.
- Некоторые enum-статусы есть в модели, но для них пока нет переходов в API.

Подробная инструкция для фронтенда находится в `FRONTEND_API_GUIDE.md`.
