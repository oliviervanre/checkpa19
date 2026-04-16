package com.example.pa19checklist.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pa19checklist.data.model.Item
import com.example.pa19checklist.viewmodel.ChecklistUiState

@Composable
fun ChecklistScreen(
    uiState: ChecklistUiState,
    onValidate: () -> Unit,
    onPauseToggle: () -> Unit,
    onReset: () -> Unit,
    onFinish: () -> Unit,
    onStart: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chargement checklist...",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            return@Surface
        }

        if (uiState.errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onReset) {
                        Text(text = "REINITIALISER")
                    }
                }
            }
            return@Surface
        }

        if (uiState.showStartMemo) {
            StartMemoScreen(onStart = onStart)
            return@Surface
        }

        if (uiState.isCompleted) {
            CompletionSpeedsScreen(
                onFinish = onFinish
            )
            return@Surface
        }

        val phase = uiState.currentPhase ?: return@Surface
        val listState = rememberLazyListState()
        val currentItemIndex = uiState.currentItemIndex
        val scrollTarget = when {
            phase.items.isEmpty() -> 0
            currentItemIndex >= phase.items.size -> phase.items.lastIndex
            currentItemIndex < 0 -> 0
            else -> currentItemIndex
        }

        LaunchedEffect(uiState.sessionState.currentPhaseIndex, uiState.sessionState.currentItemIndex) {
            if (phase.items.isNotEmpty()) {
                listState.animateScrollToItem(index = scrollTarget)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Header(
                phasePosition = "${uiState.phaseNumber} / ${uiState.phaseCount}",
                phaseName = phase.name
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                itemsIndexed(phase.items) { index, item ->
                    val itemState = when {
                        currentItemIndex > phase.items.lastIndex -> ItemVisualState.Done
                        index < currentItemIndex -> ItemVisualState.Done
                        index == currentItemIndex -> ItemVisualState.Current
                        else -> ItemVisualState.Next
                    }

                    ItemRow(
                        index = index + 1,
                        item = item,
                        state = itemState
                    )
                }
            }

            ActionBar(
                isPaused = uiState.isPaused,
                canValidate = true,
                onValidate = onValidate,
                onPauseToggle = onPauseToggle
            )
        }
    }
}

@Composable
private fun Header(phasePosition: String, phaseName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Text(
            text = phasePosition,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = phaseName,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 30.sp,
            lineHeight = 34.sp
        )
    }
}

@Composable
fun ItemRow(index: Int, item: Item, state: ItemVisualState) {
    val backgroundColor = when (state) {
        ItemVisualState.Done -> MaterialTheme.colorScheme.surfaceVariant
        ItemVisualState.Current -> MaterialTheme.colorScheme.primary
        ItemVisualState.Next -> MaterialTheme.colorScheme.surface
    }
    val textColor = when (state) {
        ItemVisualState.Done -> MaterialTheme.colorScheme.onSurfaceVariant
        ItemVisualState.Current -> MaterialTheme.colorScheme.onPrimary
        ItemVisualState.Next -> MaterialTheme.colorScheme.onSurface
    }
    val titleSize = when (state) {
        ItemVisualState.Done -> 20.sp
        ItemVisualState.Current -> 28.sp
        ItemVisualState.Next -> 22.sp
    }
    val lineHeight = when (state) {
        ItemVisualState.Done -> 24.sp
        ItemVisualState.Current -> 32.sp
        ItemVisualState.Next -> 26.sp
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 18.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = if (state == ItemVisualState.Current) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.18f)
                    } else {
                        Color.Transparent
                    },
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = index.toString(),
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = item.label,
            color = textColor,
            fontSize = titleSize,
            lineHeight = lineHeight,
            fontWeight = when (state) {
                ItemVisualState.Current -> FontWeight.ExtraBold
                ItemVisualState.Done -> FontWeight.Medium
                ItemVisualState.Next -> FontWeight.SemiBold
            }
        )
    }
}

