package com.gotalk.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.gotalk.app.ui.theme.GoTalkFieldBorder
import com.gotalk.app.ui.theme.GoTalkHintGray
import com.gotalk.app.ui.theme.GoTalkInk
import com.gotalk.app.ui.theme.GoTalkYellow

private val FieldBackground = androidx.compose.ui.graphics.Color(0xFFFFFFFE)

/** Contenedor visual común de los campos del registro: fondo, borde 1 dp (2 dp amarillo con foco), radio 16. */
@Composable
internal fun GoTalkFieldContainer(
    modifier: Modifier = Modifier,
    focused: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val border = BorderStroke(
        width = if (focused) 2.dp else 1.dp,
        color = if (focused) GoTalkYellow else GoTalkFieldBorder
    )
    val shape = RoundedCornerShape(16.dp)
    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            color = FieldBackground,
            border = border,
            content = content
        )
    } else {
        Surface(
            modifier = modifier,
            shape = shape,
            color = FieldBackground,
            border = border,
            content = content
        )
    }
}

/** Campo con ícono a la izquierda, etiqueta opcional arriba y placeholder. Alto 48 dp (52 dp con etiqueta). */
@Composable
fun GoTalkOutlinedField(
    leadingIcon: ImageVector,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    var focused by remember { mutableStateOf(false) }
    GoTalkFieldContainer(
        modifier = modifier.height(if (label != null) 52.dp else 48.dp),
        focused = focused
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = GoTalkInk
            )
            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                if (label != null) {
                    Text(text = label, style = MaterialTheme.typography.bodyMedium, color = GoTalkInk)
                }
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GoTalkHintGray
                        )
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focused = it.isFocused },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = GoTalkInk),
                        cursorBrush = SolidColor(GoTalkInk),
                        keyboardOptions = keyboardOptions
                    )
                }
            }
        }
    }
}
