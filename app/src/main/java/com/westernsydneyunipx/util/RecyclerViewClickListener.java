package com.westernsydneyunipx.util;

import android.view.View;

/**
 * @author PA1810.
 */
public interface RecyclerViewClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
