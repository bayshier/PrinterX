package com.printer.tscdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.List;

/**
 * @author deadline
 * @time 2016-10-28
 * @usage android >=M 的权限申请统一处理
 * <p>
 * notice:
 * 很多手机对原生系统做了修改，比如小米4的6.0的shouldShowRequestPermissionRationale
 * 就一直返回false，而且在申请权限时，如果用户选择了拒绝，则不会再弹出对话框了, 因此有了
 * void doAfterDenied(String... permission);
 */

public class PermissionUtils {

    private static final int REQUEST_PERMISSION_CODE = 1000;

    private Object mContext;

    private PermissionListener mListener;

    private List<String> mPermissionList;

    public PermissionUtils(@NonNull Object object) {
        checkCallingObjectSuitability(object);
        this.mContext = object;

    }


    /**
     * 权限授权申请
     *
     * @param hintMessage 要申请的权限的提示
     * @param permissions 要申请的权限
     * @param listener    申请成功之后的callback
     */
    public void requestPermissions(@NonNull CharSequence hintMessage,
                                   @Nullable PermissionListener listener,
                                   @NonNull final String... permissions) {
        if (listener != null) {
            mListener = listener;
        }
        mPermissionList = Arrays.asList(permissions);
        //没全部权限
        if (!hasPermissions(mContext, permissions)) {
            //需要向用户解释为什么申请这个权限
            boolean shouldShowRationale = false;
            for (String perm : permissions) {
                shouldShowRationale =
                        shouldShowRationale || shouldShowRequestPermissionRationale(mContext, perm);
            }

            if (shouldShowRationale) {
                showMessageOKCancel(hintMessage, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executePermissionsRequest(mContext, permissions,
                                REQUEST_PERMISSION_CODE);

                    }
                });
            } else {
                executePermissionsRequest(mContext, permissions,
                        REQUEST_PERMISSION_CODE);
            }
        } else if (mListener != null) { //有全部权限
            mListener.doAfterGrand(permissions);
        }
    }

    /**
     * 处理onRequestPermissionsResult
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void handleRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                boolean allGranted = true;
                for (int grant : grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                        break;
                    }
                }

                if (allGranted && mListener != null) {

                    mListener.doAfterGrand((String[]) mPermissionList.toArray());

                } else if (!allGranted && mListener != null) {
                    mListener.doAfterDenied((String[]) mPermissionList.toArray());
                }
                break;
        }
    }

    /**
     * 判断是否具有某权限
     *
     * @param object
     * @param perms
     * @return
     */
    public static boolean hasPermissions(@NonNull Object object, @NonNull String... perms) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String perm : perms) {
            boolean hasPerm = (ContextCompat.checkSelfPermission(getActivity(object), perm) ==
                    PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                return false;
            }
        }

        return true;
    }


    /**
     * 兼容fragment
     *
     * @param object
     * @param perm
     * @return
     */
    @TargetApi(23)
    private static boolean shouldShowRequestPermissionRationale(@NonNull Object object, @NonNull String perm) {
        if (object instanceof Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, perm);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else {
            return false;
        }
    }

    /**
     * 执行申请,兼容fragment
     *
     * @param object
     * @param perms
     * @param requestCode
     */
    @TargetApi(23)
    private void executePermissionsRequest(@NonNull Object object, @NonNull String[] perms, int requestCode) {
        if (object instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) object, perms, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).requestPermissions(perms, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).requestPermissions(perms, requestCode);
        }
    }

    /**
     * 检查传递Context是否合法
     *
     * @param object
     */
    private void checkCallingObjectSuitability(@Nullable Object object) {
        if (object == null) {
            throw new NullPointerException("Activity or Fragment should not be null");
        }

        boolean isActivity = object instanceof Activity;
        boolean isSupportFragment = object instanceof Fragment;
        boolean isAppFragment = object instanceof Fragment;
        if (!(isSupportFragment || isActivity || (isAppFragment && isNeedRequest()))) {
            if (isAppFragment) {
                throw new IllegalArgumentException(
                        "Target SDK needs to be greater than 23 if caller is android.app.Fragment");
            } else {
                throw new IllegalArgumentException("Caller must be an Activity or a Fragment.");
            }
        }
    }


    @TargetApi(11)
    private static Activity getActivity(@NonNull Object object) {
        if (object instanceof Activity) {
            return ((Activity) object);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else {
            return null;
        }
    }

    public static boolean isNeedRequest() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public void showMessageOKCancel(CharSequence message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity(mContext))
                .setMessage(message)
                .setPositiveButton(getActivity(mContext).getString(android.R.string.ok), okListener)
                .setNegativeButton(getActivity(mContext).getString(android.R.string.cancel), null)
                .setCancelable(false)
                .create()
                .show();
    }

    public interface PermissionListener {

        void doAfterGrand(String... permission);

        void doAfterDenied(String... permission);
    }
}