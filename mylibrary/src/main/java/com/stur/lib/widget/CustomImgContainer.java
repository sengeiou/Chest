package com.stur.lib.widget;

import com.stur.lib.Log;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CustomImgContainer extends ViewGroup
{
    public CustomImgContainer(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public CustomImgContainer(Context context)
    {
        super(context);
    }

    public CustomImgContainer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
    * 鐠侊紕鐣婚幍锟介張濉乭ildView閻ㄥ嫬顔旀惔锕�鎷版妯哄 閻掕泛鎮楅弽瑙勫祦ChildView閻ㄥ嫯顓哥粻妤冪波閺嬫粣绱濈拋鍓х枂閼奉亜绻侀惃鍕啍閸滃矂鐝�
    */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        /**
        * 閼惧嘲绶卞顥糹ewGroup娑撳﹦楠囩�圭懓娅掓稉鍝勫従閹恒劏宕橀惃鍕啍閸滃矂鐝敍灞间簰閸欏﹨顓哥粻妤伳佸锟�
        */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        Log.e(this, (heightMode == MeasureSpec.UNSPECIFIED) + "," + sizeHeight
                + "," + getLayoutParams().height);

        // 鐠侊紕鐣婚崙鐑樺閺堝娈慶hildView閻ㄥ嫬顔旈崪宀勭彯
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        /**
        * 鐠佹澘缍嶆俊鍌涚亯閺勭椇rap_content閺勵垵顔曠純顔炬畱鐎硅棄鎷版锟�
        */
        int width = 0;
        int height = 0;

        int cCount = getChildCount();

        int cWidth = 0;
        int cHeight = 0;
        MarginLayoutParams cParams = null;

        // 閻€劋绨拋锛勭暬瀹革箒绔熸稉銈勯嚋childView閻ㄥ嫰鐝惔锟�
        int lHeight = 0;
        // 閻€劋绨拋锛勭暬閸欏疇绔熸稉銈勯嚋childView閻ㄥ嫰鐝惔锔肩礉閺堬拷缂佸牓鐝惔锕�褰囨禍宀冿拷鍛闂傛潙銇囬崐锟�
        int rHeight = 0;

        // 閻€劋绨拋锛勭暬娑撳﹨绔熸稉銈勯嚋childView閻ㄥ嫬顔旀惔锟�
        int tWidth = 0;
        // 閻€劋绨拋锛勭暬娑撳娼版稉銈勯嚋childiew閻ㄥ嫬顔旀惔锔肩礉閺堬拷缂佸牆顔旀惔锕�褰囨禍宀冿拷鍛闂傛潙銇囬崐锟�
        int bWidth = 0;

        /**
        * 閺嶈宓乧hildView鐠侊紕鐣婚惃鍕毉閻ㄥ嫬顔旈崪宀勭彯閿涘奔浜掗崣濠咁啎缂冾喚娈憁argin鐠侊紕鐣荤�圭懓娅掗惃鍕啍閸滃矂鐝敍灞煎瘜鐟曚胶鏁ゆ禍搴☆啇閸ｃ劍妲竪arp_content閺冿拷
        */
        for (int i = 0; i < cCount; i++)
        {
            View childView = getChildAt(i);
            cWidth = childView.getMeasuredWidth();
            cHeight = childView.getMeasuredHeight();
            cParams = (MarginLayoutParams) childView.getLayoutParams();

            // 娑撳﹪娼版稉銈勯嚋childView
            if (i == 0 || i == 1)
            {
                tWidth += cWidth + cParams.leftMargin + cParams.rightMargin;
            }

            if (i == 2 || i == 3)
            {
                bWidth += cWidth + cParams.leftMargin + cParams.rightMargin;
            }

            if (i == 0 || i == 2)
            {
                lHeight += cHeight + cParams.topMargin + cParams.bottomMargin;
            }

            if (i == 1 || i == 3)
            {
                rHeight += cHeight + cParams.topMargin + cParams.bottomMargin;
            }

        }

        width = Math.max(tWidth, bWidth);
        height = Math.max(lHeight, rHeight);

        /**
        * 婵″倹鐏夐弰鐥簉ap_content鐠佸墽鐤嗘稉鐑樺灉娴狀剝顓哥粻妤冩畱閸婏拷
        * 閸氾箑鍨敍姘辨纯閹恒儴顔曠純顔昏礋閻栬泛顔愰崳銊吀缁犳娈戦崐锟�
        */
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
                : width, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
                : height);
    }

    // abstract method in viewgroup
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        int cCount = getChildCount();
        int cWidth = 0;
        int cHeight = 0;
        MarginLayoutParams cParams = null;
        /**
        * 闁秴宸婚幍锟介張濉﹉ildView閺嶈宓侀崗璺侯啍閸滃矂鐝敍灞间簰閸欏argin鏉╂稖顢戠敮鍐ㄧ湰
        */
        for (int i = 0; i < cCount; i++)
        {
            View childView = getChildAt(i);
            cWidth = childView.getMeasuredWidth();
            cHeight = childView.getMeasuredHeight();
            cParams = (MarginLayoutParams) childView.getLayoutParams();

            int cl = 0, ct = 0, cr = 0, cb = 0;

            switch (i)
            {
            case 0:
                cl = cParams.leftMargin;
                ct = cParams.topMargin;
                break;
            case 1:
                cl = getWidth() - cWidth - cParams.leftMargin
                        - cParams.rightMargin;
                ct = cParams.topMargin;

                break;
            case 2:
                cl = cParams.leftMargin;
                ct = getHeight() - cHeight - cParams.bottomMargin;
                break;
            case 3:
                cl = getWidth() - cWidth - cParams.leftMargin
                        - cParams.rightMargin;
                ct = getHeight() - cHeight - cParams.bottomMargin;
                break;

            }
            cr = cl + cWidth;
            cb = cHeight + ct;
            childView.layout(cl, ct, cr, cb);
        }

    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams()
    {
        Log.e(this, "generateDefaultLayoutParams");
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(
            ViewGroup.LayoutParams p)
    {
        Log.e(this, "generateLayoutParams p");
        return new MarginLayoutParams(p);
    }

    /*
    * if (heightMode == MeasureSpec.UNSPECIFIED)
        {
            int tmpHeight = 0 ;
            LayoutParams lp = getLayoutParams();
            if (lp.height == LayoutParams.MATCH_PARENT)
            {
                Rect outRect = new Rect();
                getWindowVisibleDisplayFrame(outRect);
                tmpHeight = outRect.height();
            }else
            {
                tmpHeight = getLayoutParams().height ;
            }
            height = Math.max(height, tmpHeight);

        }
    */
}
