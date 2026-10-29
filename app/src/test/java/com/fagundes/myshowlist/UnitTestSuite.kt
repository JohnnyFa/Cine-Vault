package com.fagundes.myshowlist

import com.fagundes.myshowlist.core.data.local.mapper.ContentMapperTest
import com.fagundes.myshowlist.core.data.repository.CacheRepositoryImplTest
import com.fagundes.myshowlist.feat.catalog.domain.usecase.GetMoviesByGenreUseCaseTest
import com.fagundes.myshowlist.feat.catalog.presentation.catalog.CatalogViewModelTest
import com.fagundes.myshowlist.feat.catalog.presentation.upcoming.UpcomingViewModelTest
import com.fagundes.myshowlist.feat.detail.domain.usecase.ToggleFavoriteUseCaseTest
import com.fagundes.myshowlist.feat.detail.presentation.detail.DetailViewModelTest
import com.fagundes.myshowlist.feat.home.data.repository.HomeRepositoryImplTest
import com.fagundes.myshowlist.feat.home.domain.model.TasteProfileTest
import com.fagundes.myshowlist.feat.home.domain.usecase.RefreshRecommendationsUseCaseTest
import com.fagundes.myshowlist.feat.home.presentation.home.HomeViewModelTest
import com.fagundes.myshowlist.feat.login.presentation.login.LoginViewModelTest
import com.fagundes.myshowlist.feat.options.presentation.options.OptionsViewModelTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    ContentMapperTest::class,
    CacheRepositoryImplTest::class,
    LoginViewModelTest::class,
    HomeViewModelTest::class,
    HomeRepositoryImplTest::class,
    TasteProfileTest::class,
    RefreshRecommendationsUseCaseTest::class,
    CatalogViewModelTest::class,
    UpcomingViewModelTest::class,
    GetMoviesByGenreUseCaseTest::class,
    DetailViewModelTest::class,
    ToggleFavoriteUseCaseTest::class,
    OptionsViewModelTest::class,
)
class UnitTestSuite
