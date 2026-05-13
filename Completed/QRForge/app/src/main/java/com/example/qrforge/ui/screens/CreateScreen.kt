package com.example.qrforge.ui.screens

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.example.qrforge.ui.theme.*
import com.example.qrforge.ui.viewmodel.CreateViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

data class QRType(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

val qrTypes = listOf(
    QRType("Website", Icons.Outlined.Language,   QRForgeColors.AccentBlue),
    QRType("Text",    Icons.Outlined.TextFields, QRForgeColors.AccentOrange),
    QRType("Email",   Icons.Outlined.Email,      QRForgeColors.AccentTeal),
    QRType("SMS",     Icons.Outlined.Sms,        QRForgeColors.AccentPink),
    QRType("WiFi",    Icons.Outlined.Wifi,       QRForgeColors.AccentPurple),
    QRType("Phone",   Icons.Outlined.Phone,      QRForgeColors.AccentGreen),
    QRType("Contact", Icons.Outlined.Person,     QRForgeColors.AccentRed),
)

val presetColors = listOf(
    Color.Black, Color(0xFF1A1A2E), Color(0xFF4B3FC7),
    Color(0xFF4F8EF7), Color(0xFF00C9A7), Color(0xFFFF6B9D),
    Color(0xFFFF8C42), Color.White
)

@Composable
fun CreateScreen(viewModel: CreateViewModel) {
    var selectedType by remember { mutableStateOf<QRType?>(null) }
    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var qrColor by remember { mutableStateOf(Color.Black) }
    var bgColor by remember { mutableStateOf(Color.White) }
    var qrSize by remember { mutableStateOf(512) }
    val context = LocalContext.current

    if (selectedType == null) {
        TypeSelectorScreen { selectedType = it }
    } else {
        QRFormScreen(
            type = selectedType!!,
            qrColor = qrColor,
            bgColor = bgColor,
            qrSize = qrSize,
            generatedBitmap = generatedBitmap,
            onQrColorChange = { qrColor = it },
            onBgColorChange = { bgColor = it },
            onSizeChange = { qrSize = it },
            onGenerate = { content, logo ->
                generatedBitmap = generateQRBitmap(content, qrSize, qrColor, bgColor, logo)
                viewModel.saveGenerated(content, selectedType!!.name)
            },
            onShare = { bmp -> shareQR(context, bmp) },
            onSave = { bmp -> saveQRToGallery(context, bmp) },
            onBack = { selectedType = null; generatedBitmap = null }
        )
    }
}

@Composable
fun TypeSelectorScreen(onTypeSelected: (QRType) -> Unit) {
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Row {
                Text(
                    "QR", style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    "Forge", style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "Choose", style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                "your Destination!", style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(8.dp))

        qrTypes.forEach { type ->
            Row(
                Modifier.fillMaxWidth().clickable { onTypeSelected(type) }
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(48.dp).clip(RoundedCornerShape(14.dp))
                        .background(type.color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(type.icon, null, tint = type.color, modifier = Modifier.size(26.dp))
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    type.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.ChevronRight, null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(0.3f)
                )
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline,
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun QRFormScreen(
    type: QRType,
    qrColor: Color, bgColor: Color, qrSize: Int,
    generatedBitmap: Bitmap?,
    onQrColorChange: (Color) -> Unit,
    onBgColorChange: (Color) -> Unit,
    onSizeChange: (Int) -> Unit,
    onGenerate: (String, Bitmap?) -> Unit,
    onShare: (Bitmap) -> Unit,
    onSave: (Bitmap) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var ssid by remember { mutableStateOf("") }
    var wifiPass by remember { mutableStateOf("") }
    var wifiSec by remember { mutableStateOf("WPA") }
    var emailTo by remember { mutableStateOf("") }
    var emailSubject by remember { mutableStateOf("") }
    var emailBody by remember { mutableStateOf("") }
    var smsTo by remember { mutableStateOf("") }
    var smsBody by remember { mutableStateOf("") }
    var contactName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var logoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val logoPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val bmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    android.graphics.ImageDecoder.decodeBitmap(
                        android.graphics.ImageDecoder.createSource(context.contentResolver, it)
                    ) { decoder, _, _ ->
                        decoder.allocator = android.graphics.ImageDecoder.ALLOCATOR_SOFTWARE
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                }
                // Always convert to ARGB_8888 mutable
                logoBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true)
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    Icons.Default.ArrowBack, null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(Modifier.width(12.dp))
            Box(
                Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                    .background(type.color.copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(type.icon, null, tint = type.color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(10.dp))
            Text(
                type.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Type specific fields
            when (type.name) {
                "Website", "Text" -> {
                    QRTextField(
                        label = if (type.name == "Website") "Enter URL" else "Enter text",
                        value = text,
                        onValueChange = { text = it },
                        multiLine = type.name == "Text"
                    )
                }
                "WiFi" -> {
                    QRTextField("Network Name (SSID)", ssid, { ssid = it })
                    QRTextField("Password", wifiPass, { wifiPass = it })
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("WPA", "WEP", "None").forEach { sec ->
                            FilterChip(
                                selected = wifiSec == sec,
                                onClick = { wifiSec = sec },
                                label = { Text(sec) }
                            )
                        }
                    }
                }
                "Email" -> {
                    QRTextField("To", emailTo, { emailTo = it })
                    QRTextField("Subject", emailSubject, { emailSubject = it })
                    QRTextField("Message", emailBody, { emailBody = it }, multiLine = true)
                }
                "SMS" -> {
                    QRTextField(
                        "Phone Number", smsTo, { smsTo = it },
                        keyboard = KeyboardType.Phone
                    )
                    QRTextField("Message", smsBody, { smsBody = it }, multiLine = true)
                }
                "Phone" -> {
                    QRTextField(
                        "Phone Number", text, { text = it },
                        keyboard = KeyboardType.Phone
                    )
                }
                "Contact" -> {
                    QRTextField("Full Name", contactName, { contactName = it })
                    QRTextField(
                        "Phone", contactPhone, { contactPhone = it },
                        keyboard = KeyboardType.Phone
                    )
                    QRTextField(
                        "Email", contactEmail, { contactEmail = it },
                        keyboard = KeyboardType.Email
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // Customize
            Text(
                "Customize",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ColorPickerRow("QR Color", qrColor, onQrColorChange, Modifier.weight(1f))
                ColorPickerRow("Background", bgColor, onBgColorChange, Modifier.weight(1f))
            }

            // Size selector
            Text(
                "Export Size",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(0.6f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(256, 512, 1024).forEach { size ->
                    FilterChip(
                        selected = qrSize == size,
                        onClick = { onSizeChange(size) },
                        label = { Text("${size}px") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            // Logo / Watermark
            Text(
                "Logo (optional)",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(0.6f)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { logoPickerLauncher.launch("image/*") },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Icon(
                        Icons.Outlined.AddPhotoAlternate, null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(if (logoBitmap != null) "Change Logo" else "Add Logo")
                }
                if (logoBitmap != null) {
                    OutlinedButton(
                        onClick = { logoBitmap = null },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.error.copy(0.4f)
                        )
                    ) {
                        Text("Remove", color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            // Logo preview
            if (logoBitmap != null) {
                Box(
                    Modifier.size(56.dp).clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                ) {
                    Image(
                        logoBitmap!!.asImageBitmap(), null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Generate button
            Button(
                onClick = {
                    val content = buildQRContent(
                        type.name, text, ssid, wifiPass, wifiSec,
                        emailTo, emailSubject, emailBody,
                        smsTo, smsBody, contactName, contactPhone, contactEmail
                    )
                    if (content.isNotBlank()) onGenerate(content, logoBitmap)
                    else Toast.makeText(
                        context, "Please fill in the required fields",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = type.color)
            ) {
                Icon(Icons.Outlined.QrCode, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Generate QR Code",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            // QR Preview Card
            AnimatedVisibility(
                visible = generatedBitmap != null,
                enter = fadeIn() + scaleIn()
            ) {
                generatedBitmap?.let { bmp ->
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Image(
                                bmp.asImageBitmap(), null,
                                modifier = Modifier.size(220.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Text(
                                "Here your code!!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = type.color
                            )
                            Text(
                                "This is your unique QR code for others to scan",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                            )
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onShare(bmp) },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, type.color.copy(0.4f))
                                ) {
                                    Icon(
                                        Icons.Outlined.Share, null,
                                        tint = type.color,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        "Share", color = type.color,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Button(
                                    onClick = { onSave(bmp) },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = type.color
                                    )
                                ) {
                                    Icon(
                                        Icons.Outlined.Save, null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        "Save", color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun QRTextField(
    label: String, value: String,
    onValueChange: (String) -> Unit,
    multiLine: Boolean = false,
    keyboard: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        minLines = if (multiLine) 3 else 1,
        maxLines = if (multiLine) 5 else 1,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun ColorPickerRow(
    label: String, current: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(0.6f)
        )
        presetColors.chunked(4).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { color ->
                    Box(
                        Modifier.size(28.dp).clip(CircleShape)
                            .background(color)
                            .border(
                                if (current == color) 2.dp else 0.dp,
                                MaterialTheme.colorScheme.primary, CircleShape
                            )
                            .border(0.5.dp, Color.Gray.copy(0.2f), CircleShape)
                            .clickable { onColorChange(color) }
                    )
                }
            }
        }
    }
}

fun generateQRBitmap(
    content: String,
    size: Int,
    fg: Color,
    bg: Color,
    logoBitmap: Bitmap? = null
): Bitmap? {
    return try {
        val hints = mapOf(EncodeHintType.MARGIN to 1)
        val matrix = MultiFormatWriter().encode(
            content, BarcodeFormat.QR_CODE, size, size, hints
        )
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (x in 0 until size) for (y in 0 until size)
            bmp.setPixel(x, y, if (matrix[x, y]) fg.toArgb() else bg.toArgb())

        // Overlay logo in center
        // Overlay logo in center
        if (logoBitmap != null) {
            try {
                // Convert QR bitmap to mutable ARGB_8888
                val mutableBmp = bmp.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = android.graphics.Canvas(mutableBmp)
                val logoSize = size / 5
                val left = (size - logoSize) / 2f
                val top = (size - logoSize) / 2f

                // Convert logo to ARGB_8888 too
                val safeLogo = logoBitmap.copy(Bitmap.Config.ARGB_8888, false)

                // White rounded background behind logo
                val bgPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    isAntiAlias = true
                    style = android.graphics.Paint.Style.FILL
                }
                val padding = logoSize / 6f
                canvas.drawRoundRect(
                    left - padding,
                    top - padding,
                    left + logoSize + padding,
                    top + logoSize + padding,
                    20f, 20f,
                    bgPaint
                )

                // Draw scaled logo
                val scaledLogo = Bitmap.createScaledBitmap(safeLogo, logoSize, logoSize, true)
                val logoPaint = android.graphics.Paint().apply {
                    isAntiAlias = true
                    isFilterBitmap = true
                }
                canvas.drawBitmap(scaledLogo, left, top, logoPaint)

                return mutableBmp
            } catch (e: Exception) {
                e.printStackTrace()
                // Return QR without logo if logo overlay fails
                return bmp
            }
        }
        bmp
    } catch (e: Exception) { null }
}

fun buildQRContent(
    type: String, text: String,
    ssid: String, wifiPass: String, wifiSec: String,
    emailTo: String, emailSubject: String, emailBody: String,
    smsTo: String, smsBody: String,
    contactName: String, contactPhone: String, contactEmail: String
): String = when (type) {
    "Website" -> text.trim().let { if (it.startsWith("http")) it else "https://$it" }
    "Text"    -> text
    "WiFi"    -> "WIFI:T:$wifiSec;S:$ssid;P:$wifiPass;;"
    "Email"   -> "mailto:$emailTo?subject=${Uri.encode(emailSubject)}&body=${Uri.encode(emailBody)}"
    "SMS"     -> "smsto:$smsTo:$smsBody"
    "Phone"   -> "tel:$text"
    "Contact" -> "BEGIN:VCARD\nVERSION:3.0\nFN:$contactName\nTEL:$contactPhone\nEMAIL:$contactEmail\nEND:VCARD"
    else      -> text
}

fun saveQRToGallery(context: Context, bitmap: Bitmap) {
    try {
        val filename = "QRForge_${System.currentTimeMillis()}.png"
        val fos: OutputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/QRForge"
                )
            }
            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
            )!!
            context.contentResolver.openOutputStream(uri)!!
        } else {
            val dir = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ), "QRForge"
            )
            dir.mkdirs()
            FileOutputStream(File(dir, filename))
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
        Toast.makeText(context, "Saved to Gallery!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
    }
}

fun shareQR(context: Context, bitmap: Bitmap) {
    try {
        val cachePath = File(context.cacheDir, "qrforge_share.png")
        FileOutputStream(cachePath).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        val uri = FileProvider.getUriForFile(
            context, "${context.packageName}.provider", cachePath
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share QR Code"))
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to share", Toast.LENGTH_SHORT).show()
    }
}