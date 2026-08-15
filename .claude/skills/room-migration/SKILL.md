---
name: room-migration
description: Change the Room database schema in MyShowList — add or modify an entity, column, index, or DAO query. Covers the version bump, hand-written Migration, and AppModule registration required to avoid destroying user data.
---

# Changing the Room schema

**A schema change without a matching migration throws at app start.** `fallbackToDestructiveMigration(false)` is set in `core/di/AppModule.kt`, so Room will not silently wipe and recreate — it crashes with `IllegalStateException: A migration from N to N+1 was required but not found`. That's the safe failure, but it's still a failure.

Current state: `AppDatabase` is at `version = 5`, with `MIGRATION_3_4` and `MIGRATION_4_5` hand-written in `core/db/AppDatabase.kt`.

## The four edits

Every one of these is required. Missing #4 is the classic bug — the migration exists but is never registered.

### 1. Change the entity

`core/data/local/entity/`. Nullable columns are far easier to migrate than non-null ones — a new non-null column needs a `DEFAULT` in the `ALTER TABLE`.

### 2. Bump the version

`core/db/AppDatabase.kt`:
```kotlin
@Database(entities = [...], version = 6)
```

### 3. Write the migration

Same file, following the existing style:

```kotlin
val MIGRATION_5_6 =
    object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `content` ADD COLUMN `tagline` TEXT")
        }
    }
```

For anything SQLite's `ALTER TABLE` can't do — changing a primary key, dropping a column, changing a type — use the create/copy/drop/rename dance that `MIGRATION_4_5` demonstrates:

1. `CREATE TABLE IF NOT EXISTS content_new (...)` with the new shape
2. `INSERT OR REPLACE INTO content_new (...) SELECT ... FROM content` — name every column explicitly; supply literals (`NULL`, `0`) for new ones
3. `DROP TABLE content`
4. `ALTER TABLE content_new RENAME TO content`
5. **Recreate every index.** They do not survive the rename:
   `CREATE INDEX IF NOT EXISTS index_content_category ON content (category)`

The new table's SQL must match what Room generates for the entity **exactly** — column order, types, nullability, and the primary key. A mismatch fails Room's identity-hash check at open time with a diff in the message; read that diff, it tells you precisely which column is wrong.

### 4. Register it

`core/di/AppModule.kt`:
```kotlin
.addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
```

## Type mapping

| Kotlin | SQLite |
|---|---|
| `Int`, `Long`, `Boolean` | `INTEGER` |
| `Double`, `Float` | `REAL` |
| `String`, enum via `ContentTypeConverter` | `TEXT` |

Non-null Kotlin types get `NOT NULL` in the SQL. Enums are stored as `TEXT` through the converter registered in `@TypeConverters`.

## Testing the migration

There is no Room migration test infrastructure in this project yet, and `room.schemaLocation` is configured (`$projectDir/schemas`) but `app/schemas/` has not been generated — exported schemas only appear after a build following a version change. To test properly you would add `androidx.room:room-testing` and a `MigrationTestHelper` instrumented test.

At minimum, verify by hand before shipping:

1. Install the **previous** version: `git stash && ./gradlew installDevDebug`
2. Open the app so the old DB is created and populated.
3. Restore your change and install over the top — do **not** uninstall, that defeats the test: `git stash pop && ./gradlew installDevDebug`
4. Open the app. If it starts and the data is intact, the migration works.
5. Check for a crash with `adb logcat -d -t 200 *:E`.

## Verify

```bash
./gradlew assembleDevDebug   # KSP regenerates the Room implementation
./gradlew testDevDebugUnitTest
```

If the generated schema JSON appears under `app/schemas/`, commit it — it's the record of each version's shape.
