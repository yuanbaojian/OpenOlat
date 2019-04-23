/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.modules.gotomeeting.restapi;

import java.util.List;
import java.util.Locale;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.olat.core.logging.OLog;
import org.olat.core.logging.Tracing;
import org.olat.core.util.StringHelper;
import org.olat.core.util.Util;
import org.olat.modules.gotomeeting.GoToMeeting;
import org.olat.modules.gotomeeting.GoToMeetingManager;
import org.olat.modules.gotomeeting.GoToOrganizer;
import org.olat.modules.gotomeeting.model.GoToError;
import org.olat.modules.gotomeeting.model.GoToErrors;
import org.olat.modules.gotomeeting.model.GoToType;
import org.olat.modules.gotomeeting.ui.GoToMeetingRunController;
import org.olat.repository.RepositoryEntry;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 24.03.2016<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class GoToTrainingWebService {
	
	private static final OLog log = Tracing.createLoggerFor(GoToTrainingWebService.class);
	
	private final String subIdentifier;
	private final RepositoryEntry entry;
	
	@Autowired
	private GoToMeetingManager meetingManager;
	
	public GoToTrainingWebService(RepositoryEntry entry, String subIdentifier) {
		this.entry = entry;
		this.subIdentifier = subIdentifier;
	}

	/**
	 * returns the list of booking of the resource.
	 * 
	 * @response.representation.200.qname {http://www.example.com}goToTrainingVO
	 * @response.representation.200.mediaType application/xml, application/json
	 * @response.representation.200.doc This is the list of all training of a resource
	 * @response.representation.200.example {@link org.olat.modules.gotomeeting.restapi.Examples#SAMPLE_GoToTrainingVO}
	 * @return The list of trainings
	 */
	@GET
	@Path("trainings")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getTrainings() {
		try {
			List<GoToMeeting> meetings = meetingManager.getMeetings(GoToType.training, entry, subIdentifier, null);
			GoToTrainingVO[] bookingVos = new GoToTrainingVO[meetings.size()];
			int count = 0;
			for(GoToMeeting meeting:meetings) {
				bookingVos[count++] = new GoToTrainingVO(meeting);
			}
			return Response.ok(bookingVos).build();
		} catch (Exception e) {
			log.error("", e);
			return handleUnexpectedException();
		}	
	}
	
	/**
	 * Return the created or updated training
	 * 
	 * @response.representation.200.qname {http://www.example.com}goToTrainingVO
	 * @response.representation.200.mediaType application/xml, application/json
	 * @response.representation.200.doc Created a training
	 * @response.representation.200.example {@link org.olat.modules.gotomeeting.restapi.Examples#SAMPLE_GoToTrainingVO}
	 * @return The list of vitero booking
	 */
	@PUT
	@Path("trainings")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response createTraining(GoToTrainingVO training) {
		return saveTraining(training);
	}
	
	/**
	 * Return the created or updated training
	 * 
	 * @response.representation.200.qname {http://www.example.com}goToTrainingVO
	 * @response.representation.200.mediaType application/xml, application/json
	 * @response.representation.200.doc The created booking
	 * @response.representation.200.example {@link org.olat.modules.gotomeeting.restapi.Examples#SAMPLE_GoToTrainingVO}
	 * @return The list of vitero booking
	 */
	@POST
	@Path("trainings")
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response updateTraining(GoToTrainingVO training) {
		return saveTraining(training);
	}
	
	private Response saveTraining(GoToTrainingVO training) {
		try {
			GoToMeeting meeting = null;
			GoToError error = new GoToError();
			if(training.getKey() == null) {
				boolean organizerFound = false;
				List<GoToOrganizer> organizers = meetingManager.getSystemOrganizers();
				for(GoToOrganizer organizer:organizers) {
					boolean available = meetingManager.checkOrganizerAvailability(organizer, training.getStart(), training.getEnd());
					if(available) {
						meeting = meetingManager.scheduleTraining(organizer, training.getName(), training.getExternalId(), "-",
							training.getStart(), training.getEnd(), entry, subIdentifier, null, error);
						organizerFound = true;
						if(!error.hasError()) {
							break;
						}
					}
				}
				
				if(!organizerFound) {
					error.setError(GoToErrors.OrganizerOverlap);
				}
			} else {
				meeting = meetingManager.getMeetingByExternalId(training.getExternalId());
				if(meeting == null) {
					List<GoToOrganizer> organizers = meetingManager.getSystemOrganizers();
					for(GoToOrganizer organizer:organizers) {
						meeting = meetingManager.scheduleTraining(organizer, training.getName(), training.getExternalId(), "-",
							training.getStart(), training.getEnd(), entry, subIdentifier, null, error);
						if(!error.hasError()) {
							break;
						}
					}
				} else {
					meetingManager.updateTraining(meeting, training.getName(), "-", training.getStart(), training.getEnd(), error);
				}
			}
		
			Response response;
			if(error.hasError()) {
				response = handleGoToTrainingError(error);
			} else if (meeting == null){
				response = handleUnexpectedException();
			} else {
				response = Response.ok(new GoToTrainingVO(meeting)).build();
			}
			return response;
		} catch (Exception e) {
			log.error("", e);
			return handleUnexpectedException();
		}	
	}
	
	/**
	 * Delete the training
	 * 
	 * @response.representation.200.doc The training is deleted
	 * @return Nothing
	 */
	@DELETE
	@Path("/trainings/{trainingKey}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response deleteTraining(@PathParam("trainingKey") Long trainingKey) {
		try {
			GoToMeeting meeting = meetingManager.getMeetingByKey(trainingKey);
			if(meeting == null) {
				return Response.serverError().status(Status.NOT_FOUND).build();
			} else if(meetingManager.delete(meeting)) {
				return Response.ok().build();
			} else {
				return Response.serverError().status(500).build();
			}
		} catch (Exception e) {
			log.error("", e);
			return handleUnexpectedException();
		}	
	}
	
	private Response handleGoToTrainingError(GoToError error) {
		return Response.serverError().entity(goToErrorVO(error)).status(500).build();
	}
	
	private GoToErrorVO goToErrorVO(GoToError error) {
		String msg = "";
		if(error.getError() != null) {
			msg = Util.createPackageTranslator(GoToMeetingRunController.class, Locale.ENGLISH)
				.translate(error.getError().i18nKey());
		}
		if(!StringHelper.containsNonWhitespace(msg) || msg.length() > 1024) {
			msg = error.getDescription();
		}
		return new GoToErrorVO(error, msg);
	}
	
	private Response handleUnexpectedException() {
		GoToError status = new GoToError(GoToErrors.Unkown);
		GoToErrorVO error = new GoToErrorVO(status, "GoToTraining server returned an unexpected error");
		return Response.serverError().entity(error).status(Status.INTERNAL_SERVER_ERROR).build();
	}
}