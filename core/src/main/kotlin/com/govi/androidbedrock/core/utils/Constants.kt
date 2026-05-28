package com.govi.androidbedrock.core.utils

object NetworkConstants {
    const val DEFAULT_BASE_URL = "https://api.example.com/v1/"
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
    
    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_BEARER = "Bearer"
    const val HEADER_SKIP_AUTH = "X-Skip-Auth"
    
    const val CONTENT_TYPE_JSON = "application/json"
}

object AuthConstants {
    const val KEY_ACCESS_TOKEN = "auth_access_token"
    const val KEY_REFRESH_TOKEN = "auth_refresh_token"
    const val KEY_TOKEN_EXPIRY = "auth_token_expiry"
    
    const val PATH_LOGIN = "auth/login"
    const val PATH_REFRESH = "auth/refresh"
    const val PATH_HEALTH = "health"
}

object StorageConstants {
    const val PREFS_SECURE_NAME = "secure_app_prefs"
}
