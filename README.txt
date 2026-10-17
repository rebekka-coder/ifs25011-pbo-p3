Praktikum Java Exception & Generic - Clean Architecture
Folder proyek: ifs18005-java-oop (ganti "ifs18005" dengan username Anda -> {username}-java-oop)

Studi kasus: catatan-keuangan, buku-tamu, inventaris-barang, jadwal-kegiatan, kontak-teman

Modul bersama:
  shared/src/domain/repository/IRepository.java       kontrak repository generik
  shared/src/adapter/repository/InMemoryRepository.java  basis in-memory generik
  (dipakai oleh kelima studi kasus, tidak lagi di-copy-paste)

Struktur tiap studi kasus:
  src/domain/{entity,exception,repository}   aturan & model inti (tanpa I/O)
  src/usecase                                logika bisnis, melempar checked DomainException
  src/adapter/{repository,presenter}         InMemoryRepository<T, ID> generik + presenter output
  src/framework/{view,util}                  UI konsol (try-catch) + InputUtil
  src/App.java                               Composition Root

Compile & jalankan (dari dalam tiap folder studi kasus, mis. catatan-keuangan/):

  Linux/macOS/Git Bash:
    javac -d output -sourcepath src:../shared/src src/App.java
  Windows (CMD/PowerShell):
    javac -d output -sourcepath "src;../shared/src" src/App.java

  java -cp output App

Uji dengan test case (Linux/macOS/Git Bash):

  java -cp output App < test-cases/TC-01.tc

Windows PowerShell:

  Get-Content test-cases/TC-01.tc | java -cp output App
