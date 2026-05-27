package com.example.androidbedrock.core.network

import android.content.SharedPreferences
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
        whenever(editor.putString("auth_access_token", "test_token")).thenReturn(editor)
        whenever(editor.apply()).thenReturn(Unit)
    }

    @Test
    fun testSetAndGetAccessToken() {
        whenever(sharedPreferences.getString("auth_access_token", null)).thenReturn("test_token")
        authTokenManager.setAccessToken("test_token")
        assertEquals("test_token", authTokenManager.getAccessToken())
    }

    @Test
    fun testIsAuthenticated() {
        whenever(sharedPreferences.getString("auth_access_token", null)).thenReturn("test_token")
        authTokenManager.setAccessToken("test_token")
        assertTrue(authTokenManager.isAuthenticated())
    }

    @Test
    fun testClearTokens() {
        authTokenManager.clearTokens()
        whenever(sharedPreferences.getString("auth_access_token", null)).thenReturn(null)
        assertFalse(authTokenManager.isAuthenticated())
    }
}
