# PA19 Checklist

Application Android native de checklist PA19, concue pour un usage simple, local et hors ligne.

## Intention

Cette application n'a pas ete concue pour moderniser le PA19 ni pour remplacer la checklist papier du club.
Elle est nee d'un besoin pratique vecu en situation reelle : au point d'attente, la checklist papier m'a echappe des mains et est tombee a l'arriere de la cabine, ce qui m'a oblige a couper le moteur et a me debreler pour aller la recuperer.

L'idee est donc simplement de disposer, sur un support deja present et fixe a la cuisse, d'un acces stable a la meme checklist que celle de l'avion, avec les memes termes et le meme ordre.
L'objectif est de limiter les manipulations hasardeuses de documents dans des phases ou la charge de travail, le bruit, la radio et le stress peuvent deja etre presents.

La checklist papier reste bien entendu a bord et accessible dans le cockpit.
L'application n'est pas un prerequis au vol, ni un substitut aux procedures enseignees.
Elle constitue seulement un support complementaire, pratique, fidele au document club, pour un usage plus stable et plus simple au sol, de la mise en route jusqu'au decollage.

## Caracteristiques

- Kotlin
- Jetpack Compose
- Material 3
- fonctionnement hors ligne
- checklist chargee depuis un fichier JSON embarque
- restauration de session via `SharedPreferences`
- aucune API distante
- aucune authentification
- aucune dependance operationnelle au telephone pour voler

## Fonctionnement

- affichage d'un memo de depart
- progression lineaire phase par phase
- validation d'une action par appui
- restauration exacte de la progression apres fermeture de l'application
- ecran final `VITESSES PA19`
- checklist `12. ARRET MOTEUR` en validation sequentielle

## Fichiers principaux

- `app/src/main/assets/checklist_pa19.json`
- `app/src/main/java/com/example/pa19checklist/MainActivity.kt`
- `app/src/main/java/com/example/pa19checklist/viewmodel/ChecklistViewModel.kt`
- `app/src/main/java/com/example/pa19checklist/ui/screen/ChecklistScreen.kt`
- `app/src/main/java/com/example/pa19checklist/data/ChecklistJsonLoader.kt`
- `app/src/main/java/com/example/pa19checklist/data/SessionStorage.kt`

## Build

Depuis la racine du projet :

```bash
./gradlew :app:assembleDebug
```

APK genere :

- `app/build/outputs/apk/debug/app-debug.apk`

## Important

Le contenu de la checklist reprend strictement les termes de la checklist club embarquee dans l'avion.
Cette application vise a ameliorer la stabilite d'acces au support, pas a redefinir la procedure.
