package com.gotalk.app.presentation.profile

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** Tope de tamaño de la foto elegida (evita copiar archivos enormes por error). */
const val MaxPhotoBytes = 20L * 1024 * 1024

/** Los archivos más recientes que esto no se limpian: puede que estén a punto de entrar al borrador. */
private const val CleanupGraceMillis = 60_000L

/**
 * Guarda la foto de perfil elegida en el almacenamiento interno de la app. El permiso que da el selector de
 * fotos es temporal, así que se copia el archivo (con su orientación EXIF intacta; Coil la aplica al mostrarla)
 * y el perfil guarda la Uri de esa copia. Cada foto es un archivo nuevo, así la pantalla siempre ve el cambio.
 */
class ProfilePhotoStore(context: Context) {
    private val appContext = context.applicationContext
    private val dir: File get() = File(appContext.filesDir, "profile_photos").apply { mkdirs() }

    /** Copia [source] a la app y devuelve la Uri de la copia, o null si no se pudo leer o es demasiado grande. */
    suspend fun import(source: Uri): String? = withContext(Dispatchers.IO) {
        val target = File(dir, "photo_${System.currentTimeMillis()}.img")
        try {
            val input = appContext.contentResolver.openInputStream(source) ?: return@withContext null
            input.use { stream ->
                target.outputStream().use { out ->
                    val buffer = ByteArray(16 * 1024)
                    var total = 0L
                    while (true) {
                        val read = stream.read(buffer)
                        if (read < 0) break
                        total += read
                        if (total > MaxPhotoBytes) {
                            target.delete()
                            return@withContext null
                        }
                        out.write(buffer, 0, read)
                    }
                }
            }
            if (target.length() == 0L) {
                target.delete()
                null
            } else {
                Uri.fromFile(target).toString()
            }
        } catch (e: Exception) {
            target.delete()
            null
        }
    }

    /** Borra las fotos que ya no usa ni lo guardado ni el borrador. */
    suspend fun cleanup(keep: Set<String?>) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        dir.listFiles()?.forEach { file ->
            val isKept = Uri.fromFile(file).toString() in keep
            if (!isKept && now - file.lastModified() > CleanupGraceMillis) file.delete()
        }
    }
}
