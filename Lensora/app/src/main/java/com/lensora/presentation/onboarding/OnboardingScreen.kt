package com.lensora.presentation.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lensora.core.ui.theme.*
import com.lensora.domain.usecase.onboarding.CompleteOnboardingUseCase
import com.lensora.domain.usecase.onboarding.IsOnboardingCompletedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingPage(val emoji: String, val title: String, val description: String)

val onboardingPages = listOf(
    OnboardingPage("🎬", "Cinematic by Default", "Every shot looks like it was taken with a professional DSLR. No settings. No knowledge needed."),
    OnboardingPage("🤖", "AI That Thinks for You", "Lensora detects your scene, adjusts lighting, color, and mood automatically in real time."),
    OnboardingPage("🧍", "Perfect Pose Every Time", "Live pose guidance helps you frame the perfect shot whether you're traveling or taking portraits."),
    OnboardingPage("✨", "Effortless. Beautiful. Yours.", "Open camera. Point. Shoot. Get stunning cinematic photos without editing.")
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
    private val isOnboardingCompletedUseCase: IsOnboardingCompletedUseCase
) : ViewModel() {
    private val _isCompleted = MutableStateFlow(false)
    val isCompleted = _isCompleted.asStateFlow()

    init { viewModelScope.launch { _isCompleted.value = isOnboardingCompletedUseCase() } }

    fun completeOnboarding() { viewModelScope.launch { completeOnboardingUseCase() } }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val isLastPage = pagerState.currentPage == onboardingPages.size - 1

    Box(modifier = Modifier.fillMaxSize().background(Black)) {
        Box(modifier = Modifier.fillMaxSize().background(Brush.radialGradient(colors = listOf(ElectricBlueDim.copy(alpha = 0.12f), Color.Transparent), radius = 900f)))

        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(60.dp))
            Text("Lensora AI", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
            Spacer(modifier = Modifier.height(40.dp))

            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().weight(1f)) { page ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(modifier = Modifier.size(120.dp).background(SurfaceGray, RoundedCornerShape(32.dp)), contentAlignment = Alignment.Center) {
                        Text(text = onboardingPages[page].emoji, fontSize = 52.sp)
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(onboardingPages[page].title, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = White, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(onboardingPages[page].description, fontSize = 15.sp, color = WhiteDim, textAlign = TextAlign.Center, lineHeight = 22.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                repeat(onboardingPages.size) { index ->
                    Box(modifier = Modifier.size(if (pagerState.currentPage == index) 24.dp else 8.dp, 8.dp).background(color = if (pagerState.currentPage == index) ElectricBlue else SurfaceGray, shape = RoundedCornerShape(4.dp)))
                }
            }
            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { viewModel.completeOnboarding(); onFinish() },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
            ) {
                Text(if (isLastPage) "Get Started" else "Next", color = Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(visible = !isLastPage, enter = fadeIn(tween(300)), exit = fadeOut(tween(300))) {
                TextButton(onClick = { viewModel.completeOnboarding(); onFinish() }) {
                    Text("Skip", color = WhiteDim, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
