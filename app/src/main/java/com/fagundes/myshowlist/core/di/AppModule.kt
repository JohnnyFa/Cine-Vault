package com.fagundes.myshowlist.core.di

import androidx.room.Room
import com.fagundes.myshowlist.core.data.local.dao.ContentDao
import com.fagundes.myshowlist.core.data.local.dao.FavoriteDao
import com.fagundes.myshowlist.core.data.local.dao.MovieDetailCacheDao
import com.fagundes.myshowlist.core.data.local.dao.RecentDao
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.data.remote.api.AnimeApi
import com.fagundes.myshowlist.core.data.remote.api.MovieApi
import com.fagundes.myshowlist.core.db.AppDatabase
import com.fagundes.myshowlist.core.db.MIGRATION_3_4
import com.fagundes.myshowlist.core.db.MIGRATION_4_5
import com.fagundes.myshowlist.core.network.provideJikanHttpClient
import com.fagundes.myshowlist.core.network.provideTmdbHttpClient
import com.fagundes.myshowlist.feat.catalog.data.repository.CatalogRepository
import com.fagundes.myshowlist.feat.catalog.data.repository.CatalogRepositoryImpl
import com.fagundes.myshowlist.feat.catalog.vm.CatalogViewModel
import com.fagundes.myshowlist.feat.catalog.vm.UpcomingViewModel
import com.fagundes.myshowlist.feat.detail.data.repository.DetailRepository
import com.fagundes.myshowlist.feat.detail.data.repository.DetailRepositoryImpl
import com.fagundes.myshowlist.feat.detail.domain.usecase.ObserveFavoriteStateUseCase
import com.fagundes.myshowlist.feat.detail.domain.usecase.ToggleFavoriteUseCase
import com.fagundes.myshowlist.feat.detail.vm.DetailViewModel
import com.fagundes.myshowlist.feat.home.data.local.datasource.HomeLocalDataSource
import com.fagundes.myshowlist.feat.home.data.local.datasource.HomeLocalDataSourceImpl
import com.fagundes.myshowlist.feat.home.data.remote.HomeRemoteDataSource
import com.fagundes.myshowlist.feat.home.data.remote.HomeRemoteDataSourceImpl
import com.fagundes.myshowlist.feat.home.data.repository.FavoriteRepository
import com.fagundes.myshowlist.feat.home.data.repository.FavoriteRepositoryImpl
import com.fagundes.myshowlist.feat.home.data.repository.HomeRepository
import com.fagundes.myshowlist.feat.home.data.repository.HomeRepositoryImpl
import com.fagundes.myshowlist.feat.home.data.repository.RecentRepository
import com.fagundes.myshowlist.feat.home.data.repository.RecentRepositoryImpl
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveFavoritesUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveRecentsUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.SaveRecentMovieUseCase
import com.fagundes.myshowlist.feat.home.vm.HomeViewModel
import com.fagundes.myshowlist.feat.login.data.FirebaseAuthRepository
import com.fagundes.myshowlist.feat.login.domain.AuthRepository
import com.fagundes.myshowlist.feat.login.domain.LoginWithGoogleUseCase
import com.fagundes.myshowlist.feat.login.vm.LoginViewModel
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearCacheUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearFavoritesUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearRecentsUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ClearUserDataUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ObserveFavoritesCountUseCase
import com.fagundes.myshowlist.feat.options.domain.usecase.ObserveRecentsCountUseCase
import com.fagundes.myshowlist.feat.options.vm.OptionsViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule =
    module {

        // ---------- Firebase ----------
        single { FirebaseAuth.getInstance() }

        // ---------- Auth ----------
        single<AuthRepository> { FirebaseAuthRepository(get()) }
        factory { LoginWithGoogleUseCase(get()) }

        // ---------- HttpClients ----------
        val tmdbClient = named("TmdbClient")
        val jikanClient = named("JikanClient")

        single(tmdbClient) { provideTmdbHttpClient() }
        single(jikanClient) { provideJikanHttpClient() }

        // ---------- Database ----------
        single {
            Room.databaseBuilder(
                androidContext(),
                AppDatabase::class.java,
                "myshowlist.db",
            ).addMigrations(MIGRATION_3_4, MIGRATION_4_5).fallbackToDestructiveMigration(false).build()
        }

        single<ContentDao> { get<AppDatabase>().contentDao() }
        single<FavoriteDao> { get<AppDatabase>().favoriteDao() }
        single<RecentDao> { get<AppDatabase>().recentDao() }
        single<MovieDetailCacheDao> { get<AppDatabase>().movieDetailCacheDao() }
        single { Dispatchers.IO }

        // ---------- APIs ----------
        single { MovieApi(get(tmdbClient)) }
        single { AnimeApi(get(jikanClient)) }

        // ---------- Remote DataSource ----------
        single<HomeRemoteDataSource> {
            HomeRemoteDataSourceImpl(movieApi = get())
        }

        // ---------- Local DataSource ----------
        single<HomeLocalDataSource> {
            HomeLocalDataSourceImpl(get())
        }

        // ---------- Repository ----------
        single<HomeRepository> {
            HomeRepositoryImpl(
                local = get(),
                remote = get(),
            )
        }

        single<CatalogRepository> {
            CatalogRepositoryImpl(
                movieApi = get(),
                local = get(),
            )
        }

        single<DetailRepository> {
            DetailRepositoryImpl(get(), get(), get())
        }

        single<FavoriteRepository> {
            FavoriteRepositoryImpl(get())
        }

        single<RecentRepository> {
            RecentRepositoryImpl(get())
        }

        factory { ObserveFavoriteStateUseCase(get()) }
        factory { ToggleFavoriteUseCase(get()) }
        factory { ObserveFavoritesUseCase(get()) }
        factory { SaveRecentMovieUseCase(get()) }
        factory { ObserveRecentsUseCase(get()) }
        factory { ClearUserDataUseCase(get(), get()) }
        factory { ObserveFavoritesCountUseCase(get()) }
        factory { ObserveRecentsCountUseCase(get()) }
        factory { ClearFavoritesUseCase(get()) }
        factory { ClearRecentsUseCase(get()) }
        factory { ClearCacheUseCase(get(), get()) }

        // ---------- ViewModels ----------
        viewModelOf(::LoginViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::CatalogViewModel)
        viewModelOf(::UpcomingViewModel)
        viewModelOf(::OptionsViewModel)

        viewModel { (id: Int, type: ContentType) ->
            DetailViewModel(
                id = id,
                type = type,
                repository = get(),
                observeFavoriteStateUseCase = get(),
                toggleFavoriteUseCase = get(),
                saveRecentMovieUseCase = get(),
            )
        }
    }
