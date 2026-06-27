package com.fagundes.myshowlist

import com.fagundes.myshowlist.feat.catalog.vm.CatalogViewModelTest
import com.fagundes.myshowlist.feat.catalog.vm.UpcomingViewModelTest
import com.fagundes.myshowlist.feat.home.vm.HomeViewModelTest
import com.fagundes.myshowlist.feat.login.vm.LoginViewModelTest
import com.fagundes.myshowlist.feat.options.vm.OptionsViewModelTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    LoginViewModelTest::class,
    HomeViewModelTest::class,
    CatalogViewModelTest::class,
    UpcomingViewModelTest::class,
    OptionsViewModelTest::class,
)
class UnitTestSuite
