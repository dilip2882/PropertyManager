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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
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
) {
    var revealState by remember { mutableStateOf(RevealState.Hidden) }
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
        targetValue = if (revealState == RevealState.Revealed) maxOffset else 0f,
        animationSpec = swipeAnimation,
        label = "offset",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        // Action buttons
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(IntrinsicSize.Min)
                .shadow(8.dp, RoundedCornerShape(12.dp)),
            horizontalArrangement = Arrangement.End,
        ) {
            Box(
                modifier = Modifier
                    .width(actionButtonWidth)
                    .fillMaxHeight()
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
                    )
                    .clickable(onClick = onEditClick),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Edit",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .width(actionButtonWidth)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.error, RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                    .clickable(onClick = onDeleteClick),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Delete",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }

        // Main card content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX < maxOffset / 2) {
                                revealState = RevealState.Revealed
                            } else {
                                revealState = RevealState.Hidden
                            }
                        },
                        onDragCancel = {
                            offsetX = if (revealState == RevealState.Revealed) maxOffset else 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            val newOffset = offsetX + dragAmount
                            offsetX = newOffset.coerceIn(maxOffset, 0f)
                            change.consume()
                        },
                    )
                }
                .clickable {
                    if (revealState == RevealState.Revealed) {
                        revealState = RevealState.Hidden
                    }
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
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                        ),
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
