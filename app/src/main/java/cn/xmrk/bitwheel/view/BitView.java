package cn.xmrk.bitwheel.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

import cn.xmrk.bitwheel.R;
import cn.xmrk.bitwheel.pojo.BitWheelInfo;


/**
 * 作者：请叫我百米冲刺 on 2016/10/17 14:34
 * 邮箱：mail@hezhilin.cc
 */

public class BitView extends View {

    private Object obj = new Object();

    /**
     * 两种种状态
     * 0 表示未被选中
     * 1 表示选中
     **/
    private static final int TYPE_UNCHECKED = 0;


    private static final int TYPE_CHECKED = 1;


    /**
     * 控件要绘制的宽度
     **/
    private int mDrawWidth;

    /**
     * 避免重复计算宽度
     **/
    private boolean hasMeasured = false;

    /**
     * 500毫秒内的点击被认为是点击事件
     **/
    private long timeClick = 500;

    /**
     * 保存点击的时间
     **/
    private long nowClick;

    /**
     * 被选中的position,默认第0个选中
     **/
    private int checkPosition = 0;


    /**
     * 绘画区域的图片。这边会将各个小图片拼接成一张图片
     **/
    private Bitmap fontBitmap;


    private List<BitWheelInfo> bitInfos;


    /**
     * 选项的个数
     **/
    private int mItemCount;

    /**
     * 保存的画布
     **/
    private Canvas mCanvas;

    /**
     * 非选中和选择的颜色色块
     */
    private int[] mColors = new int[]{0xffffeb8c, 0xffffe670};

    /**
     * 触摸点的坐标位置
     **/
    private float touchX;
    private float touchY;

    /**
     * 绘制盘块的范围
     */
    private RectF mRange = new RectF();

    /**
     * 绘制盘快的画笔
     */
    private Paint mArcPaint;

    /**
     * 绘制文字的画笔
     */
    private TextPaint mTextPaint;

    /**
     * 绘制分割线的画笔
     */
    private Paint mLinePaint;

    /**
     * 圆形背景的画笔
     **/
    private Paint mBackColorPaint;

    /**
     * 圆盘角度
     **/
    private volatile float mStartAngle = 0;

    /**
     * 控件的中心位置,处于中心位置。x和y是相等的
     */
    private int mCenter;

    /**
     * 圆形背景的宽度，这边是在1080p下的780
     **/
    private float mBackColorWidth = 780;

    /**
     * 内圈画盘小圆的宽度，这边是在1080p下的730，绘画区域
     **/
    private float mRangeWidth = 730;

    /**
     * 里面的小图大小，这边是1080p下的115
     **/
    private float mLitterBitWidth = 115;

    /**
     * 文字的大小为26px
     */
    private float mTextSize = 26;

