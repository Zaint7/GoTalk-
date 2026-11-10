@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.gotalk.app.presentation.discover

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotalk.app.ui.components.CircleIconButton
import com.gotalk.app.ui.components.FloatingPanel
import com.gotalk.app.ui.components.GoTalkPillButton
import com.gotalk.app.ui.theme.GoTalkCream
import com.gotalk.app.ui.theme.GoTalkFieldBorder
import com.gotalk.app.ui.theme.GoTalkInk
import com.gotalk.app.ui.theme.GoTalkMuted
import com.gotalk.app.ui.theme.GoTalkRedEnd
import com.gotalk.app.ui.theme.GoTalkYellow
import com.gotalk.app.ui.theme.Nunito
import com.gotalk.app.ui.theme.PillShape
import kotlin.math.roundToInt

// Idiomas y países principales que ofrece el filtro (todos tienen bandera propia)
private val LanguageOptions = listOf("Español", "Inglés", "Francés", "Italiano")
private val CountryOptions = listOf("ES", "GB", "FR", "IT")

/**
 * Ventana de filtros: tarjeta flotante bajo el botón de filtros (vía [FloatingPanel]), hermana de la de
 * notificaciones. Encabezado amarillo con "Filtros" y "Restablecer" (solo si hay algo distinto al valor por
 * defecto), cuerpo crema con scroll y botón "Aplicar" fijo abajo.
 *
 * Sin estado propio: trabaja sobre el borrador [filters]; cada cambio sale por [onChange] y solo
 * [onApply] lo convierte en filtro vigente.
 */
@Composable
fun FiltersPanel(
    visible: Boolean,
    filters: DiscoverFilters,
    onDismiss: () -> Unit,
    onChange: ((DiscoverFilters) -> DiscoverFilters) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier,
    matchCount: Int? = null
) {
    FloatingPanel(
        visible = visible,
        onDismiss = onDismiss,
        modifier = modifier,
        maxHeightFraction = 0.75f,
        paneTitle = "Filtros",
        containerColor = GoTalkCream
    ) {
        // Encabezado amarillo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GoTalkYellow)
                .padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filtros",
                color = GoTalkInk,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                modifier = Modifier.weight(1f)
            )
            if (!filters.isDefault) {
                Text(
                    text = "Restablecer",
                    color = GoTalkInk,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .clip(PillShape)
                        .clickable(role = Role.Button, onClick = onReset)
                        .padding(horizontal = 10.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                )
            }
            CircleIconButton(
                icon = Icons.Outlined.Close,
                contentDescription = "Cerrar filtros",
                onClick = onDismiss,
                size = 36.dp,
                iconSize = 20.dp,
                containerBrush = SolidColor(Color.White.copy(alpha = 0.6f))
            )
        }

        // Cuerpo con scroll interno: nunca se corta en pantallas pequeñas
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AgeSection(filters, onChange)

            FilterSection(title = "Género") {
                ChipFlow {
                    GenderFilter.entries.forEach { option ->
                        FilterChip(
                            label = option.label,
                            selected = filters.gender == option,
                            onClick = { onChange { it.copy(gender = option) } },
                            singleChoice = true
                        )
                    }
                }
            }

            FilterSection(title = "Idiomas que habla") {
                ChipFlow {
                    LanguageOptions.forEach { language ->
                        FilterChip(
                            label = language,
                            flagRes = flagForLanguage(language),
                            selected = language in filters.languagesTheySpeak,
                            onClick = {
                                onChange {
                                    val set = it.languagesTheySpeak
                                    it.copy(languagesTheySpeak = if (language in set) set - language else set + language)
                                }
                            }
                        )
                    }
                }
            }

            FilterSection(title = "Idioma que quiere aprender") {
                ChipFlow {
                    LanguageOptions.forEach { language ->
                        FilterChip(
                            label = language,
                            flagRes = flagForLanguage(language),
                            selected = filters.languageTheyLearn == language,
                            // Tocar el elegido otra vez lo quita: vuelve a "cualquiera"
                            onClick = {
                                onChange {
                                    it.copy(languageTheyLearn = if (it.languageTheyLearn == language) null else language)
                                }
                            },
                            singleChoice = true
                        )
                    }
                }
            }

            FilterSection(title = "País") {
                ChipFlow {
                    CountryOptions.forEach { code ->
                        FilterChip(
                            label = countryNameFor(code),
                            flagRes = flagForCountry(code),
                            selected = code in filters.countries,
                            onClick = {
                                onChange {
                                    val set = it.countries
                                    it.copy(countries = if (code in set) set - code else set + code)
                                }
                            }
                        )
                    }
                }
            }

            OnlineRow(
                checked = filters.onlyOnline,
                onCheckedChange = { checked -> onChange { it.copy(onlyOnline = checked) } }
            )
        }

        // Pie fijo: el botón no hace scroll
        Column(modifier = Modifier.background(GoTalkCream)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(GoTalkFieldBorder)
            )
            // Resultado en vivo: cuántos GoTalkers dejaría este borrador
            if (matchCount != null) {
                Text(
                    text = when (matchCount) {
                        0 -> "Ningún GoTalker coincide"
                        1 -> "1 GoTalker coincide"
                        else -> "$matchCount GoTalkers coinciden"
                    },
                    color = if (matchCount == 0) GoTalkRedEnd else GoTalkMuted,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp)
                )
            }
            GoTalkPillButton(
                text = if (filters.activeFiltersCount > 0) "Aplicar (${filters.activeFiltersCount})" else "Aplicar",
                onClick = onApply,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 12.dp)
            )
        }
    }
}

