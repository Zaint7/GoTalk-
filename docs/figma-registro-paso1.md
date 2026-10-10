# Registro - Paso 1 de 2 ("¡Conozcámonos!")

> Fuente: imagen exportada del frame (941 px de ancho, factor aprox. 2.28 px por dp sobre un frame de 412 dp).
> Las medidas van en dp con margen de +-4 dp. Los colores nuevos son estimados y se afinan al pulir.

## 1. Propósito y navegación
- Primer paso del registro: datos personales básicos del usuario.
- Flecha atrás -> vuelve a Login.
- Botón "Continuar" -> Registro Paso 2 (frame aún sin diseñar; por ahora navegar a una ruta placeholder).
- Ruta nueva sugerida: `Routes.REGISTER_STEP1 = "register_step1"`.
- SUPUESTO: en este paso NO hay correo ni contraseña; probablemente van en el Paso 2. Confirmar.
- DECISIÓN PENDIENTE: ¿la pestaña "registrarse" del login navega a esta pantalla?

## 2. Estructura de arriba abajo
Fondo de pantalla crema (ver colores). Todo en una Column con scroll vertical.

1. **Ola amarilla superior**: franja amarilla pegada arriba, ancho completo, borde inferior ondulado. Alto aprox. 36 dp en el punto más bajo. Dibujar con Canvas/Path (o exportar SVG desde Figma).
2. **Fila de cabecera** (alto aprox. 64 dp, justo debajo de la ola):
   - Izquierda: flecha atrás (ícono 24 dp), margen izquierdo 24 dp, color texto oscuro.
   - Centro: logo "G" (PNG a exportar de Figma, `logo_g.png`), aprox. 56 x 60 dp.
   - Derecha: texto "Paso 1 de 2", alineado a la derecha, margen 20 dp.
3. **Título** "¡Conozcámonos!" centrado, grande y en negrita (aprox. 34 sp), con 3 trazos naranjas tipo chispas arriba a la derecha de la palabra (decorativo, ver sección 6). Espacio sobre el título: aprox. 40 dp.
4. **Subtítulo** (2 líneas, centrado, aprox. 17 sp): "Completa tu perfil para conectar con personas de todo el mundo." Máximo 280 dp de ancho de texto. Espacio entre título y subtítulo: 12 dp.
5. **Fila Nombre / Apellido** (espacio sobre la fila: 24 dp):
   - Dos campos con ícono de persona a la izquierda, cada uno con `weight(1f)`, alto 48 dp, separados por 12 dp.
   - Placeholders: "Nombre" y "Apellido".
6. **Campo Fecha de nacimiento** (17 dp bajo la fila anterior): alto 52 dp, ícono de calendario a la izquierda. Dos líneas dentro: etiqueta "Fecha de nacimiento" (14 sp, color oscuro) y debajo el placeholder "DD/MM/AAAA" (14 sp, gris).
7. **Campo Género** (18 dp más abajo): alto 52 dp, ícono de género a la izquierda, chevron hacia abajo a la derecha. Etiqueta "Género" y placeholder "Selecciona una opción".
8. **Campo País de origen** (18 dp más abajo): igual al anterior, con ícono de globo. Etiqueta "País de origen" y placeholder "Selecciona una opción".
9. **Campo de texto largo** (18 dp más abajo): alto 110 dp, ícono de lápiz arriba a la izquierda, placeholder "Cuéntanos algo sobre ti (opcional)", contador "0/300" abajo a la derecha (12 sp, gris).
10. **Botón "Continuar ->"**: forma de píldora (radio completo), alto 48 dp, ancho completo menos márgenes, amarillo, texto "Continuar" en negrita (17 sp) + flecha derecha de 20 dp. Espacio sobre el botón: 28 dp. Espacio bajo el botón: 32 dp (más barra del sistema).

Margen lateral general de la pantalla: 28 dp.

## 3. Estilo de los campos (todos iguales)
- Fondo casi blanco (`#FFFFFE`), borde de 1 dp gris claro, radio 16 dp. Sin sombra.
- Ícono de 24 dp a la izquierda, color gris oscuro. Texto empieza 12 dp después del ícono.
- Padding interno horizontal 16 dp.

