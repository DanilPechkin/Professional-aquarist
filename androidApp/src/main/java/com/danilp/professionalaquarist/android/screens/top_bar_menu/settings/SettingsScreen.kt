package com.danilp.professionalaquarist.android.screens.top_bar_menu.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.danilp.professionalaquarist.android.R
import com.danilp.professionalaquarist.android.screens.AquariumTopBar
import com.danilp.professionalaquarist.android.screens.OutlinedDropDownMenuField
import com.danilp.professionalaquarist.android.screens.destinations.SettingsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph
@Destination
@Composable
fun SettingsScreen(
    navigator: DestinationsNavigator,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val state = viewModel.state

    var isTopMenuExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(key1 = context) {
        viewModel.savingEvents.collect { event ->
            when (event) {
                is SettingsViewModel.SavingEvent.Success -> {
                    Toast.makeText(
                        context,
                        context.getText(R.string.save_settings_toast),
                        Toast.LENGTH_SHORT
                    ).show()

                    navigator.navigateUp()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AquariumTopBar(
                title = stringResource(R.string.settings_title),
                switchMenuVisibility = { isTopMenuExpanded = !isTopMenuExpanded },
                isMenuExpanded = isTopMenuExpanded,
                hideMenu = { isTopMenuExpanded = false },
                navigateBack = { navigator.navigateUp() },
                navigateToSettings = { navigator.navigate(SettingsScreenDestination) },
                navigateToAccount = { }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {

            OutlinedDropDownMenuField(
                label = stringResource(R.string.capacity_label),
                items = state.capacityList,
                selectedItem = state.capacityList[state.capacityMeasureCode],
                changeSelectedItem = {
                    viewModel.onEvent(
                        SettingsEvent.CapacityChanged(
                            state.capacityList.indexOf(it)
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedDropDownMenuField(
                label = stringResource(R.string.alkalinity_label),
                items = state.alkalinityList,
                selectedItem = state.alkalinityList[state.alkalinityMeasureCode],
                changeSelectedItem = {
                    viewModel.onEvent(
                        SettingsEvent.AlkalinityChanged(
                            state.alkalinityList.indexOf(it)
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedDropDownMenuField(
                label = stringResource(R.string.metric_label),
                items = state.metricList,
                selectedItem = state.metricList[state.metricMeasureCode],
                changeSelectedItem = {
                    viewModel.onEvent(
                        SettingsEvent.MetricChanged(
                            state.metricList.indexOf(it)
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedDropDownMenuField(
                label = stringResource(R.string.temperature_label),
                items = state.temperatureList,
                selectedItem = state.temperatureList[state.temperatureMeasureCode],
                changeSelectedItem = {
                    viewModel.onEvent(
                        SettingsEvent.TemperatureChanged(
                            state.temperatureList.indexOf(it)
                        )
                    )
                }
            )

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        viewModel.onEvent(SettingsEvent.DefaultButtonPressed)
                    }
                ) {
                    Text(text = stringResource(R.string.set_default_button))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        viewModel.onEvent(SettingsEvent.SaveButtonPressed)
                    }
                ) {
                    Text(text = stringResource(R.string.save_button))
                }
            }
        }
    }
}