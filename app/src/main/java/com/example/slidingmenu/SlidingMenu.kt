package com.example.slidingmenu


import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView

class SlidingMenu @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private val menuWidth: Int

    private lateinit var contentView: View

    private lateinit var menuView: View

    // handle fling gesture
    private var gestureDetector: GestureDetector

    private var isMenuOpen = false

    private var isIntercept = false

    private val gestureListener: GestureDetector.OnGestureListener =
            object : SimpleOnGestureListener() {
                override fun onFling(
                        e1: MotionEvent,
                        e2: MotionEvent,
                        velocityX: Float,
                        velocityY: Float
                ): Boolean {
                    Log.e("TAG", "velocityX -> $velocityX")
                    if (isMenuOpen) {
                        if (velocityX < 0) {
                            closeMenu()
                            return true
                        }
                    } else {
                        if (velocityX > 0) {
                            openMenu()
                            return true
                        }
                    }
                    return super.onFling(e1, e2, velocityX, velocityY)
                }
            }

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenu)
        val rightMargin = array.getDimension(
                R.styleable.SlidingMenu_menuRightMargin, dip2px(context, 50.0f)
        )

        menuWidth = (getScreenWidth(context) - rightMargin).toInt()
        array.recycle()

        gestureDetector = GestureDetector(context, gestureListener)
    }


    /**
     * Recalculate the menu and content views' widths.
     * This has to be done in code at runtime because we want to use the screen size.
     */
    override fun onFinishInflate() {
        super.onFinishInflate()

        val container = getChildAt(0) as ViewGroup

        val childCount = container.childCount
        if (childCount != 2) {
            throw RuntimeException("Must contain 2 children views")
        }

        menuView = container.getChildAt(0)
        val menuParams = menuView.layoutParams
        menuParams.width = menuWidth
        menuView.layoutParams = menuParams


        contentView = container.getChildAt(1)
        val contentParams = contentView.layoutParams
        contentParams.width = getScreenWidth(context)
        contentView.layoutParams = contentParams
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        // scroll to content view when start
        smoothScrollTo(menuWidth, 0)
    }


    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        val scrolledPercent = 1f * l / menuWidth
        val contentScale = 0.7f + 0.3f * scrolledPercent
        contentView.pivotX = 0f
        contentView.pivotY = contentView.measuredHeight / 2.toFloat()
        contentView.scaleX = contentScale
        contentView.scaleY = contentScale

        val menuAlpha = 0.5f + (1 - scrolledPercent) * 0.5f
        menuView.alpha = menuAlpha
        val menuScale = 0.7f + (1 - scrolledPercent) * 0.3f
        menuView.scaleX = menuScale
        menuView.scaleY = menuScale
    }


    override fun onTouchEvent(ev: MotionEvent): Boolean {

        if (isIntercept) {
            return true
        }

        if (gestureDetector.onTouchEvent(ev)) {
            return true
        }


        if (ev.action == MotionEvent.ACTION_UP) {
            val currentX = scrollX

            if (currentX > menuWidth / 2) {
                closeMenu()
            } else {
                openMenu()
            }

            return true
        }
        return super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        isIntercept = false
        if (isMenuOpen) {
            val currentX = ev.x
            if (currentX > menuWidth) {
                closeMenu()
                isIntercept = true
                return true
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    private fun openMenu() {
        smoothScrollTo(0, 0)
        isMenuOpen = true
    }

    private fun closeMenu() {
        smoothScrollTo(menuWidth, 0)
        isMenuOpen = false
    }
}
