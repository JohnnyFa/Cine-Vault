package com.fagundes.myshowlist

import com.fagundes.myshowlist.feat.catalog.ui.CatalogScreenTest
import com.fagundes.myshowlist.feat.home.ui.HomeScreenTest
import com.fagundes.myshowlist.feat.login.ui.LoginScreenTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    LoginScreenTest::class,
    HomeScreenTest::class,
    CatalogScreenTest::class,
)
class InstrumentedTestSuite
