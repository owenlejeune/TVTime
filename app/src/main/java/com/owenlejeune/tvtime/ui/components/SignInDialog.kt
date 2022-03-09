package com.owenlejeune.tvtime.ui.components

import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.owenlejeune.tvtime.R
import com.owenlejeune.tvtime.extensions.isEmailValid

@Composable
fun SignInDialog(
    showDialog: MutableState<Boolean>,
    onSuccess: (success: Boolean) -> Unit
) {
    val context = LocalContext.current

    var emailState by rememberSaveable { mutableStateOf("") }
    var emailHasErrors by rememberSaveable { mutableStateOf(false) }
    var emailError = ""

    var passwordState by rememberSaveable { mutableStateOf("") }
    var passwordHasErrors by rememberSaveable { mutableStateOf(false) }
    var passwordError = ""

    fun validate(): Boolean {
        emailError = ""
        passwordError = ""

        if (TextUtils.isEmpty(emailState)) {
            emailError = context.getString(R.string.email_not_empty_error)
        } else if (!emailState.isEmailValid()) {
            emailError = context.getString(R.string.email_invalid_error)
        }
        if (TextUtils.isEmpty(passwordState)) {
            passwordError = context.getString(R.string.password_empty_error)
        }

        emailHasErrors = emailError.isNotEmpty()
        passwordHasErrors = passwordError.isNotEmpty()

        return !emailHasErrors && !passwordHasErrors
    }

    AlertDialog(
        title = { Text(text = stringResource(R.string.action_sign_in)) },
        onDismissRequest = { showDialog.value = false },
        confirmButton = { CancelButton(showDialog = showDialog) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.sign_in_dialog_message)
                )
                ThemedOutlineTextField(
                    value = emailState,
                    onValueChange = {
                        emailHasErrors = false
                        emailState = it
                    },
                    label = { Text(text = stringResource(R.string.email_label)) },
                    isError = emailHasErrors,
                    errorMessage = emailError
                )
                PasswordOutlineTextField(
                    value = passwordState,
                    onValueChange = {
                        passwordHasErrors = false
                        passwordState = it
                    },
                    label = { Text(text = stringResource(R.string.password_label)) },
                    isError = passwordHasErrors,
                    errorMessage = passwordError
                )
                SignInButton(validate = ::validate) { success ->
                    if (success) {
                        showDialog.value = false
                    } else {
                        Toast.makeText(context, "An error occurred, please try again", Toast.LENGTH_SHORT).show()
                    }
                    onSuccess(success)
                }
                CreateAccountLink()
            }
        }
    )
}

@Composable
private fun CancelButton(showDialog: MutableState<Boolean>) {
    TextButton(onClick = { showDialog.value = false }) {
        Text(text = stringResource(R.string.action_cancel))
    }
}

@Composable
private fun SignInButton(validate: () -> Boolean, onSuccess: (success: Boolean) -> Unit) {
    var signInInProgress by remember { mutableStateOf(false) }
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            if (!signInInProgress) {
                if (validate()) {
                    signInInProgress = true
//                                signIn(context, emailState, passwordState) { success ->
//                                    signInInProgress = false
//                                    if (success) {
//                                        showDialog.value = false
//                                    }
//                                }
                    onSuccess(false)
                }
            }
        }
    ) {
        if (signInInProgress) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.background,
                strokeWidth = 2.dp
            )
        } else {
            Text(text = stringResource(id = R.string.action_sign_in))
        }
    }

}

@Composable
private fun CreateAccountLink() {
    val context = LocalContext.current
    LinkableText(
        text = stringResource(R.string.no_account_message),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    val url = "https://www.themoviedb.org/signup"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    context.startActivity(intent)
                }
            )
    )
}