Modul shared: kode generik yang dipakai semua studi kasus (satu sumber kebenaran).

Package-nya sama dengan package di studi kasus (domain.exception, domain.repository,
adapter.repository, adapter.presenter, usecase, framework.util), jadi import di studi kasus
tidak berbeda dari kode lokal. Cukup tambahkan ../shared/src ke -sourcepath saat compile
(sudah dilakukan oleh build.sh di root repo).

Isi:
  domain/exception/EntityNotFoundException.java, ValidationException.java, DomainException.java
  domain/repository/IRepository.java            port repository generik <T, ID>
  adapter/repository/InMemoryRepository.java    penyimpanan in-memory generik; nextId() protected,
                                                data private, menolak ID kembar
  adapter/presenter/BasePresenter.java          basis presenter generik
  usecase/InputValidator.java                   parseId/parseInt/parseLong, requireText, requireFormat, blankToNull
  usecase/TextMatcher.java                      containsIgnoreCase untuk fitur pencarian
  framework/util/InputUtil.java                 input keyboard + CANCEL_INPUT ("x")

Jangan membuat salinan file-file ini di dalam folder studi kasus: versi ganda akan
menimpa versi shared (karena src dicari lebih dulu) dan bisa menjadi tidak konsisten.
