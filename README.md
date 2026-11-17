# GoTalk!

App Android de intercambio de idiomas: descubres a otros GoTalkers, les das like o los rechazas, les escribes un mensaje para romper el hielo y mantienes tu propio perfil.

> Estado: **front primero**. Todo funciona con datos de prueba (mock); el backend (Firebase) está declarado en el proyecto pero todavía no se usa en estas pantallas.

## Pantallas y funciones

**Descubrir**
- Tarjeta de perfil con panel de detalles desplegable (se arrastra en vertical o con el botón).
- Botones X, chat y corazón, y **swipe táctil** de la tarjeta: derecha = me gusta, izquierda = no me gusta. Botones y gesto entran por el mismo punto (`triggerSwipe`), con la animación de olas.
- Ventanas flotantes de **notificaciones** y **filtros** (edad, género, idiomas, países, solo en línea).
- **Enviar mensaje**: ventana con sugerencias para romper el hielo, contador de 200 caracteres, estados Enviando / Enviado / Error con reintento. Enviar un mensaje cuenta como like, y a esa tarjeta ya no se le puede dar X.

**Perfil**
- Foto (selector del sistema, copia local), país, idioma nativo y descripción de hasta 300 caracteres.
- Selectores con buscador, validación (país e idioma nativo obligatorios), borrador separado de lo guardado y descartar cambios.

## Tecnología

- Kotlin, Jetpack Compose, Material 3
- Hilt (inyección), Navigation Compose, Coil (imágenes)
- Firebase (Auth, Firestore, Storage, Messaging) declarado para el backend
- `minSdk 26`, `compileSdk 35`, `applicationId com.gotalk.app`

## Estructura

```
app/src/main/java/com/gotalk/app/
├── presentation/
│   ├── auth/ register/      Login, registro y navegación
│   ├── main/                Contenedor con la barra inferior
│   ├── discover/            Descubrir: pantalla, ViewModel, filtros, notificaciones, mensajes
│   └── profile/             Perfil: pantalla, ViewModel, modelo y catálogo
└── ui/
    ├── components/          Componentes reutilizables (tarjeta, botones, ventanas flotantes, ondas...)
    └── theme/               Colores, formas, tipografía y degradados (tokens Composer*)
```

La lógica de cada pantalla vive en su ViewModel (estado inmutable + funciones); las pantallas son composables sin estado que reciben datos y lambdas.

## Cómo ejecutarlo

1. Android Studio reciente y JDK 11 o superior.
2. Crea tu proyecto en [Firebase](https://console.firebase.google.com), registra la app Android `com.gotalk.app`, descarga su `google-services.json` y colócalo en `app/`. El archivo **no está en el repositorio** (está en `.gitignore`); `app/google-services.json.example` muestra su forma.
3. Abre la carpeta del proyecto y deja que Gradle sincronice.
4. Ejecuta la configuración `app` en un emulador o dispositivo (API 26+).

Pruebas unitarias (ViewModels, filtros, gesto de swipe, buscador, perfil y mensajes):

```
./gradlew :app:testDebugUnitTest
```

## Pendiente

- Probar y ajustar todo en dispositivo.
- Pestañas Comunidad y Chats (hoy son marcadores); la bandeja de enviados ya está lista para alimentar Chats.
- Backend real: perfiles, mensajes y ids de GoTalker (hoy el destinatario se identifica por nombre).
- Banderas propias para más países e idiomas (hoy ES, GB, FR e IT).
- Edición de la ciudad y de los idiomas adicionales en Perfil.
