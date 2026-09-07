package com.fagundes.myshowlist.core.di

import androidx.room.Room
import com.fagundes.myshowlist.core.data.local.dao.ContentDao
import com.fagundes.myshowlist.core.data.local.dao.FavoriteDao
import com.fagundes.myshowlist.core.data.local.dao.MovieDetailCacheDao
import com.fagundes.myshowlist.core.data.local.dao.RecentDao
import com.fagundes.myshowlist.core.data.local.datasource.ContentLocalDataSource
import com.fagundes.myshowlist.core.data.local.datasource.ContentLocalDataSourceImpl
import com.fagundes.myshowlist.core.data.local.datasource.FavoriteLocalDataSource
import com.fagundes.myshowlist.core.data.local.datasource.FavoriteLocalDataSourceImpl
import com.fagundes.myshowlist.core.data.local.datasource.RecentLocalDataSource
import com.fagundes.myshowlist.core.data.local.datasource.RecentLocalDataSourceImpl
import com.fagundes.myshowlist.core.data.local.enum.ContentType
import com.fagundes.myshowlist.core.data.remote.api.AnimeApi
import com.fagundes.myshowlist.core.data.remote.api.MovieApi
import com.fagundes.myshowlist.core.data.repository.FavoriteRepositoryImpl
import com.fagundes.myshowlist.core.data.repository.RecentRepositoryImpl
import com.fagundes.myshowlist.core.db.AppDatabase
import com.fagundes.myshowlist.core.db.MIGRATION_3_4
import com.fagundes.myshowlist.core.db.MIGRATION_4_5
import com.fagundes.myshowlist.core.domain.repository.FavoriteRepository
import com.fagundes.myshowlist.core.domain.repository.RecentRepository
import com.fagundes.myshowlist.core.network.provideJikanHttpClient
import com.fagundes.myshowlist.core.network.provideTmdbHttpClient
import com.fagundes.myshowlist.feat.catalog.data.remote.CatalogRemoteDataSource
import com.fagundes.myshowlist.feat.catalog.data.remote.CatalogRemoteDataSourceImpl
import com.fagundes.myshowlist.feat.catalog.data.repository.CatalogRepositoryImpl
import com.fagundes.myshowlist.feat.catalog.domain.repository.CatalogRepository
import com.fagundes.myshowlist.feat.catalog.domain.usecase.GetMoviesByGenreUseCase
import com.fagundes.myshowlist.feat.catalog.domain.usecase.GetUpcomingMoviesUseCase
import com.fagundes.myshowlist.feat.catalog.domain.usecase.SearchMoviesUseCase
import com.fagundes.myshowlist.feat.catalog.presentation.catalog.CatalogViewModel
import com.fagundes.myshowlist.feat.catalog.presentation.upcoming.UpcomingViewModel
import com.fagundes.myshowlist.feat.detail.data.local.DetailLocalDataSource
import com.fagundes.myshowlist.feat.detail.data.local.DetailLocalDataSourceImpl
import com.fagundes.myshowlist.feat.detail.data.remote.DetailRemoteDataSource
import com.fagundes.myshowlist.feat.detail.data.remote.DetailRemoteDataSourceImpl
import com.fagundes.myshowlist.feat.detail.data.repository.DetailRepositoryImpl
import com.fagundes.myshowlist.feat.detail.domain.repository.DetailRepository
import com.fagundes.myshowlist.feat.detail.domain.usecase.ObserveContentDetailUseCase
import com.fagundes.myshowlist.feat.detail.domain.usecase.ObserveFavoriteStateUseCase
import com.fagundes.myshowlist.feat.detail.domain.usecase.RefreshContentDetailUseCase
import com.fagundes.myshowlist.feat.detail.domain.usecase.SaveRecentMovieUseCase
import com.fagundes.myshowlist.feat.detail.domain.usecase.ToggleFavoriteUseCase
import com.fagundes.myshowlist.feat.detail.presentation.detail.DetailViewModel
import com.fagundes.myshowlist.feat.home.data.remote.HomeRemoteDataSource
import com.fagundes.myshowlist.feat.home.data.remote.HomeRemoteDataSourceImpl
import com.fagundes.myshowlist.feat.home.data.repository.HomeRepositoryImpl
import com.fagundes.myshowlist.feat.home.domain.repository.HomeRepository
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveFavoritesUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveRecentsUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveRecommendedMoviesUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveShowOfTheDayUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.ObserveTrendingMoviesUseCase
import com.fagundes.myshowlist.feat.home.domain.usecase.RefreshHomeUseCase
import com.fagundes.myshowlist.feat.home.presentation.home.HomeViewModel
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

        single<CatalogRemoteDataSource> {
            CatalogRemoteDataSourceImpl(movieApi = get())
        }

        single<DetailRemoteDataSource> {
            DetailRemoteDataSourceImpl(movieApi = get())
        }

        // ---------- Local DataSource ----------
        single<ContentLocalDataSource> {
            ContentLocalDataSourceImpl(get())
        }

        single<FavoriteLocalDataSource> {
            FavoriteLocalDataSourceImpl(get())
        }

        single<RecentLocalDataSource> {
            RecentLocalDataSourceImpl(get())
        }

        single<DetailLocalDataSource> {
            DetailLocalDataSourceImpl(detailCacheDao = get(), favoriteDao = get())
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
                remote = get(),
                local = get(),
            )
        }

        single<DetailRepository> {
            DetailRepositoryImpl(
                remote = get(),
                local = get(),
            )
        }

        single<FavoriteRepository> {
            FavoriteRepositoryImpl(local = get())
        }

        single<RecentRepository> {
            RecentRepositoryImpl(local = get())
        }

        // ---------- UseCases ----------
        factory { ObserveContentDetailUseCase(get()) }
        factory { RefreshContentDetailUseCase(get()) }
        factory { ObserveFavoriteStateUseCase(get()) }
        factory { ToggleFavoriteUseCase(get()) }
        factory { ObserveTrendingMoviesUseCase(get()) }
        factory { ObserveRecommendedMoviesUseCase(get()) }
        factory { ObserveShowOfTheDayUseCase(get()) }
        factory { RefreshHomeUseCase(get()) }
        factory { ObserveFavoritesUseCase(get()) }
        factory { SaveRecentMovieUseCase(get()) }
        factory { ObserveRecentsUseCase(get()) }
        factory { ClearUserDataUseCase(get(), get()) }
        factory { ObserveFavoritesCountUseCase(get()) }
        factory { ObserveRecentsCountUseCase(get()) }
        factory { ClearFavoritesUseCase(get()) }
        factory { ClearRecentsUseCase(get()) }
        factory { ClearCacheUseCase(get(), get()) }
        factory { GetUpcomingMoviesUseCase(get()) }
        factory { GetMoviesByGenreUseCase(get()) }
        factory { SearchMoviesUseCase(get()) }

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
                observeContentDetail = get(),
                refreshContentDetail = get(),
                observeFavoriteState = get(),
                toggleFavorite = get(),
                saveRecentMovie = get(),
            )
        }
    }