@Composable
fun ActionBar(
    isPaused: Boolean,
    canValidate: Boolean,
    onValidate: () -> Unit,
    onPauseToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onValidate,
            modifier = Modifier
                .weight(1f)
                .height(72.dp),
            enabled = canValidate,
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = "VALIDER",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onPauseToggle,
            modifier = Modifier
                .weight(1f)
                .height(72.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPaused) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                contentColor = if (isPaused) {
                    MaterialTheme.colorScheme.onSecondary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = if (isPaused) "REPRENDRE" else "PAUSE",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

enum class ItemVisualState {
    Done,
    Current,
    Next
}

@Composable
private fun CompletionMemoScreen(onFinish: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            MemoTitle(title = "MEMO PA19")
            Spacer(modifier = Modifier.height(16.dp))
            FullMemoContent()
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = "TERMINER",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StartMemoScreen(onStart: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            MemoTitle(title = "MEMO PA19")
            Spacer(modifier = Modifier.height(16.dp))
            FullMemoContent()
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = "COMMENCER",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CompletionSpeedsScreen(onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            MemoTitle(title = "VITESSES PA19")
            Spacer(modifier = Modifier.height(16.dp))
            MemoSection(
                title = "Vitesses caracteristiques",
                lines = listOf(
                    "VNE : 138 MPH",
                    "VNO : 110 MPH",
                    "Va : 94 MPH",
                    "Vz max : 71 MPH",
                    "Pente max : 63,5 MPH",
                    "V plane optima : 70 MPH"
                )
            )
            MemoTable()
            MemoBanner(text = "Limitation vent de travers : 15 kt")
            MemoSection(
                title = "References",
                lines = listOf(
                    "rotation : 60 MPH",
                    "montee normale : 71 MPH",
                    "Base : 1500-1700 tr/min    70 MPH",
                    "(Rechauffe carbu tiree)",
                    "Finale : 60 MPH (+Kve)"
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(
                text = "TERMINER",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MemoTitle(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun FullMemoContent() {
    MemoSection(
        title = "Masse vide",
        lines = listOf(
            "voir fiche pesee",
            "M max : 681 kg",
            "Bagages : 23 kg (en soute)"
        )
    )
    MemoSection(
        title = "Carburant",
        lines = listOf(
            "100LL, capacite totale = 136 l",
            "2 reservoirs d'ailes non communicants = 2x68 l",
            "Consommation : 19 l/h"
        )
    )
    MemoSection(
        title = "Huile",
        lines = listOf(
            "SAE40 ; aero D80 ; max = 4,8 l ; min = 1,8 l"
        )
    )
    MemoSection(
        title = "Vitesses caracteristiques",
        lines = listOf(
            "VNE : 138 MPH",
            "VNO : 110 MPH",
            "Va : 94 MPH",
            "Vz max : 71 MPH",
            "Pente max : 63,5 MPH",
            "V plane optima : 70 MPH"
        )
    )
    MemoTable()
    MemoBanner(text = "Limitation vent de travers : 15 kt")
    MemoSection(
        title = "References",
        lines = listOf(
            "rotation : 60 MPH",
            "montee normale : 71 MPH",
            "croisiere : 2200 / 2300 tr/min",
            "Vent arriere : 2200 tr/min",
            "Base : 1500-1700 tr/min    70 MPH",
            "(Rechauffe carbu tiree)",
            "Finale : 60 MPH (+Kve)"
        )
    )
}

@Composable
private fun MemoSection(title: String, lines: List<String>) {
    Column(
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        lines.forEach { line ->
            Text(
                text = line,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 17.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun MemoBanner(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(
                color = Color(0xFFD62828),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(vertical = 10.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MemoTable() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
    ) {
        MemoTableRow(
            values = listOf("Vitesses", "Vs", "1,2 Vs", "1,3 Vs", "1,45 Vs"),
            backgroundColor = Color(0xFFD62828),
            textColor = Color.White,
            fontWeight = FontWeight.Bold
        )
        MemoTableRow(
            values = listOf("MPH", "44", "53", "57", "63"),
            backgroundColor = Color(0xFFF3F0C2),
            textColor = MaterialTheme.colorScheme.onSurface
        )
        MemoTableRow(
            values = listOf("inclinaisons", "", "max 15°", "max 20°", "max 37°"),
            backgroundColor = Color(0xFFE7E2A8),
            textColor = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun MemoTableRow(
    values: List<String>,
    backgroundColor: Color,
    textColor: Color,
    fontWeight: FontWeight = FontWeight.Medium
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 8.dp, horizontal = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        values.forEach { value ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value,
                    color = textColor,
                    fontSize = 13.sp,
                    fontWeight = fontWeight,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
