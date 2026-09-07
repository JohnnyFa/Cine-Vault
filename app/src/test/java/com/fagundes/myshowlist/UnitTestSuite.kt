package com.fagundes.myshowlist

import com.fagundes.myshowlist.core.data.local.mapper.ContentMapperTest
import com.fagundes.myshowlist.core.data.repository.CacheRepositoryImplTest
import com.fagundes.myshowlist.feat.catalog.domain.usecase.GetMoviesByGenreUseCaseTest
import com.fagundes.myshowlist.feat.catalog.presentation.catalog.CatalogViewModelTest
import com.fagundes.myshowlist.feat.catalog.presentation.upcoming.UpcomingViewModelTest
import com.fagundes.myshowlist.feat.detail.presentation.detail.DetailViewModelTest
import com.fagundes.myshowlist.feat.home.presentation.home.HomeViewModelTest
import com.fagundes.myshowlist.feat.login.vm.LoginViewModelTest
import com.fagundes.myshowlist.feat.options.vm.OptionsViewModelTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    ContentMapperTest::class,
    CacheRepositoryImplTest::class,
    LoginViewModelTest::class,
    HomeViewModelTest::class,
    CatalogViewModelTest::class,
    UpcomingViewModelTest::class,
    GetMoviesByGenreUseCaseTest::class,
    DetailViewModelTest::class,
    OptionsViewModelTest::class,
)
class UnitTestSuite
