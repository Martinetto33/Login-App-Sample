import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginUsernameTextField(
    text: String = "",
    isError: Boolean = false,
    onTextChange: (String) -> Unit,
    focusRequester: FocusRequester, // this is needed, otherwise the focus requester is uninitialised
    selectText: () -> Unit,
    onEnterPressed: () -> Unit = {},
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text("Username") },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (it.isFocused) {
                    selectText()
                }
            }
            .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.Enter) {
                    println("Enter pressed usr")
                    onEnterPressed()
                    true // return true to consume the event
                } else {
                    false
                }
            },
        singleLine = true,
        isError = isError,
    )
}

@Composable
fun LoginPasswordTextField(
    text: String = "",
    isError: Boolean = false,
    onTextChange: (String) -> Unit,
    isVisible: Boolean = false,
    onVisibilityChange: () -> Unit,
    focusRequester: FocusRequester, // this is needed, otherwise the focus requester is uninitialised
    selectText: () -> Unit,
    onEnterPressed: () -> Unit = {},
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text("Password") },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    /*
                    * Any time the user selects a field, select
                    * the whole text.
                    * */
                    selectText()
                }
            }
            .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.Enter) {
                    println("Enter pressed pw")
                    onEnterPressed()
                    true // return true to consume the event
                } else {
                    false
                }
            },
        singleLine = true,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        isError = isError,
        trailingIcon = {
            IconButton(
                onClick = onVisibilityChange,
            ) {
                PasswordVisibilityToggle(isVisible)
            }
        }
    )
}

@Composable
fun PasswordVisibilityToggle(
    isVisible: Boolean
) {
    Icon(
        painterResource(resourcePath = if (isVisible) "hidden.png" else "eye.png"),
        contentDescription = "Visibility Toggle",
        modifier = Modifier
            .size(32.dp)
            .pointerHoverIcon(icon = PointerIcon.Default)
    )
}