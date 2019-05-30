# 1. LinkPrice 소스 다운로드 받기

1. LinkPrice 소스 를 App 에 포함시키기 위해서는, 먼저 해당 App 을 LinkPrice 머천트 사이트에 등록해야
   합니다.
2. LinkPrice 소스 는 [LinkPrice 소스 다운로드](http://ac.linkprice.com/info/com.linkprice.lpmat-1.0.0.jar) 링크를 클릭해서 직접 다운로드

# 2. LinkPrice 소스 를 프로젝트에 추가하기

## 2.1 Android Studio 에 추가하기

1. 다운로드 받은 파일을 프로젝트 소스 트리 libs 폴더에 복사해서 넣어줍니다.
2. 라이브러리로 등록한다.
   ![Android Studio 에 추가하기](http://ac.linkprice.com/images/app/android_studio_add_lpmat.png)


## 2.2 Eclipse 에 추가하기

1. 다운로드 받은 파일을 프로젝트 소스 트리 libs 폴더에 복사해서 넣어줍니다.

# 3. 프로그램 코드 수정하기

## 3.1 URL Scheme 등록

* URL Scheme 이 포함되어 있는 링크를 클릭할 때 해당 앱이 설치되어 있으면 앱이 실행되고, 설치가 되어 있지
  않으면 Google Play 스토어로 이동합니다.

### 3.1.1 AndroidMainfest.xml
```
<activity
	android:name=".MainActivity"
	android:label="@string/app_name" >
	<intent-filter>
		<action android:name="android.intent.action.MAIN" />
		<category android:name="android.intent.category.LAUNCHER" />
	</intent-filter>
	<intent-filter>
		<action android:name="android.intent.action.VIEW" />
		<category android:name="android.intent.category.DEFAULT" />
		<category android:name="android.intent.category.BROWSABLE" />
		<data
			android:host="{발급받은 머천트 아이디}"
			android:scheme="lpfront" />

	</intent-filter>
</activity>
```

## 3.2 권한 설정

* 인터넷 연결 및 네트워크 상태를 확인 할 수 있도록 권한을 추가해주세요.

### 3.2.1 AndroidMainfest.xml
```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

## 3.3 리시버 등록

* Google Play 스토어를 통해서 앱을 다운로드 받아 설치하면 설치 이벤트 가 발생합니다.
* 리시버를 등록해 주면 설치 이벤트 를 수신받을 수 있으며, 이 때 INSTALL_REFERRER 값이 전달됩니다.

### 3.3.1. AndroidMainfest.xml
```
<receiver
	android:name=".InstallReceiver"
	android:enabled="true"
	android:exported="true" >
	<intent-filter>
		<action android:name="com.android.vending.INSTALL_REFERRER" />
	</intent-filter>
</receiver>
```

### 3.2.2. 리시버 수정(InstallReceiver.java)
```
@Override
public void onReceive(Context context, Intent intent) {
    LpMobileAT lpMobileAT = new LpMobileAT(context, intent);
    lpMobileAT.setTagValueReceiver();
}
```

# 4. 실적 전송

## 4.1 CPS 실적 전송 (상품 구매)

```
LpMobileAT lpMobileAT = new LpMobileAT(this, getIntent());

Map<String, String> params = new HashMap<>();
params.put("m_id", "{발급받은 머천트 아이디}"); // merchant id (fixed)
params.put("orderCode", "orderCode");  // order code
params.put("memberID", "userInfo");    // user Info
params.put("currency", "KRW");
params.put("remoteAddress", "127.0.0.1"); // user ip

lpMobileAT.setParams(params);

Map<String, String> item = new HashMap<>();
item.put("productCode", "productCode");    // product code
item.put("qty", "1"); // order count
item.put("sales", "5000"); // total amount
item.put("category", "and_cps");
item.put("product", "productName"); // product name

lpMobileAT.addItem(item);

lpMobileAT.send();
```

### 4.1.1 CPS 실적 전송 (상품 여러개)

```
LpMobileAT lpMobileAT = new LpMobileAT(this, getIntent());

Map<String, String> params = new HashMap<>();
params.put("m_id", "{발급받은 머천트 아이디}"); // merchant id (fixed)
params.put("orderCode", "orderCode");  // order code
params.put("memberID", "userInfo");    // user Info
params.put("currency", "KRW");
params.put("remoteAddress", "127.0.0.1"); // user ip

lpMobileAT.setParams(params);

// Product 1
Map<String, String> item = new HashMap<>();
item.put("productCode", "productCode");    // product code
item.put("qty", "1"); // order count
item.put("sales", "5000"); // total amount
item.put("category", "and_cps");
item.put("product", "productName"); // product name

lpMobileAT.addItem(item);

// Product 2
Map<String, String> item2 = new HashMap<>();
item2.put("productCode", "productCode1");    // product code
item2.put("qty", "5"); // order count
item2.put("sales", "2000"); // total amount
item2.put("category", "and_cps");
item2.put("product", "productName1"); // product name

lpMobileAT.addItem(item2);

lpMobileAT.send();
```

## 4.2 CPA 실적 전송 (설치, 회원 가입, 미션 수행)

```
LpMobileAT lpMobileAT = new LpMobileAT(this, getIntent());

Map<String, String> params = new HashMap<>();
params.put("m_id", "{발급받은 머천트 아이디}"); // merchant id (fixed)
params.put("orderCode", "orderCode");  // order code
params.put("memberID", "userInfo");    // user Info
params.put("currency", "KRW");
params.put("remoteAddress", "127.0.0.1"); // user ip

lpMobileAT.setParams(params);

Map<String, String> item = new HashMap<>();
item.put("productCode", "member");    // product code (fixed)
item.put("qty", "1"); // order count (fixed)
item.put("sales", "0"); // total amount (fixed)
item.put("category", "and_cpe"); // and_cpe(설치) or and_cpa(회원 가입, 미션 수행)
item.put("product", "free"); // product name

lpMobileAT.addItem(item);

lpMobileAT.send();
```

* 설치일 경우 orderCode 에 디바이스 아이디를 입력하면 중복으로 실적 인정되지 않습니다.

## 4.3 실적 전송 콜백 받기

```
final Context mContext = v.getContext();

LpMobileAT lpMobileAT = new LpMobileAT(mContext, getIntent());

Map<String, String> params = new HashMap<>();
params.put("m_id", "{발급받은 머천트 아이디}"); // merchant id (fixed)
params.put("orderCode", "orderCode");  // order code
params.put("memberID", "userInfo");    // user Info
params.put("currency", "KRW");
params.put("remoteAddress", "127.0.0.1"); // user ip

lpMobileAT.setParams(params);

Map<String, String> item = new HashMap<>();
item.put("productCode", "productCode");    // product code
item.put("qty", "1"); // order count
item.put("sales", "5000"); // total amount
item.put("category", "and_cps");
item.put("product", "productName"); // product name

lpMobileAT.addItem(item);

lpMobileAT.send(new LpResponse() {

    @Override
    public void fail(String result) {
        super.fail(result);

        Toast.makeText(mContext, "postback : fail - " + result, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void success() {
        super.success();

        Toast.makeText(mContext, "postback : success", Toast.LENGTH_SHORT).show();
    }
});
```

# 5. 실행(배너 클릭시)할 때 마다 어필리에이트 변경

## MainActivity.java
```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    LpMobileAT lpMobileAT = new LpMobileAT(this, getIntent());
    lpMobileAT.setTagValueActivity();
}
```

# 6. 사용자 정의 링크(DeepLink)
* dl 파라미터로 전달됩니다.

# 7. ProGuard 사용시
* Proguard 설정 파일에 아래 내용을 반드시 추가해주세요.
```
# LpMobileAT
-keep class com.linkprice.lpmat.** { *; }
```
