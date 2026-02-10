package com.sekyiemmanuel.mina.feature.gallery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sekyiemmanuel.mina.core.ui.theme.AccentFlame
import com.sekyiemmanuel.mina.core.ui.theme.CanvasBackground
import com.sekyiemmanuel.mina.feature.gallery.domain.GalleryArtwork
import com.sekyiemmanuel.mina.feature.gallery.domain.GalleryGenerationStatus
import com.sekyiemmanuel.mina.feature.gallery.domain.GalleryMood
import java.util.Locale

@Composable
fun GalleryRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GalleryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onEvent(GalleryUiEvent.OnAppear)
    }

    GalleryScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    uiState: GalleryUiState,
    onEvent: (GalleryUiEvent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.isShowingFilters) {
        GalleryFilterSheet(
            timeFilter = uiState.timeFilter,
            moodFilter = uiState.moodFilter,
            onDismiss = { onEvent(GalleryUiEvent.DismissFiltersSheet) },
            onTimeFilterSelected = { onEvent(GalleryUiEvent.TimeFilterChanged(it)) },
            onMoodSelected = { onEvent(GalleryUiEvent.MoodFilterChanged(it)) },
            onClearFilters = { onEvent(GalleryUiEvent.ClearFilters) },
        )
    }

    uiState.selectedArtwork?.let { artwork ->
        ArtworkDetailDialog(
            artwork = artwork,
            onDismiss = { onEvent(GalleryUiEvent.DismissArtworkDetail) },
            onShare = { onEvent(GalleryUiEvent.ShareSelectedArtwork) },
            onSave = { onEvent(GalleryUiEvent.SaveSelectedArtwork) },
            onRegenerate = { onEvent(GalleryUiEvent.RegenerateSelectedArtwork) },
            onDelete = { onEvent(GalleryUiEvent.DeleteArtwork(artwork.id)) },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        GalleryTopBar(
            hasActiveFilters = uiState.hasActiveFilters,
            onBackClick = onBackClick,
            onFilterClick = { onEvent(GalleryUiEvent.ToggleFiltersSheet) },
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { onEvent(GalleryUiEvent.SearchQueryChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            placeholder = { Text("Search entries...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = Color(0xFF8F8D94),
                )
            },
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(modifier = Modifier.weight(1f)) {
            when {
                uiState.isLoading && uiState.artworks.isEmpty() -> GalleryLoadingState()
                uiState.filteredArtworks.isEmpty() -> {
                    GalleryEmptyState(
                        title = uiState.emptyStateTitle,
                        message = uiState.emptyStateMessage,
                        canClearFilters = uiState.hasActiveFilters,
                        onClearFilters = { onEvent(GalleryUiEvent.ClearFilters) },
                    )
                }
                else -> {
                    GalleryGridContent(
                        uiState = uiState,
                        onArtworkClick = { onEvent(GalleryUiEvent.ArtworkTapped(it)) },
                        onRemoveTimeFilter = { onEvent(GalleryUiEvent.TimeFilterChanged(GalleryTimeFilter.ALL)) },
                        onRemoveMoodFilter = { onEvent(GalleryUiEvent.MoodFilterChanged(null)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun GalleryTopBar(
    hasActiveFilters: Boolean,
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            onClick = onBackClick,
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 0.dp,
            modifier = Modifier.size(42.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "Gallery",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.weight(1f),
        )

        Surface(
            onClick = onFilterClick,
            shape = CircleShape,
            color = if (hasActiveFilters) AccentFlame.copy(alpha = 0.14f) else Color.White,
            shadowElevation = 0.dp,
            modifier = Modifier.size(42.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (hasActiveFilters) Icons.Filled.FilterAltOff else Icons.Filled.FilterAlt,
                    contentDescription = "Filters",
                    tint = if (hasActiveFilters) AccentFlame else MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun GalleryLoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(
            color = AccentFlame,
            strokeWidth = 3.dp,
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Loading gallery...",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF88858F),
        )
    }
}

@Composable
private fun GalleryEmptyState(
    title: String,
    message: String,
    canClearFilters: Boolean,
    onClearFilters: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.AutoAwesome,
            contentDescription = null,
            tint = Color(0xFFCAC6CF),
            modifier = Modifier.size(56.dp),
        )
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF87858E),
            textAlign = TextAlign.Center,
        )
        if (canClearFilters) {
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(onClick = onClearFilters) {
                Text(
                    text = "Clear Filters",
                    color = AccentFlame,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }
}

@Composable
private fun GalleryGridContent(
    uiState: GalleryUiState,
    onArtworkClick: (GalleryArtwork) -> Unit,
    onRemoveTimeFilter: () -> Unit,
    onRemoveMoodFilter: () -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 28.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            GalleryHeader(uiState = uiState)
        }

        if (uiState.timeFilter != GalleryTimeFilter.ALL || uiState.moodFilter != null) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ActiveFiltersRow(
                    timeFilter = uiState.timeFilter,
                    moodFilter = uiState.moodFilter,
                    onRemoveTimeFilter = onRemoveTimeFilter,
                    onRemoveMoodFilter = onRemoveMoodFilter,
                )
            }
        }

        items(uiState.filteredArtworks, key = { it.id }) { artwork ->
            GalleryCard(
                artwork = artwork,
                onClick = { onArtworkClick(artwork) },
            )
        }
    }
}

@Composable
private fun GalleryHeader(uiState: GalleryUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "${uiState.filteredArtworks.size} Artworks",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            if (uiState.filteredArtworks.size != uiState.artworkCount) {
                Text(
                    text = "of ${uiState.artworkCount} total",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8F8D94),
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            uiState.artStyles.take(3).forEach { style ->
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color.White,
                    shadowElevation = 0.dp,
                ) {
                    Text(
                        text = style.replaceFirstChar { it.titlecase(Locale.US) },
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF7C7983),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveFiltersRow(
    timeFilter: GalleryTimeFilter,
    moodFilter: GalleryMood?,
    onRemoveTimeFilter: () -> Unit,
    onRemoveMoodFilter: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (timeFilter != GalleryTimeFilter.ALL) {
            ActiveFilterChip(
                label = timeFilter.label,
                onRemove = onRemoveTimeFilter,
            )
        }
        moodFilter?.let {
            ActiveFilterChip(
                label = "${it.emoji} ${it.label}",
                onRemove = onRemoveMoodFilter,
            )
        }
    }
}

@Composable
private fun ActiveFilterChip(
    label: String,
    onRemove: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = AccentFlame.copy(alpha = 0.12f),
        modifier = Modifier.clickable(onClick = onRemove),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = AccentFlame,
            )
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                tint = AccentFlame,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Composable
private fun GalleryCard(
    artwork: GalleryArtwork,
    onClick: () -> Unit,
) {
    val imageHeight = (148f + artwork.aspectRatio * 52f).dp.coerceIn(150.dp, 228.dp)

    Column(
        modifier = Modifier.clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .clip(RoundedCornerShape(14.dp))
                .background(artwork.placeholderColor()),
        ) {
            GalleryArtworkPattern(
                style = artwork.artStyle,
                modifier = Modifier.fillMaxSize(),
            )

            if (artwork.status != GalleryGenerationStatus.COMPLETED) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.34f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = when (artwork.status) {
                                GalleryGenerationStatus.GENERATING -> Icons.Filled.AutoAwesome
                                GalleryGenerationStatus.PENDING -> Icons.Filled.Schedule
                                GalleryGenerationStatus.FAILED -> Icons.Filled.ErrorOutline
                                GalleryGenerationStatus.COMPLETED -> Icons.Filled.AutoAwesome
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp),
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = artwork.status.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = artwork.entryTitle,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            artwork.mood?.let {
                Text(
                    text = it.emoji,
                    style = MaterialTheme.typography.labelSmall,
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = artwork.formattedDateLabel(),
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF8B8891),
            )
        }
    }
}

@Composable
private fun GalleryArtworkPattern(
    style: String,
    modifier: Modifier = Modifier,
) {
    when (style.lowercase(Locale.US)) {
        "watercolor" -> WatercolorPattern(modifier = modifier)
        "abstract" -> AbstractPattern(modifier = modifier)
        "minimalist" -> MinimalistPattern(modifier = modifier)
        "impressionist" -> ImpressionistPattern(modifier = modifier)
        else -> DreamyPattern(modifier = modifier)
    }
}

@Composable
private fun DreamyPattern(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                listOf(Color(0x66A68DFF), Color(0x44F1B8D9), Color.Transparent),
            ),
        ),
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 12.dp)
                .background(
                    Brush.radialGradient(listOf(Color(0x55FFFFFF), Color.Transparent)),
                    CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .size(84.dp)
                .align(Alignment.BottomStart)
                .padding(start = 10.dp, bottom = 10.dp)
                .background(
                    Brush.radialGradient(listOf(Color(0x4472C7E7), Color.Transparent)),
                    CircleShape,
                ),
        )
    }
}

@Composable
private fun WatercolorPattern(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                listOf(Color(0x5574B2F7), Color(0x4449C6A1), Color.Transparent),
            ),
        ),
    ) {
        Box(
            modifier = Modifier
                .size(120.dp, 70.dp)
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(Color(0x5557A9E8)),
        )
        Box(
            modifier = Modifier
                .size(90.dp, 72.dp)
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 18.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(Color(0x4442C79F)),
        )
    }
}

@Composable
private fun AbstractPattern(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                listOf(Color(0x55F2A65A), Color(0x447A8DF0), Color.Transparent),
            ),
        ),
    ) {
        Box(
            modifier = Modifier
                .size(68.dp, 50.dp)
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 20.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x66F6B163)),
        )
        Box(
            modifier = Modifier
                .size(74.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .background(Color(0x667588EE), CircleShape),
        )
        Box(
            modifier = Modifier
                .size(46.dp, 32.dp)
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x66F8CB5E)),
        )
    }
}

