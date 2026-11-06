package govi.core.network.auth

/**
 * Pluggable contract for providing and refreshing authentication tokens.
 *
 * Applications requiring Bearer token authentication can implement this interface
 * and bind it in Dagger Hilt. If unbound, OkHttp automatically omits the auth
 * interceptor and authenticator with zero overhead.
 */
interface AuthTokenProvider {

    /**
     * Retrieves the current cached access token, or null if unauthenticated.
     */
    suspend fun getAccessToken(): String?

    /**
     * Refreshes the access token using the stored refresh token.
     * Returns [Result.success] with the new access token, or [Result.failure] if refresh fails.
     */
    suspend fun refreshToken(): Result<String>

    /**
     * Callback invoked when token refresh fails permanently (e.g. invalid refresh token / 401).
     * Applications should clear session state and redirect to the login screen.
     */
    suspend fun onAuthFailed()
}
