package com.example.jingbiaozhen.androidutils.recyclerview;
/*
 * Created by jingbiaozhen on 2018/5/18.
 * GridLayoutManager or StaggeredGridLayoutManager spacing
 *  int spanCount = 3; // 3 columns
 * int spacing = 50; // 50px
 * boolean includeEdge = false;
 * recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
 **/

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ImageItemDecoration extends RecyclerView.ItemDecoration
{
    private int spanCount;

    private int spacing;

    private boolean includeEdge;

    public ImageItemDecoration(int spanCount, int spacing, boolean includeEdge)
    {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column

        if (includeEdge)
        {
            outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount)
            { // top edge
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // item bottom
        }
        else
        {
            outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f / spanCount)
                                                                          // * spacing)
            if (position >= spanCount)
            {
                outRect.top = spacing; // item top
            }
        }
    }
}
