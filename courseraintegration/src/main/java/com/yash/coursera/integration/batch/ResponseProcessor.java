
package com.yash.coursera.integration.batch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yash.coursera.integration.model.Element;
import com.yash.coursera.integration.model.Elements;
import com.yash.coursera.integration.model.SFLmsMapper;
import com.yash.coursera.integration.model.Title;

@Component
public class ResponseProcessor implements ItemProcessor<Elements, List<SFLmsMapper>> {

	private JobExecution jobExecution;
	private String jobName;

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		jobExecution = stepExecution.getJobExecution();
		jobName = jobExecution.getJobParameters().getString("jobName");
	}

	@Override
	public List<SFLmsMapper> process(Elements element) throws Exception {

		if (element == null || CollectionUtils.isEmpty(element.getElement())) {
			return null;
		}

		List<SFLmsMapper> mappers = new ArrayList<SFLmsMapper>();
		element.getElement().stream().forEach((item) -> {
			mappers.add(getMapper(item));
		});
		return mappers;
	}

	private SFLmsMapper getMapper(Element item) {
		SFLmsMapper mapper = new SFLmsMapper();
		mapper.setContentID(item.getContentId());
		mapper.setContentTitle(item.getName());
		mapper.setProviderID("YASH");
		mapper.setStatus("ACTIVE");

		mapper.setId(item.getId());
		mapper.setUserId(item.getUserId());
		mapper.setIsCompleted(item.getIsCompleted());
		mapper.setCompletedAt(item.getCompletedAt());
		mapper.setGrade(item.getGrade());

		mapper.setTitle(new Title(item.getLanguageCode(), item.getName()));
		mapper.setDescription(new Title(item.getLanguageCode(), item.getDescription()));
		if (jobName.equals("loadContentAPI")) {
			String photoUrl = !CollectionUtils.isEmpty(item.getInstructors())
					? item.getInstructors().get(0).getPhotoUrl() : "";
			String courseId = !CollectionUtils.isEmpty(item.getPrograms()) ? item.getPrograms().get(0).getProgramId()
					: "";
			String contentURL = !CollectionUtils.isEmpty(item.getPrograms()) ? item.getPrograms().get(0).getContentUrl()
					: "";
			mapper.setThumbnailURI(photoUrl);
			mapper.setCourseID(courseId);
			mapper.setLaunchURL(contentURL);
		} else {
			mapper.setCourseID(item.getId());
			mapper.setLaunchURL(item.getUrl());
		}
		return mapper;
	}
}
