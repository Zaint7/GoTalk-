package com.gotalk.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gotalk.app.R
import com.gotalk.app.ui.theme.GoTalkCream
import com.gotalk.app.ui.theme.GoTalkFieldBorder
import com.gotalk.app.ui.theme.GoTalkHintGray
import com.gotalk.app.ui.theme.GoTalkInk
import com.gotalk.app.ui.theme.GoTalkMuted
import com.gotalk.app.ui.theme.GoTalkYellow
import com.gotalk.app.ui.theme.GoTalkYellowDark
import com.gotalk.app.ui.theme.Nunito
import java.text.Normalizer

/** Opción de un selector: [key] es lo que se guarda (p. ej. "ES"), [label] lo que se ve y se busca. */
data class PickerOption(
    val key: String,
    val label: String,
    @DrawableRes val flagRes: Int
)

private val DiacriticsRegex = "\\p{M}+".toRegex()

private fun String.searchable(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD).replace(DiacriticsRegex, "").lowercase().trim()

/** Filtra por texto sin distinguir mayúsculas ni acentos ("ingles" encuentra "Inglés"). Consulta vacía = todo. */
fun filterPickerOptions(options: List<PickerOption>, query: String): List<PickerOption> {
    val q = query.searchable()
    if (q.isEmpty()) return options
    return options.filter { it.label.searchable().contains(q) }
}

private val DialogShape = RoundedCornerShape(28.dp)
private val SearchShape = RoundedCornerShape(16.dp)

/**
 * Ventana flotante con buscador para elegir una opción (país, idioma...). Mismo lenguaje que las demás
 * ventanas: crema, borde dorado fino, sombra suave y entrada con spring. Al elegir llama a [onSelect] y se cierra.
 */
@Composable
fun SearchPickerDialog(
    title: String,
    options: List<PickerOption>,
    selectedKey: String?,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    searchPlaceholder: String = "Buscar"
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filtered = remember(query, options) { filterPickerOptions(options, query) }
    val enter = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        enter.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .heightIn(max = 520.dp)
                .graphicsLayer {
                    val s = 0.9f + 0.1f * enter.value
                    scaleX = s
                    scaleY = s
                    alpha = enter.value.coerceIn(0f, 1f)
                }
                .shadow(16.dp, DialogShape, ambientColor = GoTalkYellowDark, spotColor = GoTalkYellowDark)
                .clip(DialogShape)
                .background(GoTalkCream)
                .border(1.dp, GoTalkYellowDark, DialogShape)
                .padding(18.dp)
        ) {
            Text(
                text = title,
                color = GoTalkInk,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            )
            Spacer(Modifier.size(12.dp))
            SearchField(query = query, onQueryChange = { query = it }, placeholder = searchPlaceholder)
            Spacer(Modifier.size(8.dp))

            if (filtered.isEmpty()) {
                Text(
                    text = "Sin resultados",
                    color = GoTalkMuted,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(vertical = 24.dp, horizontal = 4.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                    items(filtered, key = { it.key }) { option ->
                        OptionRow(
                            option = option,
                            selected = option.key == selectedKey,
                            onClick = {
                                onSelect(option.key)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit, placeholder: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(SearchShape)
            .background(androidx.compose.ui.graphics.Color.White)
            .border(1.dp, GoTalkFieldBorder, SearchShape)
            .padding(start = 14.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.Search, contentDescription = null, tint = GoTalkInk, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(10.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(text = placeholder, color = GoTalkHintGray, fontFamily = Nunito, fontSize = 16.sp)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = GoTalkInk, fontFamily = Nunito, fontSize = 16.sp),
                cursorBrush = SolidColor(GoTalkInk),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (query.isNotEmpty()) {
            CircleIconButton(
                icon = Icons.Outlined.Close,
                contentDescription = "Borrar búsqueda",
                onClick = { onQueryChange("") },
                size = 32.dp,
                iconSize = 18.dp,
                contentColor = GoTalkMuted
            )
        }
    }
}

@Composable
private fun OptionRow(option: PickerOption, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(option.flagRes),
            contentDescription = null, // decorativa: el texto ya lo dice
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = option.label,
            color = GoTalkInk,
            fontFamily = Nunito,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
            fontSize = 17.sp,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Icon(Icons.Filled.Check, contentDescription = "Seleccionado", tint = GoTalkYellowDark, modifier = Modifier.size(22.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 700)
@Composable
private fun SearchPickerDialogPreview() {
    SearchPickerDialog(
        title = "Elige tu país",
        options = listOf(
            PickerOption("ES", "España", R.drawable.flag_es),
            PickerOption("GB", "Reino Unido", R.drawable.flag_gb),
            PickerOption("FR", "Francia", R.drawable.flag_fr),
            PickerOption("IT", "Italia", R.drawable.flag_it)
        ),
        selectedKey = "ES",
        onSelect = {},
        onDismiss = {}
    )
}
