package kr.co.cardledger;

/**
 * 카드 사용내역을 담을 데이터 클래스
 */
public class CardData {
	private String cardCompany =""; // 카드회사
	private String price =""; 		// 가격
	private String shop =""; 		// 사용처
	private String date =""; 		// 날짜 밀리세컨드
	private int buy;					//  승인 or 취소
	private long millis;					// 저장시간 밀리세컨

	public String getCardCompany() {
		return cardCompany;
	}

	public void setCardCompany(final String cardCompany) {
		this.cardCompany = cardCompany;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(final String price) {
		this.price = price;
	}

	public String getShop() {
		return shop;
	}

	public void setShop(final String shop) {
		this.shop = shop;
	}

	public String getDate() {
		return date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public int getBuy() {
		return buy;
	}

	public void setBuy(final int buy) {
		this.buy = buy;
	}

	public long getMillis() {
		return millis;
	}

	public void setMillis(long millis) {
		this.millis = millis;
	}
	
	

}
