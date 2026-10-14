package com.gotalk.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotalk.app.ui.theme.BottomBarShape
import com.gotalk.app.ui.theme.GoTalkInk
import com.gotalk.app.ui.theme.GoTalkMuted
import com.gotalk.app.ui.theme.GoTalkNavBar
import com.gotalk.app.ui.theme.GoTalkYellow
import com.gotalk.app.ui.theme.Nunito

/** Destinos de la barra inferior. Soporte vive dentro de Perfil, no aquí. */
enum class BottomBarItem(val label: String, val icon: ImageVector) {
    DISCOVER("Descubrir", Icons.Outlined.Explore),
    COMMUNITY("Comunidad", Icons.Outlined.Groups),
    CHATS("Chats", Icons.Outlined.ChatBubbleOutline),
    PROFILE("Perfil", Icons.Outlined.PersonOutline)
}

/** Barra inferior flotante de 74 dp. Respeta la barra de navegación del sistema. */
@Composable
fun GoTalkBottomBar(
    selected: BottomBarItem,
    onSelect: (BottomBarItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(74.dp)
                .shadow(8.dp, BottomBarShape, ambientColor = GoTalkInk, spotColor = GoTalkInk)
                .clip(BottomBarShape)
                .background(GoTalkNavBar),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarItem.entries.forEach { item ->
                BottomBarTab(
                    item = item,
                    isSelected = item == selected,
                    onClick = { onSelect(item) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomBarTab(
    item: BottomBarItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint = if (isSelected) GoTalkInk else GoTalkMuted
    Column(
        modifier = modifier
            .fillMaxHeight()
            .selectable(selected = isSelected, role = Role.Tab, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .then(if (isSelected) Modifier.background(GoTalkYellow) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            // Sin contentDescription propia: la etiqueta de texto ya nombra el ítem
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = item.label,
            color = tint,
            fontFamily = Nunito,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
            fontSize = 12.sp
        )
        Spacer(Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .width(30.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .then(if (isSelected) Modifier.background(GoTalkYellow) else Modifier)
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun GoTalkBottomBarPreview() {
    GoTalkBottomBar(selected = BottomBarItem.DISCOVER, onSelect = {})
}

@Preview(showBackground = true, widthDp = 320, heightDp = 600)
@Composable
private fun GoTalkBottomBarSmallPreview() {
    GoTalkBottomBar(selected = BottomBarItem.CHATS, onSelect = {})
}
