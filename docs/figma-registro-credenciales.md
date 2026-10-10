# Registro - credenciales (pestaña "registrarse" del Login)

> Fuente: imagen del frame "Registro" (290 px de ancho, factor aprox. 1.42 dp por px sobre un frame de 412 dp).
> Medidas en dp con margen de +-5 dp. Colores nuevos estimados.

## 1. Propósito y navegación
- NO es una pantalla nueva: es la misma pantalla del Login con la pestaña "registrarse" activa. Lo que cambia es el contenido de la tarjeta blanca. El bloque amarillo con saludos, el logo, la tarjeta (mismo tamaño y sombra) y "Políticas de privacidad" no cambian.
- Pestaña "iniciar sesion" -> vuelve al contenido del login.
- Botón "SIGUIENTE" -> Registro Paso 1 "¡Conozcámonos!" (ver `figma-registro-paso1.md`).
- Flujo completo: Login <-> Registro (esta pantalla) -> Paso 1 (datos personales) -> Paso 2 (perfil de idioma, frame aún por documentar).
- Esto aclara dos supuestos del doc del Paso 1: el correo y la contraseña se piden AQUÍ (no en el Paso 2), y la pestaña "registrarse" NO navega a otra pantalla: cambia el contenido de la tarjeta.
- La flecha atrás del Paso 1 debe volver a esta tarjeta (Login con pestaña registrarse).

## 2. Qué cambia respecto al Login (contenido de la tarjeta)
| Elemento | Login | Registro |
|---|---|---|
| Pestaña activa | izquierda | derecha ("registrarse") |
| Título | "Bienvenido a GoTalk!" | "¡Completa tu registro!" |
| Correo / Contraseña | sí | sí, iguales |
| Debajo de los campos | fila de 4 íconos, botón, "¿Olvidaste...?" | 3 botones sociales anchos |
| Botón principal | "iniciar sesion" (activo) | "SIGUIENTE" (gris, deshabilitado al inicio) |
| "¿Olvidaste tu contraseña?" | sí | no |

## 3. Estructura de la tarjeta, de arriba abajo
Tarjeta: mismo ancho, radio, borde y sombra que en el Login. Alto de contenido casi igual al del Login (aprox. 460 dp), así que al cambiar de pestaña la tarjeta no debería saltar de tamaño. Padding interno 24-28 dp.

