package cn.tellyouwhat.freelypay.activity;

import android.graphics.Color;

import com.stephentuso.welcome.BackgroundColor;
import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

import cn.tellyouwhat.freelypay.R;


/**
 * Created by Harbor-Laptop on 2017/6/13.
 * 首次启动和点击了“帮助”都会进入这个活动
 */

public class IntroActivity extends WelcomeActivity {

    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .canSkip(true)
                .backButtonSkips(false)
                .swipeToDismiss(true)
                .defaultBackgroundColor(new BackgroundColor(
                        Color.parseColor("#c41411")))
                .page(new BasicPage(R.drawable.advantage_of_qr_code,
                        "在支付宝申请收钱码",
                        "被支付后，享受提现免费，收钱免费等诸多福利"))

                .defaultBackgroundColor(new BackgroundColor(
                        Color.parseColor("#738ffe")))
                .page(new BasicPage(R.drawable.use_demo,
                        "切换到自动提现到卡模式\n\n",
                        "否则，收到的钱会被转到账户余额" +
                                "提现仍会收费"))

                .defaultBackgroundColor(R.color.md_teal_600_color_code)
                .page(new BasicPage(R.drawable.how_to_use,
                        "要么输入收钱码，要么选一张二维码",
                        "收钱码可通过微信等二维码识别软件识别出来，" +
                                "取URL最后面的一串字符\n\n" +
                                "二维码识别完毕后，收钱码会被填写在方法1中"))
                .showPrevButton(true)
                .build();
    }

    public static String welcomeKey() {
        return "201706301";
    }
}
