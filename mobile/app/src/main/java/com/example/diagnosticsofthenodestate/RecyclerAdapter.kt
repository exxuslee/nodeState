package com.example.diagnosticsofthenodestate


import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_view_item.view.*


class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
    val lstPoint = itemView.lstPoint
    val lstRed = itemView.lstRed
    val lstYellow = itemView.lstYellow
    val lstGreen = itemView.lstGreen
    val lstMI = itemView.lstMI




    fun bind(user: User, clickListener: OnItemClickListener, onEditTextChanged: OnEditTextChanged)
    {
        lstPoint.text = user.username
        lstRed.text = user.red
        lstYellow.text = user.yellow
        lstGreen.text = user.green

 //       lstPoint.setBackgroundColor(Color.LTGRAY)
        lstPoint.setBackgroundColor(when (user.state) {
            0 -> Color.LTGRAY
            1 -> Color.GREEN
            2 -> Color.YELLOW
            3 -> Color.RED
            else -> Color.LTGRAY
        })

        lstMI.text.clear()
        lstMI.visibility = View.GONE

        itemView.lstPoint.setOnClickListener {
            clickListener.onItemClicked(user)

            if (user.green == "null") {
                if (lstMI.visibility == View.GONE) lstMI.visibility = View.VISIBLE
                else {
                    lstMI.visibility = View.GONE
                }
            } else {
                if (lstRed.visibility == View.VISIBLE) {
                    lstRed.visibility = View.GONE
                    lstYellow.visibility = View.GONE
                    lstGreen.visibility = View.GONE
                    lstMI.visibility -View.GONE
                } else {
                    lstRed.visibility = View.VISIBLE
                    if (lstYellow.text != "null") lstYellow.visibility = View.VISIBLE
                    if (lstRed.text != "null") lstRed.visibility = View.VISIBLE
                    lstGreen.visibility = View.VISIBLE
                }
            }
        }

        itemView.lstRed.setOnClickListener {
            clickListener.onItemClicked(user)
            lstRed.visibility = View.GONE
            lstYellow.visibility = View.GONE
            lstGreen.visibility = View.GONE
            lstPoint.setBackgroundColor(Color.RED)
            user.state = 3
            user.stateRYG = user.red
        }

        itemView.lstYellow.setOnClickListener {
            clickListener.onItemClicked(user)
            lstRed.visibility = View.GONE
            lstYellow.visibility = View.GONE
            lstGreen.visibility = View.GONE
            lstPoint.setBackgroundColor(Color.YELLOW)
            user.state = 2
            user.stateRYG = user.yellow
        }

        itemView.lstGreen.setOnClickListener {
            clickListener.onItemClicked(user)
            lstRed.visibility = View.GONE
            lstYellow.visibility = View.GONE
            lstGreen.visibility = View.GONE
            lstPoint.setBackgroundColor(Color.GREEN)
            user.state = 1
            user.stateRYG = user.green
        }

        itemView.lstMI.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
//                Log.i("OTDO", "afterTextChanged: $editable")
//                Log.i("OTDO", "afterTextChanged: ${user.yellow}")
//                Log.i("OTDO", "afterTextChanged: ${user.green}")
//                Log.i("OTDO", "test: ${user.yellow.toFloatOrNull()}  ${user.green.toFloatOrNull()}")

                if (editable.isEmpty() ||
                    user.yellow.toFloatOrNull() == null ||
                    user.red.toFloatOrNull() == null) {
                    lstPoint.setBackgroundColor(Color.LTGRAY)
                    user.state = 0
                    user.stateRYG = ""
                } else if (editable.toString().toFloat() > user.red.toFloat()) {
                    lstPoint.setBackgroundColor(Color.RED)
                    user.state = 3
                    user.stateRYG = editable.toString()
                } else if (editable.toString().toFloat() > user.yellow.toFloat()) {
                    lstPoint.setBackgroundColor(Color.YELLOW)
                    user.state = 2
                    user.stateRYG = editable.toString()
                } else {
                    lstPoint.setBackgroundColor(Color.GREEN)
                    user.state = 1
                    user.stateRYG = editable.toString()
                }
            }
        })
    }

}


class RecyclerAdapter(
    var users: MutableList<User>,
    val itemClickListener: OnItemClickListener,
    val onEditTextChanged: OnEditTextChanged

):RecyclerView.Adapter<MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_view_item,
            parent,
            false
        )
        return MyHolder(view)


    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(myHolder: MyHolder, position: Int) {
        val user = users.get(position)
        myHolder.bind(user, itemClickListener, onEditTextChanged)


    }
}


interface OnItemClickListener{
    fun onItemClicked(user: User)
}

interface OnEditTextChanged {
    fun onTextChanged(position: Int, charSeq: String?)
}