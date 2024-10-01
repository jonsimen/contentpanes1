package com.example.contentpanes1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.* // For layout components
import androidx.compose.material3.Button
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.* // For state management
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleContentPanesApp()
        }
    }
}

@Composable
fun SimpleContentPanesApp() {
    var letterCorrect = remember { mutableStateOf(false) }
    var snackbarVisibleState = remember { mutableStateOf(false) }
    var lives = rememberSaveable { mutableIntStateOf(6) }
    var hintCount = rememberSaveable { mutableIntStateOf(3) }
    var clickedItems = rememberSaveable { mutableListOf("") }
    val windowInfo = calculateCurrentWindowInfo()
    val items = listOf('a', 'b', 'c', 'd','e','f','g','k','o','t','l','i','n') // sample tasks
    val word = listOf('k','o','t','l','i','n')
    if(lives.intValue <= 0){
        //end game
        Column {
        Text("GAME OVER")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            lives.intValue = 6
            clickedItems = mutableListOf("")
            hintCount.intValue = 3
        }) {
            Text("Reset")
        }
        }
    }
    else if(word.all { char ->
            clickedItems.any { string -> char in string }
        }){
        //end game
        Column {
            Text("YOU WIN")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                lives.intValue = 6
                clickedItems = mutableListOf("")
                hintCount.intValue = 3
            }) {
                Text("Reset")
            }
        }
    }
    else if (windowInfo.isWideScreen) {
        // Two-pane layout for wide screens, one for the task list
        // the other for the task details
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(1f)) {
                ChooseLetterPane(lives = lives, items, word, letterCorrect, snackbarVisibleState)
                Spacer(modifier = Modifier.height(16.dp))
                HintPane(lives = lives, hintCount = hintCount)
                Text(lives.intValue.toString())
                Text(hintCount.intValue.toString())
                Text(clickedItems.toString())
                if (snackbarVisibleState.value) {
                    Snackbar(
                        action = {
                            Button(onClick = {
                                snackbarVisibleState.value = false //dismiss on click

                            }) {
                                Text("Dismiss")
                            }
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        if (letterCorrect.value) {
                            Text(text = "Correct")
                        } else {
                            Text(text = "Wrong")
                        }
                    }
                }

            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
            VisualPane(lives)
            }
        }
    } else {
        Column {
            VisualPane(lives)
            ChooseLetterPane(lives = lives, items, word, letterCorrect, snackbarVisibleState)
            if (snackbarVisibleState.value) {
                Snackbar(
                    action = {
                        Button(onClick = {
                            snackbarVisibleState.value = false //dismiss on click

                        }) {
                            Text("Dismiss")
                        }
                    },
                    modifier = Modifier.padding(8.dp)
                ) {
                    if (letterCorrect.value) {
                        Text(text = "Correct")
                    } else {
                        Text(text = "Wrong")
                    }
                }
            }
        }
    }

}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChooseLetterPane(lives: MutableState<Int>, letters: List<Char>, word:List<Char>, letterCorrect: MutableState<Boolean>, snackbarVisibleState:MutableState<Boolean>, modifier: Modifier = Modifier) {
    var clickedLetters = mutableListOf<Char>() //need to change this to top level declaration
    val buttonStates = remember { mutableStateMapOf<Char, Boolean>() }
        Text(
            text = "Choose A Letter",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    letters.forEach { letter ->
        if (buttonStates[letter] == null) {
            buttonStates[letter] = true
        }
    }
    FlowRow {
    letters.forEach { letter ->
        val isEnabled = buttonStates[letter] ?: true
        Button(
            //if letter in word, update word display
            //if not in word, increment picture variable
            onClick = {
                if (letter in word){
                    snackbarVisibleState.value = true
                    letterCorrect.value = true
                }
                else{
                    lives.value -=1
                    snackbarVisibleState.value = true
                    letterCorrect.value = false
                }
                buttonStates[letter] = false
                clickedLetters.add(letter)
            },
            enabled = isEnabled,
            modifier = Modifier.size(30.dp),
            contentPadding = PaddingValues(0.dp)
        )// Sets both width and height to 40.dp
        {
            Text(text = letter.toString())

        }
    }
    }

}

@Composable
fun HintPane(lives: MutableState<Int>, hintCount: MutableState<Int>) {
    // Task details pane used when the user selects a particular task
    Button(onClick = {
        when(hintCount.value){
            3 ->{
                hintCount.value--
                //snackbar "It's your favorite coding language!"
                //do first hint
            }
            2 -> {
                hintCount.value--
                lives.value--
                //do second hint
            }
            1 -> {
                hintCount.value--
                lives.value--
                //do third hint
            }
        }
    }
    )
        {
        Text("Click for a hint!")
    }
}

@Composable
fun VisualPane(lives: MutableState<Int>) {
        Text(
            text = "Visual Pane",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    when(lives.value){
        6 -> {
            Image(
                painter = painterResource(id = R.drawable.preyingboy),
                contentDescription = "preying mantis"
                //size modifier so it doesn't go off screen
            )
        }
        5 -> {
            Image(
                painter = painterResource(id = R.drawable.cat),
                contentDescription = "cat"
                //size modifier so it doesn't go off screen
            )
        }
        4 -> {
            Image(
                painter = painterResource(id = R.drawable.preyingboy),
                contentDescription = "preying mantis"
                //size modifier so it doesn't go off screen
            )
        }
        3 -> {
            Image(
                painter = painterResource(id = R.drawable.preyingboy),
                contentDescription = "preying mantis"
                //size modifier so it doesn't go off screen
            )
        }
        2 -> {
            Image(
                painter = painterResource(id = R.drawable.preyingboy),
                contentDescription = "preying mantis"
                //size modifier so it doesn't go off screen
            )
        }
        1 -> {
            Image(
                painter = painterResource(id = R.drawable.preyingboy),
                contentDescription = "preying mantis"
                //size modifier so it doesn't go off screen
            )
        }

    }

}

@Composable
fun calculateCurrentWindowInfo(): WindowInfo {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    // Set a breakpoint for wide vs narrow screens (600dp is commonly used)
    val isWideScreen = screenWidth >= 600

    return WindowInfo(
        isWideScreen = isWideScreen
    )
}

data class WindowInfo(
    val isWideScreen: Boolean
)

@Preview(showBackground = true)
@Composable
fun SimpleContentPanesAppPreview() {
    SimpleContentPanesApp()
}