#!/bin/bash

# Скрипт для запуска приложения с пробросом порта
# Использование: ./start.sh

set -e

# Параметры
SSH_HOST="ifmo"
REMOTE_DIR="~/papersplease"
REMOTE_PORT="23561"
LOCAL_PORT="8080"

echo "=== Запуск Papers Please ==="
echo ""
echo "Приложение будет доступно на: http://localhost:$LOCAL_PORT"
echo "API документация: http://localhost:$LOCAL_PORT/v3/api-docs"
echo ""
echo "Нажмите Ctrl+C для остановки"
echo ""

# Функция для очистки при выходе
cleanup() {
    echo ""
    echo "Остановка приложения на сервере..."
    ssh $SSH_HOST "pkill -f 'papersplease-0.0.1-SNAPSHOT.jar' || true"
    echo "Готово!"
    exit 0
}

trap cleanup INT TERM

# Проверка, не занят ли порт
if lsof -Pi :$LOCAL_PORT -sTCP:LISTEN -t >/dev/null 2>&1 ; then
    echo "⚠️  Порт $LOCAL_PORT уже занят!"
    echo "Освободите порт или измените LOCAL_PORT в скрипте"
    exit 1
fi

# Запуск приложения на сервере в фоне и проброс порта
ssh -L $LOCAL_PORT:localhost:$REMOTE_PORT $SSH_HOST \
    "cd $REMOTE_DIR && java -Xmx512m -Xms256m -jar papersplease-0.0.1-SNAPSHOT.jar \
    --spring.profiles.active=prod \
    --spring.config.location=file:./application-prod.yaml"


