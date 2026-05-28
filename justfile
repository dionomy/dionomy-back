set shell := ["zsh", "-cu"]
java_home := `asdf where java`

default:
    @just --list

dev:
    if [[ -x ./gradlew ]]; then JAVA_HOME="{{java_home}}" PATH="{{java_home}}/bin:$PATH" ./gradlew bootRun --args='--spring.profiles.active=local'; elif command -v gradle >/dev/null; then JAVA_HOME="{{java_home}}" PATH="{{java_home}}/bin:$PATH" gradle bootRun --args='--spring.profiles.active=local'; else echo "Gradle wrapper 또는 시스템 gradle이 필요합니다."; exit 127; fi

test:
    if [[ -x ./gradlew ]]; then JAVA_HOME="{{java_home}}" PATH="{{java_home}}/bin:$PATH" ./gradlew test; elif command -v gradle >/dev/null; then JAVA_HOME="{{java_home}}" PATH="{{java_home}}/bin:$PATH" gradle test; else echo "Gradle wrapper 또는 시스템 gradle이 필요합니다."; exit 127; fi
