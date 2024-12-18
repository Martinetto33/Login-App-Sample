import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import model.checkCredentials
import model.validateInput
import java.awt.Dimension

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App() {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    /**
     * These are here to improve UX. If the user accidentally leaves
     * a field blank, the focus manager can redirect the focus
     * to the wrong fields, if the user clicks on the action provided
     * by the informative snackbar.
     *
     * See https://composables.com/jetpack-compose-tutorials/focus-text
     * */
    val focusManager = LocalFocusManager.current
    val (usernameRef, passwordRef, loginButtonRef) = FocusRequester.createRefs()

    var username by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    /**
     * There's a bug in Compose that makes it hard to select the whole
     * text, as pointed out here: https://stackoverflow.com/questions/68244362/select-all-text-of-textfield-in-jetpack-compose
     * The following logic is responsible for keeping the selection even after the
     * recompositions triggered by onValueChange calls in the
     * text field.
     * */

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isUsernameEmpty by remember { mutableStateOf(false) }
    var isPasswordEmpty by remember { mutableStateOf(false) }

    /**
     * This code is stored in a callback for 2 reasons:
     * - there were many state variables involved, so passing a thousand callbacks around
     *   was not something I was looking forward to;
     * - this code is called from at least 2 different sources (the login
     *   button and the keyEvent processor); having it concentrated here
     *   means no code repetition occurs
     * */
    val processInputCallback: () -> Unit = {
        processInput(
            username = username.text,
            password = password.text,
            usernameIsEmpty = {
                isUsernameEmpty = it
            },
            requestUsernameFocus = {
                usernameRef.requestFocus()
            },
            passwordIsEmpty = {
                isPasswordEmpty = it
            },
            requestPasswordFocus = {
                passwordRef.requestFocus()
            },
            onLoginSuccess = {
                focusManager.clearFocus()
                username = username.copy("")
                password = password.copy("")
            },
            scope = scope,
            snackbarHostState = snackbarHostState
        )
    }

    MaterialTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text("Login app demo")
                    }
                )
            },
            bottomBar = {
                BottomAppBar {
                    Text(
                        text = "Alin Bordeianu",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colors.surface),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginUsernameTextField(
                    text = username.text,
                    onTextChange = {
                        username = username.copy(
                            text = it
                        )
                    },
                    isError = isUsernameEmpty,
                    focusRequester = usernameRef,
                    selectText = {
                        val text = username.text
                        username = username.copy(
                            selection = TextRange(0, text.length)
                        )
                    },
                    onEnterPressed = { focusManager.moveFocus(FocusDirection.Down) },
                )
                Space()
                LoginPasswordTextField(
                    text = password.text,
                    onTextChange = { password = password.copy(text = it) },
                    isError = isPasswordEmpty,
                    isVisible = isPasswordVisible,
                    onVisibilityChange = { isPasswordVisible = !isPasswordVisible },
                    focusRequester = passwordRef,
                    selectText = {
                        val text = password.text
                        password = password.copy(
                            selection = TextRange(0, text.length)
                        )
                    },
                    onEnterPressed = { processInputCallback() },
                )
                Space()
                TestButton(
                    text = "Login",
                    focusRequester = loginButtonRef,
                    onClick = { processInputCallback() }
                )
            }
        }
    }
}

/**
 * This function contains the logic that processes the input;
 * it was extracted from the App() function in order to separate
 * concerns a little.
 * */
fun processInput(
    username: String,
    password: String,
    usernameIsEmpty: (Boolean) -> Unit,
    requestUsernameFocus: () -> Unit,
    passwordIsEmpty: (Boolean) -> Unit,
    requestPasswordFocus: () -> Unit,
    onLoginSuccess: () -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    scope.launch {
        if (!validateInput(username)) {
            usernameIsEmpty(true)
            onLoginClick(
                snackbarHostState,
                message = "Username is empty!",
                onActionClick = {
                    // Move focus to username label
                    requestUsernameFocus()
                },
                actionLabel = "Correct"
            )
            return@launch
        } else {
            usernameIsEmpty(false)
        }
        if (!validateInput(password)) {
            passwordIsEmpty(true)
            onLoginClick(
                snackbarHostState,
                message = "Password is empty!",
                onActionClick = {
                    // Move focus to username label
                    requestPasswordFocus()
                },
                actionLabel = "Correct"
            )
            return@launch
        } else {
            passwordIsEmpty(false)
        }
        if (!checkCredentials(username, password)) {
            requestUsernameFocus()
            onLoginClick(
                snackbarHostState,
                message = "Username or password is incorrect!",
            )
            return@launch
        }
        onLoginSuccess()
        onLoginClick(
            snackbarHostState,
            message = "Login succeeded!",
        )
    }
}

@Composable
fun Space(
    height: Dp = 20.dp
) {
    Spacer(Modifier.height(height))
}

@Composable
fun TestButton(
    text: String,
    onClick: () -> Unit,
    focusRequester: FocusRequester,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .focusRequester(focusRequester)
    ) {
        Text(text)
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Cosmobile App",
        icon = painterResource(resourcePath = "appicon.png")
    ) {
        window.minimumSize = Dimension(700, 400)
        App()
    }
}
