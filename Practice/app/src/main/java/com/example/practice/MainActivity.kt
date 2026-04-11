package com.example.practice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Label
import androidx.compose.material3.RadioButtonDefaults.colors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practice.ui.theme.PracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
          Show_Preview()
        }
    }
}

//@Composable
//fun Text_view() {
//    val state = remember { mutableStateOf("chesse code") }
//    TextField(
//        value = state.value,
//        onValueChange = {
//            state.value = it
//        },
//        label = { Text(text = "Enter Text") },
//
//        )
//}
//
//@Composable
//private fun Screen_view() {
//    Profile_view(R.drawable.profile, "Kanak Saini", "Android Developer")
//}
//
//@Composable
//fun Profile_view(imgid: Int, Name: String, Job: String) {
//    Row(
//        modifier = Modifier.padding(16.dp),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.Center
//    ) {
//        Image(
//            painter = painterResource(imgid),
//            contentDescription = "Profile Icon",
//            modifier = Modifier.size(100.dp)
//
//        )
//
//        Column(
//            modifier = Modifier.padding(16.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = Name,
//                fontSize = 28.sp,
//                fontWeight = FontWeight.Bold,
//            )
//
//
//            Text(
//                text = Job,
//                fontSize = 18.sp,
//                color = Gray,
//                modifier = Modifier.padding(top = 8.dp)
//            )
//        }
//    }
//
//}
//
//
//@Composable
//fun Many_Profile() {
//    Column {
//        Profile_view(R.drawable.kanak, "Kanak Saini", "Android Developer")
//        Profile_view(R.drawable.uddeshya, "Uddeshya", "Ai ML Engineer")
//        Profile_view(R.drawable.harsh, "Harsh", "Data Scientist")
//        Profile_view(R.drawable.priyanshu, "Priyanshu", "React Native Developer")
//    }
//
//
//}


@Preview(showBackground = true)
@Composable
fun modifier_view() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(

            text = "Hello ",
            fontSize = 40.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clickable {}
                .background(Color.Blue)
                .border(2.dp, Color.Red)
                .clip(CircleShape)
                .background(Color.Green)
                .size(300.dp)

        )
    }

}