    /**
     * 中心图片信息
     **/
    private Bitmap mCheckBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_qiandao_lingqu);
    private float mCheckBitmapWidth = 270;//1080p下的270
    private RectF mCheckBitmapRect = new RectF();//图片绘制区域


    public BitView(Context context) {
        this(context, null);
    }


    public BitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置控件为正方形
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        // 中心点
        mCenter = width / 2;
        mDrawWidth = width;
        //屏幕的宽度
        setMeasuredDimension(width, width);
        init();
    }

    /**
     * 当前切图的比例都是在1080p下进行的，所以这边的比例就是1080的
     * <p>
     * mDrawWidth 表示为控件的宽度
     **/
    private float getDrawWidth(float width) {
        return width * mDrawWidth / 1080f;
    }


    public void setBitInfos(List<BitWheelInfo> bitInfos) {
        this.bitInfos = bitInfos;
        mItemCount = this.bitInfos.size();
        onDrawInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mCanvas = canvas;
        drawCanvas();
    }

    private void init() {
        if (!hasMeasured) {
            //得到各个比例下的图片大小
            mBackColorWidth = getDrawWidth(mBackColorWidth);
            mRangeWidth = getDrawWidth(mRangeWidth);
            mCheckBitmapWidth = getDrawWidth(mCheckBitmapWidth);
            mLitterBitWidth = getDrawWidth(mLitterBitWidth);
            mTextSize = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, mTextSize / 3, getResources().getDisplayMetrics());
            hasMeasured = true;
        }
        // 初始化绘制圆弧的画笔
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);
        // 初始化绘制文字的画笔
        mTextPaint = new TextPaint();
        mTextPaint.setColor(0xff222222);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        //圆形背景的画笔
        mBackColorPaint = new Paint();
        mBackColorPaint.setColor(0xffae6112);
        mBackColorPaint.setAntiAlias(true);
        mBackColorPaint.setDither(true);
        //未选中的分割线的画笔
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setDither(true);
        mLinePaint.setStrokeWidth(dip2px(1f));
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(0xffff9406);
        // 内圈画盘
        mRange = new RectF(mCenter - mRangeWidth / 2, mCenter - mRangeWidth / 2, mCenter + mRangeWidth / 2, mCenter + mRangeWidth / 2);
        //中心图片绘制区域
        mCheckBitmapRect = new RectF(mCenter - mCheckBitmapWidth / 2, mCenter - mCheckBitmapWidth / 2, mCenter + mCheckBitmapWidth / 2, mCenter + mCheckBitmapWidth / 2);
    }

    /**
     * 对每个选项进行绘制
     **/
    private Bitmap getDrawItemBitmap(float tmpAngle, float sweepAngle, int position) {
        //是否需要重新绘制
        boolean needToNew = false;
        //根据状态判断是否需要重新绘制
        if (checkPosition == position && bitInfos.get(position).info.bitmapType == TYPE_UNCHECKED) {//这次选中，上次没选中的要更新
            needToNew = true;
            bitInfos.get(position).info.bitmapType = TYPE_CHECKED;
        } else if (checkPosition != position && bitInfos.get(position).info.bitmapType == TYPE_CHECKED) {//这次没选中，上次选中的要更新
            needToNew = true;
            bitInfos.get(position).info.bitmapType = TYPE_UNCHECKED;
        }
        if (bitInfos.get(position).info.itemBitmap == null || needToNew) {
            //选择背景颜色
            if (checkPosition == position) {
                mArcPaint.setColor(mColors[1]);
            } else {
                mArcPaint.setColor(mColors[0]);
            }
            //绘制每一个小块
            bitInfos.get(position).info.itemBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas itemCanvas = new Canvas(bitInfos.get(position).info.itemBitmap);
            //根据角度进行同步旋转
            itemCanvas.rotate(tmpAngle, mCenter, mCenter);
            //绘制背景颜色,从最上边开始画
            itemCanvas.drawArc(mRange, -sweepAngle / 2 - 90, sweepAngle, true, mArcPaint);
            //绘制小图片和文本，因为一起画好画点
            drawIconAndText(position, itemCanvas);
            //绘制分割线，这里保证没一个小块都有一条分割线，分割线的位置是在最右侧
            drawCanvasLine(itemCanvas);
        } else {
            Canvas itemCanvas = new Canvas(bitInfos.get(position).info.itemBitmap);
            //根据角度进行同步旋转
            itemCanvas.rotate(tmpAngle, mCenter, mCenter);
        }
        return bitInfos.get(position).info.itemBitmap;
    }

    private void drawCanvas() {
        if (bitInfos == null || bitInfos.size() == 0) {
            return;
        }
        //绘制背景图
        mCanvas.drawCircle(mCenter, mCenter, mBackColorWidth / 2, mBackColorPaint);
        //画前景图片
        mCanvas.drawBitmap(getFontBitmap(), 0, 0, null);
        //绘制中心图片
        mCanvas.drawBitmap(mCheckBitmap, null, mCheckBitmapRect, null);
    }

    //绘制前景图片,这里包含的是图片信息和文字信息,还有背景圆弧背景展示
    private Bitmap getFontBitmap() {
        fontBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(fontBitmap);
        //根据角度进行同步旋转
        canvas.rotate(mStartAngle, mCenter, mCenter);
        float tmpAngle = 0;
        float sweepAngle = (float) (360 / mItemCount);
        for (int i = 0; i < mItemCount; i++) {
            //这边可以得到新的bitmap
            canvas.drawBitmap(getDrawItemBitmap(tmpAngle, sweepAngle, i), 0, 0, null);
            tmpAngle += sweepAngle;
        }
        return fontBitmap;
    }

    /**
     * 绘制分割线
     **/
    private void drawCanvasLine(Canvas canvas) {
        if (mItemCount == 1) {
            return;
        }
        //计算终点角度，这边从最上边为0度开始计算,则角度在0-180之间，给0.5f的偏移
        float range = 360f / mItemCount / 2 - 0.5f;
        //计算半径的长度
        float radio = (mRange.right - mRange.left) / 2;
        //计算终点坐标
        float x = 0;
        float y = 0;
        if (range < 90) {
            x = (float) (radio * Math.sin(Math.toRadians(range)));
            y = (float) (radio * Math.cos(Math.toRadians(range)));

            x += mCenter;
            y = mCenter - y;
        } else {
            x = (float) (radio * Math.sin(Math.toRadians(180 - range)));
            y = (float) (radio * Math.cos(Math.toRadians(180 - range)));

            x += mCenter;
            y += mCenter;
        }
        canvas.drawLine(mCenter, mCenter, x, y, mLinePaint);
    }

    /**
     * 根据当前旋转的mStartAngle计算当前滚动到的区域
     *
     * @param startAngle
     */
    public int calInExactArea(float startAngle) {
        float size = 360f / mItemCount;
        // 确保rotate是正的，且在0-360度之间
        float rotate = (startAngle % 360.0f + 360.0f) % 360.0f;

        for (int i = 0; i < mItemCount; i++) {
            // 每个的中奖范围
            if (i == 0) {
                if ((rotate > 360 - size / 2) || (rotate < size / 2)) {
                    return i;
                }
            } else {
                float from = size * (i - 1) + size / 2;
                float to = from + size;
                if ((rotate > from) && (rotate < to)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 绘制小图片和文字
     *
     * @param i
     */
    private void drawIconAndText(int i, Canvas canvas) {
        //根据的标注，比例为115/730
        float rt = mLitterBitWidth / mRangeWidth;
        //计算绘画区域的直径
        int mRadius = (int) (mRange.right - mRange.left);
        int imgWidth = (int) (mRadius * rt);
        //获取中心点坐标
        int x = mCenter;
        //这边让图片从四分之一出开始画
        int y = (int) (mCenter - mRadius / 2 + (float) mRadius / 2 * 1 / 4f);
        //确定小图片的区域
        Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth
                / 2, y + imgWidth / 2);
        //将图片画上去
        canvas.drawBitmap(bitInfos.get(i).bitmap, null, rect, null);
        //绘制文本
        if (!TextUtils.isEmpty(bitInfos.get(i).text)) {
            //最大字数限制为8个字
            if (bitInfos.get(i).text.length() > 8) {
                bitInfos.get(i).text = bitInfos.get(i).text.substring(0, 8);
            }
            StaticLayout textLayout = new StaticLayout(bitInfos.get(i).text, mTextPaint, imgWidth, Layout.Alignment.ALIGN_NORMAL, 1f, 0, false);
            canvas.translate(mCenter, rect.bottom + dip2px(2));
            textLayout.draw(canvas);
            //画完之后移动回来
            canvas.translate(-mCenter, -(rect.bottom + dip2px(2)));
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //判断按点是否在圆内
        if (!mRange.contains(event.getX(), event.getY())) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = event.getX();
                touchY = event.getY();
                //按下时的时间
                nowClick = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                //得到旋转的角度
                float arc = getRoundArc(touchX, touchY, moveX, moveY);
                //重新赋值
                touchX = moveX;
                touchY = moveY;
                //起始角度变化下，然后进行重新绘制
                mStartAngle += arc;
                onDrawInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (System.currentTimeMillis() - nowClick <= timeClick) {//此时为点击事件
                    //落点的角度加上偏移量基于初始的点的位置
                    checkPosition = calInExactArea(getRoundArc(event.getX(), event.getY()) - mStartAngle);
                    onDrawInvalidate();
                    if (mOnWheelCheckListener != null) {
                        mOnWheelCheckListener.onCheck(checkPosition);
                    }
                }
                break;
        }
        return true;
    }

    //根据落点计算角度
    private float getRoundArc(float upX, float upY) {
        float arc = 0;
        //首先计算三边的长度
        float a = (float) Math.sqrt(Math.pow(mCenter - mCenter, 2) + Math.pow(0 - mCenter, 2));
        float b = (float) Math.sqrt(Math.pow(upX - mCenter, 2) + Math.pow(upY - mCenter, 2));
        float c = (float) Math.sqrt(Math.pow(upX - mCenter, 2) + Math.pow(upY - 0, 2));
        //判断是否为三角形
        if (a + b > c) {//两边之和大于第三边为三角形
            /**
             * 接下来计算角度
             *
             * acos((a2+b2-c2)/2ab)
             *
             * **/
            arc = (float) (Math.acos((Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2)) / (2 * a * b)) * 180 / Math.PI);

            //判断是大于左边还是右边，也就是180以内还是以外
            if (upX < mCenter) {//此时是180以外的
                arc = 360 - arc;
            }
        } else {//上下边界的问题
            if (upX == mCenter) {
                if (upY < mCenter) {
                    arc = 0;
                } else {
                    arc = 180;
                }
            }
        }
        return arc;
    }

    //根据三点的坐标计算旋转的角度
    private float getRoundArc(float startX, float startY, float endX, float endY) {
        float arc = 0;
        //首先计算三边的长度
        float a = (float) Math.sqrt(Math.pow(startX - mCenter, 2) + Math.pow(startY - mCenter, 2));
        float b = (float) Math.sqrt(Math.pow(endX - mCenter, 2) + Math.pow(endY - mCenter, 2));
        float c = (float) Math.sqrt(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2));
        //判断是否为三角形
        if (a + b > c) {//两边之和大于第三边为三角形
            /**
             * 接下来计算角度
             *
             * acos((a2+b2-c2)/2ab)
             *
             * **/
            arc = (float) (Math.acos((Math.pow(a, 2) + Math.pow(b, 2) - Math.pow(c, 2)) / (2 * a * b)) * 180 / Math.PI);

            if (startX <= mCenter && endX >= mCenter && startY < mCenter && endY < mCenter) {//上边顺时针越界，不管他
            } else if (startX >= mCenter && endX <= mCenter && startY < mCenter && endY < mCenter) {//上边逆时针越界
                arc = -arc;
            } else if (startX <= mCenter && endX >= mCenter && startY > mCenter && endY > mCenter) {//下边逆时针越界
                arc = -arc;
            } else if (startX <= mCenter && endX >= mCenter && startY < mCenter && endY < mCenter) {//下边顺时针越界，不管他
            } else if (endX >= mCenter && startX >= mCenter) {//这个时候表示在右半区
                if (startY > endY) {
                    arc = -arc;
                }
            } else if (endX < mCenter && startX < mCenter) {//此时在左半区
                if (startY < endY) {
                    arc = -arc;
                }
            }
        }
        if (Math.abs(arc) >= 0 && Math.abs(arc) <= 180) {//主要解决nan的问题
            return arc;
        } else {
            return 0;
        }
    }

    /**
     * dp转像素
     *
     * @param dpValue
     * @return
     */
    public final int dip2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getContext().getResources().getDisplayMetrics());
    }

    /**
     * 刷新画布
     **/
    private void onDrawInvalidate() {
        synchronized (obj) {
            invalidate();
        }
    }

    public int getCheckPosition() {
        return checkPosition;
    }


    private OnWheelCheckListener mOnWheelCheckListener;

    public void setOnWheelCheckListener(OnWheelCheckListener mOnWheelCheckListener) {
        this.mOnWheelCheckListener = mOnWheelCheckListener;
    }

    public static interface OnWheelCheckListener {
        void onCheck(int position);
    }


}
