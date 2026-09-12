# 🔍 Review Aplikasi Ghusl vs prompt.md — Gap Analysis

Berikut daftar lengkap hal-hal yang **belum sesuai** atau **belum lengkap** dibandingkan dengan spesifikasi di [prompt.md](file:///c:/Users/webhe/Downloads/MSI/Local%20Sites/Ghusl/prompt.md).

---

## 🔴 Masalah Kritis (Fitur utama belum berfungsi)

### 1. Iklan AdMob TIDAK TERPASANG di layar mana pun
**Referensi**: prompt.md §5 — Tabel format iklan & penempatan

Meskipun infrastruktur AdMob sudah dibangun lengkap di [`AdManager.kt`](file:///c:/Users/webhe/Downloads/MSI/Local%20Sites/Ghusl/app/src/main/java/com/niatmandiwajib/ghusl/ads/AdManager.kt) dan [`AdBannerView.kt`](file:///c:/Users/webhe/Downloads/MSI/Local%20Sites/Ghusl/app/src/main/java/com/niatmandiwajib/ghusl/ui/components/AdBannerView.kt), **tidak ada satu pun layar** yang memanggil komponen iklan:

| Format | Status | Detail |
|---|---|---|
| **Adaptive Banner** | ❌ Belum dipasang | Seharusnya ada di: Beranda, list Panduan, Search/FAQ/Riwayat/Bookmark, layar Tanya Ustadz |
| **Native Ad** | ❌ Belum diimplementasikan | Composable native ad belum dibuat; seharusnya disisipkan tiap 4–5 item di list Panduan & hasil pencarian |
| **Interstitial** | ❌ Belum dipanggil | `showInterstitialIfReady()` tidak dipanggil saat user selesai baca 1 konten Panduan penuh |
| **Rewarded** | ❌ Belum dihubungkan | Belum ada integrasi ke fitur "percepat jawaban Tanya Ustadz" atau "unlock unduhan audio offline" |
| **App Open** | ⚠️ Infrastruktur ada | Observer terpasang via [`AppOpenAdObserver.kt`](file:///c:/Users/webhe/Downloads/MSI/Local%20Sites/Ghusl/app/src/main/java/com/niatmandiwajib/ghusl/ads/AppOpenAdObserver.kt) tapi belum tentu berfungsi karena `requestConsent()` belum dipanggil |

### 2. UMP Consent belum dipanggil
**Referensi**: prompt.md §7 — Privasi & Kepatuhan

`AdManager.requestConsent()` sudah diimplementasikan di [`AdManager.kt:48`](file:///c:/Users/webhe/Downloads/MSI/Local%20Sites/Ghusl/app/src/main/java/com/niatmandiwajib/ghusl/ads/AdManager.kt#L48-L71) tapi **tidak pernah dipanggil** dari `MainActivity` maupun UI mana pun. Ini wajib untuk kepatuhan GDPR/DMA sebelum memuat iklan personalisasi.

### 3. Tema Settings TIDAK berpengaruh ke UI
**Referensi**: prompt.md §2 — light / dark / system theme

[`MainActivity.kt`](file:///c:/Users/webhe/Downloads/MSI/Local%20Sites/Ghusl/app/src/main/java/com/niatmandiwajib/ghusl/MainActivity.kt#L24) memanggil `GhuslTheme { ... }` tanpa mengobservasi `themeMode` dari DataStore. Artinya pilihan tema Light/Dark/System di Settings tidak pernah mengubah tema yang tampil — selalu mengikuti system default.

### 4. Bahasa UI tidak berubah saat dipilih di Settings
**Referensi**: prompt.md §4 & §6 — Pilih bahasa, locale

[`SettingsScreen.kt`](file:///c:/Users/webhe/Downloads/MSI/Local%20Sites/Ghusl/app/src/main/java/com/niatmandiwajib/ghusl/ui/screens/more/SettingsScreen.kt) hanya menyimpan kode bahasa ke DataStore, tapi **tidak menerapkan** `AppCompatDelegate.setApplicationLocales()` atau `LocaleManager`. String resource bawaan (`R.string.*`) tidak berganti bahasa tanpa restart manual + perubahan locale OS.

---

## 🟡 Masalah Sedang (Fitur hilang atau tidak lengkap)

### 5. Font Noto Nastaliq Urdu BELUM ADA
**Referensi**: prompt.md §2 — Font Urdu (Nastaliq/Naskh)

- Folder `res/font/` tidak ada
- Tidak ada file `.ttf` atau `.otf` di project
- Di [`Type.kt`](file:///c:/Users/webhe/Downloads/MSI/Local%20Sites/Ghusl/app/src/main/java/com/niatmandiwajib/ghusl/ui/theme/Type.kt#L11-L15), font `NotoNastaliqUrdu` masih **di-comment out** dengan keterangan TODO

### 6. Beranda — Deep Link "Lanjutkan Bacaan Terakhir" tidak ada
**Referensi**: prompt.md §3.1 — Dashboard: lanjutkan bacaan terakhir (deep link ke slide terakhir)

[`HomeScreen.kt`](file:///c:/Users/webhe/Downloads/MSI/Local%20Sites/Ghusl/app/src/main/java/com/niatmandiwajib/ghusl/ui/screens/home/HomeScreen.kt) hanya menampilkan 3 kartu navigasi statis (Panduan, Wizard, Tanya Ustadz). Tidak ada card "Lanjutkan bacaan terakhir" yang me-link ke slide terakhir dari `read_history`.

### 7. Menu & Layar FAQ tidak ada
**Referensi**: prompt.md §3.5 — Search (index semua konten Panduan + FAQ), FAQ

Tidak ada menu FAQ di "Lainnya", tidak ada layar FAQ, dan pencarian ([`SearchScreen.kt`](file:///c:/Users/webhe/Downloads/MSI/Local%20Sites/Ghusl/app/src/main/java/com/niatmandiwajib/ghusl/ui/screens/more/SearchScreen.kt)) hanya mengindeks konten Panduan (belum FAQ).

### 8. Riwayat — Belum menampilkan riwayat Tanya Ustadz
**Referensi**: prompt.md §3.5 — Riwayat (riwayat baca + riwayat Tanya Ustadz)

[`HistoryScreen.kt`](file:///c:/Users/webhe/Downloads/MSI/Local%20Sites/Ghusl/app/src/main/java/com/niatmandiwajib/ghusl/ui/screens/more/HistoryScreen.kt) hanya menampilkan riwayat baca Panduan. Riwayat Tanya Ustadz hanya bisa diakses dari layar Tanya Ustadz sendiri, belum terintegrasi di sini.

### 9. Hardcoded Strings Indonesia di AskUstadzScreen
**Referensi**: prompt.md §6 — Resource string terpisah per locale

Di [`AskUstadzScreen.kt`](file:///c:/Users/webhe/Downloads/MSI/Local%20Sites/Ghusl/app/src/main/java/com/niatmandiwajib/ghusl/ui/screens/ustadz/AskUstadzScreen.kt), banyak teks yang masih hardcoded (bukan `stringResource`):
- Baris 38: `"Pertanyaan berhasil dikirim!"` — seharusnya `R.string.ustadz_submitted`
- Baris 55: `"Tulis pertanyaan Anda di sini..."` — seharusnya `R.string.ustadz_input_hint`
- Baris 73: `"Kirim"` — seharusnya `R.string.ustadz_submit`
- Baris 79: Disclaimer Indonesia — seharusnya `R.string.ustadz_disclaimer`
- Baris 89: `"Riwayat Pertanyaan"` — seharusnya localized string
- Baris 159–163: Status badge teks ("Menunggu", "Diproses", "Terjawab", "Gagal") — seharusnya `R.string.ustadz_status_*`

### 10. FCM (Push Notification) belum terintegrasi
**Referensi**: prompt.md §3.4 — jawaban dikirim lewat push notification (FCM)

- Tidak ada dependency Firebase/FCM (`com.google.firebase:firebase-messaging`) di `build.gradle.kts`
- Tidak ada plugin `google-services` maupun file `google-services.json`
- Notifikasi jawaban Tanya Ustadz dikirim sebagai **notifikasi lokal** via `NotificationCompat` oleh WorkManager — bukan FCM push notification seperti yang diminta

### 11. Banner Ad tidak ada di layar Beranda
**Referensi**: prompt.md §5 — Adaptive Banner di Beranda

Tidak ada komponen `AdBannerView` di [`HomeScreen.kt`](file:///c:/Users/webhe/Downloads/MSI/Local%20Sites/Ghusl/app/src/main/java/com/niatmandiwajib/ghusl/ui/screens/home/HomeScreen.kt). Komponen sudah ada tapi belum dipasang.

---

## 🟠 Masalah Minor / Polish

### 12. Caching Offline Audio belum ada
**Referensi**: prompt.md §8 — caching offline untuk konten Panduan (gambar/audio) yang sudah pernah dibuka

- Gambar sudah otomatis di-cache oleh Coil
- **Audio (ExoPlayer)** di-stream langsung tanpa disk cache (`SimpleCache` Media3). Jika offline dan audio belum di-cache HTTP-level, playback gagal.

### 13. Offline Caching Panduan belum robust
**Referensi**: prompt.md §8 — user bisa saja mengakses tanpa koneksi internet

OkHttp cache 10 MB sudah ada, tapi tidak ada mekanisme `max-stale` interceptor atau penyimpanan ke Room untuk full offline-first. Jika cache OkHttp habis/expired dan tidak ada internet, konten panduan menampilkan error.

### 14. Nama app Urdu tidak sesuai di prompt.md
**Referensi**: prompt.md §1 — Urdu: "Ghusl: Ghsal Wajib ki Makmal Faqah"

Di `values-ur/strings.xml`, `app_name` = `"غسل: غسل واجب کی مکمل فقہ"` (script Urdu, bukan transliterasi Latin). Prompt menyebutkan nama transliterasi `"Ghusl: Ghsal Wajib ki Makmal Faqah"` — perlu klarifikasi apakah intent-nya adalah teks Urdu script (sudah benar) atau transliterasi Latin (belum sesuai).

---

## ✅ Yang SUDAH Sesuai

| Aspek | Status |
|---|---|
| Package name `com.niatmandiwajib.ghusl` | ✅ |
| Kotlin + Jetpack Compose | ✅ |
| Bottom nav 5 item (Beranda, Panduan, Cek Kondisi, Tanya Ustadz, Lainnya) | ✅ |
| Panduan fetch dari remote JSON GitHub | ✅ |
| Slide show dengan Arab RTL, Latin, terjemah, gambar, audio, sumber dalil | ✅ |
| Tombol Share per slide | ✅ |
| Bookmark (simpan konten favorit — Room) | ✅ |
| Wizard engine decision tree berbasis JSON config | ✅ |
| Wizard: disclaimer "panduan umum, konsultasikan ke ustadz/ulama" | ✅ |
| Tanya Ustadz: Gemini API direct client, API key hardcoded | ✅ |
| Komentar `TODO fase 2` di kode Gemini (3 lokasi) | ✅ |
| Grounding: system prompt dari ustadz.skill.md | ✅ |
| Mekanisme antrian (WorkManager) + delay 2-4 jam | ✅ |
| Room DB untuk Bookmark, Read History, QnA History | ✅ |
| DataStore untuk bahasa & tema | ✅ |
| Palette warna hijau/biru muda/putih | ✅ |
| Light & Dark color scheme | ✅ |
| `android:supportsRtl="true"` | ✅ |
| Layout pakai `start`/`end` (bukan left/right) | ✅ |
| `Icons.AutoMirrored` untuk panah terarah | ✅ |
| Teks Arab di slide: `textDirection = TextDirection.Rtl` | ✅ |
| AdMob App ID test di Manifest | ✅ |
| Remote config ads_config.json dari GitHub | ✅ |
| Interstitial tidak tampil dari wizard (`isFromWizard`) | ✅ |
| App Open Ad: bukan cold start pertama, ada cooldown | ✅ |
| String resources 4 bahasa (en, id, ms, ur) | ✅ |
| About: versi app + Privacy Policy link + Rate Us + Follow Facebook | ✅ |
| URL Privacy Policy, Rate Us, Facebook sesuai prompt | ✅ |
| Search mengindeks konten Panduan | ✅ |
| Dependencies lengkap (Room, DataStore, Retrofit, Coil, ExoPlayer, Ads, UMP, Gemini, WorkManager) | ✅ |

---

## 📊 Ringkasan

| Kategori | Jumlah |
|---|---|
| 🔴 **Kritis** (fitur utama rusak/belum terhubung) | 4 |
| 🟡 **Sedang** (fitur hilang/tidak lengkap) | 7 |
| 🟠 **Minor** (polish/enhancement) | 3 |
| **Total gap** | **14** |

> [!IMPORTANT]
> Masalah terbesar adalah **infrastruktur AdMob sudah lengkap tapi TIDAK TERPASANG ke layar** sama sekali, dan **UMP Consent tidak pernah dipanggil**. Tanpa ini, aplikasi tidak akan menghasilkan pendapatan iklan dan melanggar kebijakan Google AdMob.

update perbaikan.md untuk item2 yg sudah diperbaiki.
OPUS: Fix #1, #2, #3, #4, #6, #7, #8, #10, #12, #13. Do not modify unrelated features. Preserve all requirements from the original prompt.
FLASH: Fix #5, #9, #11, #14 according to the implementation decisions already made. Do not redesign architecture or change existing behavior.
OPUS: do final review