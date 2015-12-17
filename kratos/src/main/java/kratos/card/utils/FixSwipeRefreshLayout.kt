package kratos.card.utils

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.View

public class FixSwipeRefreshLayout : SwipeRefreshLayout {

    public var target: View? = null

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attr: AttributeSet) : super(ctx, attr)

    override fun canChildScrollUp(): Boolean {
        return target?.canScrollVertically(-1) ?: super.canChildScrollUp()
    }
}