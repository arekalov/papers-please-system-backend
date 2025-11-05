# Papers Please API

API для системы Papers Please.

## 📖 Документация API

Swagger UI документация автоматически публикуется на GitHub Pages при каждом изменении `api.yaml`.

**URL документации:** `https://<ваш-username>.github.io/PapersPlease/`

## 🚀 Настройка GitHub Pages

### Шаг 1: Включите GitHub Pages в репозитории

1. Откройте репозиторий на GitHub
2. Перейдите в **Settings** → **Pages**
3. В разделе **Source** выберите **GitHub Actions**

### Шаг 2: Запуск деплоя

После включения GitHub Pages:

1. Сделайте коммит и push изменений в ветку `master` или `main`
2. GitHub Action автоматически запустится
3. Через ~2-3 минуты документация будет доступна по адресу: `https://<username>.github.io/PapersPlease/`

### Ручной запуск

Вы также можете запустить деплой вручную:

1. Перейдите в **Actions** → **Deploy Swagger UI to GitHub Pages**
2. Нажмите **Run workflow**

## 📝 Локальная разработка

Для локального просмотра API документации используйте Swagger Editor:

```bash
# Вариант 1: Swagger Editor онлайн
# Откройте https://editor.swagger.io/
# Скопируйте содержимое api.yaml

# Вариант 2: Локальный Swagger UI с Docker
docker run -p 8080:8080 -e SWAGGER_JSON=/api.yaml -v $(pwd):/api swaggerapi/swagger-ui

# Откройте http://localhost:8080
```

## 🔧 Структура проекта

```
.
├── api.yaml                    # OpenAPI спецификация
├── .github/
│   └── workflows/
│       └── deploy-swagger.yml  # GitHub Action для деплоя
└── README.md
```

## 📚 Полезные ссылки

- [OpenAPI Specification](https://swagger.io/specification/)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)
- [GitHub Pages](https://pages.github.com/)

## 👤 Автор

- **arekalov**
- Email: artyom.rekalov@gmail.com
- GitHub: [@arekalov](https://github.com/arekalov)

## 📄 Лицензия

MIT License
