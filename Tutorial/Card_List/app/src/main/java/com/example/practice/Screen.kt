package com.example.practice

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showBackground = true, heightDp = 500)
@Composable
fun Show_Preview() {
    LazyColumn(content = {
        items(getCategoryList()) {
            BlogCategory(img = it.img, title = it.title, subtitle = it.subtitle)
        }
    })
}

@Composable
fun BlogCategory(img: Int, title: String, subtitle: String) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Image(
                painter = painterResource(id = img),
                contentDescription = "",
                modifier = Modifier
                    .size(120.dp)
                    .padding(8.dp)
                    .weight(.2f)
            )
            ItemDescription(title, subtitle, modifier = Modifier.weight(.8f))
        }
    }
}

@Composable
public fun RowScope.ItemDescription(
    title: String,
    subtitle: String,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
//            fontWeight = FontWeight.Thin,
            fontSize = 12.sp,
        )

    }
}

data class Category(val img: Int, val title: String, val subtitle: String)

fun getCategoryList(): MutableList<Category> {
    val list = mutableListOf<Category>()
    list.add(Category(R.drawable.kanak, "Kanak", "Android Developer"))
    list.add(Category(R.drawable.uddeshya, "Uddeshya", "AI ML Engineer"))
    list.add(Category(R.drawable.priyanshu, "Priyanshu", "React Native Developer"))
    list.add(Category(R.drawable.harsh, "Harsh", "Data Scientist"))
    list.add(Category(R.drawable.profile, "Ashish", "Python Developer"))
    list.add(Category(R.drawable.uddeshya, "Abhishek", "Full Stack Developer"))
    list.add(Category(R.drawable.kanak, "Rajeev", "IOT Developer"))
    list.add(Category(R.drawable.priyanshu, "Sneha", "UI/UX Designer"))
    list.add(Category(R.drawable.harsh, "Rahul", "DevOps Engineer"))
    list.add(Category(R.drawable.profile, "Anjali", "Cloud Architect"))
    list.add(Category(R.drawable.kanak, "Vikram", "Backend Developer"))
    list.add(Category(R.drawable.uddeshya, "Pooja", "Cybersecurity Analyst"))
    list.add(Category(R.drawable.priyanshu, "Arjun", "Blockchain Developer"))
    list.add(Category(R.drawable.harsh, "Neha", "QA Engineer"))
    list.add(Category(R.drawable.profile, "Rohit", "Game Developer"))
    list.add(Category(R.drawable.kanak, "Divya", "Flutter Developer"))
    list.add(Category(R.drawable.uddeshya, "Manish", "System Architect"))
    list.add(Category(R.drawable.priyanshu, "Sakshi", "Database Administrator"))
    list.add(Category(R.drawable.harsh, "Gaurav", "Embedded Systems Engineer"))
    list.add(Category(R.drawable.profile, "Ritika", "Product Manager"))
    list.add(Category(R.drawable.kanak, "Amit", "Kotlin Developer"))
    list.add(Category(R.drawable.uddeshya, "Priya", "Machine Learning Engineer"))
    list.add(Category(R.drawable.priyanshu, "Suresh", "Web Developer"))
    list.add(Category(R.drawable.harsh, "Meena", "Network Engineer"))
    list.add(Category(R.drawable.profile, "Tarun", "AR/VR Developer"))

    return list
}