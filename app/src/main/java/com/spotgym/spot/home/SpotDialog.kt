package com.spotgym.spot.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.spotgym.spot.R

@Composable
@ExperimentalComposeUiApi
fun SpotDialog(
    title: String,
    setShowDialog: (Boolean) -> Unit,
    onPositiveClick: () -> Unit,
    validate: () -> ValidationResult,
    modifier: Modifier = Modifier,
    content: @Composable
    () -> Unit,
) {
    var errorField by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = { setShowDialog(false) },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            elevation = 10.dp,
            modifier = modifier,
        ) {
            Column(
                modifier = Modifier.padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = title, fontWeight = FontWeight.Bold)

                content()

                if (errorField.isNotBlank()) {
                    Text(text = errorField, color = colorResource(R.color.error_red))
                }

                DialogResultButtons(
                    onNegativeClick = { setShowDialog(false) },
                    onPositiveClick = {
                        val result = validate()
                        if (result.isSuccess) {
                            onPositiveClick()
                            setShowDialog(false)
                        } else {
                            errorField = result.error?.message ?: ""
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DialogValidationTextField(
    label: String,
    value: MutableState<String>,
    isError: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    TextField(
        label = { Text(label) },
        value = value.value,
        onValueChange = {
            value.value = it
            isError.value = false
        },
        isError = isError.value,
        singleLine = true,
        modifier = modifier,
    )
}

@Suppress("DataClassPrivateConstructor")
data class ValidationResult private constructor(
    val isSuccess: Boolean,
    val error: ValidationErrorInformation?,
) {
    companion object {
        val success = ValidationResult(true, null)
        fun failure(message: String, property: String) =
            ValidationResult(false, ValidationErrorInformation(message, property))
    }
}

data class ValidationErrorInformation(
    val message: String?,
    val property: String?,
)

@Composable
private fun DialogResultButtons(
    onNegativeClick: () -> Unit,
    onPositiveClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextButton(onClick = onNegativeClick) {
            Text(text = stringResource(android.R.string.cancel))
        }
        Spacer(modifier = Modifier.width(3.dp))
        TextButton(onClick = onPositiveClick) {
            Text(text = stringResource(android.R.string.ok))
        }
    }
}
