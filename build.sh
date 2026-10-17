#!/usr/bin/env bash
# Compile satu atau semua studi kasus. Modul shared/src selalu ikut lewat -sourcepath,
# jadi tidak perlu menyalin BasePresenter, InputValidator, InMemoryRepository, dll. ke tiap proyek.
#
# Pemakaian (dari folder root repo):
#   ./build.sh                  # compile semua studi kasus
#   ./build.sh kontak-teman     # compile satu studi kasus
# Hasil compile ada di <studi-kasus>/output/ (tidak ikut di-commit).
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ALL_PROJECTS=(buku-tamu catatan-keuangan inventaris-barang jadwal-kegiatan kontak-teman)

# Pemisah classpath: ";" di Windows (Git Bash), ":" di Linux/macOS
case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*) SEP=";" ;;
    *) SEP=":" ;;
esac

build_project() {
    local project="$1"
    local dir="$ROOT/$project"
    [[ -d "$dir/src" ]] || { echo "Studi kasus tidak ditemukan: $project" >&2; return 1; }

    rm -rf "$dir/output"
    mkdir -p "$dir/output"
    javac -Xlint:all -d "$dir/output" -sourcepath "$dir/src${SEP}$ROOT/shared/src" "$dir/src/App.java"
    echo "OK   $project"
}

projects=("$@")
[[ ${#projects[@]} -gt 0 ]] || projects=("${ALL_PROJECTS[@]}")

for project in "${projects[@]}"; do
    build_project "$project"
done
