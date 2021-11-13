package com.jiayou.suanpan

import android.app.ActionBar
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class KeyAdapter(val keyList:List<String>):RecyclerView.Adapter<KeyAdapter.KeyViewHolder>() {
    var itemClickListener: IKotlinItemClickListener? = null
    //设置联系上下文参数
    val context: Context? = null

//创建viewHolder子类,并说明viewHolder内包含的子类需要在加载时改变的内容
    class KeyViewHolder(view: View):RecyclerView.ViewHolder(view){
        val item_title:TextView

        init {
            item_title=view.findViewById(R.id.key_context)

        }
    }
//onCreateViewHolder方法用来定位子布局文件位置，并进行子布局和父布局的装饰
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyViewHolder {
        val view=LayoutInflater.from(parent.context)
            .inflate(R.layout.keykids,parent,false)
    Log.d("shushu", "parentHeight:${parent} ")
            val viewHolder=KeyViewHolder(view)
            val parentHeight=parent.height
            val layoutParams=viewHolder.itemView.layoutParams
            val itemRawNumbers=keyList.size/4
    Log.d("shushu", "parentHeight:${parentHeight} ")
            layoutParams.height=parentHeight/itemRawNumbers
    Log.d("shushu", "kidViewheight:${layoutParams.height} ")

        return KeyViewHolder(view)

    }

    override fun onBindViewHolder(holder: KeyViewHolder, position: Int) {
       holder.item_title.text= keyList[position]
        holder.itemView.setOnClickListener {
            itemClickListener!!.onItemClickListener(position)
        }
    }

    override fun getItemCount()= keyList.size
    //下面的set方法作用是使传入类中参数this.itemClickListener指向onBindViewHolder中最后将position参数暴露在
    //接口中
    fun setOnKotlinItemClickListener(itemClickListener: IKotlinItemClickListener){
        this.itemClickListener=itemClickListener
    }
    interface IKotlinItemClickListener{
        fun onItemClickListener(position: Int)
    }
}
