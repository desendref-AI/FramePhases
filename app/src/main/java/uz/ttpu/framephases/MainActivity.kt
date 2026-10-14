package uz.ttpu.framephases

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { MaterialTheme { App() } } }
}
data class Demo(val title:String, val body:@Composable ()->Unit)
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun App() {
 var selected by remember { mutableStateOf<Demo?>(null) }
 val demos = listOf(
  Demo("1–3, 8, 12. Nazariya") { Theory() }, Demo("4. Lecture Tree") { LectureTree() },
  Demo("5. Padding") { PaddingDemo() }, Demo("6. Offset") { OffsetDemo() }, Demo("7. Color") { ColorDemo() },
  Demo("9. Parallax") { ParallaxDemo() }, Demo("10. derivedStateOf") { DerivedDemo() },
  Demo("11. Skipping") { SkipDemo() }, Demo("13. Cyclic dependency") { CycleDemo() }, Demo("14. StackBelow") { StackDemo() }
 )
 Scaffold(topBar={ TopAppBar(title={Text(selected?.title ?: "Jetpack Compose Phases")}, navigationIcon={if(selected!=null) TextButton(onClick={selected=null}){Text("← Orqaga")}}) }) { p ->
  Box(Modifier.fillMaxSize().padding(p)) { if(selected==null) LazyColumn(contentPadding=PaddingValues(16.dp), verticalArrangement=Arrangement.spacedBy(8.dp)) {
   item { Text("Practice 7", style=MaterialTheme.typography.headlineMedium); Text("Logcat: Phases, Parallax, Derived, Skip, Cycle, StackBelow") }
   items(demos){ d -> ElevatedCard(onClick={selected=d}, modifier=Modifier.fillMaxWidth()){Text(d.title, Modifier.padding(18.dp), style=MaterialTheme.typography.titleMedium)} }
  } else key(selected!!.title){selected!!.body()} }
 }
}
