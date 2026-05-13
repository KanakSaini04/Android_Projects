package com.codexcraft.fileflow.data.repository

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import io.ktor.http.HttpStatusCode
import com.codexcraft.fileflow.domain.repository.ToolsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.documentfile.provider.DocumentFile
import io.ktor.server.application.call
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToolsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ToolsRepository {
    private var server: ApplicationEngine? = null
    private var flowSharePin: String = "0000"

    override suspend fun createPdfFromImages(imageUris: List<Uri>, outputUri: Uri): Boolean =
        withContext(Dispatchers.IO) {
            runCatching {
                val doc = PdfDocument()
                imageUris.forEachIndexed { index, uri ->
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        val bmp = BitmapFactory.decodeStream(input) ?: return@use
                        val pageInfo = PdfDocument.PageInfo.Builder(bmp.width, bmp.height, index + 1).create()
                        val page = doc.startPage(pageInfo)
                        val canvas: Canvas = page.canvas
                        canvas.drawBitmap(bmp, 0f, 0f, null)
                        doc.finishPage(page)
                        bmp.recycle()
                    }
                }
                context.contentResolver.openOutputStream(outputUri, "wt")?.use { out ->
                    doc.writeTo(out)
                }
                doc.close()
                true
            }.getOrDefault(false)
        }

    override suspend fun findDuplicateCandidates(): List<Pair<String, List<Uri>>> = withContext(Dispatchers.IO) {
        val filesToHash = mutableListOf<File>()
        context.cacheDir?.listFiles()?.filter { it.isFile }?.let { filesToHash.addAll(it) }
        context.externalCacheDir?.listFiles()?.filter { it.isFile }?.let { filesToHash.addAll(it) }

        val bySize = filesToHash.groupBy { it.length() }.filter { it.value.size > 1 }
        val duplicates = mutableListOf<Pair<String, List<Uri>>>()

        for ((size, files) in bySize) {
            val byHash = files.groupBy { file ->
                val md = MessageDigest.getInstance("SHA-256")
                file.inputStream().use { input ->
                    val buffer = ByteArray(8192)
                    var totalRead = 0
                    while (true) {
                        val bytesRead = input.read(buffer)
                        if (bytesRead == -1 || totalRead >= 65536) break
                        val chunk = if (totalRead + bytesRead > 65536) 65536 - totalRead else bytesRead
                        md.update(buffer, 0, chunk)
                        totalRead += chunk
                    }
                }
                md.digest().joinToString("") { "%02x".format(it) }
            }

            byHash.filter { it.value.size > 1 }.forEach { (hash, dupList) ->
                duplicates.add("Hash: ${hash.take(8)}... Size: ${size}B" to dupList.map { Uri.fromFile(it) })
            }
        }
        duplicates
    }

    override suspend fun findCacheCandidates(): List<Uri> = emptyList()

    override suspend fun deleteFiles(uris: List<Uri>): Int = withContext(Dispatchers.IO) {
        var deletedCount = 0
        uris.forEach { uri ->
            runCatching {
                val doc = DocumentFile.fromSingleUri(context, uri)
                if (doc?.delete() == true) deletedCount++
            }
        }
        deletedCount
    }

    override suspend fun startFlowShare(port: Int): String = withContext(Dispatchers.IO) {
        stopFlowShare()
        flowSharePin = (1000..9999).random().toString()
        server = embeddedServer(CIO, port = port, host = "0.0.0.0") {
            routing {
                get("/") {
                    call.respondText("FileFlow FlowShare running. Open /download/{pin}.")
                }
                get("/download/{enteredPin}") {
                    val enteredPin = call.parameters["enteredPin"]
                    if (enteredPin == flowSharePin) {
                        call.respondText("Authenticated. File listing endpoint placeholder.")
                    } else {
                        call.respondText("Unauthorized. Invalid PIN.", status = HttpStatusCode.Unauthorized)
                    }
                }
            }
        }.start(wait = false)
        val ip = getLocalIpAddress() ?: "127.0.0.1"
        "http://$ip:$port/download/$flowSharePin"
    }

    override suspend fun stopFlowShare() {
        server?.stop(1000, 2000)
        server = null
    }

    private fun getLocalIpAddress(): String? {
        return try {
            val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val intf = interfaces.nextElement()
                val addrs = intf.inetAddresses
                while (addrs.hasMoreElements()) {
                    val addr = addrs.nextElement()
                    if (!addr.isLoopbackAddress && addr is java.net.Inet4Address) return addr.hostAddress
                }
            }
            null
        } catch (_: Exception) {
            null
        }
    }
}
