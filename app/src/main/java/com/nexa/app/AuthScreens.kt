package com.nexa.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AuthMode {
    Login,
    SignUp,
    ResetPassword,
}

private val AuthBackground = Color(0xFF050515)
private val AuthInputBackground = Color(0xFF10122D)
private val AuthPurple = Color(0xFF6131E6)
private val AuthTeal = Color(0xFF00D6D2)
private val AuthMuted = Color(0xFFA7AAC3)
private val AuthBorder = Color(0xFFB5B5CC)

@Composable
fun AuthFlowScreen(
    startMode: AuthMode,
    onAuthenticated: () -> Unit,
) {
    var mode by rememberSaveable(startMode) {
        mutableStateOf(startMode)
    }
    var name by rememberSaveable {
        mutableStateOf("")
    }
    var email by rememberSaveable {
        mutableStateOf("")
    }
    var password by rememberSaveable {
        mutableStateOf("")
    }
    var showPassword by rememberSaveable {
        mutableStateOf(false)
    }
    var agree by rememberSaveable {
        mutableStateOf(false)
    }
    var message by rememberSaveable {
        mutableStateOf("")
    }
    var resetSent by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthBackground)
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AuthLogo()

        when (mode) {
            AuthMode.Login -> {
                LoginContent(
                    email = email,
                    onEmailChange = {
                        email = it
                        message = ""
                    },
                    password = password,
                    onPasswordChange = {
                        password = it
                        message = ""
                    },
                    showPassword = showPassword,
                    onTogglePassword = {
                        showPassword = !showPassword
                    },
                    message = message,
                    onForgotPassword = {
                        mode = AuthMode.ResetPassword
                        message = ""
                        resetSent = false
                    },
                    onSignIn = {
                        message = if (email.isBlank() || password.isBlank()) {
                            "Enter your email and password."
                        } else {
                            onAuthenticated()
                            ""
                        }
                    },
                    onCreateAccount = {
                        mode = AuthMode.SignUp
                        message = ""
                    },
                )
            }

            AuthMode.SignUp -> {
                SignUpContent(
                    name = name,
                    onNameChange = {
                        name = it
                        message = ""
                    },
                    email = email,
                    onEmailChange = {
                        email = it
                        message = ""
                    },
                    password = password,
                    onPasswordChange = {
                        password = it
                        message = ""
                    },
                    showPassword = showPassword,
                    onTogglePassword = {
                        showPassword = !showPassword
                    },
                    agree = agree,
                    onAgreeChange = {
                        agree = it
                        message = ""
                    },
                    message = message,
                    onCreateAccount = {
                        val validPassword =
                            password.length >= 8 &&
                                    password.any { it.isUpperCase() } &&
                                    password.any { it.isDigit() }

                        message = when {
                            name.isBlank() || email.isBlank() || password.isBlank() ->
                                "Complete the required fields."

                            !validPassword ->
                                "Use at least 8 characters, including a number and an uppercase letter."

                            !agree ->
                                "Agree to the Terms of Service and Privacy Policy to continue."

                            else -> {
                                onAuthenticated()
                                ""
                            }
                        }
                    },
                    onSignIn = {
                        mode = AuthMode.Login
                        message = ""
                    },
                )
            }

            AuthMode.ResetPassword -> {
                ResetPasswordContent(
                    email = email,
                    onEmailChange = {
                        email = it
                        message = ""
                        resetSent = false
                    },
                    message = message,
                    resetSent = resetSent,
                    onSendResetLink = {
                        if (email.isBlank()) {
                            message = "Enter your account email."
                            resetSent = false
                        } else {
                            message = ""
                            resetSent = true
                        }
                    },
                    onBackToSignIn = {
                        mode = AuthMode.Login
                        message = ""
                        resetSent = false
                    },
                )
            }
        }
    }
}

@Composable
private fun AuthLogo() {
    Image(
        painter = painterResource(R.drawable.nexa_logo),
        contentDescription = "NEXA logo",
        modifier = Modifier
            .padding(top = 4.dp)
            .size(width = 150.dp, height = 128.dp),
    )
}

