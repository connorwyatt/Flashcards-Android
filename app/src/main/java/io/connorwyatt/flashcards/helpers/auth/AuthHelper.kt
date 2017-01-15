/*
 * Copyright (c) 2016-2017 Connor Wyatt <connorwyatt1@gmail.com>.
 *
 * This file can not be copied and/or distributed without the express permission of Connor Wyatt.
 */

package io.connorwyatt.flashcards.helpers.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import io.connorwyatt.flashcards.helpers.auth.exceptions.AuthException
import io.connorwyatt.flashcards.helpers.auth.exceptions.EmailAlreadyInUseException
import io.connorwyatt.flashcards.helpers.auth.exceptions.InvalidEmailException
import io.connorwyatt.flashcards.helpers.auth.exceptions.UserDisabledException
import io.connorwyatt.flashcards.helpers.auth.exceptions.UserNotFoundException
import io.connorwyatt.flashcards.helpers.auth.exceptions.WeakPasswordException

class AuthHelper private constructor(private val firebaseAuth: FirebaseAuth) {
  val currentUser: FirebaseUser?
    get() = firebaseAuth.currentUser
  val isSignedIn: Boolean
    get() = currentUser !== null

  fun loginWithEmailAndPassword(email: String,
                                password: String,
                                onComplete: ((AuthOperationResult) -> Unit)?): Unit {
    val result = firebaseAuth.signInWithEmailAndPassword(email, password)

    result.addOnCompleteListener(getOnAuthCompleteListener(onComplete))
  }

  fun registerWithEmailAndPassword(email: String,
                                   password: String,
                                   onComplete: ((AuthOperationResult) -> Unit)?): Unit {
    val result = firebaseAuth.createUserWithEmailAndPassword(email, password)

    result.addOnCompleteListener(getOnAuthCompleteListener(onComplete))
  }

  fun signOut(): Unit {
    firebaseAuth.signOut()
  }

  private fun getExceptionFromAuthResultException(exception: Exception): AuthException {
    return when (exception) {
      is FirebaseAuthWeakPasswordException -> WeakPasswordException()
      is FirebaseAuthInvalidUserException -> {
        when (exception.errorCode) {
          "ERROR_USER_NOT_FOUND" -> UserNotFoundException()
          "ERROR_USER_DISABLED" -> UserDisabledException()
          else -> AuthException()
        }
      }
      is FirebaseAuthUserCollisionException -> EmailAlreadyInUseException()
      is FirebaseAuthInvalidCredentialsException -> InvalidEmailException()
      else -> AuthException()
    }
  }

  private fun getOnAuthCompleteListener(
    onComplete: ((AuthOperationResult) -> Unit)?): (Task<AuthResult>) -> Unit {
    return fun(result: Task<AuthResult>) {
      if (result.isSuccessful) {
        onComplete?.invoke(AuthOperationResult(result.result.user))
      } else {
        onComplete?.let { onComplete ->
          val exception = getExceptionFromAuthResultException(result.exception!!)

          onComplete.invoke(AuthOperationResult(exception))
        }
      }
    }
  }

  companion object {
    private val _instance: AuthHelper = AuthHelper(FirebaseAuth.getInstance())

    fun getInstance(): AuthHelper = _instance
  }
}
