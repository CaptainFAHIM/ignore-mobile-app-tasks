package com.example.photogalleryapp

import android.graphics.Matrix
import android.graphics.PointF
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class FullscreenActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var matrix: Matrix
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    
    private var mode = NONE
    private var last = PointF()
    private var start = PointF()
    private var minScale = 1.0f
    private var maxScale = 4.0f
    private var saveScale = 1.0f



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        initializeViews()
        setupScaleGestureDetector()
        loadImage()
    }

    private fun initializeViews() {
        imageView = findViewById(R.id.ivFullscreen)
        matrix = Matrix()
        imageView.imageMatrix = matrix
        imageView.scaleType = ImageView.ScaleType.MATRIX
    }

    private fun setupScaleGestureDetector() {
        scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                var scaleFactor = detector.scaleFactor
                val originalScale = saveScale
                saveScale *= scaleFactor
                
                if (saveScale > maxScale) {
                    saveScale = maxScale
                    scaleFactor = maxScale / originalScale
                } else if (saveScale < minScale) {
                    saveScale = minScale
                    scaleFactor = minScale / originalScale
                }
                
                matrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                fixTrans()
                imageView.imageMatrix = matrix
                return true
            }
        })
    }

    private fun loadImage() {
        val imageResource = intent.getIntExtra("imageResource", 0)
        if (imageResource != 0) {
            imageView.setImageResource(imageResource)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        
        val current = PointF(event.x, event.y)
        
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                last.set(current)
                start.set(last)
                mode = DRAG
            }
            MotionEvent.ACTION_MOVE -> {
                if (mode == DRAG) {
                    val deltaX = current.x - last.x
                    val deltaY = current.y - last.y
                    
                    matrix.postTranslate(deltaX, deltaY)
                    fixTrans()
                    imageView.imageMatrix = matrix
                    last.set(current.x, current.y)
                }
            }
            MotionEvent.ACTION_UP -> {
                mode = NONE
            }
            MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
            }
        }
        
        return true
    }

    private fun fixTrans() {
        matrix.getValues(floatArray)
        val transX = floatArray[Matrix.MTRANS_X]
        val transY = floatArray[Matrix.MTRANS_Y]
        
        // Simple boundary checking to prevent excessive panning
        val scaledWidth = imageView.drawable.intrinsicWidth * saveScale
        val scaledHeight = imageView.drawable.intrinsicHeight * saveScale
        
        if (scaledWidth > imageView.width) {
            if (transX > 0) {
                matrix.postTranslate(-transX, 0f)
            } else if (transX < imageView.width - scaledWidth) {
                matrix.postTranslate(-(transX - (imageView.width - scaledWidth)), 0f)
            }
        }
        
        if (scaledHeight > imageView.height) {
            if (transY > 0) {
                matrix.postTranslate(0f, -transY)
            } else if (transY < imageView.height - scaledHeight) {
                matrix.postTranslate(0f, -(transY - (imageView.height - scaledHeight)))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_fullscreen, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_share -> {
                // Share functionality
                true
            }
            R.id.action_delete -> {
                // Delete functionality
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
        private val floatArray = FloatArray(9)
    }
}