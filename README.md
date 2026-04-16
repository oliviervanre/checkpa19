# Golf Score Android

Application Android native de saisie de scores de golf, pensee pour un usage terrain simple, local et peu gourmand.

## Pile technique

- Kotlin
- Jetpack Compose pour l'interface
- Material 3
- Stockage local via `SharedPreferences`
- Serialisation JSON avec `org.json`
- Import / export de parcours via le selecteur de documents Android
- Aucune API distante
- Aucune authentification
- Aucune geolocalisation

## Choix d'architecture

L'application reste volontairement legere :

- pas de backend
- pas de synchronisation cloud
- donnees stockees uniquement sur l'appareil
- historique de parties conserve une copie des trous et des PAR au moment de la partie

Consequence importante :

- modifier un parcours n'affecte pas les anciennes parties
- supprimer un parcours est bloque s'il existe deja des parties liees

## Fonctionnalites actuelles

- creation de parcours 9 ou 18 trous
- definition du PAR trou par trou
- modification d'un parcours
- suppression d'un parcours seulement si aucune partie n'y est rattachee
- nouvelle partie depuis un parcours existant
- reprise d'une partie en cours
- saisie rapide du score avec passage automatique au trou suivant
- confirmation visuelle courte apres saisie rapide
- ajustement manuel avec `+1` et `-1`
- saisie optionnelle des putts
- option `Trou releve` avec affichage `X`
- historique des parties terminees
- detail trou par trou dans l'historique
- suppression d'une partie de l'historique par appui long avec confirmation
- statistiques simples sur les parties completes
- ecran d'administration
- export JSON des parcours
- import JSON des parcours avec ajout aux parcours existants
- remise a zero complete avec double confirmation

## Guide utilisateur rapide

### 1. Creer un parcours

Depuis l'accueil :

1. ouvrir `Gerer les parcours`
2. cliquer sur `Ajouter`
3. saisir le nom
4. choisir `9` ou `18`
5. definir les PAR
6. enregistrer

### 2. Modifier un parcours

Depuis `Gerer les parcours` :

1. cliquer sur `Modifier`
2. corriger le nom, le nombre de trous ou les PAR
3. enregistrer

Les modifications ne s'appliquent qu'aux prochaines parties.

### 3. Jouer une partie

Depuis l'accueil :

1. cliquer sur `Nouvelle partie`
2. choisir un parcours
3. saisir trou par trou

Logique de saisie :

- `Saisie rapide` : enregistre le score et passe au trou suivant
- `+1 / -1` : ajuste le score sans changer de trou
- `Putts` : facultatif
- `Trou releve` : marque le trou avec `X`
- `Remettre ce trou a zero` : efface le score et les putts du trou courant
- `Suivant / Precedent` : navigation manuelle

### 4. Historique

Depuis l'accueil :

1. ouvrir `Historique`
2. toucher une partie pour afficher le detail trou par trou
3. faire un appui long sur une partie pour la supprimer

Les stats ne prennent en compte que les parties completes, sans trou releve.

### 5. Administration

Depuis l'accueil :

1. ouvrir `Administration`
2. `Exporter les parcours` pour produire un fichier JSON partageable
3. `Importer des parcours` pour ajouter des parcours depuis un fichier JSON
4. `Remise a zero totale` pour effacer toutes les donnees locales

## Structure du projet

- `app/src/main/java/com/example/golfscore/MainActivity.kt`
- `app/src/main/java/com/example/golfscore/ui/theme/Theme.kt`
- `app/src/main/java/com/example/golfscore/ui/theme/Color.kt`
- `app/src/main/res/values/themes.xml`
- `app/build.gradle.kts`

L'application est actuellement concentree en grande partie dans `MainActivity.kt` pour aller vite sur une V1/V2 locale.

## Ouvrir et lancer le projet

1. Ouvrir le dossier du projet dans Android Studio.
2. Laisser Android Studio synchroniser Gradle et installer les composants Android manquants.
3. Brancher un telephone Android en mode developpeur avec debogage USB.
4. Selectionner l'appareil dans Android Studio.
5. Lancer `Run`.

Selon la version d'Android Studio, la verification manuelle du build se fait par :

- `Build > Assemble Project`

## Recuperer l'APK

Pour generer un APK de debug dans Android Studio :

1. ouvrir `Build`
2. utiliser `Build Bundle(s) / APK(s)`
3. choisir `Build APK(s)`

L'APK de debug est genere en general dans :

- `app/build/outputs/apk/debug/app-debug.apk`

## Installer l'APK manuellement

Sur le telephone Android :

1. recuperer le fichier `app-debug.apk`
2. l'ouvrir depuis le telephone
3. autoriser l'installation depuis cette source si Android le demande
4. installer l'application

## Partage des parcours

La fonction d'export / import actuelle concerne les parcours.

Usage type :

1. un utilisateur cree ses parcours
2. il exporte le JSON
3. il envoie ce fichier a un autre utilisateur
4. l'autre utilisateur importe le fichier dans `Administration`

Comportement actuel de l'import :

- les parcours importes sont ajoutes aux parcours deja presents
- l'import ne remplace pas automatiquement les parcours existants
- seules les definitions de parcours sont exportees, pas les parties

## Limites actuelles

- pas d'edition d'une partie terminee
- pas d'export des parties
- pas de synchronisation entre appareils
- logique metier concentree dans un seul fichier Kotlin

## Evolutions probables

- export / import complet des parties
- refactorisation en `ViewModel` + couches separees
- sauvegarde plus structuree via Room si le modele grossit
