/*
 * Copyright 2023 WhiteScent
 *
 * This file is a part of Mastify.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * Mastify is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Mastify; if not,
 * see <http://www.gnu.org/licenses>.
 */

package com.github.whitescent.mastify.screen.explore

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.github.whitescent.mastify.data.model.ui.StatusUiData
import com.github.whitescent.mastify.mapper.status.getReplyChainType
import com.github.whitescent.mastify.mapper.status.hasUnloadedParent
import com.github.whitescent.mastify.network.model.account.Account
import com.github.whitescent.mastify.network.model.status.Status
import com.github.whitescent.mastify.ui.component.AppHorizontalDivider
import com.github.whitescent.mastify.ui.component.StatusEndIndicator
import com.github.whitescent.mastify.ui.component.status.StatusListItem
import com.github.whitescent.mastify.ui.component.status.paging.EmptyStatusListPlaceholder
import com.github.whitescent.mastify.ui.component.status.paging.PageType
import com.github.whitescent.mastify.ui.component.status.paging.StatusListLoadError
import com.github.whitescent.mastify.ui.component.status.paging.StatusListLoading
import com.github.whitescent.mastify.utils.StatusAction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TrendingStatusList(
  statusList: LazyPagingItems<StatusUiData>,
  statusListState: LazyListState,
  action: (StatusAction) -> Unit,
  navigateToDetail: (Status) -> Unit,
  navigateToProfile: (Account) -> Unit,
  navigateToMedia: (ImmutableList<Status.Attachment>, Int) -> Unit,
) {
  var refreshing by remember { mutableStateOf(false) }
  val scope = rememberCoroutineScope()
  val pullRefreshState = rememberPullRefreshState(
    refreshing = refreshing,
    onRefresh = {
      scope.launch {
        refreshing = true
        delay(500)
        statusList.refresh()
        refreshing = false
      }
    }
  )
  Box(
    modifier = Modifier
      .fillMaxSize()
      .pullRefresh(pullRefreshState)
  ) {
    when (statusList.itemCount) {
      0 -> {
        when (statusList.loadState.refresh) {
          is LoadState.Error -> StatusListLoadError { statusList.refresh() }
          is LoadState.NotLoading ->
            EmptyStatusListPlaceholder(PageType.Profile, alignment = Alignment.TopCenter)

          else -> StatusListLoading()
        }
      }
      else -> {
        LazyColumn(
          state = statusListState,
          modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 56.dp),
        ) {
          items(
            count = statusList.itemCount,
            contentType = statusList.itemContentType(),
            key = statusList.itemKey(),
          ) { index ->
            val status = statusList[index]
            val replyChainType by remember(status, statusList.itemCount, index) {
              mutableStateOf(statusList.getReplyChainType(index))
            }
            val hasUnloadedParent by remember(status, statusList.itemCount, index) {
              mutableStateOf(statusList.hasUnloadedParent(index))
            }
            statusList[index]?.let {
              StatusListItem(
                status = it,
                action = action,
                replyChainType = replyChainType,
                hasUnloadedParent = hasUnloadedParent,
                navigateToDetail = { navigateToDetail(it.actionable) },
                navigateToProfile = navigateToProfile,
                navigateToMedia = navigateToMedia
              )
            }
            AppHorizontalDivider()
          }
          item {
            StatusEndIndicator(Modifier.padding(54.dp))
          }
        }
        PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
      }
    }
  }
}