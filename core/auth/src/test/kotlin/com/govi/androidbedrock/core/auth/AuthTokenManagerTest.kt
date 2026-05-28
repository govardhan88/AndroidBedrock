package com.govi.androidbedrock.core.auth

import android.content.SharedPreferences
import com.govi.androidbedrock.core.utils.AuthConstants
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class AuthTokenManagerTest {

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var authTokenManager: AuthTokenManager

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        authTokenManager = AuthTokenManager(sharedPreferences)
        whenever(sharedPreferences.edit()).thenReturn(editor)
        whenever(editor.putString(AuthConstants.KEY_ACCESS_TOKEN, "test_token")).thenReturn(editor)
    }

    @Test
    fun testSetAndGetAccessToken() {
        whenever(sharedPreferences.getString(AuthConstants.KEY_ACCESS_TOKEN, null)).thenReturn("test_token")
        authTokenManager.setAccessToken("test_token")
        assertEquals("test_token", authTokenManager.getAccessToken())
    }

    @Test
    fun testIsAuthenticated() {
        whenever(sharedPreferences.getString(AuthConstants.KEY_ACCESS_TOKEN, null)).thenReturn("test_token")
        authTokenManager.setAccessToken("test_token")
        assertTrue(authTokenManager.isAuthenticated())
    }

    @Test
    fun testClearTokens() {
        authTokenManager.clearTokens()
        whenever(sharedPreferences.getString(AuthConstants.KEY_ACCESS_TOKEN, null)).thenReturn(null)
        assertFalse(authTokenManager.isAuthenticated())
    }
}
