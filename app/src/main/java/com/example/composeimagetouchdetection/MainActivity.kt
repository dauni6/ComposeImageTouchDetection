package com.example.composeimagetouchdetection

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.composeimagetouchdetection.ui.theme.ComposeImageTouchDetectionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeImageTouchDetectionTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    ImageTouchDetection()
                }
            }
        }
    }
}

@Composable
fun ImageTouchDetection() {
    val imageBitmap: ImageBitmap =
        ImageBitmap.imageResource(LocalContext.current.resources, R.drawable.sample)
    val bitmapWidth = imageBitmap.width
    val bitmapHeight = imageBitmap.height

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var imageSize by remember { mutableStateOf(Size.Zero) }

    var text by remember { mutableStateOf("") }
    var colorAtTouchPosition by remember { mutableStateOf(Color.Unspecified) }

    val imageModifier = Modifier
        .background(Color.LightGray)
        .fillMaxWidth()
        .aspectRatio(4f / 3) // aspectRatio?
        .pointerInput(Unit) {
            detectTapGestures { offset ->
                offsetX = offset.x
                offsetY = offset.y

                val scaledX = (bitmapWidth / imageSize.width) * offsetX
                val scaledY = (bitmapHeight / imageSize.height) * offsetY

                try {
                    val pixel: Int = imageBitmap
                        .asAndroidBitmap() // 얜 또 뭐야?
                        .getPixel(scaledX.toInt(), scaledY.toInt())
                    val red = android.graphics.Color.red(pixel)
                    val green = android.graphics.Color.green(pixel)
                    val blue = android.graphics.Color.blue(pixel)

                    text = "Image Touch offsetX: $offsetX, offsetY: $offsetY\n" +
                            "Image width: ${imageSize.width}, height: ${imageSize.height}\n" +
                            "Bitmap width: $bitmapWidth, height: $bitmapHeight\n" +
                            "scaledX: $scaledX, scaledY: $scaledY\n" +
                            "red: $red, green: $green, blue: $blue\n" +
                            ""

                    colorAtTouchPosition = Color(red, green, blue)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        .onSizeChanged {
            Log.d("TEST", "onSizeChanged/ size = ${it.toSize()}")
            imageSize = it.toSize()
        }

    Column {
        Image(
            modifier = imageModifier
                .border(2.dp, Color.Red),
            bitmap = imageBitmap,
            contentDescription = null
        )

        Text(text = text)

        Box(
            modifier = Modifier
                .then(
                   if (colorAtTouchPosition.isUnspecified) {
                       Modifier
                   } else {
                       Modifier.background(colorAtTouchPosition)
                   }
                )
                .size(100.dp)
        )
    }

}
