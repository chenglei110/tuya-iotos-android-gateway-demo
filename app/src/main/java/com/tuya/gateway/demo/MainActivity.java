package com.tuya.gateway.demo;

import android.Manifest;
import android.app.Service;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.tuya.smart.gateway.gw_extension_sdk.DPEvent;
import com.tuya.smart.gateway.gw_extension_sdk.DevCmdCallbacks;
import com.tuya.smart.gateway.gw_extension_sdk.DevDescIf;
import com.tuya.smart.gateway.gw_extension_sdk.GwInfraCallbacks;
import com.tuya.smart.gateway.gw_extension_sdk.IoTGatewaySDKManager;
import com.tuya.smart.gateway.gw_extension_sdk.Log;
import com.tuya.smart.gateway.gw_extension_sdk.MiscDevCallbacks;
import com.tuya.smart.gateway.gw_extension_sdk.OnSpeechCallback;
import com.tuya.smart.gateway.gw_extension_sdk.SmartConfig;
import com.tuya.smart.gateway.gw_extension_sdk.Z3ApsFrame;
import com.tuya.smart.gateway.gw_extension_sdk.Z3Desc;
import com.tuya.smart.gateway.gw_extension_sdk.Z3DevCallbacks;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static android.media.AudioManager.STREAM_ALARM;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MainActivity";

    private final int PERMISSION_CODE = 123;

    private String[] requiredPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    IoTGatewaySDKManager ioTGatewaySDKManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.active_gw).setOnClickListener(this::onClick);
        findViewById(R.id.unactive_gw).setOnClickListener(this::onClick);
        findViewById(R.id.dp_send).setOnClickListener(this::onClick);
        findViewById(R.id.permit_join).setOnClickListener(this::onClick);
        findViewById(R.id.misc_dev_fresh_hb).setOnClickListener(this::onClick);
        findViewById(R.id.set_log_path).setOnClickListener(this::onClick);
        findViewById(R.id.wifi_smart_config).setOnClickListener(this::onClick);
        findViewById(R.id.upload_media_voice).setOnClickListener(this::onClick);
        findViewById(R.id.traversal_device).setOnClickListener(this::onClick);

        if (!EasyPermissions.hasPermissions(this, requiredPermissions)) {
            EasyPermissions.requestPermissions(this, "need auth for using this sdk", PERMISSION_CODE, requiredPermissions);
        } else {
            Toast.makeText(this, "already authed " + Manifest.permission.WRITE_EXTERNAL_STORAGE, Toast.LENGTH_SHORT).show();
            initSDK();
        }
    }

    private String getMethodName() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2];
        String methodName = e.getMethodName();
        return methodName;
    }

    Z3DevCallbacks z3DevCallbacks = new Z3DevCallbacks() {
        @Override
        public int onZ3DevActiveStateChanged(String id, int state) {
            Log.d(TAG, " onZ3DevActiveStateChanged called " + "id: " + id + " state: " + state);

            return 0;
        }

        @Override
        public int onZ3DevInitData() {
            Log.d(TAG, " onZ3DevInitData called");

            return 0;
        }

        @Override
        public int onZ3DevJoin(Z3Desc desc) {
            Log.d(TAG, " onZ3DevJoin called " + desc.toString());

            int uddd = 0x8000200;
            String pid = "";
            String ver = "1.0.7";

            int ret = ioTGatewaySDKManager.IotGateWayZ3DevBind(uddd, desc.mId, pid, ver);
            if (ret != 0) {
                Log.d(TAG, "IotGateWayZ3DevBind failed, ret: " + ret);
                return ret;
            }

            return 0;
        }

        @Override
        public int onZ3DevLeave(String id) {
            Log.d(TAG, " onZ3DevLeave called " + "id: " + id);

            return 0;
        }

        @Override
        public int onZ3DevZclReport(Z3ApsFrame frame) {
            Log.d(TAG, " onZ3DevZclReport called " + frame.toString());

            ioTGatewaySDKManager.IotGatewayMiscDevHbFresh(frame.mId, 120);

            // for sub-device state upload
            int Z3_PROFILE_ID_HA = 0x0104;
            int ZCL_ON_OFF_CLUSTER_ID = 0x0006;
            int ZCL_READ_ATTRIBUTES_COMMAND_ID = 0x00;
            int READ_ATTRIBUTES_RESPONSE_COMMAND_ID = 0x01;
            int REPORT_ATTRIBUTES_COMMAND_ID = 0x0A;
            int READ_ATTRIBUTER_RESPONSE_HEADER = 3;    /* Attributer ID: 2 Bytes, Status: 1 Btye */
            int REPORT_ATTRIBUTES_HEADER = 2;           /* Attributer ID: 2 Bytes */

            boolean dp_value = true;

            if (frame.mProfileId == Z3_PROFILE_ID_HA && frame.mClusterId == ZCL_ON_OFF_CLUSTER_ID) {
                if (frame.mCmdId == REPORT_ATTRIBUTES_COMMAND_ID)
                    dp_value = frame.mMessage[REPORT_ATTRIBUTES_HEADER + 1] == 0 ? false : true;
                else if (frame.mCmdId == READ_ATTRIBUTES_RESPONSE_COMMAND_ID)
                    dp_value = frame.mMessage[READ_ATTRIBUTER_RESPONSE_HEADER + 1] == 0 ? false : true;
                else {
                    Log.d(TAG, " onZ3DevZclReport called. invalid mCmdId ");
                    return -1;
                }
            }

            ioTGatewaySDKManager.sendDP(frame.mId, 0x01, (byte) DPEvent.Type.PROP_BOOL, dp_value);

            return 0;
        }

        @Override
        public int onZ3DevOnlineFresh(String id, int version) {
            Log.d(TAG, " onZ3DevOnlineFresh called " + "id:" + id + " version: " + version);

            return 0;
        }

        @Override
        public int onZ3DevUpgradeStatus(String id, int rc, int version) {
            Log.d(TAG, " onZ3DevUpgradeStatus called " + "id: " + id + "rc: " + rc + "version: " + version);
            return 0;
        }
    };

    DevCmdCallbacks devCmdCallbacks = new DevCmdCallbacks() {
        @Override
        public void onDpQuery(DPEvent event) {
            if (event == null)
                Log.d(TAG, getMethodName() + " called. send all dp info");
            else
                Log.d(TAG, getMethodName() + " called. " + "event:" + event.toString());
        }

        @Override
        public void onDpEvent(int cmd_tp, int dtt_tp, String cid, String mb_id, DPEvent event) {
            Log.d(TAG, getMethodName() + " onDpEvent called " + "cmd_tp:" + cmd_tp + " dtt_tp:" + dtt_tp + " cid: " + cid + " mb_id:" + mb_id + " " + event.toString());
            ioTGatewaySDKManager.sendDP(cid, event.dpid, event.type, event.value);
        }

        @Override
        public int onDevObjCmd() {
            // ignore
            return 0;
        }

        @Override
        public int onDevRawCmd() {
            // ignore
            return 0;
        }

        @Override
        public int onDevGroup(int action, String devId, String grpId) {
            Log.d(TAG, " onDevGroup called");
            return 0;
        }

        @Override
        public int onDevScene(int action, String devId, String grpId, String sceId) {
            Log.d(TAG, " onDevScene called");
            return 0;
        }

        @Override
        public int onDevDataQuery() {
            // ignore
            return 0;
        }

        @Override
        public void onCloudMedia(IoTGatewaySDKManager.MediaAttribute[] mediaAttributes) {
            Log.d(TAG, getMethodName() + " onCloudMedia called");
            for (IoTGatewaySDKManager.MediaAttribute mediaAttribute : mediaAttributes) {
                dumpMediaAttribute(mediaAttribute);
            }
        }

        @Override
        public SmartConfig onSmartConfig() {
            Log.d(TAG, getMethodName() + " onSmartConfig called");
            // User TODO: fill rigth ssid and password
            SmartConfig smartConfig = new SmartConfig("ssid of router", "router password");
            return smartConfig;
        }
    };

    MediaPlayer mMediaPlayer;
    AudioManager audioManager;

    public void init_media() {
        Log.d(TAG, "init");
        mMediaPlayer = new MediaPlayer();
        audioManager = (AudioManager) this.getApplication().getSystemService(Service.AUDIO_SERVICE);
        List<String> paths = new ArrayList<>();
        mMediaPlayer.reset();
    }

    private void playStep(final String remove, final boolean isDialog) {
        Log.d(TAG, "playStep: " + remove);
        if (!TextUtils.isEmpty(remove)) {
            try {
                int musicStreamMaxVolume = audioManager.getStreamMaxVolume(STREAM_ALARM);
                int musicStreamVolum = audioManager.getStreamVolume(STREAM_ALARM);
                float systemVolumn = (float) musicStreamVolum / musicStreamMaxVolume;

                mMediaPlayer.setVolume(systemVolumn, systemVolumn);
                mMediaPlayer.reset();
                mMediaPlayer.setAudioStreamType(STREAM_ALARM);
                mMediaPlayer.setDataSource(remove);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        File file = new File(remove);
                        if (file.exists()) file.delete();
                        mp.stop();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void dumpMediaAttribute(IoTGatewaySDKManager.MediaAttribute attribute) {
        Log.d(TAG, "mId             :" + attribute.mId);
        Log.d(TAG, "mDecodeType     :" + attribute.mDecodeType);
        Log.d(TAG, "mLength         :" + attribute.mLength);
        Log.d(TAG, "mDuration       :" + attribute.mDuration);
        Log.d(TAG, "mMediaType      :" + attribute.mMediaType);
        Log.d(TAG, "mUrl            :" + attribute.mUrl);
        Log.d(TAG, "mFollowAction   :" + attribute.mFollowAction);
        Log.d(TAG, "mSessionId      :" + attribute.mSessionId);
        Log.d(TAG, "mHttpMethod     :" + attribute.mHttpMethod);
        Log.d(TAG, "mRequestBody    :" + attribute.mRequestBody);
        Log.d(TAG, "mTaskType       :" + attribute.mTaskType);
        Log.d(TAG, "mCallbackValue  :" + attribute.mCallbackValue);
        Log.d(TAG, "mPhoneNumber    :" + attribute.mPhoneNumber);
    }

    MiscDevCallbacks miscDevCallbacks = new MiscDevCallbacks() {
        @Override
        public int onMiscDevAdd(boolean permit, int timeout) {
            Log.d(TAG, "onMiscDevAdd +  called" + " permit: " + permit + " timeout: " + timeout);

            int ret = 0;
            // User TODO
            int uddd = 0x83000101;
            String dev_id = "user TODO";
            String pid = "user TODO";
            String ver = "1.0.0";

            if (permit)
                ret = ioTGatewaySDKManager.IotGatewayMiscDevBind(uddd, dev_id, pid, ver);

            Log.d(TAG,"onMiscDevAdd return: " + ret);

            return 0;
        }

        @Override
        public int onMiscDevDel(String devId) {
            Log.d(TAG, "onMiscDevDel called " + " devid: " + devId);
            return 0;
        }

        @Override
        public int onMiscDevBindIfm(String devId, int result) {
            Log.d(TAG, "onMiscDevBindIfm called " + "devid:" + devId + " result: " + result);
            if (result == 0)
                Log.d(TAG, "bind device " + devId + " ok");
            else
                Log.d(TAG, "bind device " + devId + " failed");
            return 0;
        }

        @Override
        public int onMiscDevUpgrade(String devId, String img) {
            Log.d(TAG, "onMiscDevUpgrade called " + "devid:" + devId + " img: " + img);
            return 0;
        }

        @Override
        public int onMiscDevReset(String devId) {
            Log.d(TAG, "onMiscDevReset called " + "devid:" + devId);
            return 0;
        }

        @Override
        public void onDevHeartbeatSend(String devId) {
            Log.d(TAG, "onDevHeartbeatSend called " + "devid:" + devId);
        }
    };

    boolean gwInited = false;

    GwInfraCallbacks gwInfraCallbacks = new GwInfraCallbacks() {
        @Override
        public void onStartSuccess() {
            Log.d(TAG, "onStartSuccess called");
            gwInited = true;
        }

        @Override
        public void onStartFailure(int err) {
            Log.d(TAG, "onStartFailure called");

        }

        // ignore
        @Override
        public int onGetUuidAuthkey(String uuid, int uuidSize, String authkey, int authkeySize) {
            Log.d(TAG, "onGetUuidAuthkey called");
            return 0;
        }

        // ignore
        @Override
        public int onGetProductKey(String pk, int pkSize) {
            Log.d(TAG,  "onGetProductKey called");
            return 0;
        }

        @Override
        public int onGwUpgrade(String imgFile) {
            Log.d(TAG, "onGwUpgrade called");
            return 0;
        }

        @Override
        public void onGwReboot() {
            Log.d(TAG, "onGwReboot called");
        }

        @Override
        public void onGwReset() {
            Log.d(TAG, "onGwReset called");
        }

        @Override
        public void onGwEngineerFinished() {
            Log.d(TAG, "onGwEngineerFinished called");
        }

        @Override
        public String onGwFetchLocalLog(int pathLen) {
            Log.d(TAG, "onGwFetchLocalLog called");
            return null;
        }

        @Override
        public int onGwConfigureOpMode(int mode) {
            Log.d(TAG, "onGwConfigureOpMode called");
            return 0;
        }

        @Override
        public int onGwActiveStatusChanged(int status) {
            Log.d(TAG, "onGwActiveStatusChanged called");
            return 0;
        }

        @Override
        public int onGwOnlineStatusChanged(int registered, int online) {
            Log.d(TAG, "onGwOnlineStatusChanged called");
            return 0;
        }
    };

    private void initSDK() {

        int ret = 0;

        // 1. set log dir
        Log.init(this, "/sdcard/tuya/iot", 3);

        // 2. get sdk manager instance
        ioTGatewaySDKManager = IoTGatewaySDKManager.getInstance();

        // 3. set sdk callbacks
        ioTGatewaySDKManager.setGwInfraCallbacks(gwInfraCallbacks);
        ioTGatewaySDKManager.setMiscDevCallbacks(miscDevCallbacks);
        ioTGatewaySDKManager.setDevCmdCallbacks(devCmdCallbacks);
        ioTGatewaySDKManager.setZ3DevCallbacks(z3DevCallbacks);

        // 4. set config
        IoTGatewaySDKManager.Config config = new IoTGatewaySDKManager.Config();
        // id key
        config.mUuid = ""; // User TODO
        config.mAuthKey = ""; // User TODO
        config.mProductKey = ""; // User TODO
        // for storage
        config.mCachePath = "/sdcard/";
        config.mStoragePath = "/sdcard/";
        // for network
        config.mEthIfname = "wlan0";
        config.mVer = "1.0.0";
        // for zigbee
        // config.mEnableZigbee = true;
        config.mEnableZigbee = false;
        config.mTtyDevice = "/dev/ttyS4";
        config.mTtyBaudrate = 115200;

        config.mUzCfg = "/sdcard/devices.json";

        config.mLogLevel = 4;

        // 5. start gateway
        ioTGatewaySDKManager.IotGatewayStart(this, config);

        Log.d(TAG, "init gateway extension sdk");
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        initSDK();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    boolean permitJoin = true;
    boolean value = true;
    boolean isHeartbeadInited = false;
    int ret = 0;

    public void onClick(View v) {
        if (ioTGatewaySDKManager == null) {
            return;
        }

        if (gwInited == false) {
            Log.d(TAG, "gateway must be inited first");
            return;
        }

        if (isHeartbeadInited == false) {
            // start sub-device heartbeat (optional)
            ioTGatewaySDKManager.IotGatewayHbInit();
            isHeartbeadInited = true;
        }

        int timestamp = (int) (System.currentTimeMillis() / 1000L);
        switch (v.getId()) {
            case R.id.active_gw:
                ioTGatewaySDKManager.IotGatewayActive("tuya token"); // User TODO
                break;
            case R.id.unactive_gw:
                ioTGatewaySDKManager.IotGatewayUnactive();
                break;
            case R.id.permit_join:
                ioTGatewaySDKManager.IotGatewayPermitJoin(permitJoin);
                permitJoin = !permitJoin;
                break;
            case R.id.misc_dev_fresh_hb:
                ioTGatewaySDKManager.IotGatewayMiscDevHbFresh("", 120); // User TODO
                ioTGatewaySDKManager.IotGatewayMiscDevHbFresh("", 120); // User TODO
                break;
            case R.id.set_log_path:
                ioTGatewaySDKManager.IotGatewayLogPathSet("/sdcard/tuya/iot/");
                break;
            case R.id.dp_send:
                DPEvent event0 = new DPEvent(1, (byte) DPEvent.Type.PROP_BOOL, value, timestamp);
                DPEvent event1 = new DPEvent(102, (byte) DPEvent.Type.PROP_VALUE, 12, timestamp);
                DPEvent event2 = new DPEvent(105, (byte) DPEvent.Type.PROP_RAW, "test".getBytes(Charset.forName("UTF-8")), timestamp);
                DPEvent[] events = {event0, event1, event2};
                ioTGatewaySDKManager.sendDPWithTimeStamp("", events); // User TODO
                value = !value;
                break;
            case R.id.wifi_smart_config:
                ret = ioTGatewaySDKManager.IotGateWaySmartConfigInit("wlan0");
                if (ret != 0) {
                    Log.d(TAG, "wifi_smart_config 1 return " + ret);
                    break;
                }
                ret = ioTGatewaySDKManager.IotGateWaySmartConfigStart(120);
                if (ret != 0) {
                    Log.d(TAG, "wifi_smart_config 2 return " + ret);
                    break;
                }
                //ret = ioTGatewaySDKManager.IotGateWaySmartConfigStop();
                Log.d(TAG, "wifi_smart_config 3 return " + ret);
                break;
            case R.id.upload_media_voice:

                ret = ioTGatewaySDKManager.tuyaIotUploadMediaStart();
                if (ret != 0) {
                    Log.d(TAG, "upload_media_voice 1 return " + ret);
                    break;
                }
                byte[] data = read_voice_file();
                if (data.length <= 0)
                    break;
                ret = ioTGatewaySDKManager.tuyaIotUploadMedia(data);
                if (ret != 0) {
                    Log.d(TAG, "upload_media_voice 2 return " + ret);
                    break;
                }
                ret = ioTGatewaySDKManager.tuyaIotUploadMediaStop(); // for stop
                //ret = ioTGatewaySDKManager.tuyaIotUploadMediaCancel(); // for cancel
                if (ret != 0) {
                    Log.d(TAG, "upload_media_voice 3 return " + ret);
                    break;
                }
                break;
            case R.id.traversal_device:
            {
                DevDescIf devDescIf = null;

                while (true) {
                    devDescIf = ioTGatewaySDKManager.IotGateWayDevTraversal();
                    if (devDescIf == null) {
                        Log.d(TAG, "Traversal done ");
                        break;
                    } else {
                        Log.d(TAG, "device: " + devDescIf.toString());
                        if (devDescIf.mAttr != null) {
                            Log.d(TAG, "devDescIf.mAttr.length: " + devDescIf.mAttr.length);
                            for (int i = 0; i < devDescIf.mAttr.length; i++)
                                Log.d(TAG, "attr[" + i + "]: "+ devDescIf.mAttr[i].toString());
                        }
                    }
                }
                break;
            }
        }
    }

    public static long getFileSize(String path) {
        File file = new File(path);
        return file.length();
    }

    byte[] read_voice_file() {
        String input_file_name = "/sdcard/test.wav";
        byte[] buf = null;
        try {
            FileInputStream fis = new FileInputStream(input_file_name);
            int size = (int)getFileSize(input_file_name);
            buf = new byte[size];
            int c = fis.read(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "read_voice_file length " + buf.length);
        return buf;
    }
}