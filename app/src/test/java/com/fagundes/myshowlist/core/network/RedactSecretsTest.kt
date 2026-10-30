package com.fagundes.myshowlist.core.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class RedactSecretsTest {
    @Test
    fun `the api_key value should be masked in a logged URL`() {
        val logged = "REQUEST: https://api.themoviedb.org/3/movie/popular?api_key=abc123def456&language=en-US"

        assertEquals(
            "REQUEST: https://api.themoviedb.org/3/movie/popular?api_key=***&language=en-US",
            redactSecrets(logged),
        )
    }

    @Test
    fun `a key at the end of the URL should be masked too`() {
        val logged = "RESPONSE https://api.themoviedb.org/3/movie/1?api_key=abc123def456 failed with exception"

        assertFalse(redactSecrets(logged).contains("abc123def456"))
        assertEquals("RESPONSE https://api.themoviedb.org/3/movie/1?api_key=*** failed with exception", redactSecrets(logged))
    }

    @Test
    fun `every occurrence in a message should be masked`() {
        val logged = "a?api_key=one&x=1 b?api_key=two"

        assertEquals("a?api_key=***&x=1 b?api_key=***", redactSecrets(logged))
    }

    @Test
    fun `a message without a key should pass through unchanged`() {
        val logged = "BODY Content-Type: application/json; charset=utf-8"

        assertEquals(logged, redactSecrets(logged))
    }
}
