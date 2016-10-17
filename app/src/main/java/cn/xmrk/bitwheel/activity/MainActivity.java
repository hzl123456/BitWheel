package cn.xmrk.bitwheel.activity;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.xmrk.bitwheel.R;
import cn.xmrk.bitwheel.pojo.BitWheelInfo;
import cn.xmrk.bitwheel.view.BitView;

public class MainActivity extends Activity {

    private BitView mBitView;
    private TextView tvInfo;


    private List<BitWheelInfo> infos = new ArrayList<>();
    /**
     * 抽奖的文字
     */
    private String[] mStrs = new String[]{"王者荣耀礼包", "永恒礼包", "斗罗大陆礼包",
            "合金弹头礼包", "我叫Mt礼包"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getBitWheelInfos();
        tvInfo = (TextView) findViewById(R.id.tv_info);
        mBitView = (BitView) findViewById(R.id.bitview);
        mBitView.setBitInfos(infos);
        mBitView.setOnWheelCheckListener(new BitView.OnWheelCheckListener() {
            @Override
            public void onCheck(int position) {
                tvInfo.setText("选中了" + infos.get(position).text);
            }
        });
    }

    public void getBitWheelInfos() {
        for (int i = 0; i < mStrs.length; i++) {
            infos.add(new BitWheelInfo(mStrs[i], BitmapFactory.decodeResource(getResources(), R.mipmap.ic_youxi)));
        }
    }
}
