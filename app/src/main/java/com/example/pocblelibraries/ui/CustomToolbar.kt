package com.example.pocblelibraries.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.pocblelibraries.R
import com.example.pocblelibraries.databinding.CustomToolbarBinding

class CustomToolbar @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var binding: CustomToolbarBinding

    init {
        setupView()
    }

    var title: String? = null
        set(value) {
            field = value
            value?.let {
                binding.tvCustomToolbarTitle.text = value
            }
        }

    var hasLeftIcon = true
        set(value) {
            field = value
            binding.ivLeftIcon.visibility = if(value) View.VISIBLE else View.INVISIBLE
        }

    var hasRightIcon = false
        set(value) {
            field = value
            binding.ivRightIcon.visibility = if(value) View.VISIBLE else View.INVISIBLE
        }

    var leftIcon: Int? = null
        set(value) {
            field = value
            value?.let {
                binding.ivLeftIcon.setImageResource(it)
            }
        }

    var rightIcon: Int? = null
        set(value) {
            field = value
            value?.let {
                binding.ivRightIcon.setImageResource(it)
            }
        }

    var titleColor: Int? = null
        set(value) {
            field = value
            value?.let {
                binding.tvCustomToolbarTitle.setTextColor(it)
            }
        }

    var backgroundColor: Int? = null
        set(value) {
            field = value
            value?.let {
                binding.root.setBackgroundColor(it)
            }
        }

    var leftIconColor: Int? = null
        set(value) {
            field = value
            value?.let {
                binding.ivLeftIcon.setColorFilter(it)
            }
        }

    var rightIconColor: Int? = null
        set(value) {
            field = value
            value?.let {
                binding.ivRightIcon.setColorFilter(it)
            }
        }

    private var iconLeftClickListener: (() -> Unit)? = null
        set(value) {
            field = value
            value?.let { callback ->
                binding.ivLeftIcon.setOnClickListener { callback() }
            }
        }

    fun setLeftIconClickListener(callback: () -> Unit) {
        iconLeftClickListener = callback
    }

    private var iconRightClickListener: (() -> Unit)? = null
        set(value) {
            field = value
            value?.let { callback ->
                binding.ivRightIcon.setOnClickListener { callback() }
            }
        }

    fun setRightIconClickListener(callback: () -> Unit) {
        iconRightClickListener = callback
    }

    private fun setupView() {
        attrs?.run {
            context.obtainStyledAttributes(this, R.styleable.CustomToolbar).run {
                inflateLayout()
                title = getString(R.styleable.CustomToolbar_title)
                hasLeftIcon = getBoolean(R.styleable.CustomToolbar_hasLeftIcon, true)
                hasRightIcon = getBoolean(R.styleable.CustomToolbar_hasRightIcon, false)
                leftIcon = getResourceId(R.styleable.CustomToolbar_leftIcon, R.drawable.ic_arrow_left)
                rightIcon = getResourceId(R.styleable.CustomToolbar_rightIcon, R.drawable.ic_nav_drawer)
                titleColor = getColor(
                    R.styleable.CustomToolbar_titleColor,
                    ContextCompat.getColor(context,R.color.black)
                )
                backgroundColor = getColor(
                    R.styleable.CustomToolbar_toolbarBackgroundColor,
                    ContextCompat.getColor(context,R.color.white)
                )
                leftIconColor = getColor(
                    R.styleable.CustomToolbar_leftIconColor,
                    ContextCompat.getColor(context,R.color.black)
                )
                rightIconColor = getColor(
                    R.styleable.CustomToolbar_rightIconColor,
                    ContextCompat.getColor(context,R.color.black)
                )
                recycle()
            }
        } ?: run {
            inflateLayout()
        }
    }

    private fun inflateLayout() {
        binding = CustomToolbarBinding.bind(
            inflate(
                context,
                R.layout.custom_toolbar,
                this
            )
        )
    }
}