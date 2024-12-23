package propertymanager.feature.tenant.home.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.propertymanager.domain.model.MaintenanceRequest
import com.propertymanager.domain.model.formatDate
import kotlin.math.roundToInt

enum class RevealState {
    Hidden,
    Revealed
}

@Composable
fun MaintenancePostCard(
    maintenanceRequest: MaintenanceRequest,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCardClick: () -> Unit,
    revealState: Boolean,
    onRevealStateChange: (Boolean) -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val actionButtonWidth = 80.dp
    val maxOffset = with(LocalDensity.current) { -actionButtonWidth.times(2).toPx() }

    val swipeAnimation = remember {
        SpringSpec<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        )
    }

    val animatedOffset by animateFloatAsState(
        targetValue = if (revealState) maxOffset else 0f,
        animationSpec = swipeAnimation,
        label = "offset",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
    ) {
        // Action buttons (Edit & Delete)
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.End,
        ) {

                ActionButton(
                    modifier = Modifier.width(actionButtonWidth),
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    icon = Icons.Default.Edit,
                    text = "Edit",
                    onClick = onEditClick
                )
                ActionButton(
                    modifier = Modifier.width(actionButtonWidth),
                    backgroundColor = MaterialTheme.colorScheme.error,
                    icon = Icons.Default.Delete,
                    text = "Delete",
                    onClick = onDeleteClick
                )

        }

        // Main card content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val isRevealed = offsetX < maxOffset / 2
                            onRevealStateChange(isRevealed)
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            offsetX = (offsetX + dragAmount).coerceIn(maxOffset, 0f)
                            change.consume()
                        },
                    )
                }
                .clickable(enabled = !revealState) { // Only allow clicking when not revealed
                    onCardClick()
                },
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp,
                pressedElevation = 4.dp,
            ),
            shape = RoundedCornerShape(12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = maintenanceRequest.issueDescription,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = maintenanceRequest.createdAt.toDate().formatDate(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(24.dp),
            )
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}
