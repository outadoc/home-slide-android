package fr.outadoc.homeslide.hassapi.repository

import fr.outadoc.homeslide.hassapi.model.auth.Token

interface AuthRepository {

    suspend fun getToken(code: String): Result<Token>

    suspend fun refreshToken(): Result<Token>
}