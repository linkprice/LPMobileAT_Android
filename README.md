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
    
    /* CPI 실적 전송
     * param1: 링크프라이스에서 발급받은 머천트ID
     * param2: User-Agent 값
     * param3: Client IP 값
     * param4: CPI 실적 전송 사용 여부(true: 사용, false: 미사용)
    */ 
    lpMobileAT.autoCpi("merchant_id", "user_agent", "remote_addr",false);
}
```



# 4. 실적 전송

## 4.1 CPS 실적 전송 (상품 구매)

```
LpMobileAT lpMobileAT = new LpMobileAT(this, getIntent());

Map<String, Object> order = new HashMap<>();
order.put("order_id", "o111232-323234"); // order code
order.put("final_paid_price", 59000);    // fianl paid price for order
order.put("currency", "KRW");            // currency
order.put("user_name", "tester");        // name of who order

Map<String, Object> lp = new HashMap<>();
lp.put("user_agent", "user agent");      // user agent
lp.put("remote_addr", "127.0.0.1");      // user IP
lp.put("merchant_id", "clickbuy");       // merchant ID

lpMobileAT.setOrder(order, lp);

Map<String, Object> item = new HashMap<>();
item.put("product_id", "productCode");                  // product code
item.put("product_name", "sample");                     // product name
item.put("category_code", "111");                       // category code
item.put("category_name", "web");                       // category name
item.put("quantity", 1);                                // quantity 
item.put("product_final_price", 59000);                 // price
item.put("paid_at", "2019-02-12T11:13:44+00:00");       // paid time
item.put("confirmed_at", "");
item.put("canceled_at", "");

lpMobileAT.addItem(item);

lpMobileAT.send();
```

### 4.1.1 CPS 실적 전송 (상품 여러개)

```
LpMobileAT lpMobileAT = new LpMobileAT(this, getIntent());

Map<String, Object> order = new HashMap<>();
order.put("order_id", "o111232-323234"); // order code
order.put("final_paid_price", 70000);    // fianl paid price for order
order.put("currency", "KRW");            // currency
order.put("user_name", "tester");        // name of who order

Map<String, Object> lp = new HashMap<>();
lp.put("user_agent", "user agent");      // user agent
lp.put("remote_addr", "127.0.0.1");      // user IP
lp.put("merchant_id", "clickbuy");       // merchant ID

lpMobileAT.setOrder(order, lp);

Map<String, Object> item1 = new HashMap<>();
item.put("product_id", "productCode");                  // product code
item.put("product_name", "sample");                     // product name
item.put("category_code", "111");                       // category code
item.put("category_name", "web");                       // category name
item.put("quantity", 1);                                // quantity 
item.put("product_final_price", 59000);                 // price
item.put("paid_at", "2019-02-12T11:13:44+00:00");       // paid time
item.put("confirmed_at", "");
item.put("canceled_at", "");

lpMobileAT.addItem(item1);

Map<String, Object> item2 = new HashMap<>();
item.put("product_id", "productCode");                 // product code
item.put("product_name", "sample2");                   // product name
item.put("category_code", "222");                      // category code
item.put("category_name", "web2");                     // category name
item.put("quantity", 5);                               // quantity 
item.put("product_final_price", 11000);                // price
item.put("paid_at", "2019-02-12T11:13:44+00:00");      // paid time
item.put("confirmed_at", "");
item.put("canceled_at", "");

lpMobileAT.addItem(item2);

lpMobileAT.send();
```

## 4.2 CPA 실적 전송 (회원 가입, 미션 수행)

```
LpMobileAT lpMobileAT = new LpMobileAT(this, getIntent());

Map<String, Object> order = new HashMap<>();
order.put("unique_id", "unique ID");
order.put("final_paid_price", 0);
order.put("currency", "KRW");
order.put("member_id", "member ID");
order.put("action_code", "register");
order.put("action_name", "free register");
order.put("category_code", "register");

Map<String, Object> lp = new HashMap<>();
lp.put("merchant_id", "merchant ID"); 
lp.put("user_agent", "user agent");  
lp.put("remote_addr", "remote_addr");    

lpMobileAT.setOrder(order, lp);
lpMobileAT.send();
```

* 설치일 경우 orderCode 에 디바이스 아이디를 입력하면 중복으로 실적 인정되지 않습니다.

## 4.3 실적 전송 콜백 받기

```
final Context mContext = v.getContext();

LpMobileAT lpMobileAT = new LpMobileAT(mContext, getIntent());

Map<String, Object> order = new HashMap<>();
order.put("order_id", "o111232-323234"); // order code
order.put("final_paid_price", 59000);    // fianl paid price for order
order.put("currency", "KRW");            // currency
order.put("user_name", "tester");        // name of who order

Map<String, Object> lp = new HashMap<>();
lp.put("user_agent", "user agent");      // user agent
lp.put("remote_addr", "127.0.0.1");      // user IP
lp.put("merchant_id", "clickbuy");       // merchant ID

lpMobileAT.setOrder(order, lp);

Map<String, Object> item = new HashMap<>();
item.put("product_id", "productCode");                  // product code
item.put("product_name", "sample");                     // product name
item.put("category_code", "111");                       // category code
item.put("category_name", "web");                       // category name
item.put("quantity", 1);                                // quantity 
item.put("product_final_price", 59000);                 // price
item.put("paid_at", "2019-02-12T11:13:44+00:00");       // paid time
item.put("confirmed_at", "");
item.put("canceled_at", "");

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