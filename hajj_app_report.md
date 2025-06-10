# Raporti Final i Projektit: Aplikacioni Android për Rregullat e Haxhit

## Përmbledhje e Projektit

Ky projekt përfshin krijimin e një aplikacioni Android që përmban rregullat e Haxhit të organizuara në format JSON dhe të integruara në një ndërfaqe përdoruesi intuitive. Projekti është zhvilluar duke ndjekur këto hapa:

1. Kërkimi dhe mbledhja e rregullave të Haxhit nga burime të besueshme
2. Përmbledhja dhe organizimi i rregullave në format narrativ
3. Strukturimi i rregullave në format JSON
4. Dizajnimi i ndërfaqes së aplikacionit Android
5. Implementimi i aplikacionit Android me të dhënat JSON
6. Testimi dhe validimi i funksionaliteteve

## Përmbajtja e Projektit

Projekti përmban këto skedarë kryesorë:

### Të dhënat e Rregullave të Haxhit
- `hajj_rules_summary.md` - Përmbledhje narrative e rregullave të Haxhit
- `hajj_rules.json` - Rregullat e Haxhit të strukturuara në format JSON

### Dizajni i Aplikacionit
- `hajj_app_design.md` - Dokumenti i dizajnit të aplikacionit Android

### Kodi i Aplikacionit Android
- `hajj_app/` - Direktoria kryesore e projektit Android
  - `app/src/main/java/com/example/hajjrules/` - Kodi burim i aplikacionit
    - `model/` - Modelet e të dhënave
    - `util/` - Utilitetet dhe menaxhimi i të dhënave
    - `fragments/` - Fragmentet e ndërfaqes së përdoruesit
    - `MainActivity.java` - Aktiviteti kryesor i aplikacionit
  - `app/src/main/res/` - Burimet e aplikacionit
    - `values/strings.xml` - Stringjet e aplikacionit
    - `layout/activity_main.xml` - Layout-i kryesor
    - `menu/bottom_navigation_menu.xml` - Menuja e navigimit

## Funksionalitetet Kryesore

Aplikacioni ofron këto funksionalitete kryesore:

1. **Shfaqja e Kategorive të Rregullave** - Përdoruesi mund të shohë të gjitha kategoritë e rregullave të Haxhit në ekranin kryesor.
2. **Shfletimi i Rregullave sipas Kategorive** - Përdoruesi mund të zgjedhë një kategori dhe të shohë të gjitha rregullat që i përkasin asaj kategorie.
3. **Kërkimi i Rregullave** - Përdoruesi mund të kërkojë rregulla specifike duke përdorur fjalë kyçe.
4. **Funksionimi Offline** - Aplikacioni funksionon plotësisht pa internet, pasi të gjitha të dhënat ruhen lokalisht.

## Udhëzime për Zhvillim të Mëtejshëm

Për të vazhduar zhvillimin e aplikacionit:

1. **Importimi i Projektit** - Importoni projektin në Android Studio.
2. **Vendosja e JSON** - Kopjoni skedarin `hajj_rules.json` në direktorinë `app/src/main/assets/` të projektit.
3. **Ndërtimi i Aplikacionit** - Ndërtoni dhe ekzekutoni aplikacionin në një emulator ose pajisje fizike.

## Përmirësime të Mundshme

Aplikacioni mund të përmirësohet më tej me këto funksionalitete:

1. **Ruajtja e Rregullave të Preferuara** - Shtimi i mundësisë për të shënuar rregulla si të preferuara.
2. **Ndarja e Rregullave** - Mundësia për të ndarë rregulla specifike me të tjerët.
3. **Përkthimi në Gjuhë të Tjera** - Shtimi i mbështetjes për gjuhë të tjera përveç shqipes.
4. **Tema të Ndryshme** - Shtimi i temave të ndryshme vizuale (e errët, e çelët, etj.).
5. **Njoftimet** - Shtimi i njoftimeve për përkujtues të rëndësishëm gjatë sezonit të Haxhit.

## Burimet e Përdorura

Informacioni për rregullat e Haxhit është marrë nga këto burime:

1. Mesazhi.com - "Haxhi dhe rregullat e tij" (https://www.mesazhi.com/haxhi-dhe-rregullat-e-tij/)
2. Wikipedia - "Haxhillëku" (https://sq.wikipedia.org/wiki/Haxhill%C3%ABku)
3. Udhaebesimtareve.com - "Haxhi dhe disa rregulla në lidhje me të" (https://udhaebesimtareve.com/haxhi-dhe-disa-rregulla-ne-lidhje-me-te/)

## Përfundim

Ky projekt ofron një aplikacion Android funksional që përmban rregullat e Haxhit të organizuara në mënyrë të strukturuar dhe të aksesueshme. Aplikacioni është i lehtë për t'u përdorur dhe funksionon plotësisht offline, duke e bërë atë të dobishëm për muslimanët që planifikojnë të kryejnë Haxhin.
