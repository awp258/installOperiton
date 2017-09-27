package com.awp.demo.silent;

import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.VerificationParams;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      Button mBtnSilent= (Button) this.findViewById(R.id.btn_silent_install);

        install(mBtnSilent);
    }

    /***
     * 静默卸载实例
     */

    public void unInstallApp()
    {
        final LocalIntentReceiver receiver = new LocalIntentReceiver();
        mInstaller.uninstall(pkg, flags, receiver.getIntentSender(), userId);

        final Intent result = receiver.getResult();
        final int status = result.getIntExtra(PackageInstaller.EXTRA_STATUS,
                PackageInstaller.STATUS_FAILURE);
        if (status == PackageInstaller.STATUS_SUCCESS) {
            System.out.println("Success");
        } else {
            Log.e(TAG, "Failure details: " + result.getExtras());
            System.out.println("Failure ["
                    + result.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE) + "]");
        }
    }

    /**
     * Button点击事件
     *
     * @param view
     */
    public void install(View view) {
        String path = "";
        if (FileUtils.isSdcardReady()) {
            path = FileUtils.getSdcardPath();
        } else {
            path = FileUtils.getCachePath(this);
        }
        String fileName = path + "/AidlServerDemo.apk";
        File file = new File(fileName);

        try {
            if (!file.exists())
                copyAPK2SD(fileName);
            Uri uri = Uri.fromFile(new File(fileName));
            Class<?> clazz = Class.forName("android.os.ServiceManager");
            Method method = clazz.getMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null, "package");
            IPackageManager ipm = IPackageManager.Stub.asInterface(iBinder);
            @SuppressWarnings("deprecation")
            VerificationParams verificationParams = new VerificationParams(null, null, null, VerificationParams.NO_UID, null);

            ipm.installPackage(fileName, new PackageInstallObserver(), 2, null, verificationParams, "");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // 用于显示结果
    class PackageInstallObserver extends IPackageInstallObserver2.Stub {

        @Override
        public void onUserActionRequired(Intent intent) throws RemoteException {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPackageInstalled(String basePackageName, int returnCode, String msg, Bundle extras) throws RemoteException {
            // TODO Auto-generated method stub

        }
    }

    ;

    /**
     * 拷贝assets文件夹的APK插件到SD
     *
     * @param strOutFileName
     * @throws IOException
     */
    private void copyAPK2SD(String strOutFileName) throws IOException {
        FileUtils.createDipPath(strOutFileName);
        InputStream myInput = this.getAssets().open("AidlServerDemo.apk");
        OutputStream myOutput = new FileOutputStream(strOutFileName);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }
        myOutput.flush();
        myInput.close();
        myOutput.close();
    }
}