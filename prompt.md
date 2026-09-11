# Project Brief — "Ghusl: Fiqih Mandi Wajib Lengkap" (Android)

Kamu bertindak sebagai tim pengembang Android untuk membangun aplikasi edukasi Islam berikut. Baca seluruh brief ini dulu. Mulai di **Planning mode**: buat `implementation_plan.md` dan `task.md` (checklist per fase di bawah), lalu tunggu persetujuanku sebelum mulai coding. Jangan mem-final-kan hal yang ditandai **[TANYA DULU]** atau **[JANGAN DIISI SENDIRI]** tanpa konfirmasi.
buat sesuai aturan google playstore dan google admob

## 1. Overview
- **Nama package**: `com.niatmandiwajib.ghusl`
- **Nama app per bahasa**:
  - Indonesia / Melayu: "Ghusl: Fiqih Mandi Wajib Lengkap"
  - Urdu: "Ghusl: Ghsal Wajib ki Makmal Faqah"
  - English: "Ghusl: Islamic Guide to Ritual Bath"
- **Platform**: Android native. Kotlin + Jetpack Compose .
- **Tujuan**: panduan niat, doa, dan tata cara mandi wajib (ghusl) — termasuk junub, setelah haid, nifas — untuk pria & wanita, dengan bantuan AI tanya-jawab dan wizard cek kondisi.

## 2. Prinsip Desain (UI/UX)
- Sopan, menenangkan, mudah, intuitif, nyaman, menarik.
- Palet warna kalem: hijau, biru muda, putih. Font mudah dibaca.
- Hindari kesan medis/klinis yang kaku, tapi juga hindari kartunis — topik ini sensitif & serius.
- Dukung **light / dark / system theme**.
- **Dukungan RTL (Right-to-Left) untuk Urdu** — wajib, checklist:
  - Semua layout (XML/Compose) pakai `start`/`end`, JANGAN `left`/`right`, supaya Android auto-mirror saat locale Urdu aktif.
  - Ikon terarah (panah "lanjut", back, share) perlu di-mirror manual — Android tidak otomatis membalik gambar/icon, hanya posisi layout.
  - Font: gunakan font yang render huruf Urdu (Nastaliq/Naskh) dengan baik, mis. Noto Nastaliq Urdu — font default Android sering kurang bagus untuk Urdu.
  - Teks Arab (dalil/doa) tetap RTL natural di semua bahasa; teks Latin/transliterasi & angka tetap LTR — pastikan arah tidak tertukar saat digabung dalam satu slide.
  - Test di semua layar dengan device locale = Urdu: bottom nav, urutan tombol Ya/Tidak di Wizard, progress indicator slide show.
  - Cek preview banner/native ad AdMob di layout RTL — SDK ad pihak ketiga tidak selalu ikut mirror otomatis.

## 3. Struktur Navigasi
Bottom nav / menu utama: **Beranda · Panduan · Cek Kondisi · Tanya Ustadz · Lainnya**

### 3.1 Beranda
Dashboard: lanjutkan bacaan terakhir (deep link ke slide terakhir), quick access ke Panduan/Wizard/Tanya Ustadz.

