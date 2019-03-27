package com.yash.coursera.integration.helper;

import java.util.ArrayList;
import java.util.List;

import com.yash.coursera.integration.model.SFLmsMapper;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SFLmsData {

	private static SFLmsData mappedData = null;
	private List<SFLmsMapper> lmsDataList;

	private SFLmsData() {
		this.lmsDataList = new ArrayList<>();
	}

	public static SFLmsData getInstance() {

		if(mappedData == null) {
			mappedData = new SFLmsData();
		}

		return mappedData;
	}
}