@Composable
private fun LoginContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    showPassword: Boolean,
    onTogglePassword: () -> Unit,
    message: String,
    onForgotPassword: () -> Unit,
    onSignIn: () -> Unit,
    onCreateAccount: () -> Unit,
) {
    Spacer(Modifier.height(4.dp))

    Text(
        text = "N E X A",
        color = Color.White,
        letterSpacing = 5.sp,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.headlineSmall,
    )

    Text(
        text = "O N E   P L A C E.   E V E R Y   P R O J E C T.",
        color = AuthTeal,
        letterSpacing = 1.8.sp,
        style = MaterialTheme.typography.labelSmall,
    )

    Spacer(Modifier.height(34.dp))

    Text(
        text = "Welcome back",
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
    )

    Text(
        text = "Sign in to continue to your projects",
        modifier = Modifier.fillMaxWidth(),
        color = AuthMuted,
        style = MaterialTheme.typography.bodyMedium,
    )

    Spacer(Modifier.height(26.dp))

    AuthField(
        label = "Email or username",
        value = email,
        onValueChange = onEmailChange,
        placeholder = "e.g. heba@nexaapp.io",
        uppercaseLabel = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = AuthMuted,
                modifier = Modifier.size(18.dp),
            )
        },
    )

    Spacer(Modifier.height(12.dp))

    AuthField(
        label = "Password",
        value = password,
        onValueChange = onPasswordChange,
        placeholder = "Enter your password",
        uppercaseLabel = true,
        visualTransformation = if (showPassword) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(onClick = onTogglePassword) {
                Icon(
                    imageVector = if (showPassword) {
                        Icons.Default.VisibilityOff
                    } else {
                        Icons.Default.Visibility
                    },
                    contentDescription = "Show or hide password",
                    tint = AuthMuted,
                )
            }
        },
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        TextButton(
            onClick = onForgotPassword,
            modifier = Modifier.heightIn(min = 38.dp),
            contentPadding = PaddingValues(horizontal = 4.dp),
        ) {
            Text("Forgot password?", color = AuthTeal)
        }
    }
    AuthMessage(message)

    Button(
        onClick = onSignIn,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AuthPurple,
        ),
    ) {
        Text("Sign In", fontWeight = FontWeight.Bold)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Color(0xFF252641),
        )
        Text(
            text = "or",
            modifier = Modifier.padding(horizontal = 14.dp),
            color = AuthMuted,
            style = MaterialTheme.typography.bodySmall,
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Color(0xFF252641),
        )
    }

    OutlinedButton(
        onClick = onCreateAccount,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(28.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AuthPurple),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = AuthPurple,
        ),
    ) {
        Text("Create an account", fontWeight = FontWeight.SemiBold)
    }

    Spacer(Modifier.height(22.dp))

    Text(
        text = "By signing in you agree to NEXA’s Terms of Service and Privacy Policy.",
        modifier = Modifier.fillMaxWidth(),
        color = AuthMuted,
        style = MaterialTheme.typography.labelSmall,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
    )
}

