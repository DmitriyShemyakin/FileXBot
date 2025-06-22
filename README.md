# FileXBot - Микросервисная платформа для Telegram-бота

Проект представляет собой распределенную систему Telegram-бота с микросервисной архитектурой на Java Spring, использующую RabbitMQ для асинхронной обработки сообщений.

## Модули проекта

### Основные микросервисы
| Модуль                          | Технологии                     | Назначение                                                                 |
|---------------------------------|--------------------------------|----------------------------------------------------------------------------|
| **[dispatcher](https://github.com/DmitriyShemyakin/FileXBot/tree/master/dispatcher)** | Spring Boot, Telegram API      | Приём сообщений, первичная валидация, взаимодействие с RabbitMQ            |
| **[node](https://github.com/DmitriyShemyakin/FileXBot/tree/master/node)** | Spring Boot, Spring Data       | Обработка бизнес-логики, работа с файлами и пользователями                |
| **[rest-service](https://github.com/DmitriyShemyakin/FileXBot/tree/master/rest-service)** | Spring Boot, REST API          | Обработка HTTP-запросов, подтверждение регистрации                        |
| **[mails-service](https://github.com/DmitriyShemyakin/FileXBot/tree/master/mails-service)** | Spring Boot, JavaMailSender    | Асинхронная отправка email-уведомлений                                    |

### Общие библиотеки
| Модуль                          | Назначение                                                                 |
|---------------------------------|----------------------------------------------------------------------------|
| **[common-jpa](https://github.com/DmitriyShemyakin/FileXBot/tree/master/common-jpa)** | Общие сущности и репозитории JPA для всех сервисов                        |
| **[common-utils](https://github.com/DmitriyShemyakin/FileXBot/tree/master/common-utils)** | Утилитарные классы и вспомогательные функции                             |
| **[comon-rabbitmq](https://github.com/DmitriyShemyakin/FileXBot/tree/master/comon-rabbitmq)** | Конфигурация RabbitMQ, DTO для обмена сообщениями                         |

## Технологический стек
- **Язык**: Java 17
- **Фреймворки**: Spring Boot 3.x, Spring AMQP, Spring Data JPA
- **Брокер сообщений**: RabbitMQ
- **Базы данных**: PostgreSQL
- **API**: REST, Telegram Bot API
- **Рассылка email**: JavaMailSender с SMTP

## Архитектура системы
```mermaid
graph TD
    A[Пользователь Telegram] --> B[dispatcher]
    B --> C[RabbitMQ]
    C --> D[node]
    D --> E[rest-service]
    D --> F[mails-service]
    E --> G[Подтверждение регистрации]
    F --> H[Email рассылка]
    
    style B fill:#4CAF50,stroke:#388E3C
    style C fill:#FF8307,stroke:#FFA000
    style D fill:#2196F3,stroke:#1976D2
    style E fill:#9C27B0,stroke:#7B1FA2
    style F fill:#FF5722,stroke:#E64A19
