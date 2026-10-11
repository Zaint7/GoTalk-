# Registro - Paso 2 de 2 ("Completa los filtros")

> Fuente: imagen exportada del frame (838 px de ancho, factor aprox. 2.03 px por dp sobre un frame de 412 dp).
> Medidas en dp con margen de +-5 dp. Colores nuevos estimados.
> Antes de empezar: este es el archivo de la pantalla que sigue a `figma-registro-paso1.md`. Reutiliza lo que ya se creó allí (ver sección 6).

## 1. Propósito y navegación
- Último paso del registro: el usuario define qué busca (idioma y país de interés, género preferido de su "GoTalker") y elige su foto de perfil.
- "GoTalker" = la persona con la que se practica el idioma (término de la app).
- Flecha atrás (botón circular) -> vuelve al Paso 1 (`popBackStack()`).
- Botón "Continuar" -> Home. Como es el final del registro, la navegación debe sacar de la pila las pantallas de registro (`popUpTo` hasta el login, inclusive), para que el botón atrás de Android no vuelva al registro.
- Ruta: `Routes.REGISTER_STEP2 = "register_step2"`. Si ya existe con un placeholder (`Text("Paso 2")`, creado en el pedido del Paso 1), reemplazar el placeholder; no duplicar la ruta.
- Por ahora no se crea cuenta ni se sube la foto (backend después). Los datos quedan en estado local.

## 2. Diferencias con el Paso 1 (importantes)
| | Paso 1 | Paso 2 |
|---|---|---|
| Cabecera | flecha + logo G + "Paso 1 de 2" | SOLO un botón circular de atrás (sin logo, sin "Paso x de 2") |
| Márgenes laterales | aprox. 28 dp | aprox. 54 dp (contenido más angosto, 305 dp de ancho) |
| Etiquetas de campos | dentro del campo | ENCIMA del campo |
| Ícono izquierdo en campos | sí | no |
| Chevron | flecha simple | flecha blanca dentro de un círculo gris oscuro |
| Ola inferior | no | sí (decorativa, abajo) |
- DECISIÓN PENDIENTE: ¿agregar "Paso 2 de 2" en la cabecera por consistencia? El diseño no lo trae. Se sigue el diseño.

## 3. Estructura de arriba abajo
Fondo crema. Ola amarilla arriba y ola amarilla abajo. Contenido en una Column con scroll, centrada, con contenido de ancho máximo 320 dp.

1. **Ola superior**: franja amarilla pegada arriba con borde inferior ondulado, aprox. 50 dp en su punto más bajo (más la barra de estado). Usar el mismo componente de ola del Paso 1.
2. **Botón atrás**: círculo de 42 dp, fondo amarillo suave (`GoTalkYellowSoft`), flecha atrás oscura de 22 dp. Margen izquierdo 28-34 dp, debajo de la barra de estado (`statusBarsPadding()`), alineado a la izquierda.
3. **Título** "Completa los filtros": centrado, negrita, mismo estilo que el título del Paso 1. Espacio sobre el título: 20 dp. SIN las chispas naranjas (misma decisión que en el Paso 1). Con Montserrat Bold el texto es más ancho que la fuente del diseño: usar 28 sp (en pantallas angostas puede pasar a 2 líneas, y está bien).
4. **Etiqueta 1** "Selecciona el idioma de tu interés" (16 sp, color tinta, alineada al inicio del campo con 8 dp de sangría). Espacio sobre la etiqueta: 40 dp bajo el título.
5. **Desplegable 1** (12 dp bajo la etiqueta): placeholder "Selecciona una opción". Alto 50 dp, ancho del contenido (305 dp), radio 18 dp, borde gris claro de 1 dp, fondo casi blanco. Chevron: círculo gris oscuro de 22 dp con flecha hacia abajo blanca, a 16 dp del borde derecho. Texto desde 22 dp del borde izquierdo.
6. **Etiqueta 2** "Selecciona el país de tu interés" (misma estilo, 28 dp bajo el desplegable 1) y **Desplegable 2** (12 dp bajo la etiqueta), igual al 1.
7. **Texto informativo** "Puedes seleccionar el género de tu GoTalker": centrado, 2 líneas (15 sp), a 26 dp bajo el desplegable 2. Indica que este campo es OPCIONAL.
8. **Desplegable 3** (19 dp bajo el texto): igual a los anteriores, mismo placeholder.
9. **Etiqueta 4** "Por último selecciona una foto de perfil": centrada, 16 sp, a 32 dp bajo el desplegable 3.
10. **Selector de foto** (24 dp bajo la etiqueta): cuadrado de esquinas muy redondeadas, 168 x 168 dp, radio 40 dp, fondo amarillo suave (`GoTalkYellowSoft`). En el centro un signo "+" grueso de 72 dp en color caqui (`GoTalkPlusKhaki`), trazo de 14 dp con extremos redondeados. Centrado horizontal.
    - En el diseño hay 3 chispas naranjas a cada lado del cuadrado (decorativas). DECISIÓN PENDIENTE: se recomienda omitirlas por ahora, igual que en el título.
