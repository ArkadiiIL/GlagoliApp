package com.arkadii.glagoli.alarmmenu

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.arkadii.glagoli.R

class AlarmMenuViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val tvDay: TextView = itemView.findViewById(R.id.tv_day)
    val tvTime: TextView = itemView.findViewById(R.id.tv_time)
    val playAudioAlarmMenuBtn: ImageButton =
        itemView.findViewById(R.id.play_audio_alarm_menu_btn)
    val tvFullDate: TextView = itemView.findViewById(R.id.tv_full_date)
    val tvRecordName: TextView = itemView.findViewById(R.id.tv_record_name)
    val swEnable: SwitchCompat = itemView.findViewById(R.id.sw_enable)
    val btnEdit: ImageButton = itemView.findViewById(R.id.btn_edit)
    val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)
}
