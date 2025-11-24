# Инструкция по развертыванию на сервере без Docker

## Требования

- Java 17 или выше
- Доступ к PostgreSQL базе данных
- Минимум 512 МБ RAM (рекомендуется 1 ГБ)
- Настроенное SSH подключение к серверу (алиас `ifmo` в ~/.ssh/config)

## 🚀 Быстрый деплой (автоматический)

Самый простой способ развернуть приложение:

```bash
# Сделайте скрипт исполняемым (только первый раз)
chmod +x deployment/deploy.sh deployment/connect.sh deployment/logs.sh deployment/remote-stop.sh

# Запустите автоматический деплой
./deployment/deploy.sh
```

Скрипт автоматически:
1. Соберет JAR файл
2. Создаст необходимые директории на сервере
3. Остановит старую версию приложения (если запущена)
4. Загрузит файлы на сервер
5. Запустит приложение

После деплоя для проброса порта и просмотра приложения локально:

```bash
# Проброс порта 23561 с сервера на локальный 8080
./deployment/connect.sh
```

Приложение будет доступно по адресу: http://localhost:8080

Другие полезные команды:

```bash
# Просмотр логов
./deployment/logs.sh          # показать последние записи
./deployment/logs.sh console  # следить за console.log
./deployment/logs.sh app      # следить за papersplease.log

# Остановка приложения на сервере
./deployment/remote-stop.sh
```

## 📝 Пошаговая инструкция (ручной способ)

### 1. Сборка приложения локально

На вашей локальной машине выполните:

```bash
./gradlew bootJar
```

Это создаст файл: `build/libs/papersplease-0.0.1-SNAPSHOT.jar`

### 2. Подготовка файлов для загрузки на сервер

Необходимо загрузить на сервер следующие файлы:
- `build/libs/papersplease-0.0.1-SNAPSHOT.jar` - основное приложение
- `deployment/application-prod.yaml` - конфигурация для продакшена
- `deployment/start.sh` - скрипт запуска
- `deployment/start-background.sh` - скрипт запуска в фоне
- `deployment/stop.sh` - скрипт остановки
- `deployment/.env.example` - пример файла переменных окружения

### 3. Загрузка на сервер

Создайте директорию на сервере и загрузите файлы:

```bash
# На локальной машине
scp build/libs/papersplease-0.0.1-SNAPSHOT.jar user@server:/path/to/app/
scp deployment/* user@server:/path/to/app/
```

### 4. Настройка на сервере

Подключитесь к серверу:

```bash
ssh user@server
cd /path/to/app
```

Создайте `.env` файл из примера:

```bash
cp .env.example .env
nano .env  # или vim .env
```

Заполните файл `.env` данными вашей БД:

```bash
DB_URL=jdbc:postgresql://pg:5432/studs
DB_USERNAME=s409449
DB_PASSWORD=7ChhsEMmi316rp3x
JWT_SECRET=$(openssl rand -base64 32)
GOD_SECRET_KEY=$(openssl rand -base64 32)
SERVER_PORT=8080
```

Сделайте скрипты исполняемыми:

```bash
chmod +x start.sh start-background.sh stop.sh
```

### 5. Проверка Java

Убедитесь, что установлена Java 17+:

```bash
java -version
```

Если Java не установлена или версия < 17:

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjdk-17-jre
```

**CentOS/RHEL:**
```bash
sudo yum install java-17-openjdk
```

### 6. Применение миграций базы данных

Если у вас есть Flyway миграции (V003__create_functions.sql и другие), их нужно применить к БД:

```bash
# Подключитесь к БД и выполните миграции вручную, или
# запустите приложение один раз - Hibernate применит изменения
```

### 7. Запуск приложения

**Вариант А: Запуск в текущей сессии (для тестирования)**

```bash
./start.sh
```

Приложение запустится и будет выводить логи в консоль. Нажмите Ctrl+C для остановки.

**Вариант Б: Запуск в фоновом режиме**

```bash
./start-background.sh
```

Приложение запустится в фоне. Проверить статус:

```bash
# Проверить, что процесс работает
cat app.pid
ps aux | grep java

# Проверить логи
tail -f logs/console.log
tail -f logs/papersplease.log
```

### 8. Остановка приложения

```bash
./stop.sh
```

### 9. Проверка работы

Проверьте, что сервер доступен:

```bash
# На сервере
curl http://localhost:23561/v3/api-docs

# Или пробросьте порт и проверьте локально
ssh -L 8080:localhost:23561 ifmo
# В другом терминале:
curl http://localhost:8080/v3/api-docs
```

## Автозапуск при перезагрузке сервера (systemd)

Если вы хотите, чтобы приложение автоматически запускалось при перезагрузке сервера, создайте systemd service:

```bash
sudo nano /etc/systemd/system/papersplease.service
```

Содержимое файла:

```ini
[Unit]
Description=Papers Please Application
After=network.target postgresql.service

[Service]
Type=simple
User=YOUR_USERNAME
WorkingDirectory=/path/to/app
EnvironmentFile=/path/to/app/.env
ExecStart=/usr/bin/java -jar /path/to/app/papersplease-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod --spring.config.location=classpath:/application.yaml,file:/path/to/app/application-prod.yaml
Restart=on-failure
RestartSec=10
StandardOutput=append:/path/to/app/logs/console.log
StandardError=append:/path/to/app/logs/console.log

[Install]
WantedBy=multi-user.target
```

Активируйте сервис:

```bash
sudo systemctl daemon-reload
sudo systemctl enable papersplease
sudo systemctl start papersplease
sudo systemctl status papersplease
```

## Устранение неполадок

### Приложение не запускается

1. Проверьте логи: `tail -f logs/console.log`
2. Проверьте, что порт не занят: `netstat -tlnp | grep 8080`
3. Проверьте подключение к БД: `psql -h pg -p 5432 -U s409449 -d studs`

### Ошибка подключения к БД

Убедитесь, что:
- Сервер может подключиться к PostgreSQL
- Правильные учетные данные в `.env`
- База данных существует

### Не хватает памяти

Ограничьте память для Java:

```bash
java -Xmx512m -jar papersplease-0.0.1-SNAPSHOT.jar ...
```

## Обновление приложения

1. Остановите приложение: `./stop.sh`
2. Сделайте backup JAR: `cp papersplease-0.0.1-SNAPSHOT.jar papersplease-backup.jar`
3. Загрузите новый JAR с локальной машины
4. Запустите: `./start-background.sh`

