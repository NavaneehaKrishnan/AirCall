package com.example.sipappmerge.Merge

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sipappmerge.R
import com.example.sipappmerge.Utils.VideoData
import com.example.sipappmerge.Utils.VideoUtils
import com.example.sipappmerge.adapter.VideoListAdapter
import kotlinx.android.synthetic.main.activity_video_list.*

class VideoListActivity : AppCompatActivity() {
    private lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var videoRecyclerView : RecyclerView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_list)
        initialize()
    }

    private fun initialize() {


/*
        val bitmap = VideoUtils.createVideoThumbnail("http://220.225.104.135/SAMPLEVIDEO/APR%20report.mp4",1)
*/

        val videos = arrayListOf(
                VideoData("http://220.225.104.135/SAMPLEVIDEO/Usage%20of%20app%20-%20login,%20initiate%20and%20dispose%20calls%20and%20break.mp4", R.drawable.thumpnail_1, "just now", "Usage of app - login, initiate and dispose calls and break"),
                VideoData("http://220.225.104.135/SAMPLEVIDEO/APR%20report.mp4", R.drawable.thumpnail_2, "10 hours ago", "Usage of real time dashboard and how to force logout the agents"),
                VideoData("http://220.225.104.135/SAMPLEVIDEO/Reset%20the%20device.mp4", R.drawable.thumpnail_3, "2 days ago", "Reset the device"),
                VideoData("http://220.225.104.135/SAMPLEVIDEO/Uploading%20data.mp4", R.drawable.thumpnail_4, "September 9, 2019", "Uploading data"),
                VideoData("http://220.225.104.135/SAMPLEVIDEO/Payment%20upload%20file.mp4", R.drawable.thumpnail_5, "2 days ago", "Payment upload file"),
                VideoData("http://220.225.104.135/SAMPLEVIDEO/Auto%20rechurn.mp4", R.drawable.thumbnail_6, "2 days ago", "Auto rechurn"),
                VideoData("http://220.225.104.135/SAMPLEVIDEO/Manual%20Rechurn.mp4", R.drawable.thumbnail_7, "2 days ago", "Manual Rechurn"),
                VideoData("http://220.225.104.135/SAMPLEVIDEO/Call%20dump%20report.mp4", R.drawable.thumbnail_8, "2 days ago", "Call dump report"),
                VideoData("http://220.225.104.135/SAMPLEVIDEO/APR%20report.mp4", R.drawable.thumbnail_9, "2 days ago", "APR report")


        )

        val mLayoutManager = LinearLayoutManager(this)
        val videoListAdapter = VideoListAdapter(videos)
        linearLayoutManager = LinearLayoutManager(this)
        videoRecyclerView = findViewById(R.id.videoRecyclerView)
        videoRecyclerView.layoutManager = linearLayoutManager
        videoRecyclerView.adapter = videoListAdapter
        /*videoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisibleItem = mLayoutManager . findFirstVisibleItemPosition ()
                val firstView = mLayoutManager.findViewByPosition (firstVisibleItem)
                var playPosition = 0
                if (null != firstView) {
                    if (dy > 0) {
                        if (firstView.height + firstView.top <= firstView.height / 3) {
                            if (playPosition == firstVisibleItem + 1) {
                                return
                            }
                            playPosition = firstVisibleItem + 1
                            videoListAdapter.playVideo(playPosition)
                        } else {
                            if (playPosition == firstVisibleItem) {
                                return
                            }
                            playPosition = firstVisibleItem
                            videoListAdapter.playVideo(playPosition)
                        }

                    }else if (dy < 0) {
                        if (firstView.height + firstView.top >= firstView.height * 2 / 3) {
                            //video stop or play second
                            if (playPosition == firstVisibleItem) {
                                return
                            }
                            playPosition = firstVisibleItem
                            videoListAdapter.playVideo(playPosition)
                        } else {
                            if (playPosition == firstVisibleItem + 1) {
                                return
                            }
                            playPosition = firstVisibleItem + 1
                            videoListAdapter.playVideo(playPosition)
                        }
                    }else {
                        playPosition = 0
                        videoListAdapter.playVideo(playPosition)
                    }
                }
            }

        })*/

        imgBack.setOnClickListener(View.OnClickListener { finish() })

    }
}
