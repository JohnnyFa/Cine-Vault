---
name: add-remote-endpoint
description: Wire a new TMDB or Jikan API endpoint end to end in MyShowList — Ktor call, DTO, mapper, data source, repository. Use when adding any new remote data fetch, a new field from an existing endpoint, or a new API class.
---

# Adding a remote endpoint

The chain is fixed. Skipping a link (calling the API from a ViewModel, returning a DTO from a repository) is what breaks this codebase.

```
Api (Ktor)  ->  RemoteDataSource  ->  Repository  ->  ViewModel
   DTO      ->      domain model
```

## 1. Pick the right client

Two Ktor clients live behind Koin qualifiers in `core/di/AppModule.kt`:

| API | Qualifier | Base URL (per flavor) |
|---|---|---|
| TMDB (`MovieApi`) | `named("TmdbClient")` | `BuildConfig.TMDB_BASE_URL` |
| Jikan (`AnimeApi`) | `named("JikanClient")` | `BuildConfig.JIKAN_BASE_URL` |

Resolving the wrong one compiles fine and fails at runtime with a 404. If you add a third API, give it its own qualifier.

## 2. Add the call to the Api class

`core/data/remote/api/MovieApi.kt` — paths are relative; the base URL and the TMDB api_key are already on the client.

```kotlin
suspend fun getSimilarMovies(id: Int): TmdbResponse =
    client.get("movie/$id/similar").body()

suspend fun searchMovies(query: String): TmdbResponse =
    client.get("search/movie") { parameter("query", query) }.body()
```

Never append the API key by hand.

## 3. DTO

`core/data/remote/dto/` for objects, `core/data/remote/response/` for envelopes (`TmdbResponse`, `JikanResponse`).

```kotlin
@Serializable
data class SomeDto(
    val id: Int,
    val title: String,
    @SerialName("poster_path") val posterPath: String? = null,
)
```

- `@Serializable` from kotlinx.serialization.
- `@SerialName` for TMDB's snake_case.
- **Give every field a default or make it nullable.** The client sets `ignoreUnknownKeys = true`, which tolerates extra fields but not missing ones — a required field absent from the response throws `MissingFieldException` at parse time.
- If the DTO is only reachable through reflection-free kotlinx serialization it survives R8, but verify with `/release-check` before shipping.

## 4. Mapper

Extension function in `core/data/mapper/` (`MovieMapper.kt`, `AnimeMapper.kt`):

```kotlin
fun SomeDto.toMovie() = Movie(
    id = id,
    title = title,
    posterUrl = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
    ...
)
```

Domain models live in `core/domain/` (`Movie`, `Anime`, `Content`). Mapping never happens in a ViewModel or a composable.

## 5. RemoteDataSource

`feat/<feature>/data/remote/` — interface plus `Impl`. It calls the Api and returns **domain models**, not DTOs:

```kotlin
interface HomeRemoteDataSource {
    suspend fun getPopularMovies(): List<Movie>
}

class HomeRemoteDataSourceImpl(private val movieApi: MovieApi) : HomeRemoteDataSource {
    override suspend fun getPopularMovies(): List<Movie> =
        movieApi.getPopularMovies().results.map { it.toMovie() }
}
```

## 6. Repository

Match the existing return-type convention — this is the part most often got wrong:

- **One-shot `suspend`** → `Result<T>`, built with `runCatching { }`. Do the mapping *inside* the `runCatching` so parse failures are captured too.
  ```kotlin
  override suspend fun searchMoviesByName(query: String): Result<List<Movie>> =
      runCatching { movieApi.searchMovies(query).results.map { it.toMovie() } }
  ```
- **Observation** → `Flow<T>` unwrapped, sourced from Room with `.distinctUntilChanged()`. If the data should be cached, write it to the DAO and observe the DAO; don't observe the network.

For cached data follow `HomeRepositoryImpl.refreshHomeIfNeeded()`: compare `cachedAt` against `CACHE_DURATION`, fetch only when stale, write to the DAO, and let the UI observe the DAO.

## 7. Register in Koin

`core/di/AppModule.kt`, in the matching commented section:

```kotlin
single<SomeRemoteDataSource> { SomeRemoteDataSourceImpl(get()) }
single<SomeRepository> { SomeRepositoryImpl(local = get(), remote = get()) }
```

## 8. Test

Mock the repository interface with MockK in the ViewModel test. If the mapper has real logic (URL building, null handling, date parsing), give it its own test — mappers are pure functions and cheap to cover.

## Verify

```bash
./gradlew ktlintFormat
./gradlew assembleDevDebug testDevDebugUnitTest
```
