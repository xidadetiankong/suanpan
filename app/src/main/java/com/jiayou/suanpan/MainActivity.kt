package com.jiayou.suanpan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*



val keyList:List<String> = listOf("AC","←","%","÷","7","8","9","×","4","5","6","-","1","2","3","+","^","0",".","=")

var latestOperatorPosition:Int=0
class MainActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val myRecyclerView:RecyclerView=findViewById(R.id.keyboard_area)
        myRecyclerView.layoutManager=GridLayoutManager(this,4)
        val myadapyer=KeyAdapter(keyList)
        myRecyclerView.adapter=myadapyer
        myRecyclerView.canScrollHorizontally(0)
        myRecyclerView.canScrollVertically(0)
        myadapyer.setOnKotlinItemClickListener(object :KeyAdapter.IKotlinItemClickListener{
            override fun onItemClickListener(position: Int) {
                intputJudge(keyList[position])

            }


        })




    }
    fun intputJudge(st:String){
        val lengthOfcontex=input_context.text.length
        Log.d("bamaipi", "intputLength:${lengthOfcontex} ")
        when(st){
            "AC"->allClear()
            "←"->backSpace()
            "0", "1","2","3","4","5","6","7","8","9"-> this.input_context.text="${input_context.text}${st}"
            "."->judgeDecimal()
            "%","÷","×","-","+","^"-> operator(st)
            "="->equals()
        }
    }
    private fun allClear(){
        latestOperatorPosition=0
        input_context.text = ""
        result_context.text = ""
    }
    private fun backSpace(){

       val length=input_context.text.length

        Log.d("mamaipi", "backSpace:$length ")
        if (length==0){
            return
        }else{
            input_context.text=input_context.text.substring(0, length-1)
        }


    }
    private fun judgeDecimal(){
        val inputText=input_context.text

        val inputingText=inputText.substring(latestOperatorPosition,inputText.length)
        if (!inputingText.contains('.')){
            input_context.text="${input_context.text}."
        }
    }
    private fun equals(){
        when{
            //如果输入文本包含有-7- 或7+之类不可计算算式，返回
            input_context.text.contains(Regex("""^[\\+|\-]?\d+\D{1}$"""))->return
            //包含有以^%÷×开头的算式,清除第一个符号
            input_context.text.contains(Regex("""^[\\^\\%\\÷\\×]{1}\d+"""))->{
                val length=input_context.text.length
                input_context.text=input_context.text.substring(1,length)
            }
            //判断为可计算的算式-7-7+ or 7+7÷的类型
            input_context.text
                .contains(Regex("""(^[\\+|\-]\d+\D{1}\d+$)|(^\d+\D{1}\d+$)"""))->{
                Log.d("bamaipi", "可计算算式:${input_context.text} ")
                makeBMember(input_context.text)
            }
            input_context.text
                .contains(Regex("""(^[\\+|\-]\d+\D{1}.*\d+$)|(^\d+\D{1}.*\d+$)"""))->{
                Log.d("bamaipi", "可计算算式:${input_context.text} ")
                makeBMember(input_context.text)
            }

        }
    }
    private fun operator(st: String){
        //判断是否有重复符号并重置为最新输入的符号
        input_context.text="${input_context.text}$st"
        while (input_context.text.contains(Regex("""\D{2}$"""))){
            input_context.text=Regex("""\D{2}$""").replace(input_context.text,st)
        }
        //判断是否为可计算算式
        when{
            //如果输入文本包含有-7- 或7+之类不可计算算式，返回
            input_context.text.contains(Regex("""^[\\+|\-]?\d+\D{1}$"""))->return
            //包含有以^%÷×开头的算式,清除第一个符号
            input_context.text.contains(Regex("""^[\\^\\%\\÷\\×]{1}\d+"""))->{
                val length=input_context.text.length
                input_context.text=input_context.text.substring(1,length)
            }
            //判断为可计算的算式-7-7+ or 7+7÷的类型
            input_context.text
                .contains(Regex("""(^[\\+|\-]\d+\D{1}\d+\D{1}$)|(^\d+\D{1}\d+\D{1}$)"""))->{
                Log.d("bamaipi", "operator1可计算算式:${input_context.text} ")

                    makeAMember(input_context.text,st)
                }
            input_context.text
                .contains(Regex("""(^[\\+|\-]\d+\D{1}.*\d+\D{1}$)|(^\d+\D{1}.*\d+\D{1}$)"""))->{
                Log.d("bamaipi", "operator2可计算算式:${input_context.text} ")
                makeAMember(input_context.text,st)
            }

        }




    }
    //符号前算式分解
    private  fun makeAMember(text:CharSequence,st: String){
        val reslt_context=result_context.text

        if (reslt_context.isNullOrEmpty()){
            latestOperatorPosition=text.lastIndexOf(st)
            val oldText=text.substring(0, text.length-1)
            Log.d("bamaipi", "makeAMember做一个算式(当结果是空的时候:$oldText/$latestOperatorPosition ")
            try {
                getMember(oldText)
            }catch (e:Exception){
                Log.d("bamaipi", "makeAMember有个BUG:$e ")
            }

        }else if(reslt_context.isNotEmpty()){
            //记录前期计算截止位置
            val latestPosition=latestOperatorPosition
            Log.d("bamaipi", "latestPosition:$latestPosition ")
            //更新最后计算截止位置
            latestOperatorPosition=text.length
            Log.d("bamaipi", "latestOperatorPosition:$latestOperatorPosition ")
                //利用result_context.text和text.substring(latestPosition, latestOperatorPosition-1)组成算式
            if(latestPosition== latestOperatorPosition){
                return
            }else if( latestOperatorPosition-latestPosition==3){
                Log.d("bamaipi",
                    "makeAMember做一个算式FUCK: " +
                            "/result_context.text${result_context.text}" +
                            "/latestPosition:$latestPosition" +
                            "/text:$text" +
                            "/text.substring(latestPosition, text.length-1)${text.substring(latestPosition, text.length-1)}")
                val oldText="${result_context.text}${text.substring(latestPosition, text.length-1)}"

                try {
                    getMember(oldText)
                }catch (e:Exception){
                    Log.d("bamaipi", "makeAMember有个BUG:$e ")
                }
            }else if( latestOperatorPosition-latestPosition<3){
                Log.d("bamaipi",
                    "makeAMember做一个算式FUCK: " +
                            "/result_context.text${result_context.text}" +
                            "/latestPosition:$latestPosition" +
                            "/text:$text" +
                            "/text.substring(latestPosition, text.length-1)${text.substring(latestPosition-1, text.length-1)}")
                val oldText="${result_context.text}${text.substring(latestPosition-1, text.length-1)}"

                try {
                    getMember(oldText)
                }catch (e:Exception){
                    Log.d("bamaipi", "makeAMember有个BUG:$e ")
                }
            }


        }
    }