@Composable
private fun MinimalistPattern(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                listOf(Color(0x16FFFFFF), Color.Transparent),
            ),
        ),
    ) {
        HorizontalDivider(
            color = Color(0x22000000),
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .align(Alignment.Center),
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(70.dp)
                .align(Alignment.CenterStart)
                .padding(start = 26.dp)
                .background(Color(0x22000000)),
        )
    }
}

@Composable
private fun ImpressionistPattern(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                listOf(Color(0x33F9A77D), Color(0x338AA6FF), Color.Transparent),
            ),
        ),
    ) {
        val colors = listOf(
            Color(0x55F4A261),
            Color(0x559D7EF3),
            Color(0x5557A0F5),
            Color(0x55FBCB6E),
            Color(0x5590CB97),
        )
        colors.forEachIndexed { index, color ->
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .align(
                        when (index % 5) {
                            0 -> Alignment.TopStart
                            1 -> Alignment.TopEnd
                            2 -> Alignment.Center
                            3 -> Alignment.BottomStart
                            else -> Alignment.BottomEnd
                        },
                    )
                    .padding(
                        start = (index * 4).dp,
                        top = (index * 3).dp,
                        end = (index * 2).dp,
                        bottom = (index * 2).dp,
                    )
                    .background(color = color, shape = CircleShape),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun GalleryFilterSheet(
    timeFilter: GalleryTimeFilter,
    moodFilter: GalleryMood?,
    onDismiss: () -> Unit,
    onTimeFilterSelected: (GalleryTimeFilter) -> Unit,
    onMoodSelected: (GalleryMood?) -> Unit,
    onClearFilters: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CanvasBackground,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Filter Gallery",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = onDismiss) {
                    Text("Done", color = AccentFlame)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Time Period",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Spacer(modifier = Modifier.height(10.dp))
            GalleryTimeFilter.entries.forEach { filter ->
                GalleryFilterOptionRow(
                    label = filter.label,
                    isSelected = filter == timeFilter,
                    onClick = { onTimeFilterSelected(filter) },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Mood",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                GalleryMood.entries.forEach { mood ->
                    val isSelected = mood == moodFilter
                    Surface(
                        onClick = {
                            onMoodSelected(if (isSelected) null else mood)
                        },
                        shape = RoundedCornerShape(100.dp),
                        color = if (isSelected) AccentFlame else Color.White,
                        shadowElevation = 0.dp,
                    ) {
                        Text(
                            text = "${mood.emoji} ${mood.label}",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = onClearFilters,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Clear")
                }
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentFlame),
                    modifier = Modifier.weight(1f),
                ) {
                    Text("Apply")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun GalleryFilterOptionRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) AccentFlame.copy(alpha = 0.12f) else Color.White,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = AccentFlame,
                )
            }
        }
    }
}

