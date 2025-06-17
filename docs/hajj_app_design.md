# Dizajni i Aplikacionit Android për Rregullat e Haxhit

## Struktura e Aplikacionit

Aplikacioni do të ketë një strukturë të thjeshtë dhe intuitive që do të mundësojë përdoruesit të aksesojnë lehtësisht të gjitha rregullat e Haxhit. Më poshtë është struktura e propozuar:

### 1. Aktivitetet Kryesore

1. **SplashActivity**: Ekrani i hapjes me logon e aplikacionit
2. **MainActivity**: Aktiviteti kryesor që përmban navigimin dhe përmbajtjen kryesore
3. **DetailActivity**: Aktiviteti për shfaqjen e detajeve të një kategorie specifike të rregullave

### 2. Fragmentet

1. **HomeFragment**: Fragmenti kryesor që shfaq kategoritë kryesore të rregullave
2. **CategoryFragment**: Fragmenti që shfaq listën e rregullave për një kategori specifike
3. **DetailFragment**: Fragmenti që shfaq detajet e një rregulli specifik
4. **AboutFragment**: Fragmenti që shfaq informacion rreth aplikacionit

## Ndërfaqja e Përdoruesit (UI)

### 1. Ekrani i Hapjes (Splash Screen)
- Logo e aplikacionit me emrin "Rregullat e Haxhit"
- Animacion i thjeshtë i hapjes
- Kohëzgjatja: 2-3 sekonda

### 2. Ekrani Kryesor (Main Screen)
- **Shiriti i Navigimit të Poshtëm (Bottom Navigation Bar)** me opsionet:
  - Kryefaqja
  - Kategoritë
  - Kërko
  - Rreth Aplikacionit
- **Seksioni i Kategorive Kryesore** me karta (cards) për secilën kategori:
  - Hyrje dhe Rëndësia e Haxhit
  - Shtyllat e Islamit
  - Detyrimi i Haxhit
  - Edukata e Udhëtimit
  - Ihrami
  - Ndalesat gjatë Ihramit
  - Vendcaktimet
  - Qabja

### 3. Ekrani i Kategorisë (Category Screen)
- **Titull i Kategorisë** në krye
- **Listë e Rregullave** të kategorisë përkatëse
- **Buton Kthehu** për t'u kthyer në ekranin kryesor

### 4. Ekrani i Detajeve (Detail Screen)
- **Titull i Rregullit** në krye
- **Përshkrim i Detajuar** i rregullit
- **Imazh Ilustrues** (nëse është i disponueshëm)
- **Buton Kthehu** për t'u kthyer në ekranin e kategorisë

### 5. Ekrani i Kërkimit (Search Screen)
- **Fushë Kërkimi** në krye
- **Rezultatet e Kërkimit** të shfaqura si listë
- **Filtër Kërkimi** për të filtruar rezultatet sipas kategorive

### 6. Ekrani Rreth Aplikacionit (About Screen)
- **Informacion rreth Aplikacionit**
- **Versioni i Aplikacionit**
- **Informacion Kontakti**

## Ngjyrat dhe Stili

### Skema e Ngjyrave
- **Ngjyra Kryesore**: #4CAF50 (Jeshile)
- **Ngjyra Sekondare**: #2196F3 (Blu)
- **Ngjyra e Sfondit**: #FFFFFF (Bardhë)
- **Ngjyra e Tekstit**: #212121 (Gri e Errët)
- **Ngjyra e Theksuar**: #FFC107 (Verdhë)

### Tipografia
- **Font Kryesor**: Roboto
- **Font Sekondar**: Noto Sans Arabic (për termat arabë)

## Komponentët e UI

### 1. Kartat (Cards)
- Përdoren për të shfaqur kategoritë kryesore
- Përmbajnë ikonë, titull dhe përshkrim të shkurtër
- Kanë hije të lehtë dhe kënde të rrumbullakosura

### 2. Listat (Lists)
- Përdoren për të shfaqur rregullat brenda një kategorie
- Çdo element i listës përmban titull dhe përshkrim të shkurtër
- Kanë ndarës të hollë midis elementeve

### 3. Butonat (Buttons)
- Butonat kryesorë kanë ngjyrën kryesore (#4CAF50)
- Butonat sekondarë kanë ngjyrën sekondare (#2196F3)
- Kanë kënde të rrumbullakosura dhe efekt valëzimi kur klikohen

### 4. Ikonat (Icons)
- Përdoren ikonat e Material Design
- Ngjyra e ikonave përputhet me skemën e ngjyrave të aplikacionit

## Funksionalitetet Kryesore

### 1. Leximi i Rregullave
- Shfaqja e të gjitha kategorive të rregullave
- Shfaqja e listës së rregullave për secilën kategori
- Shfaqja e detajeve të plota për secilin rregull

### 2. Kërkimi
- Kërkimi i rregullave sipas fjalëve kyçe
- Filtrimi i rezultateve sipas kategorive

### 3. Ruajtja e Rregullave të Preferuara
- Mundësia për të shënuar rregulla si të preferuara
- Shfaqja e listës së rregullave të preferuara

### 4. Ndarja (Sharing)
- Mundësia për të ndarë rregulla specifike me të tjerët
- Ndarja përmes aplikacioneve të ndryshme (WhatsApp, Email, etj.)

### 5. Funksionimi Offline
- Të gjitha rregullat ruhen lokalisht në pajisje
- Aplikacioni funksionon plotësisht pa internet

## Arkitektura e Aplikacionit

### 1. Modeli MVVM (Model-View-ViewModel)
- **Model**: Klasa që përfaqësojnë të dhënat (Rregullat, Kategoritë, etj.)
- **View**: Aktivitetet dhe Fragmentet që shfaqin UI
- **ViewModel**: Klasa që menaxhojnë logjikën e biznesit dhe komunikimin midis Model dhe View

### 2. Komponentët e Arkitekturës
- **Room Database**: Për ruajtjen lokale të të dhënave
- **LiveData**: Për të vëzhguar ndryshimet në të dhëna
- **ViewModel**: Për të menaxhuar të dhënat e UI
- **Repository**: Për të menaxhuar burimet e të dhënave

### 3. Libraritë Kryesore
- **Retrofit**: Për komunikimin me API (nëse nevojitet në të ardhmen)
- **Gson**: Për parsimin e JSON
- **Glide**: Për ngarkimin e imazheve
- **Material Components**: Për komponentët e UI

## Plani i Implementimit

1. **Konfigurimi i Projektit**:
   - Krijimi i projektit në Android Studio
   - Konfigurimi i varësive dhe librarive

2. **Krijimi i Modeleve të të Dhënave**:
   - Krijimi i klasave që përfaqësojnë strukturën e JSON

3. **Implementimi i UI**:
   - Krijimi i layouteve për aktivitetet dhe fragmentet
   - Implementimi i navigimit

4. **Implementimi i Logjikës së Biznesit**:
   - Ngarkimi i të dhënave nga JSON
   - Implementimi i kërkimit dhe filtrimit

5. **Testimi dhe Optimizimi**:
   - Testimi i funksionaliteteve
   - Optimizimi i performancës

6. **Finalizimi**:
   - Shtimi i ikonës dhe splash screen
   - Përgatitja për publikim
