package com.ivanova.pexelsapp.RecyclerViews

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class PhotoItemDecoration(
    private val itemMarginBottom: Int,
    private val itemMarginHorizontal: Int
) :
    RecyclerView.ItemDecoration() {

    private fun isRightItem(
        view: View,
    ): Boolean {
        val layoutParams: StaggeredGridLayoutManager.LayoutParams =
            view.layoutParams as StaggeredGridLayoutManager.LayoutParams
        return layoutParams.spanIndex == 1
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            bottom = itemMarginBottom

            right = if (isRightItem(view)) {
                0
            } else {
                itemMarginHorizontal
            }

            left = if (!isRightItem(view)) {
                0
            } else {
                itemMarginHorizontal
            }
        }
    }
}