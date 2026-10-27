package com.fagundes.myshowlist.core.domain

/**
 * Returned by features that need a signed-in account when the session is a guest (or nobody).
 */
class SignInRequiredException : Exception("This feature requires a signed-in account")
