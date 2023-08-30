package com.bnyro.contacts.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bnyro.contacts.R
import com.bnyro.contacts.enums.ContactsSource
import com.bnyro.contacts.obj.NavBarItem
import com.bnyro.contacts.ui.components.ContactsPage
import com.bnyro.contacts.ui.models.ContactsModel
import com.bnyro.contacts.ui.models.SmsModel
import com.bnyro.contacts.ui.models.ThemeModel
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainAppContent(smsModel: SmsModel) {
    val themeModel: ThemeModel = viewModel()
    val contactsModel: ContactsModel = viewModel(factory = ContactsModel.Factory)

    val bottomBarHeight = 80.dp
    val bottomBarHeightPx = with(LocalDensity.current) { bottomBarHeight.roundToPx().toFloat() }
    val bottomBarOffsetHeightPx = remember { mutableStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val newOffset = bottomBarOffsetHeightPx.value + available.y
                bottomBarOffsetHeightPx.value = newOffset.coerceIn(-bottomBarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    val navItems = listOf(
        NavBarItem(
            stringResource(R.string.device),
            Icons.Default.Home
        ) {
            contactsModel.contactsSource = ContactsSource.DEVICE
        },
        NavBarItem(
            stringResource(R.string.local),
            Icons.Default.Storage
        ) {
            contactsModel.contactsSource = ContactsSource.LOCAL
        },
        NavBarItem(
            stringResource(R.string.messages),
            Icons.Default.Message
        )
    )

    var currentPage by remember {
        mutableIntStateOf(
            if (smsModel.initialAddressAndBody != null) {
                2
            } else {
                contactsModel.contactsSource.ordinal
            }
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = if (themeModel.collapsableBottomBar) {
                    Modifier
                        .height(bottomBarHeight)
                        .offset {
                            IntOffset(x = 0, y = -bottomBarOffsetHeightPx.value.roundToInt())
                        }
                } else {
                    Modifier
                },
                tonalElevation = 10.dp
            ) {
                navItems.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = (index == currentPage),
                        onClick = {
                            navItem.onClick.invoke()
                            currentPage = index
                        },
                        icon = {
                            Icon(navItem.icon, null)
                        },
                        label = {
                            Text(navItem.label)
                        }
                    )
                }
            }
        }
    ) { pV ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .let {
                    if (!themeModel.collapsableBottomBar) it.padding(pV) else it
                },
            color = MaterialTheme.colorScheme.background
        ) {
            Crossfade(targetState = currentPage, label = "crossfade pager") { index ->
                when (index) {
                    0, 1 -> ContactsPage(
                        nestedScrollConnection.takeIf { themeModel.collapsableBottomBar },
                        bottomBarOffsetHeight = with(LocalDensity.current) {
                            bottomBarHeight - bottomBarOffsetHeightPx.value.absoluteValue.toDp()
                        }.takeIf { themeModel.collapsableBottomBar } ?: 0.dp,
                        ContactsSource.values()[index]
                    )

                    2 -> SmsListScreen(smsModel, contactsModel)
                }
            }
        }
    }
}