## 4. Colores
Existentes en el tema:
- `GoTalkYellow` (#FFE626): ola superior y botón (en la imagen se ve igual o muy parecido).

Nuevos (estimados, agregar a `Color.kt`):
- `GoTalkCream` #FDFBEF: fondo de pantalla.
- `GoTalkInk` #1F2233: textos principales, íconos y flecha atrás.
- `GoTalkFieldBorder` #D9D9E0: borde de campos.
- `GoTalkHintGray` #8A8D99: placeholders, contador.
- `GoTalkSparkOrange` #FFA726: trazos decorativos del título.

## 5. Tipografía
- Título "¡Conozcámonos!": fuente redondeada tipo caricatura, negrita. En el diseño se parece a Baloo 2 Bold.
- Resto de textos: sans redondeada, parece Nunito (Regular y SemiBold). "Continuar" en Bold.
- DECISIÓN PENDIENTE: ¿descargar Baloo 2 y Nunito de Google Fonts, o reutilizar Montserrat (ya está en el proyecto) para no sumar fuentes? Recomendación: Montserrat para esta primera versión y cambiar después si hace falta.

## 6. Recursos gráficos a exportar de Figma
- `logo_g.png`: logo "G" amarillo con borde oscuro (PNG 3x).
- Chispas del título: 3 trazos naranjas. Se pueden dibujar con Canvas (3 líneas redondeadas) o exportar como SVG y usar Vector Asset (`ic_sparks.xml`).
- Ola superior: dibujarla con Path o exportar SVG (`ic_wave_top.xml`).
- Íconos (persona, calendario, género, globo, lápiz, chevron, flecha): usar `Icons` de material-icons-extended si existen equivalentes (Person, CalendarMonth, Public, Edit, KeyboardArrowDown, ArrowBack, ArrowForward). El de género no existe en Material: exportar de Figma como SVG (`ic_gender.xml`).

## 7. Componentes
Existentes (revisar si sirven):
- `PrimaryButton`: NO sirve tal cual (es rectangular de radio 10 con Luckiest Guy). Crear uno nuevo.

Nuevos reutilizables (poner en `ui/components/`, se van a repetir en pasos siguientes):
- `GoTalkOutlinedField(leadingIcon, label?, placeholder, value, onValueChange, ...)`: campo con ícono, etiqueta opcional arriba y placeholder.
- `GoTalkSelectField(leadingIcon, label, placeholder, selected, options, onSelected)`: campo tipo desplegable con chevron. Menú con `ExposedDropdownMenuBox` o BottomSheet.
- `GoTalkDateField(...)`: campo de fecha que abre un `DatePickerDialog` de Material 3 y muestra DD/MM/AAAA.
- `GoTalkTextArea(maxChars = 300, ...)`: campo multilínea con contador "n/300" que corta en 300.
- `GoTalkPillButton(text, onClick, trailingIcon, enabled)`: botón píldora amarillo.
- `StepHeader(step, total, onBack)`: flecha atrás + logo G + "Paso x de y" (se repite en el Paso 2).

## 8. Datos de ejemplo
- Géneros: "Femenino", "Masculino", "No binario", "Prefiero no decirlo".
- Países: lista corta de ejemplo (Colombia, México, Argentina, España, Estados Unidos, Brasil, Francia, Alemania, Japón).
- Estado inicial: todos los campos vacíos y contador en 0/300.

## 9. Estados NO diseñados (decisión pendiente)
- Foco del campo: sugerido borde en `GoTalkYellow` de 2 dp.
- Error de validación: sugerido borde rojo y texto pequeño debajo.
- Botón "Continuar" deshabilitado hasta completar nombre, apellido, fecha, género y país. La descripción es opcional. Sugerido: gris con baja opacidad.
- Fecha inválida o edad mínima: decidir la regla (por ejemplo una edad mínima) antes de implementarla.
- Teclado abierto: la pantalla debe desplazarse para que el campo enfocado no quede tapado.

## 10. Reglas de adaptabilidad
- Contenido en una Column con `verticalScroll` e `imePadding()`, con `widthIn(max = 480.dp)` centrada en pantallas anchas.
- Nombre y Apellido con `weight(1f)` cada uno. El resto de campos con `fillMaxWidth()`.
- Alturas de campos fijas en dp (48 y 52). La caja de texto largo con `heightIn(min = 110.dp)`.
- Ola superior: ancho completo, alto fijo.
- Si la pantalla es alta, el botón se queda al final con un `Spacer(weight(1f))` antes. Si es baja, hace scroll. Dejar siempre 32 dp bajo el botón y `navigationBarsPadding()`.
- Sin offsets fijos desde arriba.

## 11. Pedido para Claude Code (pegar tal cual, en Sonnet, tras `/clear`)

```
Lee docs/figma-registro-paso1.md y respeta CLAUDE.md. Implementa en dos tandas. No compiles hasta el final de cada una.

TANDA 1 (componentes):
1. En ui/theme/Color.kt agrega los colores nuevos de la sección 4.
2. En ui/components/ crea GoTalkOutlinedField, GoTalkSelectField, GoTalkDateField, GoTalkTextArea, GoTalkPillButton y StepHeader según la sección 7. Sin lógica de negocio.
3. Íconos: usa material-icons-extended; para el de género y las chispas deja un TODO con Icon(Icons.Default.Person) temporal.
4. Para el logo G y la ola usa un Box/Canvas simple si aún no existen los recursos (logo_g.png), con un TODO.
Dime qué creaste.

TANDA 2 (pantalla):
1. Crea presentation/register/RegisterStep1Screen.kt con RegisterStep1Screen(onBack: () -> Unit, onContinue: () -> Unit).
2. Sigue la estructura, espacios y reglas de adaptabilidad de las secciones 2, 3 y 10. Estado con remember, sin ViewModel. Datos de ejemplo de la sección 8.
3. Botón Continuar deshabilitado hasta completar nombre, apellido, fecha, género y país.
4. Agrega Routes.REGISTER_STEP1 y su composable en NavGraph. El onBack vuelve al login y onContinue navega por ahora a una pantalla placeholder con un Text("Paso 2").
5. Agrega @Preview con Pixel 4 y un teléfono pequeño (320 x 600).
No toques SplashViewModel. Dime qué cambiaste.
```

## 12. Cómo verificar
1. `.\gradlew assembleDebug`, luego ▶ en el emulador.
2. Comparar con el frame y mandar captura de pantalla completa.
3. Revisar los dos Preview, el scroll con teclado abierto y el contador 0/300.
