# Outly

## Contesto
Piattaforma per la scoperta e partecipazione ad attività dal vivo con attivazione a quorum, accessibilità dinamica e side quest.
RAD: `docs/rad/` | SDD: `docs/sdd/` | Modelli: `docs/models/` | Test frame: `docs/tests/`

## Stack e comandi
- **Backend (`/backend`)**: Java 17, Spring Boot, MySQL 8, Maven, JUnit 5, Mockito, Checkstyle.
  - Build, checkstyle e test: `cd backend && mvn -B verify`
- **Client Mobile (`/android`)**: Android Studio, Java 17, XML, Gradle.
  - Build e test unitari: `cd android && ./gradlew testDebugUnitTest`

## Struttura del repository
- `backend/src/main/java/it/unisa/outly/`: package organizzati secondo la separazione architetturale definita in `docs/models/plantuml/20-architettura.puml` (Interface, Application Logic, Storage).
- `android/app/src/main/java/it/unisa/outly/`: UI (Activity/Fragment XML), client REST e modelli lato client.
- `docs/models/plantuml/`: diagrammi UML del progetto, inclusi class diagram, activity diagram, sequence diagram, statechart, architettura e deployment.
- `docs/models/plantuml/19-class-diagram.puml`: riferimento per le classi e gli attributi del modello di dominio. 
- `docs/models/plantuml/20-architettura.puml`: riferimento per la separazione dei componenti architetturali. 
- `docs/models/plantuml/21-sdd.puml`: riferimento per l'architettura di deployment.

## Convenzioni
- Classi, attributi, enumerazioni e firme dei metodi devono coincidere esattamente con `docs/models/plantuml/19-class-diagram.puml`.
- Nomi delle eccezioni applicative come definiti nei sequence diagrams in `docs/models/plantuml/` e nei casi d'uso (`docs/rad/uc`).
- Branch: `ucNN-nome-breve` (es. `uc03-crea-attivita`).
- Commit: `"UC_NN: descrizione sintetica"`.

## Regole
### Sempre
- Lavora esclusivamente nel branch del task.
- Rispetta la separazione dei componenti definita in `docs/models/plantuml/20-architettura.puml`.
- Rispetta i vincoli di deployment definiti in `docs/models/plantuml/21-sdd.puml`.
- Scrivi un test JUnit per ogni test frame assegnato nel task.
- Esegui `mvn -B verify` (per il backend) o `./gradlew testDebugUnitTest` (per Android) prima di chiudere il lavoro.

### Chiedi prima
- Nuove classi, attributi o package non previsti dal modello di dominio o dai diagrammi architetturali.
- Modifiche a classi condivise del package `model/` o all'interfaccia di persistenza.
- Aggiunta di nuove dipendenze in `backend/pom.xml` o `android/app/build.gradle`.
- Incoerenze tra caso d'uso, sequence diagram e class diagram: fermati e segnalale invece di inventare soluzioni.

### Mai
- Layer View/Controller che accede direttamente al database saltando Service/Repository.
- Modificare test esistenti per farli passare invece di correggere il codice.
- Push diretto su `main`.
- Credenziali, password del DB o chiavi API hardcoded nel codice (usa variabili d'ambiente o `application.properties` di test).

## Quando un task è finito
1. La build e l'analisi statica (`mvn -B verify` / `./gradlew testDebugUnitTest`) passano senza errori.
2. Ogni test frame indicato nell'issue ha il corrispondente test JUnit.
3. Nessun file fuori dall'ambito approvato è stato modificato.