1. **TabSelector** (alto 49-51 dp, ancho de contenido aprox. 233 dp): "iniciar sesion" (izquierda, base gris) y "registrarse" (derecha, amarillo activo). Mismo componente, `selectedIndex = 1`.
2. **Título** "¡Completa tu registro!": centrado, aprox. 20 sp, Montserrat peso medio/semibold (se ve menos grueso que "Bienvenido a GoTalk!" del login). Espacio sobre el título: 26 dp.
3. **Campo Correo**: `UnderlinedTextField` igual al del Login. Espacio sobre el campo: 30 dp.
4. **Campo Contraseña**: igual, siempre oculta, sin ícono de ojo. Separación con Correo: igual que en el Login (aprox. 16-20 dp).
5. **Tres botones sociales** apilados (espacio sobre el primero: 24 dp, entre botones: 12 dp):
   - Cada uno: ancho completo del contenido (aprox. 227-233 dp), alto aprox. 30 dp, fondo gris `GoTalkTabBase` (#E1E1DE), esquinas redondeadas (radio 10 dp).
   - Ícono a la izquierda (20 dp, negro, margen izquierdo 8 dp) y texto centrado en Roboto 14 sp: "Facebook", "Instagram", "Gmail".
   - Íconos: reutilizar `ic_facebook`, `ic_instagram`, `ic_google`.
6. **Botón "SIGUIENTE"**: `PrimaryButton`, alto 52 dp, ancho completo del contenido, Luckiest Guy 20 sp. Espacio sobre el botón: 30 dp. En el diseño aparece GRIS (deshabilitado). Espacio bajo el botón hasta el borde de la tarjeta: 28-38 dp.
7. **"Políticas de privacidad"** debajo de la tarjeta, igual que en el Login.

## 4. Colores
Existentes: `GoTalkYellow` (pestaña activa), `GoTalkTabBase` (base de pestañas y botones sociales), `GoTalkFieldLine`, `GoTalkFieldHint`, `GoTalkTextPrimary`, `GoTalkCardBorder`, `GoTalkTextMuted`.
Nuevos (agregar a `Color.kt`):
- `GoTalkDisabled` #B8B8B8: fondo del botón "SIGUIENTE" deshabilitado.
- `GoTalkOnDisabled` #3A3A3A: texto del botón deshabilitado (estimado).
- Nota: el amarillo de la pestaña activa en la imagen se ve un poco más dorado que `GoTalkYellow`. Probablemente es compresión de la imagen. Reutilizar `GoTalkYellow`.

## 5. Tipografía
- Pestañas y botón: Luckiest Guy (ya en el proyecto).
- Título: Montserrat, tamaño 20 sp, peso SemiBold/Medium. En el Login el título es Bold; aquí se ve más liviano. DECISIÓN PENDIENTE: usar el mismo estilo `titleLarge` bold en ambos para consistencia (recomendado) o dos pesos distintos.
- Campos y botones sociales: Roboto (`bodyMedium`, 14 sp).

## 6. Componentes
Reutilizar tal cual: `TabSelector`, `UnderlinedTextField`.
Modificar:
- `PrimaryButton`: agregar parámetro `enabled: Boolean = true`. Cuando es false, fondo `GoTalkDisabled`, texto `GoTalkOnDisabled` y sin clic.
Nuevo en `ui/components/`:
- `SocialButton(icon: Int, text: String, onClick: () -> Unit, modifier: Modifier)`: botón gris ancho con ícono a la izquierda y texto centrado.

## 7. Datos y comportamiento (propuestas, sin backend)
- Estado: email y password con `remember`. Si ya existen en `LoginScreen`, se reutilizan y se conservan al cambiar de pestaña.
- "SIGUIENTE" se habilita cuando el correo tiene formato válido (contiene "@" y un punto después) y la contraseña tiene 6 o más caracteres (mínimo que exige Firebase Auth). Regla propuesta, pendiente de confirmar.
- Botones sociales: sin lógica por ahora (onClick vacío con TODO).
- Cambio de pestaña: usar `AnimatedContent` o `Crossfade` en el contenido de la tarjeta, con la tarjeta y el fondo fijos.

## 8. Estados NO diseñados (decisión pendiente)
- Error de correo o contraseña: texto pequeño rojo bajo el campo. Sugerido, solo tras salir del campo o al intentar continuar.
- Foco del campo: sin diseño; mantener el del componente actual.
- Etiqueta de pestaña: en el Login dice "inicio de sesion" y aquí "INICIAR SESION". Unificar en una sola (recomendado "iniciar sesion").
- "Gmail" vs ícono de Google: el diseño usa "Gmail" con el logo de Google. Se deja como está.

## 9. Reglas de adaptabilidad
- Misma estructura adaptativa del Login (bloque amarillo proporcional, tarjeta con `widthIn(max)`, contenido en una Column con `widthIn(max = 233.dp)` centrada). Los botones sociales y "SIGUIENTE" usan `fillMaxWidth()` sobre esa columna.
- Alto de la tarjeta: wrap content con padding vertical fijo, sin 460 dp fijo, para que no cambie de forma brusca al alternar pestañas.
- Mantener `verticalScroll` para pantallas pequeñas.

## 10. Pedido para Claude Code (Sonnet, tras `/clear`, con commit antes)

```
Lee docs/figma-registro-credenciales.md y respeta CLAUDE.md. No compiles. No toques otros archivos fuera de los indicados.

1. ui/theme/Color.kt: agrega GoTalkDisabled (#B8B8B8) y GoTalkOnDisabled (#3A3A3A).
2. ui/components/PrimaryButton.kt: agrega el parámetro enabled: Boolean = true. Si es false, usa los colores deshabilitados y no responde al clic. No cambies su alto ni su tipografía.
3. ui/components/SocialButton.kt (nuevo): SocialButton(icon: Int, text: String, onClick: () -> Unit, modifier: Modifier = Modifier). Fila de alto 30 dp, fondo GoTalkTabBase, radio 10 dp, ícono de 20 dp a la izquierda (margen 8 dp), texto centrado con MaterialTheme.typography.bodyMedium.
4. presentation/auth/login/LoginScreen.kt: el contenido de la tarjeta depende de selectedTab, con AnimatedContent (o Crossfade). Tarjeta, bloque amarillo y logo no cambian.
   - selectedTab == 0: el contenido actual del login, sin cambios.
   - selectedTab == 1: TabSelector (misma instancia, selectedIndex = 1), título "¡Completa tu registro!" (titleLarge, centrado), UnderlinedTextField Correo, UnderlinedTextField Contraseña (oculta), 12 dp entre elementos, 3 SocialButton (ic_facebook "Facebook", ic_instagram "Instagram", ic_google "Gmail") separados 12 dp, y PrimaryButton "siguiente" con enabled = correo válido y contraseña de 6 o más caracteres.
   - Conserva email y password al cambiar de pestaña.
   - Agrega el parámetro onRegisterNext: () -> Unit a LoginScreen, que se llama al tocar "siguiente".
5. presentation/auth/NavGraph.kt: pasa onRegisterNext. Si Routes.REGISTER_STEP1 ya existe, navega a él. Si no existe, créalo con un composable placeholder con Text("Registro paso 1").
6. Espacios del bloque de registro (dp): título a 26 del TabSelector, Correo a 30 del título, botones sociales a 24 de Contraseña, SIGUIENTE a 30 del último botón social.
Al final dime qué archivos cambiaste.
```

## 11. Cómo verificar
1. `.\gradlew assembleDebug`, ▶ y tocar la pestaña "registrarse".
2. "SIGUIENTE" debe verse gris al inicio y activarse (amarillo) con un correo válido y 6 o más caracteres de contraseña.
3. Alternar entre pestañas: la tarjeta no debe cambiar de tamaño de forma brusca y lo escrito en los campos debe conservarse.
4. Mandar captura de pantalla completa para comparar con el frame.
