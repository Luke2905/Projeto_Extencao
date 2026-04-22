#!/usr/bin/env bash

set -euo pipefail

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
JAVA_BIN="${JAVA_BIN:-/usr/bin/java}"
JAVAC_BIN="${JAVAC_BIN:-/usr/bin/javac}"
MYSQL_JAR="lib/mysql-connector-j-9.6.0.jar"

cd "$BASE_DIR"

mkdir -p bin

"$JAVAC_BIN" -encoding UTF-8 -cp "$MYSQL_JAR" -d bin src/projetoextensao/*.java

# O VS Code instalado via snap pode injetar bibliotecas incompatíveis no Java.
# Para evitar isso, executamos a aplicação em um ambiente mínimo e limpo.
exec env -i \
  HOME="${HOME:-}" \
  USER="${USER:-}" \
  LOGNAME="${LOGNAME:-}" \
  PATH="/usr/local/bin:/usr/bin:/bin" \
  DISPLAY="${DISPLAY:-}" \
  XAUTHORITY="${XAUTHORITY:-}" \
  XDG_RUNTIME_DIR="${XDG_RUNTIME_DIR:-}" \
  DBUS_SESSION_BUS_ADDRESS="${DBUS_SESSION_BUS_ADDRESS:-}" \
  LANG="${LANG:-C.UTF-8}" \
  LC_ALL="${LC_ALL:-}" \
  SHELL="/bin/bash" \
  "$JAVA_BIN" -cp "bin:lib/*" projetoextensao.Principal
