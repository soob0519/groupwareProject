package com.groupware.search;

import lombok.Data;

@Data
public class SearchDto {

	String search;
	
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
	}
}
