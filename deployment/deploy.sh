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

# 2. Остановка старого приложения (если запущено)
echo -e "${YELLOW}[2/5] Остановка старого приложения...${NC}"
ssh $SSH_HOST "
    # Убить процесс по имени JAR файла
    pkill -f '$APP_NAME' || true
    
    # Убить все Java процессы пользователя (осторожно!)
    pkill -9 -u \$USER java || true
    
    # Подождать завершения процессов
    sleep 2
    
    # Проверить, остались ли Java процессы
    if pgrep -u \$USER java > /dev/null; then
        echo 'Предупреждение: некоторые Java процессы все еще работают'
        pgrep -u \$USER -a java
    else
        echo 'Все Java процессы успешно остановлены'
    fi
"
echo -e "${GREEN}✓ Старое приложение остановлено${NC}\n"

# 3. Очистка диска и подготовка директории
echo -e "${YELLOW}[3/5] Очистка диска и подготовка директории...${NC}"
ssh $SSH_HOST "
    echo 'Очистка временных файлов и логов...'
    
    # Удаление ВСЕХ старых JAR файлов (освобождаем место!)
    rm -f $REMOTE_DIR/*.jar 2>/dev/null || true
    
    # Очистка логов приложения (если есть)
    rm -rf $REMOTE_DIR/logs/* 2>/dev/null || true
    rm -f $REMOTE_DIR/*.log 2>/dev/null || true
    rm -f $REMOTE_DIR/nohup.out 2>/dev/null || true
    
    # Очистка старых конфигов (кроме application-prod.yaml)
    find $REMOTE_DIR -name '*.yaml.bak' -delete 2>/dev/null || true
    find $REMOTE_DIR -name '*.yaml.old' -delete 2>/dev/null || true
    
    # Очистка временных файлов
    rm -rf $REMOTE_DIR/tmp/* 2>/dev/null || true
    rm -rf /tmp/spring-boot-* 2>/dev/null || true
    rm -rf /tmp/tomcat* 2>/dev/null || true
    rm -rf /tmp/hsperfdata_* 2>/dev/null || true
    
    # Очистка кэша Gradle (если есть)
    rm -rf ~/.gradle/caches/modules-2/files-2.1/*/papersplease* 2>/dev/null || true
    rm -rf ~/.gradle/caches/jars-* 2>/dev/null || true
    
    # Создать директорию если не существует
    mkdir -p $REMOTE_DIR 2>/dev/null || true
    
    # Показать использование диска
    echo ''
    echo 'Освобождено место. Использование диска:'
    df -h ~ 2>/dev/null || du -sh ~ 2>/dev/null || echo 'Не удалось получить информацию о диске'
    
    echo 'Очистка завершена'
"
echo -e "${GREEN}✓ Диск очищен, директория подготовлена${NC}\n"

# 4. Загрузка файлов на сервер
echo -e "${YELLOW}[4/5] Загрузка файлов на сервер...${NC}"
scp build/libs/$APP_NAME $SSH_HOST:$REMOTE_DIR/
scp deployment/application-prod.yaml $SSH_HOST:$REMOTE_DIR/
scp deployment/start.sh $SSH_HOST:$REMOTE_DIR/
ssh $SSH_HOST "chmod +x $REMOTE_DIR/start.sh"
echo -e "${GREEN}✓ Файлы загружены${NC}\n"

# 5. Финальная проверка использования диска
echo -e "${YELLOW}[5/5] Проверка использования диска...${NC}"
ssh $SSH_HOST "
    echo 'Размер директории приложения:'
    du -sh $REMOTE_DIR 2>/dev/null || echo 'Не удалось получить размер'
    echo ''
    echo 'Использование диска:'
    df -h ~ 2>/dev/null || quota 2>/dev/null || echo 'Не удалось получить информацию о квоте'
"
echo -e "${GREEN}✓ Проверка завершена${NC}\n"

echo -e "\n${GREEN}=== Деплой завершен! ===${NC}\n"
echo -e "Для запуска используйте:"
echo -e "${YELLOW}./deployment/start.sh${NC}"


