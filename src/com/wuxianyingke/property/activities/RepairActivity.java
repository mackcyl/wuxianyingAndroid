package com.wuxianyingke.property.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.mantoto.property.R;
import com.wuxianyingke.property.common.*;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApi.Flea;
import com.wuxianyingke.property.remote.RemoteApi.RepairType;
import com.wuxianyingke.property.remote.RemoteApi.FleaContent;
import com.wuxianyingke.property.remote.RemoteApi.FleaPicture;
import com.wuxianyingke.property.remote.RemoteApi.NetInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.threads.GetRepairTypeThread;

/**
 * 报修
 *
 * @author mackcyl
 */
public class RepairActivity extends BaseActivityWithRadioGroup {
    private Long fleaid = null;
    private boolean mFleaEdit = false;// 编辑模式
    private ArrayList<RepairType> mRepairTypeList;
    private ArrayList<FleaPicture> mProductList;
    private FleaContent mFleaContent = null;
    private ProgressDialog mProgressBar = null;
    private String mErrorInfo = "";
    private String desc = "";
    private SharedPreferences saving;
    private int propertyid,repairTypeId;
    public EditText mFleaNameEditText, mFleaContentEditText;
    public ImageView mFleaPicImageView, mFleaPicOneImageView,
            mFleaPicTwoImageView, mFleaPicThreeImageView,
            mFleaPicFourImageView, mFleaPicFiveImageView;
    private TextView mIsVisitorTextView;
    java.io.File file = null;
    Dialog dialog = null;
    int id_dialog6 = 0;
    private final File PHOTO_DIR = new File(
            Environment.getExternalStorageDirectory() + "/wxyk/Camera");
    private String uploadPhotoUrl = "";
    private String savePhotoPath = "";
    private Bitmap bitmapGallery = null;
    private Bitmap bitmapCamera = null;
    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;
    /* 用来标识请求gallery的activity */
    private static final int PHOTO_PICKED_WITH_DATA = 3021;
    boolean picFlag[] = { false, false, false, false, false };
    public String picFilePath[] = new String[5];// 记录新增图片地址
    public Drawable imgDw[] = new Drawable[5];// 记录编辑下载服务器来的图片
    public long deletePic[] = new long[5];// 记录删除服务器下载来的图片id
    private Button topbar_left,topbar_right ,submit_btn;
    private TextView topbar_txt,repairTypeDesc;

    private RadioGroup mRadioGroup;
    private RadioButton mRepairTypeRadio;

