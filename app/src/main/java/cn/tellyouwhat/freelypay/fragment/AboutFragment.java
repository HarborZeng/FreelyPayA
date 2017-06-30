package cn.tellyouwhat.freelypay.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.tellyouwhat.freelypay.R;
import cn.tellyouwhat.freelypay.activity.IntroActivity;
import cn.tellyouwhat.freelypay.util.PhoneInfoProvider;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Harbor-Laptop on 2017/6/14.
 */

public class AboutFragment extends Fragment {
    private static Fragment instance;

    @BindView(R.id.text_view_version)
    TextView mVersionTextView;

    private Unbinder unbinder;

    public static Fragment getInstance() {
        if (instance == null) {
            synchronized (AboutFragment.class) {
                if (instance == null) {
                    instance = new Fragment();
                }
            }
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        unbinder = ButterKnife.bind(this, view);
        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), MODE_PRIVATE);
            mVersionTextView.setText(packageInfo.versionName + " (" + packageInfo.versionCode + ")");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        ListView listView = (ListView) view.findViewById(R.id.list);

        listView.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
                new String[]{"打赏开发者",
                        "主要开发者",
                        "联系我们",
                        "去酷安给5星",
                        "版权信息",
                        "功能介绍"}));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        new AlertDialog.Builder(getContext())
                                .setTitle("选择捐赠方式")
                                .setView(R.layout.pay_method)
                                .show();
                        break;
                    case 1:
                        Snackbar.make(view, "HarborZeng", Snackbar.LENGTH_LONG).show();
                        break;
                    case 2:
                        Snackbar.make(view, "feedback@tellyouwhat.cn", Snackbar.LENGTH_LONG)
                                .setAction("发送", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String model = Build.MODEL;
                                        Intent sendEmailIntent = new Intent(Intent.ACTION_SENDTO);
                                        sendEmailIntent.setData(Uri.parse("mailto:feedback@tellyouwhat.cn"));
                                        sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, "来自" + model + "支付助手用户的反馈");
                                        sendEmailIntent.putExtra(Intent.EXTRA_TEXT,
                                                "您好，\n\n\n\n\n\n" +
                                                        PhoneInfoProvider.getInstance()
                                                                .getBuildInfo());
                                        startActivity(sendEmailIntent);
                                    }
                                }).show();
                        break;
                    case 3:
                        gotoCoolAPK();
                        break;
                    case 4:
                        new LibsBuilder()
                                //provide a style (optional) (LIGHT, DARK, LIGHT_DARK_TOOLBAR)
                                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                //start the activity
                                .start(getActivity());
                        break;
                    case 5:
                        startActivity(new Intent(getContext(), IntroActivity.class));
                        break;
                    default:
                        Snackbar.make(view, "用户不可能看到这个, 否则开发者吃屎", Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void gotoCoolAPK() {
        String packageName = getActivity().getPackageName();
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri)
                .setPackage("com.coolapk.market")  //指定应用市场
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "您的手机并没有安装酷安", Toast.LENGTH_LONG).show();
            uri = Uri.parse("http://www.coolapk.com/apk/" + packageName);
            Intent gotoWebsite = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(gotoWebsite);
        }
    }

    @OnClick(R.id.button_feedback)
    void feedback() {
        gotoCoolAPK();
    }
}
