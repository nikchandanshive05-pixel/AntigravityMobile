package com.antigravity.mobile.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antigravity.mobile.data.model.*
import com.antigravity.mobile.ui.components.ApprovalBanner
import com.antigravity.mobile.ui.components.ToolCard
import com.antigravity.mobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    connectionState: ConnectionState,
    onSendMessage: (String) -> Unit,
    onApproveAction: (String) -> Unit,
    onDenyAction: (String) -> Unit,
    onOpenDrawer: () -> Unit,
    onManualConnect: (String, Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var showPairingDialog by remember { mutableStateOf(false) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Gemini Sparkle Logo
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(GeminiSparkleGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Gemini",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Antigravity",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    ),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "2.0",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = GeminiPurple
                                )
                            }
                            // Connection Subtitle
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(
                                            if (connectionState is ConnectionState.Connected) Color(0xFF34A853) else Color(0xFFEA4335),
                                            shape = CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (connectionState is ConnectionState.Connected) "Connected to PC" else "Connecting...",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = GeminiTextSecondary
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Drawer", tint = GeminiText)
                    }
                },
                actions = {
                    IconButton(onClick = { showPairingDialog = true }) {
                        Icon(Icons.Default.Tune, contentDescription = "Connection Settings", tint = GeminiTextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GeminiBg)
            )
        },
        containerColor = GeminiBg,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Canvas
            if (messages.isEmpty() || (messages.size == 1 && messages[0].id == "welcome")) {
                // Signature Google Gemini Greeting
                GeminiEmptyState(
                    onSuggestionClick = { onSendMessage(it) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    items(messages) { msg ->
                        GeminiChatBubble(
                            message = msg,
                            onApproveAction = onApproveAction,
                            onDenyAction = onDenyAction
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Google Gemini Floating Prompt Bar
            GeminiFloatingInputBar(
                inputText = inputText,
                onTextChanged = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText.trim())
                        inputText = ""
                    }
                }
            )
        }
    }

    if (showPairingDialog) {
        ManualPairingDialog(
            onDismiss = { showPairingDialog = false },
            onConnect = { host, port, pin ->
                showPairingDialog = false
                onManualConnect(host, port, pin)
            }
        )
    }
}

@Composable
fun GeminiEmptyState(
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        // Gradient Greeting
        Text(
            text = "Hello, Nikhil",
            style = TextStyle(
                brush = GeminiSparkleGradient,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Antigravity is connected and ready.",
            style = MaterialTheme.typography.titleMedium,
            color = GeminiTextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Gemini Quick Action Pills
        Text(
            text = "SUGGESTED ACTIONS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = GeminiTextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        val suggestions = listOf(
            "✨ What are the latest workspace files changed?",
            "💻 Run git status and check repo health",
            "🔍 Inspect active Antigravity background tasks",
            "🚀 What should we build next in resilient-hertz?"
        )

        suggestions.forEach { sug ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GeminiSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable { onSuggestionClick(sug) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = sug,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GeminiText,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowOutward,
                        contentDescription = null,
                        tint = GeminiPurple,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GeminiChatBubble(
    message: ChatMessage,
    onApproveAction: (String) -> Unit,
    onDenyAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (message.sender == Sender.USER) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp),
                color = GeminiUserBubble,
                modifier = Modifier.widthIn(max = 310.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 22.sp),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    } else {
        Column(modifier = modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(GeminiSparkleGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Gemini 3.8 Flash • Antigravity",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = GeminiBlue
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Body text
            if (message.content.isNotEmpty()) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp,
                        lineHeight = 24.sp
                    ),
                    color = GeminiText,
                    modifier = Modifier.padding(start = 30.dp)
                )
            } else if (message.isStreaming) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 30.dp, top = 4.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = GeminiPurple,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Thinking...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        color = GeminiTextSecondary
                    )
                }
            }

            // Embedded Tool Calls
            if (message.toolCalls.isNotEmpty()) {
                Column(modifier = Modifier.padding(start = 30.dp, top = 8.dp)) {
                    message.toolCalls.forEach { tool ->
                        ToolCard(toolCall = tool)
                    }
                }
            }

            // Action Approval Banner
            message.pendingApproval?.let { approval ->
                Box(modifier = Modifier.padding(start = 30.dp, top = 8.dp)) {
                    ApprovalBanner(
                        approval = approval,
                        onApprove = onApproveAction,
                        onDeny = onDenyAction
                    )
                }
            }
        }
    }
}

@Composable
fun GeminiFloatingInputBar(
    inputText: String,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        color = GeminiSurface,
        shape = RoundedCornerShape(32.dp),
        border = ButtonDefaults.outlinedButtonBorder,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            IconButton(
                onClick = { onTextChanged(inputText + "/") },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Slash Command",
                    tint = GeminiTextSecondary
                )
            }

            TextField(
                value = inputText,
                onValueChange = onTextChanged,
                placeholder = {
                    Text(
                        text = "Ask Antigravity...",
                        color = GeminiTextSecondary,
                        fontSize = 15.sp
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )

            // Dynamic Send / Sparkle Button
            if (inputText.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(GeminiSparkleGradient)
                        .clickable { onSend() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = { /* Voice Mic */ },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = GeminiPurple
                    )
                }
            }
        }
    }
}

@Composable
fun ManualPairingDialog(
    onDismiss: () -> Unit,
    onConnect: (String, Int, String) -> Unit
) {
    var host by remember { mutableStateOf("192.168.31.136") }
    var port by remember { mutableStateOf("8765") }
    var pin by remember { mutableStateOf("1425") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Antigravity PC Connection", color = Color.White) },
        text = {
            Column {
                OutlinedTextField(
                    value = host,
                    onValueChange = { host = it },
                    label = { Text("PC IP Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = port,
                    onValueChange = { port = it },
                    label = { Text("Port") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    label = { Text("Pairing PIN") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = port.toIntOrNull() ?: 8765
                    onConnect(host.trim(), p, pin.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = GeminiBlue)
            ) {
                Text("Connect")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = GeminiTextSecondary)
            }
        },
        containerColor = GeminiSurface
    )
}
