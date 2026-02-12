package com.sekyiemmanuel.mina.feature.inbox.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sekyiemmanuel.mina.core.ui.theme.AccentFlame
import com.sekyiemmanuel.mina.core.ui.theme.CanvasBackground
import com.sekyiemmanuel.mina.feature.inbox.domain.InboxItem
import com.sekyiemmanuel.mina.feature.inbox.domain.InboxItemType

@Composable
fun InboxRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InboxViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(InboxUiEvent.OnAppear)
    }

    InboxScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    uiState: InboxUiState,
    onBackClick: () -> Unit,
    onEvent: (InboxUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.isShowingCaptureOptions) {
        QuickCaptureSheet(
            onDismiss = { onEvent(InboxUiEvent.HideCaptureOptions) },
            onVoiceCapture = { onEvent(InboxUiEvent.StartVoiceRecording) },
            onPhotoCapture = { onEvent(InboxUiEvent.CapturePhoto) },
            onScanCapture = { onEvent(InboxUiEvent.ScanDocument) },
        )
    }

    uiState.selectedItem?.let { selected ->
        InboxItemDetailDialog(
            item = selected,
            onDismiss = { onEvent(InboxUiEvent.DismissItemDetail) },
            onConvertToEntry = { onEvent(InboxUiEvent.ConvertToEntry(selected.id)) },
            onArchive = { onEvent(InboxUiEvent.ArchiveItem(selected.id)) },
            onDelete = { onEvent(InboxUiEvent.DeleteItem(selected.id)) },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        InboxTopBar(
            unprocessedCount = uiState.unprocessedCount,
            onBackClick = onBackClick,
        )

        Spacer(modifier = Modifier.height(10.dp))

        FilterPillsRow(
            currentFilter = uiState.filter,
            countProvider = { filter -> uiState.countForFilter(filter) },
            onFilterChanged = { onEvent(InboxUiEvent.FilterChanged(it)) },
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(modifier = Modifier.weight(1f)) {
            when {
                uiState.isLoading && uiState.items.isEmpty() -> InboxLoadingState()
                uiState.filteredItems.isEmpty() -> InboxEmptyState(
                    isArchived = uiState.filter == InboxFilter.ARCHIVED,
                    title = uiState.emptyStateTitle,
                    message = uiState.emptyStateMessage,
                    showAllVisible = uiState.filter != InboxFilter.ALL,
                    onShowAll = { onEvent(InboxUiEvent.FilterChanged(InboxFilter.ALL)) },
                )
                else -> InboxList(
                    sections = uiState.groupedItems,
                    onItemTapped = { onEvent(InboxUiEvent.ItemTapped(it)) },
                    onConvertToEntry = { onEvent(InboxUiEvent.ConvertToEntry(it)) },
                    onArchive = { onEvent(InboxUiEvent.ArchiveItem(it)) },
                    onUnarchive = { onEvent(InboxUiEvent.UnarchiveItem(it)) },
                    onDelete = { onEvent(InboxUiEvent.DeleteItem(it)) },
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
            ) {
                if (uiState.isRecording) {
                    RecordingIndicator(
                        duration = formatRecordingDuration(uiState.recordingDurationSeconds),
                        onStopClick = { onEvent(InboxUiEvent.StopVoiceRecording) },
                    )
                } else {
                    QuickCaptureButton(
                        onClick = { onEvent(InboxUiEvent.ShowCaptureOptions) },
                    )
                }
            }
        }
    }
}

@Composable
private fun InboxTopBar(
    unprocessedCount: Int,
    onBackClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            onClick = onBackClick,
            shape = CircleShape,
            color = Color.White,
            modifier = Modifier.size(42.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Inbox",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.weight(1f),
        )
        if (unprocessedCount > 0) {
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = AccentFlame,
            ) {
                Text(
                    text = unprocessedCount.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }
    }
}

@Composable
private fun FilterPillsRow(
    currentFilter: InboxFilter,
    countProvider: (InboxFilter) -> Int,
    onFilterChanged: (InboxFilter) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        InboxFilter.entries.forEach { filter ->
            val selected = filter == currentFilter
            val count = countProvider(filter)
            Surface(
                onClick = { onFilterChanged(filter) },
                shape = RoundedCornerShape(100.dp),
                color = if (selected) AccentFlame else Color.White,
                shadowElevation = 0.dp,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = filter.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                    )
                    if (count > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (selected) Color.White.copy(alpha = 0.20f) else Color(0xFFEDEBF1),
                        ) {
                            Text(
                                text = count.toString(),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (selected) Color.White else Color(0xFF78757E),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InboxLoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(color = AccentFlame)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Loading inbox...",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF8A8790),
        )
    }
}

@Composable
private fun InboxEmptyState(
    isArchived: Boolean,
    title: String,
    message: String,
    showAllVisible: Boolean,
    onShowAll: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = if (isArchived) Icons.Filled.Archive else Icons.Filled.Inbox,
            contentDescription = null,
            tint = Color(0xFFC2BEC8),
            modifier = Modifier.size(54.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF8B8891),
        )
        if (showAllVisible) {
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(onClick = onShowAll) {
                Text("Show All Items", color = AccentFlame)
            }
        }
    }
}

@Composable
private fun InboxList(
    sections: List<InboxDateSection>,
    onItemTapped: (InboxItem) -> Unit,
    onConvertToEntry: (String) -> Unit,
    onArchive: (String) -> Unit,
    onUnarchive: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        sections.forEach { section ->
            item(key = "header-${section.title}") {
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF7E7C82),
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                )
            }

            items(section.items, key = { it.id }) { item ->
                InboxItemRow(
                    item = item,
                    onClick = { onItemTapped(item) },
                    onConvertToEntry = { onConvertToEntry(item.id) },
                    onArchive = { onArchive(item.id) },
                    onUnarchive = { onUnarchive(item.id) },
                    onDelete = { onDelete(item.id) },
                )
            }
        }
    }
}

