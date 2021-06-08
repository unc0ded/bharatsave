package com.dev.`in`.drogon.view.story

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.bumptech.glide.Glide
import com.dev.`in`.drogon.R
import com.dev.`in`.drogon.custom_widget.story.PausableProgressView
import com.dev.`in`.drogon.model.StoryModel
import com.dev.`in`.drogon.view.register.RegistrationActivity
import kotlinx.android.synthetic.main.activity_story_container.*

class StoryContainerActivity : AppCompatActivity() {

    private val storyList = ArrayList<StoryModel>()
    private var currentActiveStoryIndex = -1

    private val PROGRESS_BAR_PARAMS = LinearLayout.LayoutParams(
        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
        this.setMargins(10, 10, 10, 10)
    }

    private fun callback(index: Int): PausableProgressView.ProgressCallback {
        return object : PausableProgressView.ProgressCallback {
            override fun onStartProgress() {
                currentActiveStoryIndex = index
                if (currentActiveStoryIndex == 0) {
                    loadStory(
                        storyList[currentActiveStoryIndex].imageUrl,
                        storyList[currentActiveStoryIndex].actionButtonText)
                }
            }

            override fun onFinishProgress() {
                val nextIndex: Int = currentActiveStoryIndex + 1
                if (nextIndex <= storyList.size - 1) {
                    getPausableProgressViewAt(nextIndex)?.startProgress()
                    loadStory(storyList[nextIndex].imageUrl, storyList[nextIndex].actionButtonText)
                } else {
                    startActivity(Intent(this@StoryContainerActivity, RegistrationActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_container)

        // just for testing
        storyList.add(
            StoryModel(
                "https://www.linkpicture.com/q/test_story_1.png",
                "Story 1"
            )
        )
        storyList.add(
            StoryModel(
                "https://www.linkpicture.com/q/test_story_2.jpg",
                "Story 2"
            )
        )
        storyList.add(
            StoryModel(
                "https://user-images.githubusercontent.com/46667021/97115713-2d377900-171e-11eb-8e97-06a56d2b566f.jpg",
                "Story 3"
            )
        )

        setupViews()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() {
        pausable_progress_container.weightSum = (storyList.size).toFloat()
        for (i in 0 until storyList.size) {
            val pausableProgressView = PausableProgressView(this).apply {
                this.layoutParams = PROGRESS_BAR_PARAMS
                this.setCallback(callback(i))
            }
            if (i == 0) {
                pausableProgressView.startProgress()
            }
            pausable_progress_container.addView(pausableProgressView)

        }

        iv_story.setOnTouchListener { v, event ->
            if (event != null) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        getPausableProgressViewAt(currentActiveStoryIndex)?.pauseProgress()
                    }
                    MotionEvent.ACTION_UP -> {
                        getPausableProgressViewAt(currentActiveStoryIndex)?.resumeProgress()
                    }
                }
            }
            true
        }

        btn_next.setOnClickListener {
            if (currentActiveStoryIndex >= storyList.size) {
                getPausableProgressViewAt(storyList.size - 1)?.finishProgress(true)
            } else {
                getPausableProgressViewAt(currentActiveStoryIndex)?.finishProgress(true)
            }
        }

        btn_back.setOnClickListener {
            if (currentActiveStoryIndex == 0) {
                    getPausableProgressViewAt(0)?.startProgress()
            } else {
                getPausableProgressViewAt(currentActiveStoryIndex)?.finishProgress(false)
                getPausableProgressViewAt(currentActiveStoryIndex - 1)?.startProgress()
            }
        }
    }

    private fun getPausableProgressViewAt(index: Int) : PausableProgressView? {
        if (pausable_progress_container.childCount != storyList.size || index < 0 || index >= storyList.size) {
            return null
        }
        return (pausable_progress_container[index] as PausableProgressView)
    }

    // TODO: improve this
    private fun loadStory(imageUrl: String, actionButtonText: String) {
        Glide.with(this@StoryContainerActivity)
            .load(imageUrl)
            .into(iv_story)
        btn_story_action.text = actionButtonText
        // TODO: handle button on click actions
    }

    override fun onDestroy() {
        super.onDestroy()
        for(i in 0 until storyList.size) {
            getPausableProgressViewAt(i)?.clear()
        }
    }

}