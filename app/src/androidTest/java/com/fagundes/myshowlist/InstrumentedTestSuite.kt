package com.fagundes.myshowlist

import com.fagundes.myshowlist.feat.catalog.presentation.catalog.CatalogScreenTest
import com.fagundes.myshowlist.feat.home.presentation.home.HomeScreenTest
import com.fagundes.myshowlist.feat.login.presentation.login.LoginScreenTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    LoginScreenTest::class,
    HomeScreenTest::class,
    CatalogScreenTest::class,
)
class InstrumentedTestSuite
