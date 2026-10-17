# ifs25011-pbo-p3

Praktikum Java OOP: Exception, Generic, dan Clean Architecture.
Studi kasus (masing-masing folder berdiri sendiri): `buku-tamu`, `catatan-keuangan`,
`inventaris-barang`, `jadwal-kegiatan`, `kontak-teman`.

## Struktur

```
shared/src/                 kode bersama (tidak di-copy ke tiap studi kasus)
  domain/exception/         DomainException, ValidationException, EntityNotFoundException
  domain/repository/        IRepository<T, ID>
  adapter/repository/       InMemoryRepository<T, ID> (penyimpanan list + ID otomatis)
  adapter/presenter/        BasePresenter<T> (cetak daftar, pesan umum)
  framework/util/           InputUtil (baca keyboard, CANCEL_INPUT)
  usecase/                  InputValidator (parsing & validasi input mentah)

<studi-kasus>/src/
  domain/entity/            entity & SortOption
  domain/repository/        port repository khusus entity
  usecase/                  logika bisnis, parsing & validasi, melempar DomainException
  adapter/repository/       implementasi repository (extends InMemoryRepository)
  adapter/presenter/        format output (extends BasePresenter)
  framework/view/           UI konsol: baca input, panggil use case, tangkap exception
  App.java                  composition root (satu-satunya yang tahu kelas konkret)
<studi-kasus>/test-cases/   input uji TC-xx.tc
```

Aturan dependensi: `framework -> adapter -> usecase -> domain`. View hanya meneruskan
`String` mentah ke use case; parsing/validasi ada di use case (`InputValidator`) dan
kegagalan dilaporkan sebagai `ValidationException` yang ditampilkan lewat presenter.

## Compile & jalankan

Dari dalam folder studi kasus, mis. `kontak-teman/`:

```
# Linux/macOS/Git Bash
javac -d output -sourcepath src:../shared/src src/App.java
# Windows (CMD/PowerShell)
javac -d output -sourcepath "src;../shared/src" src/App.java

java -cp output App
java -cp output App < test-cases/TC-01.tc
```
