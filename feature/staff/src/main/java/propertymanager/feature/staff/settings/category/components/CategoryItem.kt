package propertymanager.feature.staff.settings.category.components

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.propertymanager.domain.model.Category
import kotlin.math.roundToInt

enum class RevealState {
    Hidden,
    Revealed
}

@Composable
fun CategoryItem(
    category: Category,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null,
    onEditCategory: () -> Unit,
    onDeleteCategory: () -> Unit,
    onAddSubcategory: () -> Unit,
    onEditSubcategory: (String) -> Unit,
    onDeleteSubcategory: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var revealState by remember { mutableStateOf(RevealState.Hidden) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    val actionButtonWidth = 80.dp
    val maxOffset = with(LocalDensity.current) { -actionButtonWidth.times(2).toPx() }

    val swipeAnimation = remember {
        SpringSpec<Float>(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    }

    val animatedOffset by animateFloatAsState(
        targetValue = if (revealState == RevealState.Revealed) maxOffset else 0f,
        animationSpec = swipeAnimation,
        label = "offset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Action buttons
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(IntrinsicSize.Min)
                .shadow(8.dp, RoundedCornerShape(12.dp)),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .width(actionButtonWidth)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                    .clickable(onClick = onEditCategory),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Edit",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            Box(
                modifier = Modifier
                    .width(actionButtonWidth)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.error, RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                    .clickable(onClick = onDeleteCategory),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Delete",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
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
                        }
                    )
                }
                .clickable {
                    if (revealState == RevealState.Revealed) {
                        revealState = RevealState.Hidden
                    } else {
                        expanded = !expanded
                    }
                },
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp,
                pressedElevation = 4.dp
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                // Main Category Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = category.name, style = MaterialTheme.typography.titleMedium)
                    Row {
                        IconButton(onClick = onMoveUp!!) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up")
                        }
                        IconButton(onClick = onMoveDown!!) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down")
                        }
                    }
                }

                // Subcategories and Add Subcategory
                if (expanded) {
                    category.subcategories.forEach { subcategory ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 32.dp, top = 8.dp, end = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = subcategory, style = MaterialTheme.typography.bodyMedium)
                            Row {
                                IconButton(onClick = { onEditSubcategory(subcategory) }) {
                                    Icon(Icons.Outlined.Edit, contentDescription = "Edit Subcategory")
                                }
                                IconButton(onClick = { onDeleteSubcategory(subcategory) }) {
                                    Icon(Icons.Outlined.Delete, contentDescription = "Delete Subcategory", tint = Color.Red)
                                }
                            }
                        }
                    }

                    TextButton(
                        onClick = onAddSubcategory,
                        modifier = Modifier.padding(start = 32.dp, bottom = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Subcategory")
                        Text("Add Subcategory")
                    }
                }
            }
        }
    }
}
