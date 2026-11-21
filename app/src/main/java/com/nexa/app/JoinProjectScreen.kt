package com.nexa.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Uses the existing NexaTheme.kt components and palette.
 * onJoin receives the trimmed invitation code. The caller validates it, handles
 * membership, supplies joining/error, and navigates only after actual success.
 */
@Composable
fun JoinProjectScreen(
    onJoin: (String) -> Unit,
    onBack: () -> Unit,
    joining: Boolean = false,
    error: String? = null,
) {
    var code by rememberSaveable { mutableStateOf("") }
    var attempted by rememberSaveable { mutableStateOf(false) }
    val missingCode = attempted && code.isBlank()
    val submit: () -> Unit = {
        if (!joining) {
            attempted = true
            if (code.isNotBlank()) onJoin(code.trim())
        }
    }

    Column(Modifier.fillMaxSize().background(Sheet).imePadding()) {
        NexaHeader(
            title = "Join project",
            subtitle = "Connect with your project team",
            onBack = onBack,
            action = {
                TextButton(onClick = onBack) { Text("Cancel", color = Color.White) }
            },
        )
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Icon(
                        Icons.Default.GroupAdd, contentDescription = null,
                        modifier = Modifier.padding(20.dp).size(32.dp), tint = Purple,
                    )
                }
                Text("Your team is one code away", fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium)
            }
            NexaCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Project invitation", fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium)
                    Text("Enter the project code shared with you by your team.",
                        color = Muted, style = MaterialTheme.typography.bodyMedium)
                    Text("Project code *", fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it; attempted = false },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter project code") },
                        singleLine = true,
                        enabled = !joining,
                        isError = missingCode || error != null,
                        shape = CardShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Panel,
                            focusedContainerColor = Panel,
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { submit() }),
                        supportingText = {
                            Text(if (missingCode) "Enter a project code." else "Ask your project owner if you don't have a code.")
                        },
                    )
                    if (!error.isNullOrBlank()) {
                        Text(error, color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Button(
                onClick = submit,
                enabled = !joining,
                modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                shape = RoundedCornerShape(16.dp),
            ) {
                if (joining) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp,
                        color = LocalContentColor.current)
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (joining) "Joining…" else "Join project", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                shape = RoundedCornerShape(16.dp),
            ) { Text("Cancel", color = Purple, fontWeight = FontWeight.Bold) }
        }
    }
}

@Preview(name = "Join Project", showBackground = true, widthDp = 393, heightDp = 850)
@Composable
private fun JoinProjectPreview() {
    NexaTheme { JoinProjectScreen(onJoin = {}, onBack = {}) }
}