11. **Botón "Continuar ->"** (39 dp bajo el selector de foto): píldora amarilla, alto 52 dp, mismo ancho que los desplegables (máx. 320 dp), texto "Continuar" en negrita (18 sp) con flecha derecha de 20 dp. Mismo botón del Paso 1 (`GoTalkPillButton`).
12. **Ola inferior**: franja amarilla pegada al borde inferior de la pantalla, aprox. 50 dp de alto en su punto más alto, borde superior ondulado. Decorativa y detrás del contenido. Espacio entre el botón y la ola: aprox. 40 dp en el diseño.

## 4. Colores
Existentes: `GoTalkYellow` (olas y botón), `GoTalkCream`, `GoTalkInk`, `GoTalkFieldBorder`, `GoTalkHintGray` (agregados en el Paso 1), `GoTalkDisabled` y `GoTalkOnDisabled` (agregados en el registro de credenciales).
Nuevos (agregar a `Color.kt`):
- `GoTalkYellowSoft` #FDF0B4: círculo del botón atrás y fondo del selector de foto.
- `GoTalkPlusKhaki` #E4CF7E: el "+" del selector de foto.
- `GoTalkChevronGray` #6B6F7A: círculo del chevron de los desplegables.

## 5. Tipografía
- Título: Montserrat Bold 28 sp (igual al Paso 1).
- Etiquetas, placeholders y texto informativo: Montserrat Regular/Medium, 16 sp (15 sp el texto informativo de 2 líneas). Placeholders en `GoTalkHintGray`.
- "Continuar": Montserrat Bold 18 sp.
- DECISIÓN PENDIENTE: el diseño usa una sans redondeada (tipo Nunito/Baloo). Se mantiene Montserrat para no sumar fuentes.

## 6. Componentes
Reutilizar del Paso 1 (revisar `ui/components/` antes de crear nada):
- `GoTalkPillButton` (Continuar), con `enabled`.
- La ola superior. Si quedó dentro de `RegisterStep1Screen`, extraerla a `ui/components/Waves.kt` como `WaveTop` y usarla en las dos pantallas.
- Los íconos y colores ya definidos.

Nuevos en `ui/components/`:
- `WaveBottom`: ola inferior (Canvas con Path), mismo color que la superior.
- `BackCircleButton(onClick)`: círculo de 42 dp con flecha atrás.
- `FilterDropdown(label, placeholder, selected, options, onSelected, labelCentered = false, hint: String? = null)`: etiqueta encima, campo con chevron circular y menú desplegable (`ExposedDropdownMenuBox` de Material 3). NO modificar `GoTalkSelectField` del Paso 1, porque tiene otro formato (ícono a la izquierda y etiqueta dentro).
- `PhotoPickerBox(imageUri, onClick)`: cuadrado de 168 dp con radio 40 dp. Sin foto muestra el "+" (dibujado con dos `Box` redondeados). Con foto muestra la imagen recortada (`ContentScale.Crop`) y oculta el "+".

## 7. Datos y comportamiento (propuestas, sin backend)
- Idiomas de ejemplo: Español, Inglés, Francés, Alemán, Portugués, Italiano, Japonés, Coreano.
- Países de ejemplo: la misma lista del Paso 1.
- Género del GoTalker: "Sin preferencia", "Femenino", "Masculino", "No binario".
- Foto: usar el selector de fotos del sistema (`ActivityResultContracts.PickVisualMedia` con `rememberLauncherForActivityResult`). No requiere permisos. Mostrar la imagen con Coil (`AsyncImage`, ya declarado en el proyecto). Si la dependencia no está activa, dejar un TODO.
- Estado con `rememberSaveable` (idioma, país, género, uri de la foto como String).
- "Continuar" habilitado cuando hay idioma y país. El género es opcional. DECISIÓN PENDIENTE: ¿la foto es obligatoria? El texto dice "Por último selecciona...", ambiguo. Propuesta: opcional por ahora.