@Composable
private fun SignUpContent(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    showPassword: Boolean,
    onTogglePassword: () -> Unit,
    agree: Boolean,
    onAgreeChange: (Boolean) -> Unit,
    message: String,
    onCreateAccount: () -> Unit,
    onSignIn: () -> Unit,
) {
    Spacer(Modifier.height(8.dp))

    Text(
        text = "Create your account",
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
    )

    Text(
        text = "One place for your team and every project.",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        color = AuthMuted,
    )

    Spacer(Modifier.height(18.dp))

    AuthField(
        label = "Full name *",
        value = name,
        onValueChange = onNameChange,
        placeholder = "Your full name",
    )

    Spacer(Modifier.height(10.dp))

    AuthField(
        label = "Email address *",
        value = email,
        onValueChange = onEmailChange,
        placeholder = "you@example.com",
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = AuthMuted,
                modifier = Modifier.size(18.dp),
            )
        },
    )

    Spacer(Modifier.height(10.dp))

    AuthField(
        label = "Password *",
        value = password,
        onValueChange = onPasswordChange,
        placeholder = "Create a secure password",
        visualTransformation = if (showPassword) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(onClick = onTogglePassword) {
                Icon(
                    imageVector = if (showPassword) {
                        Icons.Default.VisibilityOff
                    } else {
                        Icons.Default.Visibility
                    },
                    contentDescription = "Show or hide password",
                    tint = AuthMuted,
                )
            }
        },
    )

    Text(
        text = "Use at least 8 characters, including a number and an uppercase letter.",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        color = AuthMuted,
        style = MaterialTheme.typography.bodySmall,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = agree,
            onCheckedChange = onAgreeChange,
            colors = CheckboxDefaults.colors(
                checkedColor = AuthPurple,
                uncheckedColor = AuthMuted,
            ),
        )

        Text(
            text = "I agree to the Terms of Service and Privacy Policy.",
            color = AuthMuted,
            style = MaterialTheme.typography.bodySmall,
        )
    }

    AuthMessage(message)

    Button(
        onClick = onCreateAccount,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AuthPurple,
        ),
    ) {
        Text("Create account", fontWeight = FontWeight.Bold)
    }

    TextButton(
        onClick = onSignIn,
        modifier = Modifier.padding(top = 6.dp),
    ) {
        Text(
            text = "Already have an account? Sign in",
            color = AuthTeal,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ResetPasswordContent(
    email: String,
    onEmailChange: (String) -> Unit,
    message: String,
    resetSent: Boolean,
    onSendResetLink: () -> Unit,
    onBackToSignIn: () -> Unit,
) {
    Spacer(Modifier.height(12.dp))

    Icon(
        imageVector = Icons.Default.Key,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp),
        tint = AuthTeal,
    )

    Spacer(Modifier.height(10.dp))

    Text(
        text = "Reset your password",
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
    )

    Text(
        text = "Enter your account email. We’ll send instructions to help you get back to your projects.",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        color = AuthMuted,
        style = MaterialTheme.typography.bodyMedium,
    )

    Spacer(Modifier.height(18.dp))

    AuthField(
        label = "Email address *",
        value = email,
        onValueChange = onEmailChange,
        placeholder = "heba@nexaapp.io",
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = AuthMuted,
                modifier = Modifier.size(18.dp),
            )
        },
    )

    Spacer(Modifier.height(16.dp))

    Button(
        onClick = onSendResetLink,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AuthPurple,
        ),
    ) {
        Text("Send reset link", fontWeight = FontWeight.Bold)
    }

    AuthMessage(message)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        color = Color(0xFF11132D),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = if (resetSent) "Reset link requested" else "What happens next?",
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "If an account exists for this email, you’ll receive a reset link. " +
                        "For your privacy, we don’t confirm whether an email is registered.",
                color = AuthMuted,
                style = MaterialTheme.typography.bodySmall,
            )

            Text(
                text = "Check your spam folder. The link expires after 30 minutes.",
                color = AuthMuted,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }

    TextButton(
        onClick = onBackToSignIn,
        modifier = Modifier.padding(top = 12.dp),
    ) {
        Text("‹ Back to sign in", color = AuthTeal)
    }
}

@Composable
private fun AuthField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    uppercaseLabel: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = if (uppercaseLabel) label.uppercase() else label,
            color = AuthMuted,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(Modifier.height(5.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = AuthMuted,
                )
            },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = AuthInputBackground,
                unfocusedContainerColor = AuthInputBackground,
                focusedBorderColor = AuthBorder,
                unfocusedBorderColor = AuthBorder,
                cursorColor = AuthTeal,
            ),
        )
    }
}

@Composable
private fun AuthMessage(message: String) {
    if (message.isNotBlank()) {
        Text(
            text = message,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = Color(0xFFFF9AA8),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}