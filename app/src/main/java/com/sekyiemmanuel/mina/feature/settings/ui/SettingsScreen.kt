package com.sekyiemmanuel.mina.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sekyiemmanuel.mina.R

@Composable
fun SettingsScreen(onBackClick: () -> Unit) {
    var automaticTimeZone by rememberSaveable { mutableStateOf(true) }
    var dailyReminders by rememberSaveable { mutableStateOf(false) }
    var locationEntries by rememberSaveable { mutableStateOf(false) }
    var cloudBackup by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(68.dp)
                    .height(8.dp)
                    .background(color = Color(0xFFC9C5C4), shape = RoundedCornerShape(8.dp)),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.settings),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                shape = CircleShape,
                color = Color.White,
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(56.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(id = R.string.close_settings),
                        tint = Color(0xFF88868E),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        SectionTitle(title = stringResource(id = R.string.settings_profile))
        SettingsCard {
            LabelValueRow(
                label = stringResource(id = R.string.settings_name_label),
                value = stringResource(id = R.string.settings_default_name),
            )
            CardDivider()
            LabelValueRow(
                label = stringResource(id = R.string.settings_email_label),
                value = stringResource(id = R.string.settings_default_email),
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        SectionTitle(title = stringResource(id = R.string.settings_goals_targets))
        SettingsCard {
            LeadingInfoRow(
                icon = Icons.Filled.Flag,
                iconTint = Color(0xFF3B79E5),
                title = stringResource(id = R.string.settings_journal_goal_value),
                subtitle = stringResource(id = R.string.settings_journal_goal_subtitle),
            )
            CardDivider()
            ActionRow(
                title = stringResource(id = R.string.settings_manage_journal_goals),
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        SectionTitle(title = stringResource(id = R.string.settings_preferences))
        SettingsCard {
            ToggleRow(
                icon = Icons.Filled.Public,
                iconTint = Color(0xFFA75BEA),
                title = stringResource(id = R.string.settings_automatic_time_zone),
                checked = automaticTimeZone,
                onCheckedChange = { automaticTimeZone = it },
            )
            CardDivider()
            DictationRow(
                title = stringResource(id = R.string.settings_dictation_language),
                value = stringResource(id = R.string.settings_auto_detect),
            )
            CardDivider()
            ToggleRow(
                icon = Icons.Filled.LocationOn,
                iconTint = Color(0xFF62B96A),
                title = stringResource(id = R.string.settings_use_location_entries),
                checked = locationEntries,
                onCheckedChange = { locationEntries = it },
            )
            CardDivider()
            ToggleRow(
                icon = Icons.Filled.Notifications,
                iconTint = Color(0xFF3E7BE8),
                title = stringResource(id = R.string.settings_daily_journal_reminders),
                checked = dailyReminders,
                onCheckedChange = { dailyReminders = it },
            )
            CardDivider()
            ActionRow(
                icon = Icons.Filled.Tune,
                iconTint = Color(0xFF69AEE5),
                title = stringResource(id = R.string.settings_customize_prompt_bar),
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        SectionTitle(title = stringResource(id = R.string.settings_sync))
        SettingsCard {
            ToggleRow(
                icon = Icons.Filled.Cloud,
                iconTint = Color(0xFF4B90E2),
                title = stringResource(id = R.string.settings_cloud_backup),
                subtitle = stringResource(id = R.string.settings_cloud_backup_subtitle),
                checked = cloudBackup,
                onCheckedChange = { cloudBackup = it },
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        SectionTitle(title = stringResource(id = R.string.settings_subscription))
        SubscriptionCard()

        Spacer(modifier = Modifier.height(14.dp))

        SingleActionCard(
            icon = Icons.Filled.Star,
            iconTint = Color(0xFF3E7BE8),
            title = stringResource(id = R.string.settings_give_feedback),
        )

        Spacer(modifier = Modifier.height(12.dp))

        SingleActionCard(
            icon = Icons.Filled.Favorite,
            iconTint = Color(0xFF9953D8),
            title = stringResource(id = R.string.settings_about_app),
        )

        Spacer(modifier = Modifier.height(18.dp))

        SettingsCard {
            UtilityRow(
                icon = Icons.Filled.Email,
                iconTint = Color(0xFF1B72E1),
                title = stringResource(id = R.string.settings_contact_support),
            )
            CardDivider()
            UtilityRow(
                icon = Icons.Filled.Delete,
                iconTint = Color(0xFFF29A1D),
                title = stringResource(id = R.string.settings_clear_local_cache),
            )
            CardDivider()
            UtilityRow(
                icon = Icons.Filled.Person,
                iconTint = Color(0xFFEE4A41),
                title = stringResource(id = R.string.settings_delete_account),
            )
            CardDivider()
            UtilityRow(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                iconTint = Color(0xFFEE4A41),
                title = stringResource(id = R.string.settings_sign_out),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.settings_version),
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF87848C),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(26.dp))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFF7E7C82),
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Medium),
        modifier = Modifier.padding(horizontal = 8.dp),
    )
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(content = content)
    }
}

@Composable
private fun LabelValueRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 86.dp)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = Color(0xFFAFADB1),
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.width(18.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun LeadingInfoRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 88.dp)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(30.dp),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF8E8C93),
            )
        }
    }
}

@Composable
private fun ActionRow(
    title: String,
    icon: ImageVector? = null,
    iconTint: Color = Color(0xFF3E7BE8),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 84.dp)
            .clickable(onClick = {})
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(28.dp),
            )
            Spacer(modifier = Modifier.width(14.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFBCBBC1),
            modifier = Modifier.size(30.dp),
        )
    }
}

@Composable
private fun ToggleRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    subtitle: String? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 88.dp)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(30.dp),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF8E8C93),
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF5CC066),
                uncheckedThumbColor = Color(0xFFE9E8EC),
                uncheckedTrackColor = Color(0xFFD6D6DA),
            ),
        )
    }
}

@Composable
private fun DictationRow(
    title: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 84.dp)
            .clickable(onClick = {})
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Mic,
            contentDescription = null,
            tint = Color(0xFF3E7BE8),
            modifier = Modifier.size(30.dp),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = Color(0xFFABABB0),
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            color = Color(0xFFABABB0),
            style = MaterialTheme.typography.headlineMedium,
        )
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFBCBBC1),
            modifier = Modifier.size(30.dp),
        )
    }
}

@Composable
private fun SubscriptionCard() {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = Color(0xFFB1B0B5),
                modifier = Modifier.size(28.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.settings_no_subscription_active),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                )
                Text(
                    text = stringResource(id = R.string.settings_upgrade_subtitle),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF8E8C93),
                )
            }
            Surface(
                onClick = {},
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFFF2BC37),
                modifier = Modifier.height(56.dp),
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 28.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(id = R.string.settings_upgrade),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun SingleActionCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 92.dp)
                .clickable(onClick = {})
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(30.dp),
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFBCBBC1),
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

@Composable
private fun UtilityRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 82.dp)
            .clickable(onClick = {})
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(30.dp),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = iconTint,
        )
    }
}

@Composable
private fun CardDivider() {
    HorizontalDivider(
        color = Color(0xFFE6E5E9),
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 18.dp),
    )
}
