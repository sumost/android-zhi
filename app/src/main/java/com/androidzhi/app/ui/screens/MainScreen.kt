package com.androidzhi.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.androidzhi.app.R
import com.androidzhi.app.data.model.DeviceState
import com.androidzhi.app.ui.theme.*
import com.androidzhi.app.ui.viewmodel.MainViewModel

/**
 * 主屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val deviceState by viewModel.deviceState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    ConnectionIndicator(deviceState = deviceState)
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background)
        ) {
            // 状态显示
            Text(
                text = when (deviceState) {
                    DeviceState.Idle -> stringResource(R.string.status_idle)
                    DeviceState.Connecting -> stringResource(R.string.status_connecting)
                    DeviceState.Listening -> stringResource(R.string.status_listening)
                    DeviceState.Speaking -> stringResource(R.string.status_speaking)
                },
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = when (deviceState) {
                    DeviceState.Idle -> Idle
                    DeviceState.Connecting -> Connecting
                    DeviceState.Listening -> Listening
                    DeviceState.Speaking -> Speaking
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp)
            )
            
            // 语音按钮
            VoiceButton(
                deviceState = deviceState,
                modifier = Modifier.align(Alignment.Center),
                onPressStart = { viewModel.startListening() },
                onPressEnd = { viewModel.stopListening() }
            )
            
            // 底部提示
            Text(
                text = stringResource(R.string.hold_to_talk),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp)
            )
        }
    }
}

@Composable
private fun ConnectionIndicator(deviceState: DeviceState) {
    val color = when (deviceState) {
        DeviceState.Idle -> Idle
        DeviceState.Connecting -> Connecting
        DeviceState.Listening -> Listening
        DeviceState.Speaking -> Speaking
    }
    
    Box(
        modifier = Modifier
            .padding(end = 8.dp)
            .size(12.dp)
            .background(color, CircleShape)
    )
}

@Composable
private fun VoiceButton(
    deviceState: DeviceState,
    modifier: Modifier = Modifier,
    onPressStart: () -> Unit,
    onPressEnd: () -> Unit
) {
    val buttonColor = when (deviceState) {
        DeviceState.Idle -> Idle
        DeviceState.Connecting -> Connecting
        DeviceState.Listening -> Listening
        DeviceState.Speaking -> Speaking
    }
    
    Box(
        modifier = modifier
            .size(120.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(buttonColor, buttonColor.copy(alpha = 0.7f))
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = stringResource(R.string.hold_to_talk),
            tint = androidx.compose.ui.graphics.Color.White,
            modifier = Modifier.size(48.dp)
        )
    }
}