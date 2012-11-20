package kr.co.cardledger;

import android.telephony.TelephonyManager;

/*
 *  디바이스 정보
 */
public class DeviceInfo{
	private static TelephonyManager _telephony = null;
	private static DeviceInfo di = new DeviceInfo();
	private DeviceInfo(){

	}

	public static DeviceInfo setDeviceInfo(final TelephonyManager telphony){
		_telephony = telphony;
		return di;
	}

	/**
	 * 전화번호 얻기
	 * @return
	 * 	전화번호
	 */
	public String getDeviceNumber(){
		String phoneNum = (_telephony != null)?_telephony.getLine1Number():"";
		//String cellNum = phoneNum.substring(phoneNum.length()-8, phoneNum.length());
		String cellNum = phoneNum.replace("+82", "0");	//82형식일경우는 0으로 바꿔준다.
		return cellNum;	//  010,011
	}

	/**
	 * 디바이스 아이디 얻기
	 * @return
	 * 디바이스 아이디
	 */
	public String getMyDeviceID(){
		return (_telephony != null)? _telephony.getDeviceId():"";
	}

	/**
	 * SIM 값 얻기
	 * @return
	 * SIM
	 */
	public String myDeviceSIM(){
		return (_telephony != null)? _telephony.getSimSerialNumber():"";
	}

}
