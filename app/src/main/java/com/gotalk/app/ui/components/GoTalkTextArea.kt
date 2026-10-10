package com.gotalk.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.gotalk.app.ui.theme.GoTalkHintGray
import com.gotalk.app.ui.theme.GoTalkInk

/** Campo multilínea (alto mínimo 110 dp) con contador "n/maxChars"; el texto se corta en [maxChars]. */
@Composable
fun GoTalkTextArea(
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxChars: Int = 300
) {
    var focused by remember { mutableStateOf(false) }

    GoTalkFieldContainer(
        modifier = modifier.heightIn(min = 110.dp),
        focused = focused
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = GoTalkInk
                )
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp, bottom = 18.dp)
                        .weight(1f)
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GoTalkHintGray
                        )
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = { onValueChange(it.take(maxChars)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focused = it.isFocused },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = GoTalkInk),
                        cursorBrush = SolidColor(GoTalkInk)
                    )
                }
            }
            Text(
                text = "${value.length}/$maxChars",
                style = MaterialTheme.typography.bodySmall,
                color = GoTalkHintGray,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}
