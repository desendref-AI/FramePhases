package uz.ttpu.framephases

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch

fun Modifier.logPhases(name:String)=this.layout { m,c -> Log.d("Phases","$name: layout/measure"); val p=m.measure(c); layout(p.width,p.height){Log.d("Phases","$name: layout/place");p.place(0,0)} }.drawBehind{Log.d("Phases","$name: draw")}
@Composable fun Page(content:@Composable ColumnScope.()->Unit)=Column(Modifier.fillMaxSize().padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp),content=content)

@Composable fun LectureTree(){ SideEffect{Log.d("Phases","LectureTree: composition")}; Page { Text("tag:Phases"); Row(Modifier.logPhases("Row")){Box(Modifier.size(40.dp).background(Color.Gray).logPhases("Image"));Column(Modifier.logPhases("Column")){Text("Title",Modifier.logPhases("Text A"));Text("Subtitle",Modifier.logPhases("Text B"))}} } }
@Composable fun PaddingDemo(){var p by remember{mutableStateOf(8.dp)};SideEffect{Log.d("Phases","PaddingDemo: composition")};Page{Text("Composition read");Button({p=if(p==8.dp)32.dp else 8.dp}){Text("Toggle padding")};Text("Padding demo",Modifier.padding(p).background(Color(0xFFEADDFF)).logPhases("padding demo"))}}
@Composable fun OffsetDemo(){var x by remember{mutableStateOf(0.dp)};var fast by remember{mutableStateOf(true)};SideEffect{Log.d("Phases","OffsetDemo: composition")};Page{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Button({x=if(x==0.dp)80.dp else 0.dp}){Text("Move")};FilterChip(fast,{fast=!fast},{Text("Deferred")})};if(fast)Text("Offset",Modifier.offset{IntOffset(x.roundToPx(),0)}.logPhases("offset demo"))else{val px=with(LocalDensity.current){x.roundToPx()};Text("Offset",Modifier.offset{IntOffset(px,0)}.logPhases("offset demo"))}}}
@Composable fun ColorDemo(){var c by remember{mutableStateOf(Color.Red)};var canvas by remember{mutableStateOf(true)};SideEffect{Log.d("Phases","ColorDemo: composition")};Page{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Button({c=if(c==Color.Red)Color.Blue else Color.Red}){Text("Change")};FilterChip(canvas,{canvas=!canvas},{Text("Canvas")})};if(canvas)Canvas(Modifier.size(120.dp).logPhases("color demo")){drawRect(c)}else Box(Modifier.size(120.dp).background(c).logPhases("color demo"))}}