/**
 * Fila de chips que salta de línea. Sin separación vertical propia: cada chip ya reserva una zona táctil de
 * 48 dp (ver [FilterChip]), que aporta el aire entre filas.
 */
@Composable
private fun ChipFlow(content: @Composable () -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        content()
    }
}

/** Sección con título pequeño en negrita y, opcionalmente, un valor a la derecha. */
@Composable
private fun FilterSection(
    title: String,
    trailing: String? = null,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                color = GoTalkInk,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            if (trailing != null) {
                Text(
                    text = trailing,
                    color = GoTalkInk,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .clip(PillShape)
                        .background(GoTalkYellow)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
        }
        content()
    }
}

/** Edad: rango con dos controles (mínimo y máximo) y el valor en vivo a la derecha del título. */
@Composable
private fun AgeSection(
    filters: DiscoverFilters,
    onChange: ((DiscoverFilters) -> DiscoverFilters) -> Unit
) {
    val range = filters.ageRange
    // Con los dos controles juntos se muestra un solo número ("30") en vez de "30 - 30"
    val label = if (range.first == range.last) "${range.first}" else "${range.first} - ${range.last}"
    FilterSection(title = "Edad", trailing = label) {
        RangeSlider(
            value = range.first.toFloat()..range.last.toFloat(),
            onValueChange = { value ->
                val low = value.start.roundToInt()
                val high = value.endInclusive.roundToInt()
                onChange { it.copy(ageRange = low..high) }
            },
            valueRange = DefaultAgeRange.first.toFloat()..DefaultAgeRange.last.toFloat(),
            steps = DefaultAgeRange.last - DefaultAgeRange.first - 1,
            startThumb = { AgeThumb("Edad mínima, ${range.first} años") },
            endThumb = { AgeThumb("Edad máxima, ${range.last} años") },
            colors = SliderDefaults.colors(
                thumbColor = GoTalkYellow,
                activeTrackColor = GoTalkYellow,
                inactiveTrackColor = GoTalkFieldBorder,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            )
        )
    }
}

