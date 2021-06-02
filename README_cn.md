[English](./README.md) | 简体中文

# Tuya Android Device Gateway Networking SDK
涂鸦安卓设备端网关联网SDK demo

## 介绍

Tuya安卓设备端网关联网SDK，借助网关设备的联网能力，直接与涂鸦 IoT 平台、涂鸦 App 建立通信链路并进行涂鸦标准数据交互的一个软件中间件。您可用此 SDK 开发接入涂鸦云的带有网关能力的产品。


## 如何使用
[原始接入文档地址](https://developer.tuya.com/cn/docs/iot/smart-product-solution/product-solutiongateway/gateway-link-sdk-access-solution/tuya-gateway-link-sdk-development-manual?id=K9ducoah42rl2)

## 集成SDK

1. 配置 build.gradle 文件 app 的 build.gradle 文件dependencies 里添加依赖库。

    implementation 'com.tuya.smart:tuyasmart-gw_networking_sdk:1.0.9-doorlock-1.0.1'

    implementation 'pub.devrel:easypermissions:2.0.1'

2. 根目录下 build.gradle 文件添加源:

    maven { url "https://maven-other.tuya.com/repository/maven-releases/" }

## 网关控制

**网关控制实现了参数配置、回调注册、网关启动、入网、DP点下发/上报等功能。**

### 网关启动

#### 获取网关sdk实例
```java
ioTGatewaySDKManager = IoTGatewaySDKManager.getInstance();
```

#### 注册回调函数
```java
IoTGwCallbacks ioTGwCallbacks = new IoTGwCallbacks() {
            @Override
            public int onGwAddDev(int tp, int permit, int timeout) {
                Log.d(TAG, "add sub device get called. " + "tp: " + tp + " perimit: " + permit + " timeout: " + timeout);

                if (permit == 0)
                    return 0;

                // User TODO:

                return 0;
            }

            @Override
            public void onGwDelDev(String dev_id, int type) {
                Log.d(TAG, "delete sub device get called. dev_id " + dev_id + " type: " + type);

                // User TODO:
            }

            @Override
            public int onGwDevGrp(int action, String dev_id, String grp_id) {
                Log.d(TAG, "delete sub device get called. dev_id " + dev_id + "dev_id: " + dev_id + " grp_id: " + grp_id);

                return 0;
            }

            @Override
            public int onGwDevScene(int action, String dev_id, String grp_id, String sce_id) {
                Log.d(TAG, "sub device scene get called. action " + action + "dev_id: " + dev_id + " grp_id: " + grp_id + " sce_id: " + sce_id);

                return 0;
            }

            @Override
            public void onGwIfm(String dev_id, int op_ret) {

                Log.d(TAG, "bind sub device result inform. dev_id " + dev_id + " op_ret: " + op_ret);

            }


            @Override
            public void onGwDevSigmeshTopoUpdate(String dev_id) {

            }

            @Override
            public void onGwDevSigmeshDevCacheDel(String dev_id) {

            }

            @Override
            public void onGwDevWakeup(String dev_id, int duration) {

            }

            @Override
            public void onGwDevSigmeshConn(String sigmesh_dev_conn_inf_json) {

            }
        };

        ioTGatewaySDKManager.setIoTGwCallbacks(ioTGwCallbacks);

        IoTCallbacks ioTCallbacks = new IoTCallbacks() {

            @Override
            public void onGwDevStatusChanged(byte status) {

                Log.d(TAG, "GW TUYA-Cloud Status: " + status);

            }

            /* ignore */
            @Override
            public int onGwDevRevUpgradeInfo(Object fw) {
                return 0;
            }

            @Override
            public void onGwDevRestartReq(int type) {

                Log.d(TAG, "please restart system " + "type: " + type);

            }

            @Override
            public void onDpQuery(DPEvent event) { // 待废弃
            
            }

            @Override
            public void onDpQuery(DPQuery[] queries) {

            }

            @Override
            public void onDpEvent(int cmd_tp, int dtt_tp, String cid, String mb_id, DPEvent event) {

                Log.d(TAG, "on dp event get called: " + event.toString() + " devid: " + cid);

                ioTGatewaySDKManager.sendDP(cid, event.dpid, event.type, event.value);
            }

            /* ignore */
            @Override
            public void onGwDevObjDpCmd(Object dp) {

            }

            /* ignore */
            @Override
            public void onGwDevRawDpCmd(Object dp) {

            }

            /* ignore */
            @Override
            public void onGwDevDpQuery(Object dp_qry) {

            }

            /* ignore */
            @Override
            public int onDevUgInform(String dev_id, Object fw) {
                return 0;
            }

            /* sub-device reset callback */
            @Override
            public void onDevReset(String dev_id, int type) {
                Log.d(TAG, "on device reset get called: " + "dev_id: " + dev_id + " type: " + type);

            }

            /* ignore */
            @Override
            public int onOpeHttpcGetChcode(int is_gw, String dev_id, ChCode ch_code) {
                return 0;
            }

            /* save dp info to flash */
            @Override
            public int onGwOfflineDpSave(String dev_id, DPEvent dpEvent) {

                Log.d(TAG, "saving offline dp data: " + "dev_id: " + dev_id + " dpEvent: " + dpEvent);

                return 0;
            }

            @Override
            public int onNetworkStatus(int status) {
                Log.d(TAG, "on network status get called: " + "status: " + status);
                return 0;
            }

            @Override
            public int onEngrGwSetChannel(int channel) {
                Log.d(TAG, "on engr gw set channel. " + "channel: " + channel);
                return 0;
            }

            @Override
            public int onEngrGwGetChannel(int channel) {
                Log.d(TAG, "on engr gw get channel. " + "channel: " + channel);
                return 0;
            }

            /* for user upload local log */
            @Override
            public int onEngrGwGetLog(int lengthLimit) {
                Log.d(TAG, "on engr gw get log called: " + "len: " + lengthLimit);
                
                return 0;
            }

            @Override
            public int onEngrGwSyncConfig() {
                Log.d(TAG, "on engr gw sync config. ");
                return 0;
            }

            @Override
            public int onEngrGwEngineerFinish() {

                Log.d(TAG, "on engr gw engineer finish. " + " please resart your app" );

                return 0;
            }
        };

        ioTGatewaySDKManager.setIoTCallbacks(ioTCallbacks);

        IoTAppCallbacks ioTAppCallbacks = new IoTAppCallbacks() {
            @Override
            public void onAppLogPath(int lengthLimit) {
                Log.d(TAG, "on engr gw get log called: " + "len: " + lengthLimit);

            }

            @Override
            public void onHomeSecurityIf(String modeStr, int time, int isSound) {
                Log.d(TAG, "onHomeSecurityIf: " + "modeStr: " + modeStr + " time：" + time + " isSound: " + isSound);
            }

            @Override
            public void onHomeSecurityAlarmDev(String cid, String jsonDpInf) {
                Log.d(TAG, "onHomeSecurityAlarmDev: " + "cid: " + cid + " jsonDpInf：" + jsonDpInf);
            }

            @Override
            public void onHomeSecurityAlarmEnvDev(String cid, String jsonDpInf) {
                Log.d(TAG, "onHomeSecurityAlarmEnvDev: " + "cid: " + cid + " jsonDpInf：" + jsonDpInf);
            }

            @Override
            public void onHomeSecurityAlarmDelayStatus(int alarmStatus) {
                Log.d(TAG, "onHomeSecurityAlarmDelayStatus: " + "alarmStatus: " + alarmStatus);
            }

            @Override
            public void onHomeSecurityEvent(int securityEventStatus, int data) {
                Log.d(TAG, "onHomeSecurityEvent: " + "securityEventStatus: " + securityEventStatus + " data：" + data);
            }

            @Override
            public void onHomeSecurityCancelAlarm() {
                Log.d(TAG, "onHomeSecurityCancelAlarm");
            }

            @Override
            public void onHomeSecurityAlarmDevNew(String cid, String jsonDpInf, int dev_type) {

            }

            @Override
            public void onHomeSecurityEnterAlarm(int alarmStatus, String alarmInfo) {

            }

            @Override
            public void onGwEngrToNormalFinish(String path) {

            }

            @Override
            public void onDevHeartbeatSend(String devId) {

                Log.d(TAG, "sub device " + devId + " heartbeat");

                ioTGatewaySDKManager.freshDeviceHeartbeat(devId);

            }

            @Override
            public int onEngrGwScePanel(String devId, ScePanel scePanel, int btnNum) {

                Log.d(TAG, "sub device " + devId + " scePanel" + scePanel.toString() + " btnNum: " + btnNum);

                // User TODO:

                return 0;
            }
        };

        ioTGatewaySDKManager.setIoTAppCallbacks(ioTAppCallbacks);


        // 门锁相关回调注册
        IoTDoorLockCallbacks ioTDoorLockCallbacks = new IoTDoorLockCallbacks() {
            @Override
            public int onZigbeeDataSend(String addr, byte[] data, int cluster_id, int command_id) {
                Log.d(TAG, "onZigbeeDataSend called addr " + addr + " data.len: " + data.length + " cluid: " + cluster_id + " cmdid: " + command_id);
                // USER TODO:
                return 0;
            }
        };

        ioTGatewaySDKManager.setIoTDoorLockCallbacks(ioTDoorLockCallbacks);


```

#### 网关参数配置

**根据实际情况填充配置参数**
```java
IoTGatewaySDKManager.Config config = new IoTGatewaySDKManager.Config();

config.mPath = "/sdcard/";
config.mFirmwareKey = ""; // User TODO
config.mUUID = "";
config.mAuthKey = "";
config.mVersion = "1.0.0";
config.mGwAsDev = true;
config.mEngrMode = false;
GwAttachAttr attachAttr[] = new GwAttachAttr[1];
attachAttr[0] = new GwAttachAttr(GwAttachAttr.DEV_ZB_SNGL, "3.0.0");
```

#### 启动网关

```java
ioTGatewaySDKManager.IotGatewayStart(this, config, attachAttr);
```


## 配置说明


#### 使用者需实例化此配置类：Config，说明如下：
```java
public final static class Config {
        //存储路径，注意长度，底层为64个char字符长度的数组，还追加了一个文件名，也就是说路径要比64位还要短。
        public String mPath;
        //固件key
        public String mFirmwareKey;

        //uuid和authkey成对出现，而且设备唯一
        public String mUUID;
        public String mAuthKey;

        //固件版本号 & 包名
        public String mVersion;
        public String mPackageName;

        //网关是否具有设备的属性
        public boolean mGwAsDev;

        // 是否工程模式
        public boolean mEngrMode;

        public int mLogLevel;

        //zigbee config
        public String mSerialPort;
        //临时文件目录，注意长度，底层为64个char字符长度的数组，还追加了一个文件名，也就是说路径要比64位还要短。
        public String mTempDir;
        //bin文件目录，勿存放文件，其他平台可能为只读文件系统
        //注意长度，底层为64个char字符长度的数组，还追加了一个文件名，也就是说路径要比64位还要短。
        public String mBinDir;
        //是否带流控
        public boolean mIsCTS;
        public boolean mIsOEM;
    }
```


## 函数说明

### 回调接口

#### 网关对子设备的回调接口：IoTGwCallbacks
```java
public interface IoTGwCallbacks {
    /*
    * 当添加子设备时，在此回调中实现使能设备入网功能
    *
    * @param tp 子设备类型
    * @param permit 是否允许添加子设备。非0为允许；0为禁止。
    * @param timeout 配网超时时间。单位为秒
    * @return 成功，返回0；失败，非0
    * */
    int onGwAddDev(int tp, int permit, int timeout);

    /*
    * 当子设备被删除时，通过此回调通知用户
    *
    * @param dev_id 被删除子设备的 ID
    * @param type 设备类型
    * @return 成功，返回0；失败，非0
    * */
    void onGwDelDev(String dev_id, int type);

    /*
    * 在该回调中实现组操作处理命令的实现。比如加2个开关之后，就可以在APP上面创建群组，之后会回调到此
    *
    * @param action 组的操作类型。增加以及删除组操作。
    * @param dev_id 加入组 grp_id 的子设备 ID。
    * @param grp_id 组 ID。
    * @return 成功，返回0；失败，非0
    * */
    int onGwDevGrp(int action, String dev_id, String grp_id);

    /*
    * 在该回调中实现场景处理命令功能。场景同上。
    *
    * @param action 组的操作类型。增加以及删除，以及执行场景操作。
    * @param dev_id 加入组 grp_id 的子设备 ID。
    * @param grp_id 组 ID。
    * @param sce_id 场景 ID。
    * @return 成功，返回0；失败，非0
    * */
    int onGwDevScene(int action, String dev_id, String grp_id, String sce_id);

    /*
    * the subdevice bind result callback。绑定bind子设备成功之后，回调此函数。
    *
    * @param dev_id subdevice_id
    * @param op_ret 成功，返回0；失败，非0
    * */
    void onGwIfm(String dev_id, int op_ret);

    /*
    * the sigmesh topo update cb
    *
    * @param dev_id subdevice_id
    * */
    void onGwDevSigmeshTopoUpdate(String dev_id);

    /*
    * the sigmesh topo update cb
    *
    * @param dev_id subdevice_id
    * */
    void onGwDevSigmeshDevCacheDel(String dev_id);

    /*
    * Wake up the low power subdevice
    *
    * @param dev_id subdevice_id
    * */
    void onGwDevWakeup(String dev_id, int duration);

    /*
    * The sigmesh connection callback, tell devie to disconnect or connect timeout value
    *
    * @param sigmesh_dev_conn_inf_json json string
    * */
    void onGwDevSigmeshConn(String sigmesh_dev_conn_inf_json);
}

```


#### 网关基础回调接口：IoTCallbacks
```java
public interface IoTCallbacks {

    /*
    * the subdevice bind result callback。绑定bind子设备成功之后，回调此函数。
    *
    * @param dev_id subdevice_id
    * @param op_ret 成功，返回0；失败，非0
    * */
    void onGwDevStatusChanged(byte status);

    // 预留
    int onGwDevRevUpgradeInfo(Object fw);

    /**
     * GW设备进程重启请求入口
     *
     * @param type tuya sdk gateway reset type
     * 0：GW_LOCAL_RESET_FACTORY
     * 1：GW_REMOTE_UNACTIVE
     * 2：GW_LOCAL_UNACTIVE
     * 3：GW_REMOTE_RESET_FACTORY
     * 4：GW_RESET_DATA_FACTORY, need clear local data when active
     */
    void onGwDevRestartReq(int type);

    /**
     * dp查询回调
     @param event 参考DPEvent类说明
     */
    void onDpQuery(DPEvent event);

    /**
     * dp查询回调
     @param queries 参考DPQuery类说明
     */
    void onDpQuery(DPQuery[] queries);

    /**
     * dp事件回调
     *
     * @param cmd_tp 指令类型。
     * 0：LAN 触发；
     * 1：MQTT 触发；
     * 2：本地定时触发；
     * 3：场景联动触发；
     * 4：重发
     * @param dtt_tp 传输方式。
     * 0：单播；
     * 1：广播；
     * 2：组播；
     * 3：场景
     * @param cid cid == NULL，表示网关功能点；
     * cid != NULL，表示子设备功能点，cid 为子设备的 MAC 地址
     * @param mb_id 群组 ID，只有当 dtt_tp = 2 时，该字段才有效
     * @param event 参考DPEvent类说明
     */
    void onDpEvent(int cmd_tp, int dtt_tp, String cid, String mb_id, DPEvent event);

    // 预留
    void onGwDevObjDpCmd(Object dp);
    
    // 预留
    void onGwDevRawDpCmd(Object dp);

    // 预留
    void onGwDevDpQuery(Object dp_qry);

    // 预留
    int onDevUgInform(String dev_id, Object fw);

    /*
    * 子设备重置回调
    *
    * @param dev_id 需要重置的子设备 ID。
    * @param type 重置类型。
    * */
    void onDevReset(String dev_id, int type);

    // 预留
    int onOpeHttpcGetChcode(int is_gw, String dev_id, ChCode ch_code);
   
    /*
    * 在局域网下，断外网，执行子设备控制，会回调到此。主动通知用户把dp信息存入flash
    *
    * @param dev_id 需要重置的子设备 ID。
    * @param dpEvent dp事件。
    * @return 成功，返回0；失败，非0
    * */
    int onGwOfflineDpSave(String dev_id, DPEvent dpEvent);

    /*
    * GW外网状态变动回调
    *
    * @param status 需要重置的子设备 ID。
    * @param dpEvent dp事件。
    * @return 成功，返回0；失败，非0
    * */
    int onNetworkStatus(int status);

    /* 以下接口在工程模式使用 */

    /*
    * 设置网关控制器工作信道。如果非无线，设置该回调 NULL。
    *
    * @param channel 设置网关控制的无线工作信道。
    * @return 成功，返回0；失败，非0
    * */
    int onEngrGwSetChannel(int channel);

    /*
    * 获取网关控制器无线工作信道。如果非无线，设置该回调 NULL。
    *
    * @param channel 保存获取到的网关控制器的无线工作信道。
    * @return 成功，返回0；失败，非0
    * */
    int onEngrGwGetChannel(int channel);

    /*
    * 上传本地日志回调。
    *
    * @param lengthLimit 参数 path 支持的最大长度。
    * @return 成功，返回0；失败，非0
    * */
    int onEngrGwGetLog(int lengthLimit);

    /*
    * 同步工程文件到普通模式运行下。
    *
    * @return 成功，返回0；失败，非0
    * */
    int onEngrGwSyncConfig();

    /*
    * 工程部署完成通知回调。回调中需要实现网关应用重启。
    *
    * @return 成功，返回0；失败，非0
    * */
    int onEngrGwEngineerFinish();
}

```

#### 网关APP应用层回调接口：IoTAppCallbacks
```java
public interface IoTAppCallbacks {

    /*
    * 在此回调中实现网关日志上传
    *
    * @param lengthLimit 参数 path 支持的最大长度。
    * */
    void onAppLogPath(int lengthLimit);

    /*
    * 布防模式切换时調用
    *
    * @param modeStr 布防模式。
    * 0：disarm 撤防（固定不变）
    * 1：在家布防
    * 2：离家布防
    * @param time 布防模式延时时间。单位是秒，0表示没有延时
    * @param isSound 是否要播放声音。
    * 0：表示不需要播放声音，只切换模式，主要用于一些程序起动时，不需要播放声音的场景。
    * 1：需要播放声音
    * */
    void onHomeSecurityIf(String modeStr, int time, int isSound);

    /*
    * 当环境设备的某个 DP 触发了设备报警后，通知应用层
    *
    * @param cid 设备 ID。
    * @param jsonDpInf 触发报警的 dp 信息。json格式字符串
    * */
    void onHomeSecurityAlarmDev(String cid, String jsonDpInf);

    /*
    * 环境设备哪个 dp 触发了设备报警，通知应用层
    *
    * @param cid 备 ID。
    * @param jsonDpInf 触发报警的 dp 信息。json格式字符串
    * */
    void onHomeSecurityAlarmEnvDev(String cid, String jsonDpInf);

    /*
    * 当有延时报警时（未报警还在延时中时），通过此接口通知应用
    *
    * @param alarmStatus 报警延时状态
    * 0：ALARM_DELAY_DONOT_CREATE：报警延时未创建
    * 1：ALARM_DELAY_COUNTDOWN：报警延时进行中
    * 2：ALARM_DELAY_END：报警延时结束
    * */
    void onHomeSecurityAlarmDelayStatus(int alarmStatus);

    /*
    * 一些事件通知应用
    *
    * @param securityEventStatus 事件类型。
    * 0：DISARMED_EVENT：disarm 撤防事件（可不关心，if_cb接口有此通知）。
    * 1：ARMED_EVENT：进入布防 (倒计时后,在家或离家)。
    * 2：BYPASS_EVNET：有忽略事件发生，主要用于触发播放声音。
    * 3：WARING_COUNTDOWN：报警倒计时开始，并通过参数 data 传送时间。
    * @param data 预留
    * */
    void onHomeSecurityEvent(int securityEventStatus, int data);

    /*
    * 取消报警回调函数
    * */
    void onHomeSecurityCancelAlarm();

    /*
    * 预留
    * */
    void onHomeSecurityAlarmDevNew(String cid, String jsonDpInf, int devType);

    /*
    * 预留
    * */
    void onHomeSecurityEnterAlarm(int alarmStatus, String alarmInfo);


    /*
    * 工程模式恢复到正常模式完成回调通知
    *
    * @param path 参数 path 支持的最大长度。
    * */
    void onGwEngrToNormalFinish(String path);

    /*
    * 网关每隔 3 秒检查所有的子设备。如果子设备在心跳包超时内，子设备没有发送心跳给网关，则网关会设置子设备为离线，通过 hb_send_cb 通知用户至少三次。
    *
    * @param dev_id 参数 path 支持的最大长度。
    * */
    void onDevHeartbeatSend(String dev_id);

    /*
    * 工程模式恢复到正常模式完成回调通知
    *
    * @param dev_id 参数 path 支持的最大长度。
    * @param scePanel 参数 path 支持的最大长度。
    * @param btn_num 参数 path 支持的最大长度。
    * @return 成功，返回0；失败，非0
    * */
    int onEngrGwScePanel(String dev_id, ScePanel scePanel, int btn_num);
}
```

#### 网关门锁回调接口：IoTDoorLockCallbacks
```java
public interface IoTDoorLockCallbacks {

    /*
    * zigbee数据下发回调
    *
    * @param addr zigbee设备mac地址
    * @param data zigbee指令zcl payload
    * @param cluster_id zigbee指令cluster id
    * @param command_id zigbee指令command id
    * */
    int onZigbeeDataSend(String addr, byte[] data, int cluster_id, int command_id);

}
```

### 主调接口

#### getInstance：获取网关实例
```java
/*
* 获取网关实例
*
* @return 成功，返回实例；失败，返回null
* */
public static IoTGatewaySDKManager getInstance();
```

#### IotGatewayStart：启动网关设备
```java
/**
     * 启动网关设备
     *
     * @param context context of app
     * @param config  config
     * @return 如果start成功，返回0。
     */
    public int IotGatewayStart(Context context, Config config, GwAttachAttr[] attachAttrs);
```

#### startFwDownload：收到新固件提醒后，调用此函数开始下载固件
```java
/*
* 启动固件下载动作
*
* @param fwName 固件名称（绝对路径在config事先设置，这里可以指定相对路径）
* @param devId  子设备id
* @return 成功，返回0；失败，返回非0
* */
final public int startFwDownload(String fwName, String devId);
```

#### setEngineerMode：设置为工程模式
```java
/*
* 设置为工程模式
* */
public void setEngineerMode();
```

#### getEngineerMode：检测是否处于工程模式
```java
/*
* 检测是否处于工程模式
*
* @return 1: in engineer mode
* */
public int getEngineerMode();
```

#### gwUnactive：重置网关设备
```java
/*
* unactive this hardware from tuya-cloud
*
* @return 0: success  Other: fail
* */
public int gwUnactive();
```

#### gwEngineerCheck：网关开始从工程模式恢复到正常模式，切换完成会回调onGwEngrToNormalFinish
```java
/*
* gw started from engineer mode to normal mode.
*
* @param engineer_path  工程模式存储路径。
* @return 0: success  Other: fail
* */
public int gwEngineerCheck(String engineerPath);
```

#### homeSecurityInfoSet：家庭安防。设置当前的布防撤防模式，比如遥控器布防撤防
```java
/*
* 设置当前的布防撤防模式
*
* @param mode  布防模式。
* “0”：disarm 撤防（固定不变）
* “1”：在家布防
* “2”：离家布防
* @param nodeId  触发设置的设备 ID。
* “0123456789”：表明是 0123456789 这个设备触发了布防，可以为 NULL。
* @param delay  延时布防时间。JSON 格式：{“<mode_str>”: }。
* 例如：JSON 字符串：如 {“1”: 1000}，表明在家布防的延时时间为 1000s，可以为 NULL，即以手机 App 设置的时间为主。目前可以不用设置。
* @return 0: success  Other: fail
* */
public int homeSecurityInfoSet(String mode, String nodeId, String delay);
```

#### homeSecurityAlarmInfoGet：家庭安防。获取当前安防状态信息
```java
/*
* 获取当前安防状态信息
*
* @return 成功返回安防信息实例；失败，null
* */
public AlarmInfo homeSecurityAlarmInfoGet();
```

#### netModeReport：同步当前的网络模式
```java
/*
* 同步当前的网络模式
*
* @param mode  网络模式
* 0：HOME_SECURITY_NET_MODE_WAN 以太网
* 1：HOME_SECURITY_NET_MODE_WIFI wifi
* 2：HOME_SECURITY_NET_MODE_4G 4G
* @return 成功返回0；失败，返回非0
* */
public int netModeReport(int mode);
```

#### freshDeviceHeartbeat：子设备在线管理：当网关收到子设备心跳信息，调用此函数刷新该子设备心跳状态
```java
/*
* 当网关收到子设备心跳信息，调用此函数刷新该子设备心跳状态
*
* @param devId sub-device_id
* @return 0: success  Other: fail
* */
public int freshDeviceHeartbeat(String devId);
```

#### sendDP：发送dp
```java
/**
* 发送dp
*
* @param dpId   dp id
* @param type 类型 DPEvent.Type
* @param val  值
* @return 0: success  Other: fail
*/
final public int sendDP(String devId, int dpId, int type, Object val);
```

#### sendDP：发送dp带时间戳
```java
/**
* 发送dp带时间戳
*
* @param dpId        dp id
* @param type      类型 DPEvent.Type
* @param val       值
* @param timestamp 时间戳 单位秒
* @return 0: success  Other: fail
*/
final public int sendDPWithTimeStamp(String devId, int dpId, int type, Object val, int timestamp);
```

#### setDeviceHeartbeatTimeout：子设备在线管理：子设备心跳超时时间设置 
```java
/**
* 子设备心跳超时时间设置，若网关在超过设置时间内未收到子设备心跳，则网关会将子设备设置为离线。
*
* @param devId        子设备 ID。
* @param timeout      心跳超时时间，单位为秒。如果设置为 0xffffffff，该子设备将会被跳过心跳检查，会一直在线。
* @return 0: success  Other: fail
*/
public int setDeviceHeartbeatTimeout(String devId, long timeout);
```

#### sysManageHeartbeatInit：启动子设备心跳管理能力。
```java
/**
* 启动子设备心跳管理能力。网关每隔 3 秒检查所有的子设备。如果子设备在心跳包超时内，子设备没有发送心跳给网关，则网关会设置子设备为离线，通过onDevHeartbeatSend通知用户至少三次。
*
* @return 0: success  Other: fail
*/
public int sysManageHeartbeatInit();
```

#### gwUnbindDevice：解绑子设备。
```java
/**
* 解绑子设备。
*
* @param id        需要从网关解绑的子设备 ID。
* @return 0: success  Other: fail
*/
public int gwUnbindDevice(String id);
```

#### gwSubDeviceUpdate：更新子设备的固件版本号。
```java
/**
* 更新子设备的固件版本号。
*
* @param id        子设备 ID。不能超过 25 个字节。
* @param version   子设备固件版本号。参看本文中涂鸦对版本号的格式的定义。
* @return 0: success  Other: fail
*/
public int gwSubDeviceUpdate(String id, String version);
```

#### deviceOnlineStatusUpdate：网关更新子设备在线/离线状态。
```java
/**
* 网关更新子设备在线/离线状态。
*
* @param devId        子设备 ID。不能超过 25 个字节。
* @param online       在线状态。
* 1 为在线，
* 0 为离线。
* @return 0: success  Other: fail
*/
public int deviceOnlineStatusUpdate(String devId, int online);
```

#### gwBindDevice：网关绑定子设备接口。
```java
/**
* 网关绑定子设备接口。当应用使网关添加子设备时，调用此接口绑定发现的子设备。
*
* @param type        子设备类型。
* @param detailTypeDefine   设备类型，用户自己定义，区分不同类的设备。
* @param id   子设备 id。长度不能超过25个字节。
* @param productKey   子设备 product key。长度不能超过16个字节。
* @param version   子设备固件版本号。长度不能超过10个字节
* @return 0: success  Other: fail
*/
public int gwBindDevice(int type, int detailTypeDefine, String id, String productKey, String version);
```

#### deviceUpgradeProgressReport：上报升级进度
```java
/**
* 上报升级进度。
*
* @param percent 升级进度值。0~99
* @param devId   子设备时，传入子设备的 ID ；网关时，传入 NULL
* @param type    设备类型
* @return 0: success  Other: fail
*/
public int deviceUpgradeProgressReport(int percent, String devId, byte type);
```

#### deviceUpgradeResultReport：上报设备升级结果。
```java
/**
* 上报设备升级结果。
*
* @param devId   子设备时，传入子设备的 ID ；网关时，传入 NULL
* @param type    设备类型
* @param result 升级结果
* @return 0: success  Other: fail
*/
public int deviceUpgradeResultReport(String devId, byte type, int result);
```

#### refuseUpgrade：升级的过程中停止升级。
```java
/**
* 升级的过程中停止升级。
*
* @param devId   子设备时，传入子设备的 ID ；网关时，传入 NULL
* @return 0: success  Other: fail
*/
public int refuseUpgrade(String devId);
```

#### gwVersionUpgrade：升级完成后，更新网关或网关适配器固件版本号。
```java
/**
* 升级完成后，更新网关或网关适配器固件版本号
*
* @param type     网关类型；参考GP_DEV_xxx定义
* @param verion   固件版本号
* @return 0: success  Other: fail
*/
public int gwVersionUpgrade(byte type, String verion);
```

#### homeSecuritySyncAlarmStatus：同步报警状态
```java
/**
* 同步报警状态。应用层安防标准 DP 32 的状态变化，通过该 API 接口同步报警状态到 SDK 中。
*
* @param alarmStatus     同步的状态
* 0：正常
* 1：报警中
* @return 0: success  Other: fail
*/
public int homeSecuritySyncAlarmStatus(byte alarmStatus);
```

#### setGwEngrLogPath：设置工程模式上传日志的文件路径
```java
/**
* 设置工程模式上传日志的文件路径
*
* @param path     路径
* @return 0: success  Other: fail
*/
public int setGwEngrLogPath(String path);
```

#### setGwAppLogPath：设置普通模式网关上传日志的文件路径
```java
/**
* 设置普通模式网关上传日志的文件路径
*
* @param path     路径
* @return 0: success  Other: fail
*/
public int setGwAppLogPath(String path);
```

#### IotGateWayDevTraversal：子设备遍历，通过此接口可以遍历网关下所有的子设备
```java
/**
* 获取子设备信息：子设备遍历，通过此接口可以遍历网关下所有的子设备
*
* @return 成功，返回DevDescIf对象；失败或结束，返回null
*/
public static DevDescIf IotGateWayDevTraversal();
```

#### IotDoorLockInit：初始化整个zigbee门锁管理服务
```java
/**
     * 初始化整个zigbee门锁管理服务，在联网SDK初始化成功之后在调用该接口
     *
     * @param pan_id 网关panID
     * @param netWork_key 网关Key
     * @param net_stat 网关网络状态
     *
     * @return 成功，返回0；失败，返回错误码
     */
    public static int IotDoorLockInit(int pan_id, byte[] netWork_key, boolean net_stat);
```

#### IotDoorLockTypeGet：zigbee门锁类型获取
```java
   /**
     * zigbee门锁类型获取，需要在绑定门锁设备前调用该接口获取门锁类型，以便后续SDK判断需要
     * 		现仅支持通过model_id查找，manufacturer_name字段可输入为NULL
     *
     * @param manufacturer_name zigbee设备厂商号
     * @param model_id zigbee设备modeid
     * TRUE:正常连接
     * FALSE:断开连接
     *
     * @return 成功，返回门锁类型；失败，返回错误码
     *
     *      TUYA_DOORLOCK_TYPE_COM = 0x1,   //涂鸦门锁类型——通用
     *      TUYA_DOORLOCK_TYPE_FLATS,      //涂鸦门锁类型——公寓
     *      TUYA_DOORLOCK_TYPE_FLATS_B,     //涂鸦门锁类型——商用
      *
     */
    public static int IotDoorLockTypeGet(String manufacturer_name, String model_id);
```

#### IotDoorLockNetstatNotify：zigbee门锁服务器连接状态通知
```java
    /**
     * zigbee门锁服务器连接状态通知，在服务器连接状态变动时，调用该通知接口
     *
     * @param stat 服务器连接状态
     * TRUE:正常连接
     * FALSE:断开连接
     *
     */
    public static void IotDoorLockNetstatNotify(boolean stat);
```

#### IotDoorLockAdd：门锁设备添加
```java
    /**
     * 门锁设备添加，在云端绑定返回成功之后，调用该接口
     *
     * @param addr zigbee设备mac地址
     * @param type 门锁类型
     *
     * @return 成功，返回0；失败，返回错误码
     */
    public static int IotDoorLockAdd(String addr, int type);
```

#### IotDoorLockDel：门锁设备删除
```java
    /**
     * 在删除设备后调用该接口
     *
     * @param addr zigbee设备mac地址
     * @return 成功，返回0；失败，返回错误码
     */
    public static int IotDoorLockDel(String addr);
```

#### IotDoorLockDataSend：门锁dp下发
```java
    /**
     * 门锁dp下发
     *
     * @param addr zigbee设备mac地址
     * @param type 门锁类型
     * @param rawDP 门锁dp数据
     * @return 成功，返回0；失败，返回错误码
     */
    public static int IotDoorLockDataSend(String addr, int type, RecvRawDP rawDP);
```

#### IotDoorLockDataSend：门锁dp下发
```java
    /**
     * 门锁dp下发
     *
     * @param addr zigbee设备mac地址
     * @param type 门锁类型
     * @param objDP 门锁dp数据
     * @return 成功，返回0；失败，返回错误码
     */
    public static int IotDoorLockDataSend(String addr, int type, RecvObjDP objDP);
```

#### IotDoorLockDataReport：门锁数据上报
```java
    /**
     * 门锁数据上报，将设备端发送的原始数据内容转发透传
     *
     * @param addr zigbee设备mac地址
     * @param type 门锁类型
     * @param data ZCL的payload
     * @param cluster_id zigbee指令Cluster id
     * @param command_id zigbee指令Command id
     * @return 成功，返回0；失败，返回错误码
     */
    public static int IotDoorLockDataReport(String addr, int type, byte[] data, int cluster_id, int command_id);
```

#### IotDoorLockDataQuery：门锁dp查询
```java
      /**
     * 门锁dp查询，仅支持查询所有DP
     *
     * @param addr zigbee设备mac地址
     * @param type 门锁类型
     * @param dp_qry 需要查询的门锁dp
     * @return 成功，返回0；失败，返回错误码
     */
    public static int IotDoorLockDataQuery(String addr, int type, DPQuery dp_qry);
```


## 辅助类说明

#### DPEvent
```java
public class DPEvent {

    public class Type {
        //Boolean
        public static final int PROP_BOOL = 0;
        //Integer
        public static final int PROP_VALUE = 1;
        //String
        public static final int PROP_STR = 2;
        //Integer
        public static final int PROP_ENUM = 3;
        //Integer
        public static final int PROP_BITMAP = 4;
        //RAW
        public static final int PROP_RAW = 5;
    }

    public int dpid;            // dp id
    public short type;          // dp type
    public Object value;        // dp value
    /**
     * 发生的时间戳(单位秒)
     */
    public int timestamp;  // dp happen time. if 0, mean now
    ...
}
```

#### ScePanel
```java
public class ScePanel {

    public static final int GRP_LEN = 5;
    public static final int SCE_LEN = 3;
    public static final int SCE_NAME_LEN = 4;
    public static final int MAX_BTN = 9;

    public int btn;         // 按键 ID
    public String grp;      // 群组 ID。
    public String sce;      // 场景 ID。
    public String sce_name; // 场景名称。
    ...
}
```

#### AlarmInfo
```java
public class AlarmInfo {
    /**
    * 布防模式。
    * “0”：disarm 撤防（固定不变）
    * “1”：在家布防
    * "2"：离家布防
    */
    public String alarm_mode; // 32byte max

    /**
    * 报警状态。
    * 1：报警状态中 （报警延时，以及报警中）
    * 0：正常状态。
    */
    public byte alarm_status;

    /**
    * 布防延迟状态。
    * 1：布防延迟中
    * 0：其他
    */
    public byte enable_countdown_status;
    ...
}
```

#### ChCode
```java
public class ChCode {
    public static final int CH_CODE_LMT = 15;
    public static final int CH_SN_LMT = 20;
    public static final int CH_REPORT_CODE_LMT = 20;
    public static final int CH_MANU_ID_LMT = 10;
    public static final int CH_VERSION_LMT = 10;
    public static final int CH_ENCRYPT_KEY_LMT = 20;

    public String ch_name;
    public String ch_code;
    public String ch_sn;
    public String ch_report_code;
    public String ch_manu_id;
    public String ch_version;
    public String ch_encrype_key;
    ...
}

#### DevQos
```java
public class DevQos {
    public int msNodeID;
    public int mfNodeID;
    public int mRssi;
    public int mLqi;
    public String mFid;
    ...
}
```

#### DevDescIf：指向设备信息的结构体指针，用户可从中读取设备信息。
```java

public class DevDescIf {
    public String mId;
    public String mSwVer;
    public String mSchemaId;
    public String mProductKey;
    public String mFirmwareKey;
    public boolean mIsOem;
    public String mSigmeshDevKey;
    public String mSigmeshMac;
    public int mUddd;
    public int mUddd2;
    public int mTp;
    public int mSubTp;
    public String mUuid;
    public int mAbi;
    public boolean mBind;
    public boolean mSync;
    public boolean mSigmeshSync;
    public boolean mBleMeshBindReptSync;
    public boolean mBindStatus;
    public GwAttachAttr[] mAttr;
    public boolean mResetFlag;
    public int mSubListFlag;
    public DevQos mDevQos;
    ...
}
```

#### GwAttachAttr
 ```java
public class GwAttachAttr {

    /* tp value list: */
    public static final byte DEV_NM_ATH_SNGL   =  0;     // 主联网模块固件
    public static final byte DEV_BLE_SNGL = 1;           // BLE固件
    public static final byte DEV_ZB_SNGL = 3;            // ZigBee固件
    public static final byte DEV_NM_NOT_ATH_SNGL = 9;    // MCU固件

    // 使用attach模块固件type时，需和平台上添加的attach固件type一一对应
    public static final byte  DEV_ATTACH_MOD_1 = 10;
    public static final byte  DEV_ATTACH_MOD_2 = 11;
    public static final byte DEV_ATTACH_MOD_3 = 12;
    public static final byte  DEV_ATTACH_MOD_4 = 13;
    public static final byte  DEV_ATTACH_MOD_5 = 14;
    public static final byte  DEV_ATTACH_MOD_6 = 15;
    public static final byte  DEV_ATTACH_MOD_7 = 16;
    public static final byte  DEV_ATTACH_MOD_8 = 17;
    public static final byte  DEV_ATTACH_MOD_9 = 18;
    public static final byte  DEV_ATTACH_MOD_10 = 19;

    public byte tp;
    public String ver;
    ...
}
```

#### DPQuery
```java
public class DPQuery {

    public String cid;
    public byte []dpid;
    ...
}
```

#### RecvObjDP
```java
public class RecvObjDP {

    // DP_TRANS_TYPE_T
    public static final byte DP_CMD_LAN  =    0;       // cmd from LAN
    public static final byte DP_CMD_MQ    =   1 ;      // cmd from MQTT
    public static final byte DP_CMD_TIMER =   2;       // cmd from Local Timer
    public static final byte DP_CMD_SCENE_LINKAGE = 3;  // cmd from scene linkage
    public static final byte DP_CMD_RELIABLE_TRANSFER = 4; // cmd from reliable transfer
    public static final byte DP_CMD_BT   =    5;      // cmd from bt
    public static final byte DP_CMD_SCENE_LINKAGE_LAN = 6;  // cmd from lan scene linkage

    public int cmd_tp;
    public int dtt_tp;
    public String cid;
    public String mb_id;
    public DPEvent []dpEvent;
    ...
}
```

#### RecvRawDP
```java
public class RecvRawDP {

    // DP_TRANS_TYPE_T
    public static final byte DP_CMD_LAN  =    0;       // cmd from LAN
    public static final byte DP_CMD_MQ    =   1 ;      // cmd from MQTT
    public static final byte DP_CMD_TIMER =   2;       // cmd from Local Timer
    public static final byte DP_CMD_SCENE_LINKAGE = 3;  // cmd from scene linkage
    public static final byte DP_CMD_RELIABLE_TRANSFER = 4; // cmd from reliable transfer
    public static final byte DP_CMD_BT   =    5;      // cmd from bt
    public static final byte DP_CMD_SCENE_LINKAGE_LAN = 6;  // cmd from lan scene linkage

    public int cmd_tp;
    public int dtt_tp;
    public String cid;
    public int dpid;
    public String mb_id;
    public byte []data;
    ...
}
```

## 如何获得技术支持
You can get support from Tuya with the following methods:

Tuya Smart Help Center: https://support.tuya.com/en/help  
Technical Support Council: https://iot.tuya.com/council/   

## 使用的开源License
This Tuya Android Device SDK Sample is licensed under the MIT License.
