# Authentication Backend Microservice

⚠️ **Учебный проект. Находится в процессе активной разработки (Work in Progress).**

Микросервис авторизации и аутентификации, построенный на базе **Spring Boot 4** и **Spring Security 7**. Разрабатывается как технический проект для демонстрации современной бэкенд-архитектуры, конфигурации безопасности низкого уровня и управления жизненным циклом базы данных.

---

## 🛠 Технологический стек и архитектура

* **Ядро:** Java 17 + Spring Boot 4.0.6
* **Безопасность:** Spring Security 7 (Stateless, OAuth2 Resource Server)
* **Криптография:** Асимметричное шифрование **RSA-2048** (Nimbus JOSE + JWT) для подписи и валидации токенов
* **База данных:** PostgreSQL + Spring Data JPA + Spring Data REST
* **Документация API:** Springdoc OpenAPI 3.0.2 (Swagger UI)
* **Сборщик:** Gradle

---

## 🔐 Ключевые фичи (Уже реализовано)

* **Асимметричная подпись JWT:** Токены подписываются приватным ключом (RSA-2048) на стороне этого микросервиса. Это позволит другим микросервисам в будущем валидировать токены самостоятельно, зная только публичный ключ.
* **Чистый Swagger UI:** Интерактивная документация изолирована через настройки сборщика и отображает только кастомные эндпоинты (`/api/v1/**`), скрывая автоматический REST-мусор от Spring Data REST.
* **Автоматическая充填 ролей:** Использование `data.sql` в связке с отложенной инициализацией источника данных для безопасного предзаполнения обязательных ролей (`ROLE_USER`, `ROLE_ADMIN`) при старте.
* **Глобальная обработка исключений:** Централизованный `@ControllerAdvice`, который перехватывает ошибки и отдаёт понятный JSON с правильными HTTP-статусами.

---

## 🚀 Запуск приложения

### Настройки
Перед запуском укажи доступы к своей PostgreSQL в файле `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/имя_твоей_базы
spring.datasource.username=твой_юзернейм
spring.datasource.password=твой_пароль

# Конфигурация Swagger
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.paths-to-match=/api/v1/**
springdoc.packages-to-scan=com.github.bgrebennikov.authenticationbackend.controller