## 8. Estados NO diseñados (decisión pendiente)
- Desplegable abierto: menú estándar de Material 3 con fondo blanco y esquinas de 12 dp.
- Foco o selección: borde en `GoTalkYellow` de 2 dp (sugerido).
- Error: si se intenta continuar sin idioma o país, borde rojo y texto pequeño bajo el campo. Con el botón deshabilitado, el error puede no hacer falta.
- Foto cargando o con error: indicador de progreso simple dentro del cuadrado (sugerido).
- Botón deshabilitado: mismos colores `GoTalkDisabled` y `GoTalkOnDisabled` del registro de credenciales.

## 9. Reglas de adaptabilidad
- Column con `verticalScroll`, `widthIn(max = 320.dp)` centrada y padding horizontal mínimo de 24 dp, para que el contenido se estreche en pantallas pequeñas sin salirse.
- Desplegables y botón con `fillMaxWidth()` dentro de esa columna, para que queden del mismo ancho (simétrico).
- Selector de foto de tamaño fijo 168 dp. Si la pantalla es angosta (menos de 320 dp), reducir a 140 dp.
- Ola inferior fija al borde de abajo (Box con `align(BottomCenter)`), detrás del contenido. El contenido hace scroll sobre ella. Dejar `navigationBarsPadding()` y 32 dp bajo el botón para que no lo tape la barra del sistema.
- Sin offsets fijos desde arriba.
- Cabecera: `statusBarsPadding()`, como en el Paso 1.

## 10. Pedido para Claude Code (Sonnet, tras `/clear`, con commit antes)

```
Lee docs/figma-registro-paso2.md y respeta CLAUDE.md. Antes de crear nada, revisa ui/components y presentation/register para reutilizar lo que ya existe del Paso 1. No compiles hasta el final de cada tanda.

TANDA 1 (componentes):
1. ui/theme/Color.kt: agrega GoTalkYellowSoft, GoTalkPlusKhaki y GoTalkChevronGray (sección 4).
2. Si la ola superior está dentro de RegisterStep1Screen, extráela a ui/components/Waves.kt como WaveTop, y úsala también en el Paso 1 sin cambiar su aspecto. Agrega en el mismo archivo WaveBottom (ola inferior, Canvas con Path, mismo amarillo).
3. Crea en ui/components/: BackCircleButton, FilterDropdown y PhotoPickerBox según la sección 6. No modifiques GoTalkSelectField ni StepHeader del Paso 1.
Dime qué creaste o cambiaste.

TANDA 2 (pantalla):
1. Crea presentation/register/RegisterStep2Screen.kt con RegisterStep2Screen(onBack: () -> Unit, onContinue: () -> Unit).
2. Sigue la estructura, los espacios y las reglas de adaptabilidad de las secciones 3 y 9. Sin chispas decorativas. Estado con rememberSaveable, datos de ejemplo de la sección 7.
3. Foto con PickVisualMedia y AsyncImage de Coil. Si Coil no está configurado, deja un TODO y muestra solo el "+".
4. Continuar habilitado con idioma y país elegidos.
5. NavGraph.kt: reemplaza el placeholder de Routes.REGISTER_STEP2 por RegisterStep2Screen, sin duplicar la ruta. onBack hace popBackStack(). onContinue navega a Routes.HOME con popUpTo(Routes.LOGIN) { inclusive = true }.
6. Agrega @Preview (Pixel 4 y teléfono pequeño de 320 x 600).
Dime qué cambiaste.
```

## 11. Cómo verificar
1. `.\gradlew assembleDebug`, ▶ en el emulador, y recorrer: Login -> "registrarse" -> SIGUIENTE -> Paso 1 -> Continuar -> Paso 2.
2. Abrir cada desplegable y elegir una opción. El botón "Continuar" debe activarse con idioma y país.
3. Tocar el cuadrado de la foto, elegir una imagen y ver que se muestra recortada dentro del cuadrado.
4. Tocar "Continuar": debe abrir Home. El botón atrás de Android no debe volver al registro.
5. Mandar captura de pantalla completa para comparar con el frame.
