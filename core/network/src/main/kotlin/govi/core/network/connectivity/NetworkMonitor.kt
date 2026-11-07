package govi.core.network.connectivity

import kotlinx.coroutines.flow.Flow

/**
 * Utility interface for monitoring real-time network connectivity changes.
 */
interface NetworkMonitor {
    /**
     * Cold stream emitting true when the device is actively connected to an internet-validated network,
     * and false otherwise.
     */
    val isOnline: Flow<Boolean>

    /**
     * Instantaneous snapshot check of network connectivity.
     */
    val isCurrentlyOnline: Boolean
}
