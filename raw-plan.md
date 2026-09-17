Package    com.niatmandiwajib.ghusl                        
Nama    Bahasa, Melayu    Ghusl: Fiqih Mandi Wajib Lengkap                    
    Urdu    Ghusl: Ghsal Wajib ki Makmal Faqah                    
    English    Ghusl: Islamic Guide to Ritual Bath                    
UI/UX    dibuat semudah, se-intuitif, senyaman, dan semenarik mungkin                        
    Sopan & Menenangkan                        
    Warna kalem (hijau, biru muda, putih), font mudah dibaca                        
    Hindari kesan "medis" yang kaku, tapi juga hindari terlalu kartunis untuk topik serius ini                        
Menu    Beranda    Dashboard: lanjutkan bacaan, quick access                     
    Panduan    berisi 13 konten, tiap2 konten berupa slide show, tiap slide berisi gambar, penjelasan, teks arab, teks latin, terjemah, sumber, suara, dan share button                    
    Cek Kondisi (Wizard)    Decision tree tanya-jawab untuk cek wajib mandi atau tidak                    
    Tanya Ustadz    Tanya ke AI (google ai studio), jawaban via notifikasi 2-4 jam kemudian                    
    Lainnya    Search, Bookmark, Riwayat, FAQ, Settings page                    
Settings page    pilih bahasa: english, indonesia, melayu, urdu                        
    switch light mode/dark mode/system                        
    legal section: about & privacy policy (https://tokiocv.blogspot.com/2026/07/privacy-policy.html)                        
    rate us: https://play.google.com/store/apps/details?id=com.niatmandiwajib.ghusl                        
    follow us on facebook: https://www.facebook.com/profile.php?id=61593997451247                        
Coding    buat sesuai aturan google playstore dan google admob                        
    pastikan untuk menambahkan dependensi com.google.android.gms:play-services-ads di file build.gradle.kts                        
    pasang sample App ID AdMob di AndroidManifest.xml: ca-app-pub-3940256099942544~3347511713.                        
    pemuatan ads_config.json secara remote dari https://raw.githubusercontent.com/susantohenri/admob-remote-configs/refs/heads/main/ghusl/ads_config.json                        
    apps dibuat multi bahasa (english, indonesia, melayu, & urdu). by default, ambil setting bahasa operating system. jika tidak ada, fallback ke english                        
    Integrasikan Google UMP (User Messaging Platform) SDK untuk menangani GDPR / Privacy Consent iklan secara standar   