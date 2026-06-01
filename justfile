set shell := ["zsh", "-cu"]

default:
    @just --list

dev:
    set -a; source ../.env; set +a; if [[ -n "${JAVA_HOME:-}" ]]; then export PATH="$JAVA_HOME/bin:$PATH"; fi; if [[ -x ./gradlew ]]; then ./gradlew bootRun --args='--spring.profiles.active=local'; elif command -v gradle >/dev/null; then gradle bootRun --args='--spring.profiles.active=local'; else echo "Gradle wrapper 또는 시스템 gradle이 필요합니다."; exit 127; fi

test:
    set -a; source ../.env; set +a; if [[ -n "${JAVA_HOME:-}" ]]; then export PATH="$JAVA_HOME/bin:$PATH"; fi; if [[ -x ./gradlew ]]; then ./gradlew test; elif command -v gradle >/dev/null; then gradle test; else echo "Gradle wrapper 또는 시스템 gradle이 필요합니다."; exit 127; fi
