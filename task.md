# Checklist Implementasi Proyek — Ghusl: Fiqih Mandi Wajib Lengkap

## Fase 1: Project Scaffolding & Setup
- [x] Inisialisasi Android project (Kotlin, Jetpack Compose, Material 3, Gradle KTS)
- [x] Konfigurasi package name: `com.niatmandiwajib.ghusl`
- [x] Setup dependencies: Compose Navigation, Room, DataStore, Retrofit/OkHttp, Coil, ExoPlayer/Media3, Google Ads, UMP SDK, Google GenAI SDK / Gemini REST client, WorkManager
- [x] Setup base theme (hijau, biru muda, putih) dengan dukungan Light, Dark, dan System Theme
- [x] Konfigurasi font (termasuk font Naskh/Nastaliq untuk Urdu/Arab)

## Fase 2: Navigasi & UI Shell (RTL Ready)
- [x] Bottom Navigation: Beranda, Panduan, Cek Kondisi, Tanya Ustadz, Lainnya
- [x] Setup Navigation Graph Jetpack Compose
- [x] Konfigurasi RTL support (`start`/`end`, auto-mirroring, directional icons)
- [x] Kerangka UI Beranda (Dashboard, recent reading deep link, quick access cards)

## Fase 3: Data Model & Local Storage
- [x] Setup Room Database (Bookmark, Riwayat Baca, Riwayat Tanya Ustadz)
- [x] Setup DataStore Preferences (Pilihan bahasa, tema, onboarding, audio settings)
- [x] Repository layer & offline caching strategy

## Fase 4: Fitur Panduan (Dynamic Remote JSON)
- [x] Network client untuk fetch `https://raw.githubusercontent.com/susantohenri/admob-remote-configs/refs/heads/main/ghusl/konten/data.json`
- [x] Parsing data JSON & pemetaan multi-bahasa (id, en, ms, ur)
- [x] UI List Panduan (dengan native ad slot)
- [x] UI Slide Show Panduan (Arab RTL, Latin LTR, terjemahan, penjelasan, sumber dalil)
- [x] Fitur Share slide & Bookmark
- [x] Pemutar Audio (resitasi doa/niat)

## Fase 5: Wizard Engine Cek Kondisi (Decision Tree)
- [ ] Schema & parser engine decision tree berbasis JSON config
- [ ] Flow interaktif pertanyaan Ya / Tidak
- [ ] Layar Hasil Rekomendasi (wajib mandi / tidak, rujukan dalil ringkas, disclaimer ustadz/ulama)

## Fase 6: Tanya Ustadz (AI Q&A - Gemini)
- [ ] Integrasi Google AI Studio (Gemini API direct client dengan system prompt `ustadz.skill.md`)
- [ ] Mekanisme antrian & notifikasi jawaban (WorkManager / Notification)
- [ ] Layar kirim pertanyaan, riwayat tanya-jawab, detail jawaban, dan disclaimer
- [ ] Komentar arsitektur migrasi Fase 2 (GitHub Actions proxy)

## Fase 7: Monetisasi (AdMob) & Privasi (UMP SDK)
- [ ] Integrasi Google User Messaging Platform (UMP) consent form (GDPR/DMA)
- [ ] Remote config AdMob (`ads_config.json`)
- [ ] Implementasi Adaptive Banner (Beranda, Panduan list, Search/FAQ, Tanya Ustadz)
- [ ] Implementasi Native Ad (disisipkan tiap 4-5 item di list)
- [ ] Implementasi Interstitial Ad (frequency capped setelah selesai baca panduan)
- [ ] Implementasi Rewarded Ad & App Open Ad
- [ ] Brand safety & aturan privasi (no targeting dari data wizard)

## Fase 8: Lokalisasi & RTL (Urdu, Melayu, English, Indonesia)
- [ ] Resource strings lengkap (strings.xml per locale: en, id, ms, ur)
- [ ] RTL layout verification & icon mirroring
- [ ] Typography Urdu (Noto Nastaliq / Naskh)

## Fase 9: Settings & Legal
- [ ] Pengaturan bahasa & tema
- [ ] About screen dengan versi aplikasi & link Privacy Policy
- [ ] Rate Us link (Play Store) & Follow us (Facebook)
- [ ] Fitur Pencarian (Search index Panduan + FAQ)

## Fase 10: Pengujian, Kepatuhan Play Store & Verifikasi Build
- [ ] Verifikasi build APK / Bundle
- [ ] Pengujian fungsionalitas offline & online
- [ ] Validasi kepatuhan Google Play Store & Google AdMob
