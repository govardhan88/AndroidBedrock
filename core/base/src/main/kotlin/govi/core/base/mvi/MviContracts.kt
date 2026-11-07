package govi.core.base.mvi

/**
 * Marker interface for immutable screen state objects in Unidirectional Data Flow (UDF).
 */
interface UiState

/**
 * Marker interface for user actions, gestures, and lifecycle events.
 */
interface UiIntent

/**
 * Marker interface for one-time side effects (e.g. Navigation, Snackbars, Dialogs).
 */
interface UiEffect