@Composable
private fun InboxItemRow(
    item: InboxItem,
    onClick: () -> Unit,
    onConvertToEntry: () -> Unit,
    onArchive: () -> Unit,
    onUnarchive: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TypeIcon(type = item.type)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.type.label,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = item.formattedTime(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF8A8790),
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.displayPreview(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF7D7A83),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    StatusBadge(item = item)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!item.isProcessed && !item.isArchived) {
                    OutlinedButton(
                        onClick = onConvertToEntry,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Book,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add to Journal")
                    }
                }
                OutlinedButton(
                    onClick = if (item.isArchived) onUnarchive else onArchive,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Archive,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (item.isArchived) "Unarchive" else "Archive")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete item",
                        tint = Color(0xFFE35555),
                    )
                }
            }
        }
    }
}

@Composable
private fun TypeIcon(type: InboxItemType) {
    val icon = when (type) {
        InboxItemType.VOICE_NOTE -> Icons.Filled.Mic
        InboxItemType.PHOTO -> Icons.Filled.PhotoCamera
        InboxItemType.SCAN -> Icons.Filled.Description
        InboxItemType.FILE -> Icons.Filled.InsertDriveFile
    }
    val tint = when (type) {
        InboxItemType.VOICE_NOTE -> Color(0xFF9A56CF)
        InboxItemType.PHOTO -> Color(0xFF3B79E5)
        InboxItemType.SCAN -> Color(0xFFE6962E)
        InboxItemType.FILE -> Color(0xFF8C8992)
    }
    Surface(
        color = tint.copy(alpha = 0.12f),
        shape = CircleShape,
        modifier = Modifier.size(44.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun StatusBadge(item: InboxItem) {
    val key = item.statusIndicatorKey()
    val color = when (key) {
        "processed" -> Color(0xFF5FBF6C)
        "ready" -> AccentFlame
        else -> Color(0xFF9B98A1)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Surface(
            color = color,
            shape = CircleShape,
            modifier = Modifier.size(7.dp),
        ) {}
        Text(
            text = item.statusLabel(),
            style = MaterialTheme.typography.labelSmall,
            color = color,
        )
    }
}

@Composable
private fun QuickCaptureButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(100.dp),
        color = AccentFlame,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = Color.White,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Quick Capture",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}

@Composable
private fun RecordingIndicator(
    duration: String,
    onStopClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = Color(0xFFD84A4A),
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(10.dp),
            ) {}
            Text(
                text = duration,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                ),
            )
            IconButton(onClick = onStopClick) {
                Icon(
                    imageVector = Icons.Filled.Stop,
                    contentDescription = "Stop recording",
                    tint = Color.White,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickCaptureSheet(
    onDismiss: () -> Unit,
    onVoiceCapture: () -> Unit,
    onPhotoCapture: () -> Unit,
    onScanCapture: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CanvasBackground,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Quick Capture",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            )
            CaptureOptionRow(
                icon = Icons.Filled.Mic,
                label = "Voice Note",
                onClick = onVoiceCapture,
            )
            CaptureOptionRow(
                icon = Icons.Filled.PhotoCamera,
                label = "Take Photo",
                onClick = onPhotoCapture,
            )
            CaptureOptionRow(
                icon = Icons.Filled.Description,
                label = "Scan Document",
                onClick = onScanCapture,
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun CaptureOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun InboxItemDetailDialog(
    item: InboxItem,
    onDismiss: () -> Unit,
    onConvertToEntry: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            color = CanvasBackground,
            shape = RoundedCornerShape(24.dp),
        ) {
            var showMenu by remember { mutableStateOf(false) }
            val playbackProgress = 0.42f

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.weight(1f))
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "More actions",
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Archive") },
                                leadingIcon = { Icon(Icons.Filled.Archive, null) },
                                onClick = {
                                    showMenu = false
                                    onArchive()
                                    onDismiss()
                                },
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                leadingIcon = { Icon(Icons.Filled.Delete, null) },
                                onClick = {
                                    showMenu = false
                                    onDelete()
                                    onDismiss()
                                },
                            )
                        }
                    }
                }

                Surface(shape = RoundedCornerShape(16.dp), color = Color.White) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TypeIcon(type = item.type)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = item.type.label,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                )
                                Text(
                                    text = item.formattedFullDateTime(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF8A8790),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = item.displayPreview(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF45424B),
                        )
                    }
                }

                if (item.type == InboxItemType.VOICE_NOTE) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(shape = RoundedCornerShape(16.dp), color = Color.White) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                repeat(24) { idx ->
                                    val barHeight = (if (idx % 2 == 0) 18 else 32).dp
                                    Surface(
                                        color = Color(0x669A56CF),
                                        shape = RoundedCornerShape(2.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(barHeight),
                                    ) {}
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = Color(0xFFECEAF1),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(playbackProgress)
                                        .height(6.dp)
                                        .background(Color(0xFF9A56CF)),
                                )
                            }
                        }
                    }
                }

                if (!item.transcription.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(shape = RoundedCornerShape(16.dp), color = Color.White) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Transcription",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.transcription,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF55515C),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                if (!item.isProcessed) {
                    Button(
                        onClick = {
                            onConvertToEntry()
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentFlame),
                    ) {
                        Icon(Icons.Filled.Book, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add to Journal")
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x145FBF6C),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = Color(0xFF5FBF6C))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Already processed to journal",
                                color = Color(0xFF5FBF6C),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                }
            }
        }
    }
}
