package com.stilllife.generator

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF121212)
            ) {
                StillLifeScreen()
            }
        }
    }
}

@Composable
fun StillLifeScreen() {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var resultBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var creepinessSlider by remember { mutableFloatStateOf(0.7f) }
    var isLoading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "STILL LIFE ENTITY GENERATOR",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFD4CE46)
        )

        Box(
            modifier = Modifier
                .size(300.dp)
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            if (resultBitmap != null) {
                Image(
                    bitmap = resultBitmap!!.asImageBitmap(),
                    contentDescription = "Still Life Result",
                    modifier = Modifier.fillMaxSize()
                )
            } else if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Selected Meme/Image",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(text = "Tap below to upload meme or photo", color = Color.White)
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Creepiness Level / Abstraction:", color = Color.White)
            Slider(
                value = creepinessSlider,
                onValueChange = { creepinessSlider = it },
                valueRange = 0.3f..0.95f
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Select Photo")
                }
                
                Button(
                    onClick = { isLoading = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000))
                ) {
                    Text(if (isLoading) "Generating..." else "Transform")
                }
            }
        }
    }
}