@Composable fun ParallaxDemo(){var fast by remember{mutableStateOf(true)};Column(Modifier.fillMaxSize()){Row(Modifier.padding(8.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){FilterChip(!fast,{fast=false},{Text("Slow")});FilterChip(fast,{fast=true},{Text("Fast")})};key(fast){Parallax(fast)}}}
@Composable private fun Parallax(fast:Boolean){val s=rememberLazyListState();val n=remember{intArrayOf(0)};SideEffect{Log.d("Parallax","${if(fast)"fast" else "slow"}: #${++n[0]}")};Box(Modifier.fillMaxSize()){val b=Modifier.fillMaxWidth().height(120.dp).background(Color(0xFF6650A4));if(fast)Box(b.offset{IntOffset(0,s.firstVisibleItemScrollOffset/2)})else{val y=with(LocalDensity.current){(s.firstVisibleItemScrollOffset/2).toDp()};Box(b.offset(y=y))};LazyColumn(state=s,contentPadding=PaddingValues(top=120.dp)){items(100){Text("Item $it",Modifier.fillMaxWidth().background(Color.White.copy(alpha=.88f)).padding(16.dp))}}}}
@Composable fun DerivedDemo(){var opt by remember{mutableStateOf(true)};Column(Modifier.fillMaxSize()){Row(Modifier.padding(8.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){FilterChip(!opt,{opt=false},{Text("Naive")});FilterChip(opt,{opt=true},{Text("derivedStateOf")})};key(opt){ScrollList(opt)}}}
@Composable private fun ScrollList(opt:Boolean){val s=rememberLazyListState();val scope=rememberCoroutineScope();val n=remember{intArrayOf(0)};SideEffect{Log.d("Derived","${if(opt)"optimized" else "naive"}: #${++n[0]}")};val show=if(opt){val v by remember{derivedStateOf{s.firstVisibleItemIndex>0}};v}else s.firstVisibleItemIndex>0;Box(Modifier.fillMaxSize()){LazyColumn(state=s){items(200){Text("Item $it",Modifier.fillMaxWidth().padding(16.dp))}};if(show)Button({scope.launch{s.animateScrollToItem(0)}},Modifier.align(Alignment.BottomEnd).padding(16.dp)){Text("Top")}}}

@Composable fun SkipDemo(){var lambda by remember{mutableStateOf(false)};Column(Modifier.padding(20.dp)){Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){FilterChip(!lambda,{lambda=false},{Text("Value")});FilterChip(lambda,{lambda=true},{Text("Lambda")})};key(lambda){if(lambda)ParentLambda()else ParentValue()}}}
@Composable private fun ParentValue(){var n by remember{mutableIntStateOf(0)};SideEffect{Log.d("Skip","Parent value")};Column{Button({n++}){Text("Add")};ChildValue(n);Sibling()}}
@Composable private fun ChildValue(n:Int){SideEffect{Log.d("Skip","Child value")};Text("Count: $n")}
@Composable private fun ParentLambda(){var n by remember{mutableIntStateOf(0)};SideEffect{Log.d("Skip","Parent lambda")};Column{Button({n++}){Text("Add")};ChildLambda{n};Sibling()}}
@Composable private fun ChildLambda(n:()->Int){SideEffect{Log.d("Skip","Child lambda")};Text("Count: ${n()}")}
@Composable private fun Sibling(){SideEffect{Log.d("Skip","Sibling")};Text("I never change")}

@Composable fun CycleDemo(){var fixed by remember{mutableStateOf(false)};Page{Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){FilterChip(!fixed,{fixed=false},{Text("Cyclic")});FilterChip(fixed,{fixed=true},{Text("Fixed")})};key(fixed){if(fixed)Fixed()else Cyclic()}}}
@Composable private fun Cyclic(){var h by remember{mutableIntStateOf(0)};val read=h;val n=remember{intArrayOf(0)};SideEffect{Log.d("Cycle","#${++n[0]} height=$read")};Box{Box(Modifier.fillMaxWidth().height(120.dp).background(Color(0xFF6650A4)).onSizeChanged{Log.d("Cycle","onSizeChanged=${it.height}");h=it.height});Text("I'm below",Modifier.padding(top=with(LocalDensity.current){read.toDp()}))}}
@Composable private fun Fixed(){SideEffect{Log.d("Cycle","fixed composition")};Column{Box(Modifier.fillMaxWidth().height(120.dp).background(Color(0xFF6650A4)));Text("I'm below")}}
@Composable fun StackBelow(modifier:Modifier=Modifier,spacing:Dp=8.dp,content:@Composable ()->Unit){Layout(content,modifier){ms,c->val loose=c.copy(minWidth=0,minHeight=0);val ps=ms.map{Log.d("StackBelow","measure child");it.measure(loose)};val gap=spacing.roundToPx();val w=ps.maxOfOrNull{it.width}?:0;val h=ps.sumOf{it.height}+gap*(ps.size-1).coerceAtLeast(0);layout(c.constrainWidth(w),c.constrainHeight(h)){Log.d("StackBelow","place children");var y=0;ps.forEach{it.placeRelative(0,y);y+=it.height+gap}}}}
@Composable fun StackDemo()=Page{Text("3 measure + 1 place log");StackBelow(Modifier.fillMaxWidth()){Box(Modifier.fillMaxWidth().height(120.dp).background(Color(0xFF6650A4)));Text("Second child");Text("Third child")}}

@Composable fun Theory(){val data=listOf("1. Composition → Layout → Drawing. Layout: measurement va placement. Skipping keraksiz ishni o'tkazadi.","2. Column 100×40, Row 148×48. Image (0,0), Column (48,0), A (48,0), B (48,20). Bir-pass O(n).","3. State yaratilganda emas, ishlatilgan joyda o'qiladi. Restart scope o'qish bo'lgan qayta bajariluvchi blok.","8. padding/offset(Dp): composition; offset lambda: placement; Canvas/drawWithContent: drawing; Layout lambda: measurement.","12. Layout → state write → composition read cyclic dependency ikki frame talab qiladi. Column yoki custom Layout ishlating.");LazyColumn(contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){items(data){ElevatedCard{Text(it,Modifier.padding(16.dp))}}}}
