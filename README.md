

# 제휴 마케팅이란

> 제휴 마케팅이란 제품/ 서비스 등을 판매하는 인터넷 업체(Merchant)가 고객을 끌어들이고 진열, 판매하는 공간으로 자신의 사이트 뿐만 아니라 다른 관련 사이트(Affiliate)로 까지 공간을 확장하여 
> 이때 발생하는 수입을 제휴맺은 사이트(Affiliate)와 공유하는 새로운 형태의 마케팅 기법입니다.

1. [제휴 마케팅 소개](https://helpdest.linkprice.com/pages/merchant-faq-introduce)

# lpmat library 설정

1. 프로젝트 build.gradle 에 maven { url ‘https://jitpack.io’ } 을  다음과 같이 추가합니다.

~~~groovy
allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
~~~

2. 앱 build.gradle 에 dependency를 아래와 같이 추가합니다.

~~~groovy
dependencies {
    ...
	implementation 'com.github.linkprice:LPMobileAT_Android:1.0.+'
}
~~~



# 1. AndroidMnifest.xml 설정

> URL  클릭 시 해당 앱이 설치되어 있으면 앱이 실행되고, 설치가 되어 있지 않으면 Google Play 스토어나 특정 페이지로 redirection 하기 위한 설정 및 앱 설치 시 refferer 값 전달을 위한 설정입니다.

### 1.1 scheme 및 host 설정

* 귀사의 앱의 AndroidManifest.xml파일에서 실행하고자 하는 Acrivity 아래에 intent-filter를 선언합니다.
* 예를 들어, 귀사의 게이트웨이 페이지의 URLdl "https://gw.linkprice.com/gateway/lpfront.php" 일때, 아래와 같이 선언합니다.

```xml
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
			android:host="gw.linkprice.com" android:scheme="https" />
	</intent-filter>
</activity>
```

* \<category\> 설명
  1. **android:name="android.intent.category.DEFAILT"**: 앱이 암시적 인텐트에도 응닫 할 수 있게 선언합니다.
  2. **android:name="android.intent.category.BROWSABLE"**: intent-filter가 웹 브라우저에서 접근하기 위해 선언합니다.
* \<data\> 설명
  1. **android:host**: 게이트웨이 페이지의 host부분을 선언합니다.
  2. **android:scheme**: 게이트웨이 페이지의 scheme(일반적으로 http나 https)을 선언합니다.

### 1.2 권한 설정

* 인터넷 연결 및 네트워크 상태를 확인 할 수 있도록 AndroidManifest.xml에 권한을 추가해주세요.

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

### 1.3 리시버 등록

* Google Play 스토어를 통해서 앱을 다운로드 받아 설치하면 설치 이벤트 가 발생합니다.
* AndroidManifest.xml에 리시버를 등록해 주면 설치 이벤트 를 수신받을 수 있으며, 이 때 INSTALL_REFERRER 값이 전달됩니다.

```xml
<receiver
	android:name=".InstallReceiver"
	android:enabled="true"
	android:exported="true" >
	<intent-filter>
		<action android:name="com.android.vending.INSTALL_REFERRER" />
	</intent-filter>
</receiver>
```

### 1.4 리시버 수정(InstallReceiver.java)
```java
@Override
public void onReceive(Context context, Intent intent) {
    LpMobileAT lpMobileAT = new LpMobileAT(context, intent);
    lpMobileAT.setTagValueReceiver();
}
```

# 2. 게이트웨이 페이지

1. user-agent의 값으로부터 계산하여 android일 경우, 아래의 형식으로 URL을 생성하여, 이 URL로 redirect 한다.

~~~
intent://gw.linkprice.com?lpinfo=A100000131|2600239200004E|0000|B|1&target_url=https://www.linkprice.com/path/page?pid=17234#Intent;scheme=https;package=com.linkprice.test-app;S.browser_fallback_url=https://www.linkprice.com/your_path/?param=values;end
~~~

2. 각 변수 설명
   1. **https** 게이트웨이 페이지의 scheme: 일반적으로 http나 https 입니다
   2. **gw.linkprice.com** 게이트웨이 페이지의 host: 게이트웨이 페이지의 host 부분만 추출합니다.
   3. **lpinfo=A100000131|2600239200004E|0000|B|1** 링크프라이스가 게이트웨이 페이지로 넘길 때 같이 넘긴 lpinfo의 값
   4. **target_url=https://www.linkprice.com/path/page?pid=17234** 링크프라이스가 게이트웨이 페이지로 넘길 때 같이 넘긴 target_url의 값
   5. **com.linkprice.test-app** 귀사의 Android APP의 package name
   6. **https://www.linkprice.com/your_path/?param=values** 만일 앱이 설치 되어 있지 않을 경우 redirection할 URL

# 3. 실적 전송

### 3.1 CPS 실적 전송 (상품 구매)

```java
LpMobileAT lpMobileAT = new LpMobileAT(mContext, getIntent());

Map<String, Object> order = new HashMap<>();
order.put("order_id", "o111232-323234");            // order ID
order.put("final_paid_price", 70000);               // total price user paid
order.put("currency", "KRW");                       // currency
order.put("user_name", "tester");                   // user name

Map<String, Object> lp = new HashMap<>();
lp.put("user_agent", "user agent info");            // user agent
lp.put("remote_addr", "127.0.0.1");                 // remote address
lp.put("merchant_id", "clickbuy");                  // merchant ID that linkprice provide

lpMobileAT.setOrder(order, lp);

Map<String, Object> item = new HashMap<>();
item.put("product_id", "productCode");              // product code
item.put("product_name", "product name");           // product name
item.put("category_code", "category code");         // category code
item.put("category_name", "category name");         // category name
item.put("quantity", 1);                            // quantity
item.put("product_final_price", 59000);             // amount user paid
item.put("paid_at", "2019-02-12T11:13:44+00:00");   // time user paid
item.put("confirmed_at", "");
item.put("canceled_at", "");

lpMobileAT.addItem(item);

Map<String, Object> item2 = new HashMap<>();
item2.put("product_id", "productCode2");            // product code
item2.put("product_name", "product name2");         // product name
item2.put("category_code", "category code2");       // category code
item2.put("category_name", "category name2");       // category name
item2.put("quantity", 1);                           // quantity
item2.put("product_final_price", 11000);            // amount user paid
item2.put("paid_at", "2019-02-12T11:13:44+00:00");  // paid time
item2.put("confirmed_at", "");
item2.put("canceled_at", "");

lpMobileAT.addItem(item2);

lpMobileAT.send();
```

* user agent이 없을 경우 null로 보내 주십시요.

* 쿠폰 및 마일리지 사용에 따른 "product_final_price"는 아래 링크를 확인 하여 주세요.

  [product_final_price 계산](<https://github.com/linkprice/MerchantSetup/blob/master/CPS/README.md#final_paid_price>)

* 자세한 데이터에 대한 설명은 아래 링크플 확인 하여 주세요.

  [CPS 실적 데이터 설명](<https://github.com/linkprice/MerchantSetup/tree/master/CPS#4-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%8B%A4%EC%A0%81-%EC%A0%84%EC%86%A1>)

### 3.2 CPA 실적 전송 (회원 가입, 미션 수행)

```java
LpMobileAT lpMobileAT = new LpMobileAT(mContext, getIntent());

Map<String, Object> order = new HashMap<>();
order.put("unique_id", "");
order.put("final_paid_price", 0);
order.put("currency", "KRW");
order.put("member_id", "member ID");
order.put("action_code", "register");
order.put("action_name", "회원가입");
order.put("category_code", "register");

Map<String, Object> lp = new HashMap<>();
lp.put("merchant_id", "merchant ID");       // merchant id
lp.put("user_agent", "user agent");         // order code
lp.put("remote_addr", "remote address");    // user Info

lpMobileAT.setOrder(order, lp);
lpMobileAT.send();
```

*  CPA경우 일일 IP제한(3개)이 있으므로, 사용자의 실제 IP가 넘어오지 않을 경우, 실적에 중대한 오류가 발생됩니다.

* CPA 데이터에 대한 자세한 설명은 아래 링크를 확인하여 주세요.

  [CPA 실적 데이터 설명](<https://github.com/linkprice/MerchantSetup/tree/master/CPA#3-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%8B%A4%EC%A0%81-%EC%A0%84%EC%86%A1>)

### 3.3 CPI 실적 전송(InstallReceiver.java)

~~~java
@Override
	public void onReceive(Context context, Intent intent) {

        // set tag_value at receiver
        LpMobileAT lpMobileAT = new LpMobileAT(context, intent);
        lpMobileAT.setTagValueReceiver();

        /* CPI sending data
         * param1: merchant ID 
         * param2: user agent information
         * param3: user remote address
         * param4: Send CPI data - true: on, false: off
        */ 
        lpMobileAT.autoCpi("clickbuy", "user_agent", "remote_addr",false);

    }
~~~

* CPI 경우 일일 IP제한(3개)이 있으므로, 사용자의 실제 IP가 넘어오지 않을 경우, 실적에 중대한 오류가 발생됩니다.



# 4. 실행(배너 클릭시)할 때 마다 어필리에이트 변경

## MainActivity.java
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    LpMobileAT lpMobileAT = new LpMobileAT(this, getIntent());
    lpMobileAT.setTagValueActivity();
}
```

# 5. 사용자 정의 링크(DeepLink)
* target_url 변수로 전달 됩니다.
* DeepLink에 해당하는 Activity가 존재 하지 않을 경우, 절대 오류가 나오지 않아야 합니다.

~~~java
LpMobileAT lpMobileAT = new LpMobileAT(this, getIntent());

String deepLink = lpMobileAT.getDl();

if(Pattern.matches("regex", deepLink)) {
    Intent intent = new Intent(this, Activity.class);
    startActivity(intent);
} else if (Pattern.matches("regex2", deepLink) {
    Intent intent = new Intent(this, Activity2.class);
    startActivity(intent);
} else {
    Intent intent = new Intent(this, defaultActivity.class);
    startActivity(intent);
}
~~~