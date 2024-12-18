import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult

/**
 * Snackbars are small bars containing a brief message and disappear
 * automatically after a short time. They can contain an action, other
 * than "Dismiss" or "Cancel", that the user can do.
 *
 * I want to give the user the chance to correct its input, in case the
 * snackbar appears when one of the fields in the form is blank.
 *
 * Documentation:
 * https://developer.android.com/reference/kotlin/androidx/compose/material/package-summary#snackbar
 * https://developer.android.com/develop/ui/compose/components/snackbar
 * */
suspend fun onLoginClick(
    snackbarHostState: SnackbarHostState,
    message: String = "You clicked login",
    onActionClick: () -> Unit = {},
    actionLabel: String = ""
) {
    val result = if (actionLabel.isNotBlank()) snackbarHostState
        .showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Short
        ) else snackbarHostState
        .showSnackbar(
            message = message,
            duration = SnackbarDuration.Short
        )
    when (result) {
        SnackbarResult.ActionPerformed -> {
            onActionClick()
        }
        SnackbarResult.Dismissed -> {
            // do nothing
        }
    }
}