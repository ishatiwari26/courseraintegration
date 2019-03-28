
package com.yash.coursera.integration.batch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.yash.coursera.integration.model.Element;
import com.yash.coursera.integration.model.Elements;
import com.yash.coursera.integration.model.SFLmsMapper;
import com.yash.coursera.integration.model.Title;

@Component
public class ResponseProcessor implements ItemProcessor<Elements, List<SFLmsMapper>> {
	private JobExecution jobExecution;
	String jobName;

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		jobExecution = stepExecution.getJobExecution();
		jobName = jobExecution.getJobParameters().getString("jobName");
	}

	@Override
	public List<SFLmsMapper> process(Elements element) throws Exception {

		List<SFLmsMapper> mappers = null;

		if (element == null || CollectionUtils.isEmpty(element.getElement())) {
			return null;
		}

		mappers = new ArrayList<SFLmsMapper>();

		for (Element item : element.getElement()) {

			System.out.println("inside itemprocesssor");
			SFLmsMapper mapper = new SFLmsMapper();

			mapper.setContentID(item.getContentId());
			mapper.setContentTitle(item.getName());
			mapper.setProviderID("YASH");
			mapper.setStatus("ACTIVE");

			mapper.setTitle(new Title(item.getLanguageCode(), item.getName()));
			mapper.setDescription(new Title(item.getLanguageCode(), item.getDescription()));
			if (jobName.equals("loadContentAPI")) {
				String photoUrl = !CollectionUtils.isEmpty(item.getInstructors())
						? item.getInstructors().get(0).getPhotoUrl()
						: "";
				String courseId = !CollectionUtils.isEmpty(item.getPrograms())
						? item.getPrograms().get(0).getProgramId()
						: "";
				mapper.setThumbnailURI(photoUrl);
				mapper.setCourseID(courseId);
				mapper.setLaunchURL(item.getPrograms().get(0).getContentUrl());
			} else {
				mapper.setCourseID(item.getId());
				mapper.setLaunchURL(item.getUrl());
			}

			mappers.add(mapper);
		}

		return mappers;
	}
}
