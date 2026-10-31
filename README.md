# Ghusl: Islamic Guide to Ritual Bath (Fiqih Mandi Wajib)

Aplikasi Android panduan komprehensif tata cara dan fiqih mandi wajib (ghusl) bersumber dari dalil Al-Qur'an dan Sunnah yang shahih. Dilengkapi dengan panduan interaktif bertahap, decision tree condition check, tanya jawab AI berbasis dalil, audio bacaan, dan pengingat sunnah.

---

## Ringkasan Project & Konfigurasi

| Parameter | Konfigurasi |
|---|---|
| **Application ID / Package ID** | `com.niatmandiwajib.ghusl` |
| **App Name** | `Ghusl: Islamic Guide to Ritual Bath` (Default) / `Ghusl: Fiqih Mandi Wajib Lengkap` (`values-in`) |
| **Minimum SDK** | 24 (Android 7.0 Nougat) |
| **Target / Compile SDK** | 34 (Android 14) |
| **Arsitektur** | Single-Activity Jetpack Compose, Material 3, ViewModel, Coroutines & Flow |
| **Firebase Project** | `tempat-sambung` (Project Number: `215024871022`) |
| **Google Mobile Ads App ID** | `ca-app-pub-3360913653749022~6889498436` |

---

## Status Instrumentasi Firebase & AdMob

1. **Tahap 1 — Firebase Analytics Dasar & Screen Tracking**:
   - `google_analytics_automatic_screen_reporting_enabled = false` untuk mencegah duplikasi pencatatan screen.
   - Singleton `AnalyticsLogger` sebagai pintu tunggal instrumentasi.
   - Default event parameter: `app_version`.
   - Anti-duplicate manual screen tracking pada seluruh rute Jetpack Compose (`main`, `guide`, `wizard`, `ask_ustadz`, `more`, `slide_show`, `settings`, `search`, `bookmarks`, dll).
2. **Tahap 2 — Firebase Crashlytics**:
   - Crashlytics diaktifkan via Firebase BoM (`33.3.0`).
   - Konfigurasi `mappingFileUploadEnabled = true` pada release build type untuk deobfuscation otomatis crash stack traces.
3. **Tahap 3 — AdMob Monetization Tracking**:
   - Tracking komprehensif pada 5 format iklan: `BANNER`, `INTERSTITIAL`, `NATIVE`, `OPEN_APP`, `REWARDED`.
   - Events: `ad_request`, `ad_impression`, `ad_click`, `ad_failed`.
   - `ad_impression` menggunakan `OnPaidEventListener` resmi sebagai single source of truth dengan konversi mikros ke unit desimal mata uang (`valueMicros / 1_000_000.0`), parameter resmi `value`, `currency`, `ad_source`, dan `ad_format`.
4. **Tahap 4 — Core Feature & Engagement Events**:
   - Universal event: `notification_opened` (`friday_reminder`, `ustadz_answer`, `fcm_push`).
   - Core events: `wizard_completed`, `guide_completed`, `question_submitted`, `niat_copied`, `niat_shared`, `search`, `guide_bookmarked`.
   - Non-PII & privasi terjaga (tidak ada logging teks pertanyaan atau identitas pengguna).
5. **Tahap 5 — User Properties**:
   - Properties: `language`, `country`, `engagement_level` (`new`, `active`, `dormant`).
   - Evaluasi berbasis SharedPreferences terisolasi dengan mekanisme caching untuk menghindari pemanggilan redundant Firebase SDK.
6. **Tahap 6 — Remote Config & Google Play Linking**:
   - Dependency `firebase-config` sudah terkonfigurasi via Firebase BoM.
   - Fallback defaults tersedia di `res/xml/remote_config_defaults.xml`.
   - Dokumentasi tautan Google Play terlampir di bawah ini.

---

## TODO: Manual Firebase-Google Play Linking

> [!IMPORTANT]
> Penautan (linking) antara Firebase Project dan Google Play Developer Console **wajib dilakukan secara manual** oleh Administrator/Owner akun melalui Firebase Console. Tindakan ini tidak dapat dan tidak boleh dilakukan secara otomatis melalui kode sumber atau build script.

### Langkah-langkah Penautan di Firebase Console:

1. **Buka Firebase Console**:
   Kunjungi [Firebase Console](https://console.firebase.google.com/) menggunakan akun Google yang memiliki hak akses **Owner/Editor** pada Firebase project dan **Admin** pada akun Google Play Console terkait.

2. **Pilih Project**:
   Pilih project `tempat-sambung` (Project Number: `215024871022`).

3. **Masuk ke Pengaturan Integrasi**:
   - Klik ikon gerigi (⚙️) di sebelah menu **Project Overview** di pojok kiri atas.
   - Pilih menu **Project settings** (Setelan project).
   - Klik tab **Integrations** (Integrasi) pada bilah navigasi atas.

4. **Tautkan ke Google Play**:
   - Temukan kartu bertuliskan **Google Play**.
   - Klik tombol **Link** (Tautkan).
   - Pilih aplikasi `com.niatmandiwajib.ghusl` dari daftar aplikasi Google Play Anda.
   - Centang opsi untuk membagikan data data analytics/revenue bila relevan.
   - Klik **Next** dan konfirmasikan penautan hingga status Google Play berubah menjadi **Linked**.

### Ketentuan & Larangan:
- **JANGAN** mengaktifkan BigQuery export otomatis jika tidak ada kebutuhan analisis data mentah skala enterprise, untuk menghindari timbulnya biaya cloud hosting yang tidak diinginkan.
- **JANGAN** melakukan upgrade billing / Blaze plan hanya untuk proses linking Google Play dasar.
- **JANGAN** menambahkan layanan pihak ketiga berbayar tanpa evaluasi kebutuhan bisnis yang terencana.
