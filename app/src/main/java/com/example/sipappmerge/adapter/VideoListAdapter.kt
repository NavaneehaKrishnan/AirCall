package com.example.sipappmerge.adapter

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sipappmerge.Merge.FAQActivity
import com.example.sipappmerge.Merge.WebViewActivity
import com.example.sipappmerge.R
import com.example.sipappmerge.Utils.VideoData

/**
 * the adapter of video list
 * @author moosphon
 */
class VideoListAdapter() : RecyclerView.Adapter<VideoListAdapter.VideoViewHolder>(){

    private var videoList: List<VideoData> = ArrayList()
    private lateinit var context: Context
    var listener: OnVideoSelectListener? = null
    private var mSelectedPosition: Int = -1
    private var checkState: HashSet<Int> = HashSet()

    constructor(data: List<VideoData>):this(){
        this.videoList = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video_layout, parent, false)
        return VideoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoView  = holder.itemView.findViewById<ImageView>(R.id.card_item_videoView)
        val thumbnailImage = holder.itemView.findViewById<ImageView>(R.id.card_item_thumbnail)
        val txtDesc = holder.itemView.findViewById<TextView>(R.id.txtDesc)



        thumbnailImage.setImageResource(videoList[position].thumbnail)
        txtDesc.text = videoList[position].description

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context,WebViewActivity::class.java).putExtra("url",videoList[position].videoRes).putExtra("btnname","Close"))
        }


    }

    fun playVideo(position: Int) {
        if (mSelectedPosition != position) {
            //先取消上个item的勾选状态
            checkState.remove(mSelectedPosition)
            notifyItemChanged(mSelectedPosition)
            //设置新Item的勾选状态
            mSelectedPosition = position
            checkState.add(mSelectedPosition)
            notifyItemChanged(mSelectedPosition)
        }

    }

    fun setOnStickerSelectListener(selectListener: OnVideoSelectListener) {
        this.listener = selectListener
    }

    class VideoViewHolder(view: View): RecyclerView.ViewHolder(view)


    interface OnVideoSelectListener{
        fun onVideoSelect(url: String, position:Int)
    }
}