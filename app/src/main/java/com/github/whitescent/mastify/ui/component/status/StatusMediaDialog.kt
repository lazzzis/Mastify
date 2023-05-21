package com.github.whitescent.mastify.ui.component.status

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.github.whitescent.mastify.network.model.response.account.MediaAttachments
import com.github.whitescent.mastify.ui.component.BarStyle
import com.github.whitescent.mastify.ui.component.CenterRow
import com.github.whitescent.mastify.ui.component.CircleShapeAsyncImage
import com.github.whitescent.mastify.ui.component.FullScreenDialog
import com.github.whitescent.mastify.ui.component.MyHtmlText
import com.github.whitescent.mastify.ui.component.WidthSpacer
import com.mxalbert.zoomable.Zoomable

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun StatusMediaDialog(
  avatar: String,
  content: String,
  media: List<MediaAttachments>,
  targetMediaIndex: Int,
  onDismissRequest: () -> Unit
) {
  val pagerState = rememberPagerState(initialPage = targetMediaIndex)
  FullScreenDialog(
    onDismissRequest = onDismissRequest,
    previousBarStyle = BarStyle(
      color = Color.Transparent,
      useDarkIcons = true
    )
  ) {
    Box {
      HorizontalPager(
        state = pagerState,
        pageCount = media.size,
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black)
      ) { page ->
        Zoomable(
          modifier = Modifier.fillMaxSize()
        ) {
          val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
              .data(media[page].url)
              .size(Size.ORIGINAL)
              .build()
          )
          if (painter.state is AsyncImagePainter.State.Success) {
            val size = painter.intrinsicSize
            Image(
              painter = painter,
              contentDescription = null,
              modifier = Modifier
                .aspectRatio(size.width / size.height)
                .fillMaxSize()
            )
          }
        }
      }
      CenterRow(
        modifier = Modifier
          .align(Alignment.BottomStart)
          .background(Color.Black.copy(0.7f))
          .fillMaxWidth()
          .padding(24.dp)
      ) {
        CircleShapeAsyncImage(
          model = avatar,
          modifier = Modifier.size(48.dp)
        )
        WidthSpacer(value = 6.dp)
        MyHtmlText(
          text = content,
          color = Color.White,
//          style = AppTheme.typography.titleMedium,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}