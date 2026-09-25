# Türkçe Barkod & QR Tarayıcı

Kotlin + Jetpack Compose + Material 3 + CameraX + Google ML Kit + Room ile yazılmış,
tamamen Türkçe, ücretsiz, çevrimiçi hesap gerektirmeyen barkod/QR kod tarama uygulaması.

- namespace / applicationId: `com.turkce.barkod`
- minSdk 24 (Android 7.0) · targetSdk 34 · compileSdk 34
- API anahtarı, Firebase, backend, ücretli servis **yoktur**
- Tüm barkod verileri yalnızca cihazda saklanır, hiçbir sunucuya gönderilmez

## Termux'ta Build

### 1) Ortam kontrolü

```bash
uname -a
java -version
echo $ANDROID_HOME
ls $ANDROID_HOME
```

### 2) Gerekli paketler

```bash
pkg update -y && pkg upgrade -y
pkg install -y git openjdk-17 gradle
```

### 3) Repoyu indir

```bash
cd ~
git clone https://github.com/eraslan19j/Bar.git BarkodTarayici
cd BarkodTarayici
```

### 4) Android SDK yolunu tanımla

```bash
export ANDROID_HOME=$HOME/android-sdk
export ANDROID_SDK_ROOT=$ANDROID_HOME
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
echo "sdk.dir=$ANDROID_HOME" > local.properties
```

Derleyebilmek için `platforms/android-34` ve `build-tools/34.0.0` kurulu olmalıdır
(`sdkmanager "platforms;android-34" "build-tools;34.0.0"`).

### 5) Gradle Wrapper'ı üret

```bash
gradle wrapper --gradle-version 8.7
chmod +x gradlew
```

### 6) Build

```bash
./gradlew :app:assembleDebug
```

### 7) APK kontrolü

```bash
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

## Telefona Kurulum

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

`adb` yoksa APK dosyasını doğrudan telefona kopyalayıp dosya yöneticisinden kurabilirsin
(kurulum için "bilinmeyen kaynaklara izin" verilmesi gerekir).

## Desteklenen Barkod Formatları

QR, EAN-13, EAN-8, UPC-A, UPC-E, Code 128, Code 39, Code 93, ITF, Codabar,
PDF417, Aztec, Data Matrix

## Ekranlar

- **Barkod Tara** — kamera, çerçeve, hareketli tarama çizgisi
- **Barkod Sonucu** — tür, içerik, okuma zamanı; Kopyala / Paylaş / Bağlantıyı Aç / Yeni Tarama / Geçmişe Kaydet
- **Tarama Geçmişi** — Room ile cihazda saklanır; Sil / Tüm Geçmişi Temizle
- **Ayarlar** — Ses, Titreşim, Koyu Tema, Tarama Geçmişi, Tüm Geçmişi Temizle