### 3.2 Panduan
13 konten ambil dari **Konten ini dynamic, bukan hardcoded di app** — fetch dari remote JSON (https://raw.githubusercontent.com/susantohenri/admob-remote-configs/refs/heads/main/ghusl/konten/data.json), supaya revisi/tambah konten tidak perlu release APK baru. Tiap konten = slide show. Skema data per slide:

```json
{
  "id": "konten_id",
  "judul": "string",
  "slide_show": [
    {
      "judul_slide": "string",
      "gambar": "string (image url/asset ref)",
      "text": "string (penjelasan)",
      "arab": "string",
      "latin": "string",
      "terjemah": "string",
      "sumber": "string (dalil/rujukan)",
      "suara": "string (audio url)"
    }
  ]
}
```
Setiap field teks (`text`, `terjemah`, `judul_slide`, `judul`) perlu versi per bahasa (en/id/ms/ur). `arab`, `latin`, dan `suara` (audio) tetap sama lintas bahasa. Tiap slide: tombol share.

**[JANGAN DIISI SENDIRI]** — jangan mengarang teks fiqih/dalil. Bangun struktur, UI, dan mekanisme i18n saja; isi konten asli menunggu review ustadz/pemilik produk.

### 3.3 Cek Kondisi (Wizard)
Decision tree tanya-jawab (ya/tidak) untuk menentukan wajib mandi atau tidak, berakhir di halaman hasil + rujukan dalil singkat + disclaimer "ini panduan umum, untuk kasus rumit konsultasikan ke ustadz/ulama setempat". **[JANGAN DIISI SENDIRI]** logika fiqih-nya — buat scaffold engine decision-tree yang datanya dibaca dari JSON config, supaya percabangan bisa diisi/direvisi manusia tanpa ubah kode.

### 3.4 Tanya Ustadz (AI Q&A)
- User kirim pertanyaan → diproses AI (Google AI Studio / Gemini) → jawaban dikirim lewat **push notification (FCM)** 2–4 jam kemudian → tersimpan di Riwayat.
- **Arsitektur Fase 1 (sekarang)**: panggil Google AI Studio API **langsung dari client**, API key **hardcode di app**  (mis. `BuildConfig` field / string resource). Ini keputusan sadar untuk mempercepat fase 1 — didokumentasikan di sini sebagai known trade-off, bukan best practice, supaya mudah dilacak saat refactor.
- **Arsitektur Fase 2 (nanti, jangan dikerjakan sekarang)**: migrasi ke proxy tanpa biaya — GitHub Actions terjadwal (cron) yang membaca antrian pertanyaan (Firestore/file di repo), memanggil Gemini API pakai key yang disimpan di **GitHub Secrets**, simpan jawaban, lalu trigger FCM. Beri komentar `// TODO fase 2: pindahkan panggilan Gemini ke GitHub Actions proxy, lihat catatan arsitektur` di kode pemanggil API supaya gampang ditemukan nanti.
- **Grounding**: ikuti ustadz.skill.md

### 3.5 Lainnya
Search (index semua konten Panduan + FAQ), Bookmark (simpan konten favorit — local storage/Room), Riwayat (riwayat baca + riwayat Tanya Ustadz), FAQ, Settings.

## 4. Settings
- Pilih bahasa: English / Indonesia / Melayu / Urdu.
- Switch light/dark/system.
- Legal: About & Privacy Policy → https://tokiocv.blogspot.com/2026/07/privacy-policy.html
- Rate us → https://play.google.com/store/apps/details?id=com.niatmandiwajib.ghusl
- Follow us (Facebook) → https://www.facebook.com/profile.php?id=61593997451247

## 5. Monetisasi (AdMob)
- Dependency: `com.google.android.gms:play-services-ads` di `build.gradle.kts`.
- App ID sample untuk development: `ca-app-pub-3940256099942544~3347511713` di `AndroidManifest.xml`
- Remote config `ads_config.json` dari GitHub raw: `https://raw.githubusercontent.com/susantohenri/admob-remote-configs/refs/heads/main/ghusl/ads_config.json`. (Fase ini: tanpa fallback lokal — semua konten yang di-host di GitHub, termasuk ini dan Panduan, sengaja tanpa fallback dulu.)
- **Format iklan & penempatan** (lihat juga bagian review di bawah untuk alasannya):
  | Format | Penempatan |
  |---|---|
  | Adaptive Banner | Beranda, list Panduan, Search/FAQ/Riwayat/Bookmark, layar Tanya Ustadz |
  | Native ad | Disisipkan halus tiap 4–5 item di list Panduan & hasil pencarian (styling menyatu dengan tema kalem, bukan generic template) |
  | Interstitial | Setelah selesai baca 1 konten Panduan penuh (sebelum kembali ke list), dengan frequency cap (mis. tidak lebih dari 1x per beberapa menit) — jangan tampil langsung di transisi keluar dari hasil Wizard yang sensitif (tunda ke transisi berikutnya) |
  | Rewarded (video/interstitial) | Opsional: tonton iklan untuk mempercepat jawaban Tanya Ustadz, atau unlock unduhan audio offline |
  | App Open | Saat user kembali dari background (bukan cold start pertama), dengan cooldown |
- Set **content filtering** di AdMob dashboard untuk memblokir kategori sensitif (judi, dating, alkohol) — brand safety untuk app religi.
- Jangan pernah gunakan jawaban wizard "Cek Kondisi" (data terkait haid/hubungan suami-istri/dll.) untuk targeting/personalisasi iklan apa pun.

## 6. Lokalisasi
- 4 bahasa: en, id, ms, ur. Default ambil locale OS; fallback English kalau tidak didukung.
- Resource string terpisah per locale + `values-ur` dengan atribut RTL yang benar.
- Audio resitasi Arab tetap sama lintas bahasa; hanya teks penjelasan/terjemah yang berubah.

## 7. Privasi & Kepatuhan
- Integrasikan **Google UMP SDK** (User Messaging Platform) untuk consent GDPR/DMA sebelum request iklan personalisasi di region yang relevan.
- About, tampilkan versi aplikasi dan tombol Privacy Policy yang bisa membuka URL Web Privacy Policy luar saat diklik, url nya https://tokiocv.blogspot.com/2026/07/privacy-policy.html.

## 8. Data Lokal
- Bookmark, Riwayat, preferensi Settings → Room DB / DataStore lokal.
- Pertimbangkan caching offline untuk konten Panduan (gambar/audio) yang sudah pernah dibuka, karena user bisa saja mengakses tanpa koneksi internet.

---
**Ringkasan instruksi kerja untuk kamu (Antigravity):**
1. Buat `implementation_plan.md` + `task.md` dulu, pisahkan fase: scaffolding project → navigasi & UI shell → data model & local storage → fitur Panduan (dengan placeholder konten, dynamic dari remote JSON) → Wizard engine (data-driven) → Tanya Ustadz (Fase 1: client → Gemini langsung, key hardcoded) → AdMob integration & UMP → localization/RTL → settings & legal.