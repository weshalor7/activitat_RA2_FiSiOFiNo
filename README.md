# Fisiofino

Aplicació Java (Maven) amb PostgreSQL. Les bases de dades s'executen amb Docker.

## Posada en marxa

1. Copia les plantilles i posa-hi la teva contrasenya (la mateixa als dos fitxers):

   ```bash
   cp .env.example .env
   cp conn.properties.example conn.properties
   ```

2. Arrenca les bases de dades (cal tenir Docker Desktop en marxa):

   ```bash
   docker compose up -d
   ```

3. Executa els tests:

   ```bash
   mvn test
   ```

## Què hi ha

| Fitxer | Per a què serveix | Es puja al repositori? |
|---|---|---|
| `docker-compose.yml` | Crea dos PostgreSQL amb les taules de `schema.sql` | Sí |
| `.env` | Usuari i contrasenya de la BD de l'app (el llegeix Docker) | **No** |
| `conn.properties` | Connexió de l'aplicació (el llegeix el programa) | **No** |
| `conn.test.properties` | Connexió dels tests (dades públiques) | Sí |
| `*.example` | Plantilles sense contrasenya | Sí |

| BD | Port | Per a qui |
|---|---|---|
| `fisiofino_app` | 5432 | L'aplicació |
| `fisiofino_test` | 5433 | Els tests (els tests la buiden, mai la de l'app) |

## Compte

- La contrasenya del `.env` i la del `conn.properties` han de ser **iguals**.
- Executa l'aplicació des de l'**arrel del projecte**: és on busca `conn.properties`.
- **Mai** pugis al repositori `.env` ni `conn.properties`.
- Les taules es creen només la primera vegada. Si canvies `schema.sql` o la contrasenya,
  recrea la BD de l'app (**esborra les dades**):

  ```bash
  docker compose down -v
  docker compose up -d
  ```

## Agafar un commit del repositori del professor

Sense fer `pull` i sense perdre els teus canvis:

1. Desa la teva feina (`git commit`) o guarda-la temporalment (`git stash`), perquè
   no hi hagi canvis pendents.
2. Afegeix el repositori del professor (només la primera vegada):

   ```bash
   git remote add profe https://github.com/pau-lopez-itic/dam2-fisiofino
   ```

3. Baixa els commits (això no toca els teus fitxers) i agafa el que t'indiqui el professor:

   ```bash
   git fetch profe
   git cherry-pick <hash>
   ```

4. Si hi ha conflictes, resol-los com sempre, fes `git add` dels fitxers i continua:

   ```bash
   git cherry-pick --continue
   ```

   Per cancel·lar-ho tot i tornar a l'estat anterior: `git cherry-pick --abort`.
