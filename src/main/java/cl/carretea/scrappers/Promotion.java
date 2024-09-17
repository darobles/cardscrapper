package cl.carretea.scrappers;

import java.util.Date;
import java.util.List;

public class Promotion {

	int card_id;
	String institution_name;
	List<String> cards_name;
	String name_store;
	String url;
	List<Integer> days;
	int discount;
	int max_discount;
	String description;	
	String large_description;	
	Date expiration_date;
	
	public Promotion() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getCard_id() {
		return card_id;
	}
	public void setCard_id(int card_id) {
		this.card_id = card_id;
	}
	
	public String getInstitution_name() {
		return institution_name;
	}
	public void setInstitution_name(String institution_name) {
		this.institution_name = institution_name;
	}
	public List<String> getCards_name() {
		return cards_name;
	}
	public void setCards_name(List<String> cards_name) {
		this.cards_name = cards_name;
	}
	public String getName_store() {
		return name_store;
	}
	public void setName_store(String name_store) {
		this.name_store = name_store;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<Integer> getDays() {
		return days;
	}
	public void setDays(List<Integer> days) {
		this.days = days;
	}
	public int getDiscount() {
		return discount;
	}
	public void setDiscount(int discount) {
		this.discount = discount;
	}
	public int getMax_discount() {
		return max_discount;
	}
	public void setMax_discount(int max_discount) {
		this.max_discount = max_discount;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLarge_description() {
		return large_description;
	}
	public void setLarge_description(String large_description) {
		this.large_description = large_description;
	}
	public Date getExpiration_date() {
		return expiration_date;
	}
	public void setExpiration_date(Date expiration_date) {
		this.expiration_date = expiration_date;
	}
	
	@Override
	public String toString() {
		return "Promotion [card_id=" + card_id + ", institution_name=" + institution_name + ", cards_name=" + cards_name
				+ ", name_store=" + name_store + ", url=" + url + ", days=" + days + ", discount=" + discount
				+ ", max_discount=" + max_discount + ", description=" + description + ", large_description="
				+ large_description + ", expiration_date=" + expiration_date + "]";
	}
	
	
}
