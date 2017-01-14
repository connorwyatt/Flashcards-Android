/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.helpers.auth

import com.google.firebase.auth.FirebaseUser
import io.connorwyatt.flashcards.helpers.auth.exceptions.AuthException

class AuthOperationResult
{
    var user: FirebaseUser? = null
        private set
    var exception: AuthException? = null
        private set
    val isSuccessful: Boolean

    constructor(user: FirebaseUser)
    {
        this.user = user
        this.isSuccessful = true
    }

    constructor(exception: AuthException)
    {
        this.exception = exception
        this.isSuccessful = false
    }
}
