package cn.tellyouwhat.freelypay.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.tellyouwhat.freelypay.R;
import cn.tellyouwhat.freelypay.util.AliPayUtil;
import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.data.QRCodeImage;
import jp.sourceforge.qrcode.exception.DecodingFailedException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Harbor-Laptop on 2017/6/12.
 * 主要是用来做转跳到支付宝的主页面
 */

public class PayFragment extends Fragment {
    private static final int REQUEST_ALBUM = 999;
    private static final String TAG = "PayFragment";
    private Unbinder unbinder;

    @BindView(R.id.image_view_qr_code)
    ImageView mQrCodeImageView;

    @BindView(R.id.edit_text_id_code)
    EditText mIdCodeEditText;

    @OnClick(R.id.button_pay)
    void pay() {
        String idCode = mIdCodeEditText.getText().toString().trim();
        Log.d(TAG, "pay: id: " + idCode);
        if (!TextUtils.isEmpty(idCode)) {
            if (AliPayUtil.hasInstalledAlipayClient(getContext())) {
                AliPayUtil.startAlipayClient(getActivity(),
                        mBusinessRadioButton.isChecked() ? idCode : idCode.toUpperCase());
            } else {
                Toast.makeText(getContext(), R.string.seems_no_alipay_installed, Toast.LENGTH_SHORT).show();
            }
        } else {
            mIdCodeEditText.setError(getString(R.string.receiver_id_code_first));

        }
    }

    @OnClick(R.id.image_view_qr_code)
    void loadQRCode() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, REQUEST_ALBUM);
    }

    @BindView(R.id.business_radio_button)
    RadioButton mBusinessRadioButton;

    @BindView(R.id.personal_radio_button)
    RadioButton mPersonalRadioButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ALBUM:
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "onActivityResult: " + data.toString());
                    Uri uri = data.getData();
                    mQrCodeImageView.setImageURI(uri);
                    new ReadQRCodeTask().execute(uri);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private class Image implements QRCodeImage {
        Bitmap image;

        Image(Bitmap image) {
            this.image = image;
        }

        public int getWidth() {
            return image.getWidth();
        }

        public int getHeight() {
            return image.getHeight();
        }

        public int getPixel(int x, int y) {
            return image.getPixel(x, y);
        }
    }

    private class ReadQRCodeTask extends AsyncTask<Uri, Void, String> {

        private static final String DECODE_FAILED = "decode_failed";
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getContext());
//            progressDialog.setTitle("识别中");
            progressDialog.setMessage(getString(R.string.reading_qr_code));
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(Uri... uris) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                        uris[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            QRCodeDecoder decoder = new QRCodeDecoder();
            byte[] qrCodeContent;
            try {
                qrCodeContent = decoder.decode(new Image(bitmap));
            } catch (DecodingFailedException e) {
                return DECODE_FAILED;
            }
            return new String(qrCodeContent);
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            Log.i(TAG, "onPostExecute: 读取结果是：" + s);
            if (s.equals(DECODE_FAILED)) {
                Toast.makeText(getContext(), R.string.read_failed, Toast.LENGTH_SHORT).show();
                return;
            }
            mIdCodeEditText.setText(s.substring(s.lastIndexOf("/") + 1));
            Toast.makeText(getContext(), R.string.read_succeed, Toast.LENGTH_SHORT).show();
        }
    }

    public static PayFragment getInstance() {
        return new PayFragment();
    }
}