//等于号算式分解
    private  fun makeBMember(text: CharSequence){
    val reslt_context=result_context.text
    if (reslt_context.isNullOrEmpty()){
        latestOperatorPosition=text.length+1
        val oldText=text
        Log.d("bamaipi",
            "makeBMember做一个算式1(当结果是空的时候:" +
                    "oldText:$oldText /latestOperatorPosition:$latestOperatorPosition ")
        try {
            getMember(oldText)
        }catch (e:Exception){
            Log.d("bamaipi", "makeBMember有个BUG:$e ")
        }

    }else if(reslt_context.isNotEmpty()){
        //记录前期计算截止位置
        val latestPosition=latestOperatorPosition
        //更新最后计算截止位置
        latestOperatorPosition=text.length+1

        //利用result_context.text和text.substring(latestPosition, text.length)组成算式
        val oldText="${result_context.text.substring(0,reslt_context.length-2)}${text.substring(latestPosition-1, text.length)}"
        Log.d("bamaipi",
            "makeBMember做一个算式2(当结果非空的时候:oldText:$oldText " +
                    "/text:$text" +
                    "/latestPosition:$latestPosition" +
                    "/latestOperatorPosition:$latestOperatorPosition")
        try {
            getMember(oldText)
        }catch (e:Exception){
            Log.d("bamaipi", "makeBMember有个BUG:$e ")
        }
    }
    }
    private fun getMember(st:CharSequence){
        Log.d("bamaipi", "截出算式分解:$st ")
        //首个字符为符号时
        if (st.contains(Regex("""^\D{1}\d+"""))){
            val reString=st.substring(1,st.length)
            when {
                reString.contains("+") -> {
                    val numberList= st.trim().split("+")
                    Log.d("bamaipi", "加法1:$numberList ")
                    result_context.text=getResult(numberList[0].toDouble(),"+",numberList[1].toDouble())
                }
                reString.contains("-") -> {
                    val numberList= st.trim().split("-")
                    Log.d("bamaipi", "减法1$numberList ")
                    result_context.text=getResult(-numberList[1].toDouble(),"-",numberList[2].toDouble())
                }
                reString.contains("×") -> {
                    val numberList= st.trim().split("×")
                    Log.d("bamaipi", "乘法1$numberList ")
                    result_context.text=getResult(numberList[0].toDouble(),"×",numberList[1].toDouble())
                }
                reString.contains("÷") -> {
                    val numberList= st.trim().split("÷")
                    Log.d("bamaipi", "除法1$numberList ")
                    result_context.text=getResult(numberList[0].toDouble(),"÷",numberList[1].toDouble())
                }
                reString.contains("%") -> {
                    val numberList= st.trim().split("%")
                    Log.d("bamaipi", "取余1$numberList ")
                    result_context.text=getResult(numberList[0].toDouble(),"%",numberList[1].toDouble())
                }
                reString.contains("^") -> {
                    val numberList= st.trim().split("^")
                    Log.d("bamaipi", "次方1$numberList ")
                    result_context.text=getResult(numberList[0].toDouble(),"^",numberList[1].toDouble())
                }
            }
        }else{
            when {
                st.contains("+") -> {
                    val numberList= st.trim().split("+")
                    Log.d("bamaipi", "加法2:$numberList ")
                    result_context.text=getResult(numberList[0].toDouble(),"+",numberList[1].toDouble())
                }
                st.contains("-") -> {
                    val numberList= st.trim().split("-")
                    Log.d("bamaipi", "减法2$numberList ")
                    result_context.text=getResult(numberList[0].toDouble(),"-",numberList[1].toDouble())
                }
                st.contains("×") -> {
                    val numberList= st.trim().split("×")
                    result_context.text=getResult(numberList[0].toDouble(),"×",numberList[1].toDouble())
                }
                st.contains("÷") -> {
                    val numberList= st.trim().split("÷")
                    result_context.text=getResult(numberList[0].toDouble(),"÷",numberList[1].toDouble())
                }
                st.contains("%") -> {
                    val numberList= st.trim().split("%")
                    result_context.text=getResult(numberList[0].toDouble(),"%",numberList[1].toDouble())
                }
                st.contains("^") -> {
                    val numberList= st.trim().split("^")
                    result_context.text=getResult(numberList[0].toDouble(),"^",numberList[1].toDouble())
                }
            }
        }


    }

    private fun getResult(num1:Double, operator:String, num2:Double):String{
        Log.d("bamaipi", "截出算式分解:$num1$operator$num2 ")
       return when(operator){
           "+"->(num1+num2).toString()
           "-"->(num1-num2).toString()
           "×"->(num1*num2).toString()
           "÷"->(num1/num2).toString()
           "%"->(num1%num2).toString()
           "^"->(Math.pow(num1,num2)).toString()
           else->"计算错误"
        }
    }





}




