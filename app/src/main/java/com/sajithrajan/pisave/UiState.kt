package com.sajithrajan.pisave
/**
 * A sealed hierarchy describing the state of the text generation.
 */
sealed interface UiState {

    /**
     * Empty state when the screen is first shown
     */
    object Initial : UiState

    /**
     * Still loading
     */
    object Loading : UiState

    object chart : UiState

    /**
     * Text has been generated
     */
    data class Success(val outputText: String) : UiState
//for all
    /**
     * There was an error generating text
     */
    data class Error(val errorMessage: String) : UiState





}