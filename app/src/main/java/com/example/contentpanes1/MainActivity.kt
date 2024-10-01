package com.example.contentpanes1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.* // For layout components
import androidx.compose.material3.Button
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SimpleContentPanesApp() {
    var lives = remember { mutableStateOf(6) }
    val windowInfo = calculateCurrentWindowInfo()
    val items = listOf('a', 'b', 'c', 'd','e','f','g','k','o','t','l','i','n') // sample tasks
    val word = listOf('k','o','t','l','i','n')
    var clickedItems = remember { mutableListOf("") }
    var letters = remember{mutableListOf(Triple("",false, false ), Triple("",false,false))}
    var hintCount by remember{ mutableIntStateOf(3) }
    if (windowInfo.isWideScreen) {
        // Two-pane layout for wide screens, one for the task list
        // the other for the task details
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(1f)) {
                ChooseLetterPane(lives = lives, onLifeLost = { lives.value-- }, items, word)
                Spacer(modifier = Modifier.height(16.dp))
                HintPane(lives = lives, onLifeLost = { lives.value-- }, clickedItems, hintCount, onHint = {hintCount-- })
                Text(lives.value.toString())
                Text("$hintCount")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
            VisualPane(letters)
            if(lives.value == 0){
                //end game
                //snackbar
                Text("GAME OVER")
            }}
        }
    } else {
        Column {
            VisualPane(letters)
            ChooseLetterPane(lives = lives, onLifeLost = { lives.value-- }, items, word)
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChooseLetterPane(lives: MutableState<Int>, onLifeLost: () -> Unit, letters: List<Char>, word:List<Char>, modifier: Modifier = Modifier,) {
    var clickedLetters = mutableListOf<Char>()
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
                    //snackbar saying no
                }
                else{
                    onLifeLost()
                    //snackbar saying yes
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
}}

@Composable
fun HintPane(lives: MutableState<Int>, onLifeLost: () -> Unit, clicked: List<String>, hintCount: Int, onHint: () -> Unit, modifier: Modifier = Modifier) {
    // Task details pane used when the user selects a particular task
    Button(onClick = {
        if(hintCount==3) {
            onHint()
            //snackbar "It's your favorite coding language!"
            //do first hint
        }
        else if(hintCount==2){
            onHint()
            lives.value -=1
            //do second hint
        }
        else if(hintCount==1){
            onLifeLost()
            onHint()
            //do second hint
        }
        else{}
    }
    )
        {
        Text("Click for a hint!")
    }
}

@Composable
fun VisualPane(letters: List<Triple<String, Boolean, Boolean>>, modifier: Modifier = Modifier) {
        Text(
            text = "Visual Pane",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    Image(
        painter = painterResource(id = R.drawable.preyingboy),
        contentDescription = "preying mantis"
        //size modifier so it doesn't go off screen
    )
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