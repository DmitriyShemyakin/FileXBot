# FileXBot - Микросервисная платформа для Telegram-бота 🚀

[![Архитектура системы](https://img.youtube.com/vi/Qps-b6QRs0o/0.jpg)](https://www.youtube.com/watch?v=Qps-b6QRs0o&list=PLV_4DSIw2vvI3_a6L_z5AlNaIdFNqQlW2)

Проект представляет собой распределенную систему Telegram-бота с микросервисной архитектурой на Java Spring, использующую RabbitMQ для асинхронной обработки сообщений.

## Модули проекта

### Основные микросервисы
| Модуль                          | Технологии                     | Назначение                                                                 |
|---------------------------------|--------------------------------|----------------------------------------------------------------------------|
| **[filex-bot-dispatcher](https://github.com/DmitriyShemyakin/FileXBot/tree/main/filex-bot-dispatcher)** | Spring Boot, Telegram API      | Приём сообщений, первичная валидация, взаимодействие с RabbitMQ            |
| **[filex-bot-file-service](https://github.com/DmitriyShemyakin/FileXBot/tree/main/filex-bot-file-service)** | Spring Boot           | Управление файловыми операциями (загрузка, хранение, обработка)           |
| **[filex-bot-user-service](https://github.com/DmitriyShemyakin/FileXBot/tree/main/filex-bot-user-service)** | Spring Boot, Spring Data JPA   | Управление пользователями и аутентификацией                               |
| **[filex-bot-registration-service](https://github.com/DmitriyShemyakin/FileXBot/tree/main/filex-bot-registration-service)** | Spring Boot, REST              | Обработка регистрации, подтверждение через email                          |
| **[filex-bot-email-service](https://github.com/DmitriyShemyakin/FileXBot/tree/main/filex-bot-email-service)** | Spring Boot, JavaMailSender    | Асинхронная отправка email-уведомлений                                    |

### Вспомогательные модули
| Модуль                          | Назначение                                                                 |
|---------------------------------|----------------------------------------------------------------------------|
| **[filex-bot-commons](https://github.com/DmitriyShemyakin/FileXBot/tree/main/filex-bot-commons)** | Общие DTO, утилиты и конфигурации для всех микросервисов                   |
| **[filex-bot-config-server](https://github.com/DmitriyShemyakin/FileXBot/tree/main/filex-bot-config-server)** | Centralized configuration management для всех сервисов                     |

## Технологический стек
- **Язык**: Java 17
- **Фреймворки**: Spring Boot 3.x, Spring AMQP, Spring Data
- **Брокер сообщений**: RabbitMQ
- **Базы данных**: PostgreSQL
- **API**: REST, Telegram Bot API

## Особенности реализации
- Горизонтальное масштабирование: Каждый микросервис может масштабироваться независимо
- Асинхронная обработка: RabbitMQ гарантирует доставку сообщений при высокой нагрузке
- Подтверждение регистрации: REST API для верификации email через ссылку

## Запуск проекта
_git clone https://github.com/DmitriyShemyakin/FileXBot.git
cd FileXBot_

## 📡 Архитектура системы
```mermaid
graph TD
    A[Пользователь Telegram] --> B[filex-bot-dispatcher]
    B --> C[RabbitMQ]
    C --> D[filex-bot-file-service]
    C --> E[filex-bot-user-service]
    C --> F[filex-bot-registration-service]
    F --> G[filex-bot-email-service]
    D --> H[(PostgreSQL)]
    E --> I[(PostgreSQL)]
    G --> J[SMTP Server]
    
    style B fill:#4CAF50,stroke:#388E3C
    style C fill:#FFC107,stroke:#FFA000
    style D fill:#2196F3,stroke:#1976D2
    style E fill:#2196F3,stroke:#1976D2
    style F fill:#2196F3,stroke:#1976D2
    style G fill:#2196F3,stroke:#1976D2
