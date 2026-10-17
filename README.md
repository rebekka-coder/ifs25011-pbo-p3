# ifs25011-pbo-p3

Praktikum Java OOP: Exception, Generic, dan Clean Architecture.

Lima studi kasus aplikasi konsol, masing-masing punya folder sendiri:
`buku-tamu`, `catatan-keuangan`, `inventaris-barang`, `jadwal-kegiatan`, `kontak-teman`.
Kode yang sama di semua studi kasus ditulis **satu kali** di folder `shared/`
(tidak disalin ke tiap studi kasus), sehingga tidak ada versi ganda yang bisa berbeda.

## Struktur

```
shared/src/                       modul bersama (satu sumber kebenaran)
  domain/exception/               DomainException, ValidationException, EntityNotFoundException
  domain/repository/              IRepository<T, ID>            (port repository generik)
  adapter/repository/             InMemoryRepository<T, ID>     (penyimpanan in-memory + nextId())
  adapter/presenter/              BasePresenter<T>              (cetak daftar, pesan umum)
  usecase/                        InputValidator (parsing & validasi), TextMatcher (pencarian)
  framework/util/                 InputUtil (baca keyboard, CANCEL_INPUT)

<studi-kasus>/src/                kode khusus studi kasus
  domain/entity/                  entity & SortOption
  domain/repository/              port repository khusus entity (I<Entity>Repository)
  usecase/                        logika bisnis, parsing & validasi, melempar DomainException
  adapter/repository/             <Entity>Repository (extends InMemoryRepository)
  adapter/presenter/              <Entity>Presenter (extends BasePresenter)
  framework/view/                 UI konsol: baca input, panggil use case, tangkap exception
  App.java                        composition root (satu-satunya yang tahu kelas konkret)
<studi-kasus>/test-cases/         input uji TC-xx.tc
```

Aturan dependensi: `framework -> adapter -> usecase -> domain`. Domain tidak bergantung pada
layer lain. View hanya meneruskan `String` mentah ke use case; parsing dan validasi dilakukan di
use case (memakai `InputValidator`), dan kegagalan dilaporkan sebagai `ValidationException` /
`EntityNotFoundException` yang ditampilkan lewat presenter.

## Compile & jalankan

Modul `shared` dikompilasi bersama studi kasus lewat `-sourcepath`, jadi tidak ada file yang
perlu disalin. Dari folder root repo (Linux/macOS/Git Bash):

```
./build.sh                  # compile semua studi kasus
./build.sh kontak-teman     # compile satu studi kasus
./test-all.sh               # compile + jalankan semua test case (uji asap)

java -cp kontak-teman/output App
java -cp kontak-teman/output App < kontak-teman/test-cases/TC-01.tc
```

Tanpa skrip, dari dalam folder studi kasus, mis. `kontak-teman/`:

```
# Linux/macOS/Git Bash
javac -d output -sourcepath src:../shared/src src/App.java
# Windows (CMD/PowerShell)
javac -d output -sourcepath "src;../shared/src" src/App.java

java -cp output App
# Windows PowerShell untuk test case:
Get-Content test-cases/TC-01.tc | java -cp output App
```

Butuh JDK 17+ (memakai `switch` berpanah dan `Stream.toList()`).

## Aturan validasi

| Studi kasus | Aturan |
|---|---|
| buku-tamu | nama dan tujuan kunjungan tidak boleh kosong |
| catatan-keuangan | keterangan tidak boleh kosong; jumlah harus angka > 0 |
| inventaris-barang | nama/kategori tidak boleh kosong; jumlah stok harus angka > 0; update stok kosong = tidak diubah |
| jadwal-kegiatan | judul/hari/waktu tidak boleh kosong; waktu berformat `HH:mm`; update: field kosong = tidak diubah |
| kontak-teman | nama/telepon/email tidak boleh kosong; telepon 8-15 digit (boleh diawali `+`); email berformat `nama@domain.tld`; update: field kosong = tidak diubah, **minimal satu field harus diisi** |

Semua studi kasus: ID harus angka; ID yang tidak ditemukan dilaporkan sebagai kegagalan;
spasi di awal/akhir input dibuang sebelum disimpan.

## Keputusan desain

- **ID otomatis**: `InMemoryRepository.nextId()` menaikkan ID mulai dari 1 dan tidak memakai ulang
  ID yang sudah dihapus. `save()` menolak ID kembar dengan `IllegalArgumentException`.
- **Data repository private**: subclass hanya memakai method `IRepository` dan `nextId()`.
- **Update parsial atomik**: semua field divalidasi dulu sebelum ada yang diubah, sehingga
  data tidak pernah berubah sebagian saat salah satu field tidak valid.
