package com.example.pa19checklist

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pa19checklist.ui.screen.ChecklistScreen
import com.example.pa19checklist.ui.theme.PA19ChecklistTheme
import com.example.pa19checklist.viewmodel.ChecklistViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: ChecklistViewModel = viewModel(
                factory = ChecklistViewModel.Factory(application)
            )
            val uiState by viewModel.uiState

            KeepScreenOnEffect(keepScreenOn = true)

            PA19ChecklistTheme {
                ChecklistScreen(
                    uiState = uiState,
                    onValidate = viewModel::validateCurrentItem,
                    onValidateShutdown = viewModel::validateShutdownItem,
                    onReset = viewModel::resetSession,
                    onFinish = viewModel::finishChecklist,
                    onStart = viewModel::dismissStartMemo,
                    onShowShutdownChecklist = viewModel::showShutdownChecklist
                )
            }
        }
    }

    @Composable
    private fun KeepScreenOnEffect(keepScreenOn: Boolean) {
        DisposableEffect(keepScreenOn) {
            if (keepScreenOn) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }

            onDispose {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }
}
