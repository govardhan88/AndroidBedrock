package govi.core.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class TokenDataStoreTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var tokenDataStore: TokenDataStore

    @Before
    fun setUp() {
        val testFile = File(tempFolder.newFolder(), "test_tokens.preferences_pb")
        val dataStore =
            PreferenceDataStoreFactory.create(
                scope = testScope,
                produceFile = { testFile },
            )
        tokenDataStore = TokenDataStoreImpl(dataStore)
    }

    @Test
    fun `initial state returns null tokens`() =
        testScope.runTest {
            assertNull(tokenDataStore.getAccessToken())
            assertNull(tokenDataStore.getRefreshToken())
        }

    @Test
    fun `saveTokens stores access and refresh tokens correctly`() =
        testScope.runTest {
            tokenDataStore.saveTokens("access_123", "refresh_456")

            assertEquals("access_123", tokenDataStore.getAccessToken())
            assertEquals("refresh_456", tokenDataStore.getRefreshToken())
        }

    @Test
    fun `clearTokens removes cached tokens`() =
        testScope.runTest {
            tokenDataStore.saveTokens("access_abc", "refresh_xyz")
            assertEquals("access_abc", tokenDataStore.getAccessToken())

            tokenDataStore.clearTokens()

            assertNull(tokenDataStore.getAccessToken())
            assertNull(tokenDataStore.getRefreshToken())
        }
}
