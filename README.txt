Praktikum Java Exception & Generic - Clean Architecture (ifs25011-pbo-p3)

Studi kasus: catatan-keuangan, buku-tamu, inventaris-barang, jadwal-kegiatan, kontak-teman
Dokumentasi lengkap (struktur, aturan validasi, keputusan desain): lihat README.md

Modul bersama (satu sumber kebenaran, tidak disalin ke tiap studi kasus):
  shared/src/domain/exception/*                          DomainException, ValidationException, EntityNotFoundException
  shared/src/domain/repository/IRepository.java          kontrak repository generik
  shared/src/adapter/repository/InMemoryRepository.java  basis in-memory generik (nextId(), data private)
  shared/src/adapter/presenter/BasePresenter.java        basis presenter generik
  shared/src/usecase/InputValidator.java                 parsing & validasi input mentah
  shared/src/usecase/TextMatcher.java                    pencocokan teks untuk pencarian
  shared/src/framework/util/InputUtil.java               baca keyboard, CANCEL_INPUT

Struktur tiap studi kasus:
  src/domain/{entity,repository}   aturan & model inti (tanpa I/O)
  src/usecase                      logika bisnis, melempar checked DomainException
  src/adapter/{repository,presenter}  repository (extends InMemoryRepository) + presenter (extends BasePresenter)
  src/framework/view               UI konsol (try-catch)
  src/App.java                     Composition Root

Compile & jalankan (dari folder root repo; Linux/macOS/Git Bash):

  ./build.sh                  compile semua studi kasus (shared ikut otomatis)
  ./test-all.sh               compile + jalankan semua test case
  java -cp kontak-teman/output App < kontak-teman/test-cases/TC-01.tc

Tanpa skrip (dari dalam folder studi kasus, mis. kontak-teman/):

  Linux/macOS/Git Bash:
    javac -d output -sourcepath src:../shared/src src/App.java
  Windows (CMD/PowerShell):
    javac -d output -sourcepath "src;../shared/src" src/App.java

  java -cp output App

Windows PowerShell, uji dengan test case:

  Get-Content test-cases/TC-01.tc | java -cp output App
