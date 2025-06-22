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
    A[Пользователь] -->|Файл| B[dispatcher]
    B -->|Событие через RabbitMQ| C[node]
    C --> D[(PostgreSQL)]
    C --> E[Файловое хранилище]
    D -->|Данные для email| F[mails-service]
    D -->|Данные для верификации| G[rest-service]
    
    style C fill:#2196F3,stroke:#1976D2
    style D fill:#009688,stroke:#00796B
