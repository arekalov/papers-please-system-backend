#!/usr/bin/env bash

# Скрипт для автоматического деплоя на сервер IFMO
# Использование: ./deploy.sh

set -e  # Остановиться при ошибке

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== Автоматический деплой Papers Please ===${NC}\n"

# Определение корневой директории проекта
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$( cd "$SCRIPT_DIR/.." && pwd )"

echo -e "Корневая директория проекта: ${PROJECT_ROOT}"
cd "$PROJECT_ROOT"

# Параметры
SSH_HOST="ifmo"
REMOTE_DIR="~/papersplease"
APP_NAME="papersplease-0.0.1-SNAPSHOT.jar"

# 1. Сборка JAR файла
echo -e "${YELLOW}[1/4] Сборка JAR файла...${NC}"
./gradlew clean bootJar
if [ $? -ne 0 ]; then
    echo -e "${RED}Ошибка при сборке JAR файла${NC}"
    exit 1
fi
echo -e "${GREEN}✓ JAR файл собран${NC}\n"

# 2. Удаление старой директории и создание новой
echo -e "${YELLOW}[2/4] Подготовка директории на сервере...${NC}"
ssh $SSH_HOST "rm -rf $REMOTE_DIR && mkdir -p $REMOTE_DIR"
echo -e "${GREEN}✓ Директория подготовлена${NC}\n"

# 3. Остановка старого приложения (если запущено)
echo -e "${YELLOW}[3/4] Остановка старого приложения...${NC}"
ssh $SSH_HOST "pkill -f '$APP_NAME' || true"
sleep 2
echo -e "${GREEN}✓ Старое приложение остановлено${NC}\n"

# 4. Загрузка файлов на сервер
echo -e "${YELLOW}[4/4] Загрузка файлов на сервер...${NC}"
scp build/libs/$APP_NAME $SSH_HOST:$REMOTE_DIR/
scp deployment/application-prod.yaml $SSH_HOST:$REMOTE_DIR/
scp deployment/start.sh $SSH_HOST:$REMOTE_DIR/
ssh $SSH_HOST "chmod +x $REMOTE_DIR/start.sh"
echo -e "${GREEN}✓ Файлы загружены${NC}\n"

echo -e "\n${GREEN}=== Деплой завершен! ===${NC}\n"
echo -e "Для запуска используйте:"
echo -e "${YELLOW}./deployment/start.sh${NC}"


