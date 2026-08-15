---
paths:
  - "app/src/main/java/com/fagundes/myshowlist/feat/**/data/**/*.kt"
  - "app/src/main/java/com/fagundes/myshowlist/feat/**/domain/**/*.kt"
  - "app/src/main/java/com/fagundes/myshowlist/core/data/**/*.kt"
  - "app/src/main/java/com/fagundes/myshowlist/core/db/**/*.kt"
  - "app/src/main/java/com/fagundes/myshowlist/core/network/**/*.kt"
  - "app/src/main/java/com/fagundes/myshowlist/core/domain/**/*.kt"
---

# Data layer rules

## Flow of data

```
MovieApi / AnimeApi (Ktor)  ->  <Feature>RemoteDataSource  ->  <Feature>Repository
ContentDao / FavoriteDao …  ->  <Feature>LocalDataSource   ->  ^
```

Room is the source of truth for anything cached. Repositories observe the DAO and refresh from the network on demand — see `HomeRepositoryImpl.refreshHomeIfNeeded()`, which checks `cachedAt` against `CACHE_DURATION` before hitting the network.

## Return types

Two distinct conventions — apply the right one:

- **One-shot `suspend` functions return `Result<T>`.**
  `CatalogRepository.getMoviesByCategory(): Result<List<Movie>>`, `AuthRepository.signInWithGoogle(): Result<Unit>`.
  Build them with `runCatching { }` in the `Impl`, and map DTOs inside the `runCatching` block so parse failures are captured too.
- **Observation functions return `Flow<T>` unwrapped.**
  `HomeRepository.observePopularMovies(): Flow<List<Movie>>`, `DetailRepository.observeFavoriteState(): Flow<Boolean>`.
  A cold DB flow does not fail the way a network call does; don't wrap it in `Result`. Apply `.distinctUntilChanged()` so the UI doesn't recompose on identical emissions.

Never return a DTO or a Room entity from a repository — only `core/domain` models (`Movie`, `Anime`, `Content`) or feature domain models.

## Split interface / implementation

`HomeRepository` (interface, in `data/repository/`) + `HomeRepositoryImpl`. The interface is what Koin binds and what tests mock with MockK.

## Mappers

Extension functions, one file per model:
- DTO → domain: `core/data/mapper/MovieMapper.kt` (`MovieDto.toMovie()`)
- entity ↔ domain: `core/data/local/mapper/` (`ContentEntity.toMovie()`, `Movie.toEntity(type, category)`)

Add the mapper next to its siblings; don't inline mapping logic in a repository or ViewModel.

## Ktor

- `baseHttpClient()` in `core/network/BaseHttpClient.kt` installs `ContentNegotiation` with `ignoreUnknownKeys = true` and `Logging` gated on `BuildConfig.LOGGING_ENABLED`.
- Two configured clients, distinguished by Koin qualifiers: `named("TmdbClient")` and `named("JikanClient")`. `MovieApi` gets the TMDB one, `AnimeApi` the Jikan one. Base URLs come from `BuildConfig.TMDB_BASE_URL` / `JIKAN_BASE_URL`, which are per-flavor.
- API classes take an `HttpClient` and expose `suspend fun` returning a DTO or response wrapper. Paths are relative (`client.get("movie/popular")`) — the base URL is on the client.
- Never hardcode the API key in a request; it's injected by the client config from `BuildConfig.TMDB_API_KEY`.

## Room

- Entities in `core/data/local/entity/`, DAOs in `core/data/local/dao/`, enums + converters in `core/data/local/enum/`.
- DAO reads that feed the UI return `Flow<T>`.
- **Any schema change requires a migration.** `AppDatabase` is at `version = 5` with hand-written `MIGRATION_3_4` / `MIGRATION_4_5` objects in `AppDatabase.kt`, registered in `AppModule.kt` via `.addMigrations(...)`. Bumping the version without a migration destroys user data — `fallbackToDestructiveMigration(false)` is set, so it will throw instead. Use the `room-migration` skill.
