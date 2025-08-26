# EventClubMod (Fabric 1.21.7)

Коротко: мод добавляет игровой опыт «клуба событий», в т.ч. особую сущность Тени, приветственную записку и команды управления. Требуется Fabric Loader и Fabric API.

## Требования
- Minecraft: 1.21.7
- Fabric Loader
- Fabric API

## Установка (клиент)
1. Установите Fabric Loader (через установщик с fabricmc.net).
2. Скачайте Fabric API (совместимый с 1.21.7) и положите в `%APPDATA%\.minecraft\mods\`.
3. Положите `eventclubmod-<version>.jar` в `%APPDATA%\.minecraft\mods\`.

## Установка (сервер)
- Fabric сервер 1.21.7 + Fabric API в папку `mods/`.
- Клиенты ДОЛЖНЫ иметь Fabric и тот же мод — иначе будет отключение при входе.

## Dev-запуск
- Клиент: `./gradlew.bat runClient`
- Сервер: `./gradlew.bat runServer`

## Сборка
```powershell
./gradlew.bat build
```
Итоговый JAR: `build\libs\eventclubmod-<version>.jar`

> Примечание: версию мода (`mod_version`) меняем только по договорённости перед релизом.

## Команды (пример)
- `/eventclub note on|off` — включает/выключает выдачу приветственной записки (доступ по списку операторов в моде).
- `/eventclub shadow on|off` — включает/выключает появление Тени.

## Packwiz (автообновление модпака)
1) У себя (автор):
- Установите packwiz CLI.
- Инициализируйте пакет:
  ```powershell
  packwiz init --name "EventClub" --author "Razavik" --mc-version 1.21.7 --modloader fabric
  packwiz modrinth add P7dR8mSH   # Fabric API
  packwiz url add --name "eventclubmod" --url "<URL_на_JAR_из_GitHub_Releases>"
  packwiz refresh
  ```
- Опубликуйте `pack.toml` и `index.toml` по постоянному URL (рекомендуется GitHub Pages: положите файлы в папку `docs/`).

2) Для игроков (bootstrap):
- Скачайте `packwiz-installer-bootstrap.jar` (Modrinth или GitHub) и положите в `%APPDATA%\.minecraft\mods\`.
- В профиле Fabric добавьте JVM-аргумент:
  ```
  -Dpackwiz.bootstrap.URL=https://<user>.github.io/<repo>/pack.toml
  ```
- При каждом запуске лаунчера моды подтянутся/обновятся автоматически.

## Публикация (Modrinth/CurseForge)
- Мета: Minecraft 1.21.7, Loader: Fabric, зависимость: Fabric API (Required).
- Залить релизный JAR, добавить описание, иконку 256×256, LICENSE.

## Лицензия
- Код: MIT (см. `LICENSE`).
- Ассеты (текстуры/звуки/иконки): по умолчанию как у кода. Если хотите — можно выделить в отдельную лицензию (CC BY или CC BY-NC-SA).