    private GetRepairTypeThread rtThread;



    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mProgressBar != null) {
                mProgressBar.dismiss();
                mProgressBar = null;
            }
            switch (msg.what) {
                // 登录失败
                case 0:
                    Toast.makeText(RepairActivity.this, mErrorInfo,
                            Toast.LENGTH_SHORT).show();
                    break;

                // 登陆成功
                case 1:
                    Toast.makeText(RepairActivity.this, "发布成功",
                            Toast.LENGTH_SHORT).show();
                    RecursionDeleteFile(PHOTO_DIR);

                    dialog = new AlertDialog.Builder(RepairActivity.this).create();
                    LayoutInflater inflater = LayoutInflater.from(RepairActivity.this);
                    View backupExpandHeader = inflater.inflate(R.layout.create_repair_dialog, null);

                    LinearLayout.LayoutParams params_rb = new LinearLayout.LayoutParams(   LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT );
//                            (int)(25*density));
//                    int margin = (int)(2*density);
//                    params_rb.setMargins(margin, 0, margin, 0);

                    backupExpandHeader.setLayoutParams(params_rb);
                    TextView popTitle = (TextView) backupExpandHeader.findViewById(R.id.popDialogTitle);
                    popTitle.setText("报修发布成功");
                    TextView popInfo = (TextView) backupExpandHeader.findViewById(R.id.popDialogInfo);
                    popInfo.setText(desc);
                    Button okButton = (Button) backupExpandHeader.findViewById(R.id.ButtonOK);

                    okButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setClass(RepairActivity.this,RepairListActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    dialog.show();
                    dialog.setContentView(backupExpandHeader);


                    break;

                // 通讯错误
                case 4:
                    Toast.makeText(RepairActivity.this, "通讯错误，请检查网络或稍后再试。",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 8:
                    Toast.makeText(RepairActivity.this, desc,
                            Toast.LENGTH_SHORT).show();
                    break;
                case 9:
                    Toast.makeText(RepairActivity.this, "网络超时，请重新获取",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 201:
                    Toast.makeText(RepairActivity.this, "网络超时，请重新获取",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 200:
                    mFleaNameEditText.setText(mFleaContent.flea.header);
                    mFleaContentEditText.setText(mFleaContent.flea.description);

                    if (imgDw.length != 0) {
                        if (imgDw[0] != null) {
                            picFlag[0] = true;
                            mFleaPicOneImageView.setBackgroundDrawable(imgDw[0]);
                            mFleaPicOneImageView.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            mFleaPicOneImageView.setVisibility(View.GONE);
                        }
                        if (imgDw[1] != null) {
                            picFlag[1] = true;
                            mFleaPicTwoImageView.setBackgroundDrawable(imgDw[1]);
                            mFleaPicTwoImageView.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            mFleaPicTwoImageView.setVisibility(View.GONE);
                        }
                        if (imgDw[2] != null) {
                            picFlag[2] = true;
                            mFleaPicThreeImageView.setBackgroundDrawable(imgDw[2]);
                            mFleaPicThreeImageView.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            mFleaPicThreeImageView.setVisibility(View.GONE);
                        }
                        if (imgDw[3] != null) {
                            picFlag[3] = true;
                            mFleaPicFourImageView.setBackgroundDrawable(imgDw[3]);
                            mFleaPicFourImageView.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            mFleaPicFourImageView.setVisibility(View.GONE);
                        }
                        if (imgDw[4] != null) {
                            picFlag[4] = true;
                            mFleaPicFiveImageView.setBackgroundDrawable(imgDw[4]);
                            mFleaPicFiveImageView.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            mFleaPicFiveImageView.setVisibility(View.GONE);
                        }
                    }
                    break;
                case 500:
                    Toast.makeText(RepairActivity.this,
                            mFleaContent.netInfo.desc, Toast.LENGTH_SHORT).show();
                    break;

                case Constants.MSG_GET_REPAIR_TYPE_LIST_FINSH:
                    if(rtThread != null && rtThread.getRepairTypeList() != null){
                        showRepairType(rtThread.getRepairTypeList());
                    }

                    break;
                case Constants.MSG_GET_REPAIR_TYPE_LIST_ERROR:
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    // 删除图片
    public void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            Log.d("MyTag", "file name=" + file.getName());
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
            Log.d("MyTag", "file name=" + file.getName());
            file.delete();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.repair);
        saving = this.getSharedPreferences(LocalStore.USER_INFO, 0);
        propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);
        if (getIntent().getExtras() != null) {
            fleaid = getIntent().getLongExtra(Constants.PRODUCT_ID_ACTION, -1);
            mFleaEdit = getIntent().getBooleanExtra("fleaEdit", false);
        }

        RecursionDeleteFile(PHOTO_DIR);
        File f = new File("/sdcard/wxyk/zip/pic.zip");
        RecursionDeleteFile(f);
        initWidgets();

        mRadioGroup = (RadioGroup) findViewById(R.id.repair_type_group);
        repairTypeDesc = (TextView) findViewById(R.id.repair_type_desc);
        rtThread = new GetRepairTypeThread(this, mHandler);
        rtThread.start();


    }

    private void initWidgets() {
        topbar_txt = (TextView) findViewById(R.id.topbar_txt);
        topbar_txt.setText("报修");
        topbar_left = (Button) findViewById(R.id.topbar_left);
        topbar_left.setVisibility(View.VISIBLE);
        topbar_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });


        topbar_right = (Button) findViewById(R.id.topbar_button_right);
        topbar_right.setText("记录");
        topbar_right.setVisibility(View.VISIBLE);

        topbar_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (validateFlea()) {
                    sendRepair();
                }
            }
        });

        submit_btn = (Button) findViewById(R.id.create_repair);
        submit_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFlea()) {
                    sendRepair();
                }
            }
        });

        mFleaNameEditText = (EditText) findViewById(R.id.FleaNameEditText);  //联系方式
        mFleaContentEditText = (EditText) findViewById(R.id.FleaContentEditText); //文字描述
        mFleaPicOneImageView = (ImageView) findViewById(R.id.FleaPicOneImageView);
        mFleaPicTwoImageView = (ImageView) findViewById(R.id.FleaPicTwoImageView);
        mFleaPicThreeImageView = (ImageView) findViewById(R.id.FleaPicThreeImageView);
        mFleaPicFourImageView = (ImageView) findViewById(R.id.FleaPicFourImageView);
        mFleaPicFiveImageView = (ImageView) findViewById(R.id.FleaPicFiveImageView);
        mFleaPicImageView = (ImageView) findViewById(R.id.FleaPicImageView);
        mFleaPicOneImageView.setVisibility(View.GONE);
        mFleaPicTwoImageView.setVisibility(View.GONE);
        mFleaPicThreeImageView.setVisibility(View.GONE);
        mFleaPicFourImageView.setVisibility(View.GONE);
        mFleaPicFiveImageView.setVisibility(View.GONE);
        mFleaPicImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (picFlag[0] && picFlag[1] && picFlag[2] && picFlag[3]
                        && picFlag[4]) {
                    Toast.makeText(RepairActivity.this, "目前版本只支持上传5张图片",
                            Toast.LENGTH_SHORT).show();
                } else {
                    choicePhoto();
                }
            }
        });

        mFleaPicOneImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                confirmRemoveDialog(RepairActivity.this, 0,
                        picFilePath[0], mFleaPicOneImageView);
            }
        });
        mFleaPicTwoImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                confirmRemoveDialog(RepairActivity.this, 1,
                        picFilePath[1], mFleaPicTwoImageView);
            }
        });
        mFleaPicThreeImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                confirmRemoveDialog(RepairActivity.this, 2,
                        picFilePath[2], mFleaPicThreeImageView);
            }
        });
        mFleaPicFourImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                confirmRemoveDialog(RepairActivity.this, 3,
                        picFilePath[3], mFleaPicFourImageView);
            }
        });
        mFleaPicFiveImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                confirmRemoveDialog(RepairActivity.this, 4,
                        picFilePath[4], mFleaPicFiveImageView);
            }
        });

        if (LocalStore.getIsVisitor(this)) {
            topbar_right.setClickable(false);
//            mIsVisitorTextView.setVisibility(View.VISIBLE);
        }
    }



    public boolean validateFlea() {
        Log.d("MyTag", "mFleaNameEditText=" + mFleaNameEditText);
        if (Util.isEmpty(mFleaNameEditText)) {
            Toast.makeText(RepairActivity.this, "联系方式不能为空",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (Util.isEmpty(mFleaContentEditText)) {
            Toast.makeText(RepairActivity.this, "报修内容不能为空",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showDialog() {
        mProgressBar = new ProgressDialog(RepairActivity.this);
        String msg = getResources().getString(R.string.txt_loading);
        mProgressBar.setMessage(msg);
        mProgressBar.setCancelable(false);
        mProgressBar.show();
    }

    // 发送数据
    public void sendRepair() {
        showDialog();
        File f = new File("/sdcard/wxyk/zip/pic.zip");
        if (f.exists()) {
            f.delete();
        }
        file = new java.io.File("/sdcard/wxyk/zip/pic.zip");
        try {
            Log.d("MyTag", "Environment.getExternalStorageDirectory()="
                    + Environment.getExternalStorageDirectory());
            UtilZip.zipFiles(
                    UtilZip.listFiles(Environment.getExternalStorageDirectory()
                            + "/wxyk/Camera"), file);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        repairTypeId = mRadioGroup.getCheckedRadioButtonId();

        Log.d("MyTag", "repairType ="+repairTypeId);

//        View radioButton = mRadioGroup.findViewById(radioButtonID);
//        repairTypeId = mRadioGroup.indexOfChild(radioButton);
        Thread loginThread = new Thread() {
            public void run() {
                RemoteApiImpl remote = new RemoteApiImpl();
                NetInfo netInfo = null;
                netInfo = remote.sendRepairNew(propertyid,
                        saving.getLong(LocalStore.USER_ID, 0),
                        repairTypeId,
                        mFleaNameEditText.getText().toString().trim(),
                        mFleaContentEditText.getText().toString().trim(),
                        file);
                Message msg = new Message();
                if (netInfo == null) {
                    msg.what = 4;
                } else if (200 == netInfo.code) {
                    msg.what = 1;
                    desc = netInfo.desc;
                } else {
                    msg.what = 0;
                    mErrorInfo = netInfo.desc;
                }

                mHandler.sendMessage(msg);
            }
        };
        loginThread.start();
    }

    public void choicePhoto() {
        new AlertDialog.Builder(RepairActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("选择图片来源")
                .setSingleChoiceItems(new String[] { "从相机", "从媒体库" },
                        // 用 id_dialog6 记录那个 RadioButton 被选中
                        id_dialog6, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which)

                            {
                                id_dialog6 = which;
                            }

                        })
                .setPositiveButton("确定", new DialogInterface.OnClickListener()

                {
                    public void onClick(DialogInterface dialog, int which) {
                        String oldFileUrl = "/sdcard/wxyk/";
                        File srcFile = new File(oldFileUrl);
                        File zipFile = new File("/sdcard/wxyk/zip/");
                        if (!srcFile.exists())
                            srcFile.mkdirs();// 创建存在下载文件夹

                        if (!PHOTO_DIR.exists())
                            PHOTO_DIR.mkdirs();// 创建存在下载文件夹
                        if (!zipFile.exists())
                            zipFile.mkdirs();// 创建存在下载文件夹
                        String tmpstr = "";
                        if (id_dialog6 == 0)
                            photo();
                            // tmpstr = "Item1 is selected";
                        else
                            // tmpstr = "Item2 is selected";
                            doPickPhotoFromGallery();
                        dialog.dismiss(); // 可以没有这句
                    }
                }).create().show();
    }

    public void photo() {
        try {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            savePhotoPath = PHOTO_DIR + "/" + getPhotoFileName();
            File out = new File(savePhotoPath);

            Uri uri = Uri.fromFile(out);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, CAMERA_WITH_DATA);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    // 因为调用了Camera和Gally所以要判断他们各自的返回情况,他们启动时是这样的startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case PHOTO_PICKED_WITH_DATA: {
                // 调用Gallery返回的
                String photoName = getPhotoFileName();
                final Bitmap photo = data.getParcelableExtra("data");

                String photoPath = storeImageToFile(photo, photoName);
                Log.v("", "pathString = " + photoPath);
                bitmapGallery = BitmapFactory.decodeFile(photoPath);

                if (picFlag[0]) {
                    if (picFlag[1]) {
                        if (picFlag[2]) {
                            if (picFlag[3]) {
                                if (picFlag[4]) {
                                } else {
                                    picFilePath[4] = photoPath;
                                    picFlag[4] = true;
                                    mFleaPicFiveImageView
                                            .setVisibility(View.VISIBLE);
                                    mFleaPicFiveImageView
                                            .setImageBitmap(bitmapGallery);
                                }
                            } else {
                                picFilePath[3] = photoPath;
                                picFlag[3] = true;
                                mFleaPicFourImageView.setVisibility(View.VISIBLE);
                                mFleaPicFourImageView.setImageBitmap(bitmapGallery);
                            }
                        } else {
                            picFilePath[2] = photoPath;
                            picFlag[2] = true;
                            mFleaPicThreeImageView.setVisibility(View.VISIBLE);
                            mFleaPicThreeImageView.setImageBitmap(bitmapGallery);
                        }
                    } else {
                        picFilePath[1] = photoPath;
                        picFlag[1] = true;
                        mFleaPicTwoImageView.setVisibility(View.VISIBLE);
                        mFleaPicTwoImageView.setImageBitmap(bitmapGallery);
                    }
                } else {
                    picFilePath[0] = photoPath;
                    picFlag[0] = true;
                    mFleaPicOneImageView.setVisibility(View.VISIBLE);
                    mFleaPicOneImageView.setImageBitmap(bitmapGallery);
                }

                break;
            }

            case CAMERA_WITH_DATA: {
                // 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                try {
                    Intent cj1 = new Intent("com.android.camera.action.CROP");
                    try {
                        cj1.setData(Uri
                                .parse(android.provider.MediaStore.Images.Media
                                        .insertImage(getContentResolver(),
                                                savePhotoPath, null, null)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cj1.putExtra("crop", "true");
                    cj1.putExtra("aspectX", 1);
                    cj1.putExtra("aspectY", 1);
                    cj1.putExtra("outputX", 300);
                    cj1.putExtra("outputY", 300);
                    cj1.putExtra("return-data", true);
                    startActivityForResult(cj1, 2);

                    File camera = new File(savePhotoPath);
                    if (camera.exists())
                        camera.delete();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case 2: {
                String photoName = getPhotoFileName();
                final Bitmap photo = data.getParcelableExtra("data");

                String photoPath = storeImageToFile(photo, photoName);
                Log.v("", "pathString = " + photoPath);
                bitmapCamera = BitmapFactory.decodeFile(photoPath);
                if (picFlag[0]) {
                    if (picFlag[1]) {
                        if (picFlag[2]) {
                            if (picFlag[3]) {
                                if (picFlag[4]) {

                                } else {
                                    picFilePath[4] = photoPath;
                                    picFlag[4] = true;
                                    mFleaPicFiveImageView
                                            .setVisibility(View.VISIBLE);
                                    mFleaPicFiveImageView
                                            .setImageBitmap(bitmapCamera);
                                }
                            } else {
                                picFilePath[3] = photoPath;
                                picFlag[3] = true;
                                mFleaPicFourImageView.setVisibility(View.VISIBLE);
                                mFleaPicFourImageView.setImageBitmap(bitmapCamera);
                            }
                        } else {
                            picFilePath[2] = photoPath;
                            picFlag[2] = true;
                            mFleaPicThreeImageView.setVisibility(View.VISIBLE);
                            mFleaPicThreeImageView.setImageBitmap(bitmapCamera);
                        }
                    } else {
                        picFilePath[1] = photoPath;
                        picFlag[1] = true;
                        mFleaPicTwoImageView.setVisibility(View.VISIBLE);
                        mFleaPicTwoImageView.setImageBitmap(bitmapCamera);
                    }
                } else {
                    picFilePath[0] = photoPath;
                    picFlag[0] = true;
                    mFleaPicOneImageView.setVisibility(View.VISIBLE);
                    mFleaPicOneImageView.setImageBitmap(bitmapCamera);
                }

                break;
            }

        }
    }

    // 请求Gallery程序
    protected void doPickPhotoFromGallery() {
        try {
            final Intent intent = getPhotoPickIntent();
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "photoPickerNotFoundText1", Toast.LENGTH_LONG)
                    .show();
        }

    }

    // 封装请求Gallery的intent
    public static Intent getPhotoPickIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        return intent;
    }

    protected void doCropPhoto(File f) {
        try {
            // 启动gallery去剪辑这个照片
            final Intent intent = getCropImageIntent(Uri.fromFile(f));
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);

        } catch (Exception e) {
            Toast.makeText(this, "photoPickerNotFoundText", Toast.LENGTH_LONG)
                    .show();
        }

    }

    /**
     * Constructs an intent for image cropping. 调用图片剪辑程序
     */
    public static Intent getCropImageIntent(Uri photoUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        return intent;
    }

    /**
     * Store image to SD card.
     */
    private String storeImageToFile(Bitmap photo, String photoName) {
        try {
            File file = new File(PHOTO_DIR, photoName);
            FileOutputStream outStreamz = new FileOutputStream(file);

            photo.compress(Bitmap.CompressFormat.PNG, 50, outStreamz);

            outStreamz.close();
            uploadPhotoUrl = PHOTO_DIR + "/" + photoName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return uploadPhotoUrl;
    }

    /**
     * 用当前时间给取得的图片命名
     *
     */
    private String getPhotoFileName() {
        return System.currentTimeMillis() + ".jpg";
    }

    protected void confirmRemoveDialog(Context ctx, final int picId,
                                       final String picPath, final ImageView mPicImageView) {
        AlertDialog.Builder builder = new Builder(ctx);

        builder.setTitle("删除提示");
        builder.setMessage("确认删除吗？");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (mFleaEdit) {
                    if (picFlag[picId]) {
                        deletePic[picId] = mProductList.get(picId).fleaPictureID;
                        picFlag[picId] = false;
                        picFilePath[picId] = "";
                    }

                } else {
                    File f = new File(picPath);
                    RecursionDeleteFile(f);
                    picFlag[picId] = false;
                    picFilePath[picId] = "";
                }
                mPicImageView.setVisibility(View.GONE);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    void freeResource() {
        // TODO Auto-generated method stub

    }



    class RepairTypeItem
    {
        RadioButton repairTypeRadioBtn;
    }

    /**
     android:layout_width="80dip"
     android:layout_height="25dip"
     android:background="@drawable/blue_repair_type_btn"
     android:text="插卡"
     android:textSize="15.0sp"
     android:textColor="@color/repair_type_radio_text_color"
     android:gravity="center"
     android:layout_weight="0"
     android:button="@null"
     android:layout_marginRight="5dip"
     * @param repairTypes
     */
    public void showRepairType(ArrayList<RepairType> repairTypes){
        mRadioGroup.setVisibility(View.VISIBLE);
        float density = getResources().getDisplayMetrics().density;
        for(int i = 0; i < repairTypes.size(); i++)
        {
            final RepairType info = repairTypes.get(i);
            RadioButton repairTypeRadioBtn = (RadioButton) LayoutInflater.from(this).inflate(R.layout.repair_type_item, null);

            repairTypeRadioBtn.setId((int)info.repairTypeId);
            repairTypeRadioBtn.setText(info.repairTypeName);
//            FrameLayout.LayoutParams lytp = new FrameLayout.LayoutParams(80*2, 25*2);
            RadioGroup.LayoutParams params_rb = new RadioGroup.LayoutParams(
                    (int)(80*density),
                    (int)(25*density));
            int margin = (int)(2*density);
            params_rb.setMargins(margin, 0, margin, 0);

            repairTypeRadioBtn.setLayoutParams(params_rb);
            repairTypeRadioBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    repairTypeDesc.setText(info.repairTypeDescription);
                }
            });

            mRadioGroup.addView(repairTypeRadioBtn);
            if(0 == i){
                repairTypeDesc.setText(info.repairTypeDescription);
                mRadioGroup.check(repairTypeRadioBtn.getId());
            }
        }

    }

//    private void addRepairType(String content) {
//        View v = LayoutInflater.from(this).inflate(R.layout.repair_log_content1, null);
//        RepairLogItem item = new RepairLogItem();
//        item.repair_log_content = (TextView) v.findViewById(R.id.repair_log_content);
//        item.repair_log_time = (TextView) v.findViewById(R.id.repair_log_time);
//        item.repair_log_content.setText(content);
//        item.repair_log_time.setText(time);
//        ScrollViewLinearLayout.addView(v);
//    }

}