/** Control del slider: círculo amarillo con borde oscuro y sombra, fácil de ver y de agarrar. */
@Composable
private fun AgeThumb(description: String) {
    Box(
        modifier = Modifier
            .semantics { contentDescription = description }
            .size(28.dp)
            .shadow(3.dp, CircleShape)
            .background(GoTalkYellow, CircleShape)
            .border(2.dp, GoTalkInk, CircleShape)
    )
}

/**
 * Chip seleccionable. Seleccionado: amarillo sólido con texto negro; sin seleccionar: blanco con borde gris suave.
 * [singleChoice] decide si se anuncia como opción única (radio) o múltiple (casilla).
 */
@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes flagRes: Int? = null,
    singleChoice: Boolean = false
) {
    val background by animateColorAsState(
        targetValue = if (selected) GoTalkYellow else Color.White,
        animationSpec = tween(150),
        label = "chipBackground"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) GoTalkInk.copy(alpha = 0.25f) else GoTalkFieldBorder,
        animationSpec = tween(150),
        label = "chipBorder"
    )
    val selectionModifier = if (singleChoice) {
        Modifier.selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
    } else {
        Modifier.toggleable(value = selected, role = Role.Checkbox, onValueChange = { onClick() })
    }
    Row(
        modifier = modifier
            // La zona táctil abarca los 48 dp aunque el chip se vea de 40
            .then(selectionModifier)
            .minimumInteractiveComponentSize()
            .heightIn(min = 40.dp)
            .clip(PillShape)
            .background(background)
            .border(1.dp, borderColor, PillShape)
            .padding(start = if (flagRes != null) 8.dp else 14.dp, end = 14.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (flagRes != null) {
            Image(
                painter = painterResource(flagRes),
                contentDescription = null, // decorativa: la etiqueta ya dice el idioma o país
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = label,
            color = GoTalkInk,
            fontFamily = Nunito,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

/** Interruptor "Solo gente en línea". */
@Composable
private fun OnlineRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(PillShape)
            .background(Color.White)
            .border(1.dp, GoTalkFieldBorder, PillShape)
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(start = 16.dp, end = 10.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Solo gente en línea",
                color = GoTalkInk,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )
            Text(
                text = "Personas conectadas ahora",
                color = GoTalkMuted,
                fontFamily = Nunito,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
        }
        // El toque lo recibe toda la fila: el Switch es solo visual
        Switch(
            checked = checked,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = GoTalkInk,
                checkedTrackColor = GoTalkYellow,
                checkedBorderColor = GoTalkInk.copy(alpha = 0.25f),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = GoTalkFieldBorder,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFE626, widthDp = 390, heightDp = 760)
@Composable
private fun FiltersPanelPreview() {
    FiltersPanel(
        visible = true,
        filters = DiscoverFilters(
            ageRange = 22..35,
            gender = GenderFilter.MUJER,
            languagesTheySpeak = setOf("Español"),
            languageTheyLearn = "Inglés",
            countries = setOf("ES", "FR"),
            onlyOnline = true
        ),
        onDismiss = {}, onChange = {}, onReset = {}, onApply = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFE626, widthDp = 320, heightDp = 560)
@Composable
private fun FiltersPanelEverythingSelectedPreview() {
    // Todo seleccionado, rango de un solo valor y cero resultados, en pantalla pequeña
    FiltersPanel(
        visible = true,
        filters = DiscoverFilters(
            ageRange = 30..30,
            gender = GenderFilter.OTRO,
            languagesTheySpeak = setOf("Español", "Inglés", "Francés", "Italiano"),
            languageTheyLearn = "Italiano",
            countries = setOf("ES", "GB", "FR", "IT"),
            onlyOnline = true
        ),
        onDismiss = {}, onChange = {}, onReset = {}, onApply = {},
        matchCount = 0
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFE626, widthDp = 320, heightDp = 480)
@Composable
private fun FiltersPanelSmallDefaultPreview() {
    FiltersPanel(
        visible = true,
        filters = DiscoverFilters(),
        onDismiss = {}, onChange = {}, onReset = {}, onApply = {}
    )
}