@Composable
private fun ArtworkDetailDialog(
    artwork: GalleryArtwork,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onSave: () -> Unit,
    onRegenerate: () -> Unit,
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close artwork",
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = onShare) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share artwork",
                        )
                    }
                    IconButton(onClick = onSave) {
                        Icon(
                            imageVector = Icons.Filled.Downloading,
                            contentDescription = "Save artwork",
                        )
                    }
                    IconButton(onClick = onRegenerate) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Regenerate artwork",
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(artwork.placeholderColor()),
                ) {
                    GalleryArtworkPattern(
                        style = artwork.artStyle,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Surface(
                        color = Color.Black.copy(alpha = 0.36f),
                        shape = RoundedCornerShape(100.dp),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp),
                            )
                            Text(
                                text = "AI Generated",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = artwork.entryTitle,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = artwork.formattedDateTime(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF8D8A93),
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            artwork.mood?.let { mood ->
                                MetaChip(label = "${mood.emoji} ${mood.label}")
                            }
                            MetaChip(
                                icon = Icons.Filled.Style,
                                label = artwork.artStyle.replaceFirstChar { it.titlecase(Locale.US) },
                            )
                            MetaChip(label = artwork.timeOfDayLabel())
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onShare,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentFlame),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Share Artwork")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Save to Photos")
                }
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(
                    onClick = {
                        onDelete()
                        onDismiss()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = null,
                        tint = Color(0xFFE35555),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Delete Artwork",
                        color = Color(0xFFE35555),
                    )
                }
            }
        }
    }
}

@Composable
private fun MetaChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = Color(0xFFF6F5F8),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF76737D),
                    modifier = Modifier.size(14.dp),
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF46424D),
            )
        }
    }
}
