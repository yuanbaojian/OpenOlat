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
package org.olat.modules.taxonomy.restapi;

import static org.olat.restapi.security.RestSecurityHelper.getIdentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.olat.basesecurity.BaseSecurity;
import org.olat.core.CoreSpringFactory;
import org.olat.core.id.Identity;
import org.olat.modules.taxonomy.Taxonomy;
import org.olat.modules.taxonomy.TaxonomyCompetence;
import org.olat.modules.taxonomy.TaxonomyCompetenceAuditLog;
import org.olat.modules.taxonomy.TaxonomyCompetenceTypes;
import org.olat.modules.taxonomy.TaxonomyLevel;
import org.olat.modules.taxonomy.TaxonomyLevelManagedFlag;
import org.olat.modules.taxonomy.TaxonomyLevelType;
import org.olat.modules.taxonomy.TaxonomyLevelTypeManagedFlag;
import org.olat.modules.taxonomy.TaxonomyLevelTypeToType;
import org.olat.modules.taxonomy.TaxonomyService;
import org.olat.modules.taxonomy.model.TaxonomyCompetenceRefImpl;
import org.olat.modules.taxonomy.model.TaxonomyLevelRefImpl;
import org.olat.modules.taxonomy.model.TaxonomyLevelTypeRefImpl;

/**
 * 
 * Initial date: 5 Oct 2017<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class TaxonomyWebService {
	
	private final Taxonomy taxonomy;
	private final BaseSecurity securityManager;
	private final TaxonomyService taxonomyService;
	
	public TaxonomyWebService(Taxonomy taxonomy) {
		this.taxonomy = taxonomy;
		taxonomyService = CoreSpringFactory.getImpl(TaxonomyService.class);
		securityManager = CoreSpringFactory.getImpl(BaseSecurity.class);
	}
	
	/**
	 * Return the taxonomy object specified by the key in path.
	 * 
	 * @response.representation.200.qname {http://www.example.com}taxonomyVO
	 * @response.representation.200.mediaType application/xml, application/json
	 * @response.representation.200.doc A taxonomy
	 * @response.representation.200.example {@link org.olat.modules.taxonomy.restapi.Examples#SAMPLE_TAXONOMYVO}
	 * @response.representation.401.doc The roles of the authenticated user are not sufficient
	 * @param taxonomyKey If true, the status of the block is done or the status of the roll call is closed or auto closed
	 * @param httpRequest  The HTTP request
	 * @return The taxonomy
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getTaxonomy() {
		TaxonomyVO taxonomyVo = new TaxonomyVO(taxonomy);
		return Response.ok(taxonomyVo).build();
	}
	
	/**
	 * Return the flatted levels of a taxonomy.
	 * 
	 * @response.representation.200.qname {http://www.example.com}taxonomyLevelVO
	 * @response.representation.200.mediaType application/xml, application/json
	 * @response.representation.200.doc A taxonomy
	 * @response.representation.200.example {@link org.olat.modules.taxonomy.restapi.Examples#SAMPLE_TAXONOMYLEVELVO}
	 * @response.representation.401.doc The roles of the authenticated user are not sufficient
	 * @param taxonomyKey If true, the status of the block is done or the status of the roll call is closed or auto closed
	 * @param httpRequest  The HTTP request
	 * @return An array of taxonomy levels
	 */
	@GET
	@Path("levels")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getFlatTaxonomyLevels() {
		List<TaxonomyLevel> levels = taxonomyService.getTaxonomyLevels(taxonomy);
		List<TaxonomyLevelVO> levelVOes = new ArrayList<>(levels.size());
		for(TaxonomyLevel level:levels) {
			levelVOes.add(TaxonomyLevelVO.valueOf(level));
		}
		return Response.ok(levelVOes.toArray(new TaxonomyLevelVO[levelVOes.size()])).build();
	}
	
	/**
	 * Create or update a taxonomy level. The method changes to tree structure, a
	 * null parent key will make the level a root one, a new parent key will move
	 * the level.
	 * 
	 * @response.representation.200.qname {http://www.example.com}taxonomyLevelVO
	 * @response.representation.200.mediaType application/xml, application/json
	 * @response.representation.200.doc A taxonomy level
	 * @response.representation.200.example {@link org.olat.modules.taxonomy.restapi.Examples#SAMPLE_TAXONOMYLEVELVO}
	 * @response.representation.401.doc The roles of the authenticated user are not sufficient
	 * @response.representation.404.doc An existant level was not found
	 * @param taxonomyKey The taxonomy tree where this level is
	 * @param httpRequest  The HTTP request
	 * @param levelVo The roll call to update
	 * @return The updated roll call
	 */
	@PUT
	@Path("levels")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response putTaxonomyLevel(TaxonomyLevelVO levelVo) {
		TaxonomyLevel parentLevel = null;
		if(levelVo.getParentKey() != null) {
			parentLevel = taxonomyService.getTaxonomyLevel(new TaxonomyLevelRefImpl(levelVo.getParentKey()));
		}
		
		TaxonomyLevel level;
		if(levelVo.getKey() != null) {
			level = taxonomyService.getTaxonomyLevel(new TaxonomyLevelRefImpl(levelVo.getKey()));
			if(levelVo.getIdentifier() != null) {
				level.setIdentifier(levelVo.getIdentifier());
			}
			if(levelVo.getDisplayName() != null) {
				level.setDisplayName(levelVo.getDisplayName());
			}
			if(levelVo.getDescription() != null) {
				level.setDescription(levelVo.getDescription());
			}
			if(levelVo.getExternalId() != null) {
				level.setExternalId(levelVo.getExternalId());
			}
			if(levelVo.getTypeKey() != null) {
				TaxonomyLevelType type = taxonomyService.getTaxonomyLevelType(new TaxonomyLevelTypeRefImpl(levelVo.getTypeKey()));
				level.setType(type);
			}
			if(levelVo.getManagedFlags() != null) {
				level.setManagedFlags(TaxonomyLevelManagedFlag.toEnum(levelVo.getManagedFlags()));
			}
			level = taxonomyService.updateTaxonomyLevel(level);
		} else {
			level = taxonomyService.createTaxonomyLevel(levelVo.getIdentifier(), levelVo.getDisplayName(),
				levelVo.getDescription(), levelVo.getExternalId(), TaxonomyLevelManagedFlag.toEnum(levelVo.getManagedFlags()),
				parentLevel, taxonomy);
			if(levelVo.getTypeKey() != null) {
				TaxonomyLevelType type = taxonomyService.getTaxonomyLevelType(new TaxonomyLevelTypeRefImpl(levelVo.getTypeKey()));
				level.setType(type);
				level = taxonomyService.updateTaxonomyLevel(level);
			}
		}
		
		if((level.getParent() != null &&  levelVo.getParentKey() == null)
				|| (level.getParent() == null && levelVo.getParentKey() != null)
				|| (level.getParent() != null && !level.getParent().getKey().equals(levelVo.getParentKey()))) {
			TaxonomyLevel newParentLevel = null;
			if(levelVo.getParentKey() != null) {
				newParentLevel = taxonomyService.getTaxonomyLevel(new TaxonomyLevelRefImpl(levelVo.getParentKey()));
			}
			level = taxonomyService.moveTaxonomyLevel(level, newParentLevel);
		}
		
		TaxonomyLevelVO newLevelVo = TaxonomyLevelVO.valueOf(level);
		return Response.ok(newLevelVo).build();
	}
	
	/**
	 * Delete the taxonomy level definitively.
	 * 
	 * @response.representation.200.doc The level was successfully deleted
	 * @response.representation.304.doc The level cannot be deleted and was not modified
	 * @response.representation.401.doc The roles of the authenticated user are not sufficient
	 * @response.representation.404.doc The level was not found
	 * @response.representation.404.doc The level taxonomy doesn't match the taxonomy of the web service
	 * 
	 * @param taxonomyKey The taxonomy tree
	 * @param taxonomyLevelKey The level of the taxonomy to delete
	 * @return Nothing
	 */
	@DELETE
	@Path("levels/{taxonomyLevelKey}")
	public Response deleteTaxonomyLevel(@PathParam("taxonomyLevelKey") String taxonomyLevelKey) {
		TaxonomyLevel level = taxonomyService.getTaxonomyLevel(new TaxonomyLevelRefImpl(new Long(taxonomyLevelKey)));
		if(level == null) {
			return Response.serverError().status(Status.NOT_FOUND).build();
		}
		if(level.getTaxonomy() != null && !level.getTaxonomy().equals(taxonomy)) {
			return Response.serverError().status(Status.CONFLICT).build();
		}
		
		boolean canDelete = taxonomyService.deleteTaxonomyLevel(level, null);
		if(canDelete) {
			return Response.ok().build();
		}
		return Response.notModified().build();
	}
	
	/**
	 * Return the competences of users on the taxonomy level specified in the key in path.
	 * 
	 * @response.representation.200.qname {http://www.example.com}taxonomyCompetenceVO
	 * @response.representation.200.mediaType application/xml, application/json
	 * @response.representation.200.doc An array of competences
	 * @response.representation.200.example {@link org.olat.modules.taxonomy.restapi.Examples#SAMPLE_TAXONOMYCOMPETENCEVO}
	 * @response.representation.401.doc The roles of the authenticated user are not sufficient
	 * @param taxonomyKey The taxonomy tree
	 * @param taxonomyLevelKey The level of the taxonomy
	 * @param httpRequest  The HTTP request
	 * @return An array of competences
	 */
	@GET
	@Path("levels/{taxonomyLevelKey}/competences")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getTaxonomyLevelComptences(@PathParam("taxonomyLevelKey") Long taxonomyLevelKey) {
		TaxonomyLevel level = taxonomyService.getTaxonomyLevel(new TaxonomyLevelRefImpl(new Long(taxonomyLevelKey)));
		if(level == null) {
			return Response.serverError().status(Status.NOT_FOUND).build();
		}
		
		List<TaxonomyCompetence> competences = taxonomyService.getTaxonomyLevelCompetences(level);
		List<TaxonomyCompetenceVO> competenceVOes = new ArrayList<>(competences.size());
		for(TaxonomyCompetence competence:competences) {
			competenceVOes.add(new TaxonomyCompetenceVO(competence));
		}
		return Response.ok(competenceVOes.toArray(new TaxonomyCompetenceVO[competenceVOes.size()])).build();
	}
	
	/**
	 * Return the competences of a specific user in the taxonomy tree.
	 * 
	 * @response.representation.200.qname {http://www.example.com}taxonomyCompetenceVO
	 * @response.representation.200.mediaType application/xml, application/json
	 * @response.representation.200.doc An array of competences
	 * @response.representation.200.example {@link org.olat.modules.taxonomy.restapi.Examples#SAMPLE_TAXONOMYCOMPETENCEVO}
	 * @response.representation.401.doc The roles of the authenticated user are not sufficient
	 * @param taxonomyKey The taxonomy tree
	 * @param identityKey The user
	 * @param httpRequest  The HTTP request
	 * @return An array of competences
	 */
	@GET
	@Path("competences/{identityKey}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getTaxonomyComptencesByIdentity(@PathParam("identityKey") Long identityKey) {
		Identity identity = securityManager.loadIdentityByKey(identityKey);
		if(identity == null) {
			return Response.serverError().status(Status.NOT_FOUND).build();
		}

		List<TaxonomyCompetence> competences = taxonomyService.getTaxonomyCompetences(identity);
		List<TaxonomyCompetenceVO> competenceVOes = new ArrayList<>(competences.size());
		for(TaxonomyCompetence competence:competences) {
			competenceVOes.add(new TaxonomyCompetenceVO(competence));
		}
		return Response.ok(competenceVOes.toArray(new TaxonomyCompetenceVO[competenceVOes.size()])).build();
	}
	
	/**
	 * Return the competences of a specific user on the taxonomy level
	 * specified in the key in path.
	 * 
	 * @response.representation.200.qname {http://www.example.com}taxonomyCompetenceVO
	 * @response.representation.200.mediaType application/xml, application/json
	 * @response.representation.200.doc An array of competences
	 * @response.representation.200.example {@link org.olat.modules.taxonomy.restapi.Examples#SAMPLE_TAXONOMYCOMPETENCEVO}
	 * @response.representation.401.doc The roles of the authenticated user are not sufficient
	 * @param taxonomyKey The taxonomy tree
	 * @param taxonomyLevelKey The level of the taxonomy
	 * @param identityKey The user
	 * @param httpRequest  The HTTP request
	 * @return An array of competences
	 */
	@GET
	@Path("levels/{taxonomyLevelKey}/competences/{identityKey}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getTaxonomyLevelComptencesByIdentity(@PathParam("taxonomyLevelKey") Long taxonomyLevelKey,
			@PathParam("identityKey") Long identityKey) {
		TaxonomyLevel level = taxonomyService.getTaxonomyLevel(new TaxonomyLevelRefImpl(new Long(taxonomyLevelKey)));
		if(level == null) {
			return Response.serverError().status(Status.NOT_FOUND).build();
		}
		Identity identity = securityManager.loadIdentityByKey(identityKey);
		if(identity == null) {
			return Response.serverError().status(Status.NOT_FOUND).build();
		}

		List<TaxonomyCompetence> competences = taxonomyService.getTaxonomyLevelCompetences(level, identity);
		List<TaxonomyCompetenceVO> competenceVOes = new ArrayList<>(competences.size());
		for(TaxonomyCompetence competence:competences) {
			competenceVOes.add(new TaxonomyCompetenceVO(competence));
		}
		return Response.ok(competenceVOes.toArray(new TaxonomyCompetenceVO[competenceVOes.size()])).build();
	}
	
	/**
	 * Add a competence on a specific level of a taxonomy tree.
	 * 
	 * @response.representation.200.qname {http://www.example.com}taxonomyCompetenceVO
	 * @response.representation.200.mediaType application/xml, application/json
	 * @response.representation.200.doc A competence
	 * @response.representation.200.example {@link org.olat.modules.taxonomy.restapi.Examples#SAMPLE_TAXONOMYCOMPETENCEVO}
	 * @response.representation.401.doc The roles of the authenticated user are not sufficient
	 * @response.representation.404.doc The taxonomy level type to update was not found
	 * @response.representation.409.doc The taxonomy level key of the competence doesn't match the one in URL
	 * @param taxonomyKey The taxonomy tree
	 * @param taxonomyLevelKey The taxonomy level
	 * @param comptenceVo The competence to add or update
	 * @param httpRequest  The HTTP request
	 * @return The added/updated competence
	 */
	@PUT
	@Path("levels/{taxonomyLevelKey}/competences")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response putTaxonomyLevelComptencesByIdentity(@PathParam("taxonomyLevelKey") Long taxonomyLevelKey,
			TaxonomyCompetenceVO comptenceVo, @Context HttpServletRequest httpRequest) {
		Identity executor = getIdentity(httpRequest);
		return addTaxonomyLevelComptencesByIdentity(taxonomyLevelKey, comptenceVo, executor);
	}
	
	private Response addTaxonomyLevelComptencesByIdentity(Long taxonomyLevelKey, TaxonomyCompetenceVO comptenceVo, Identity executor) {
		if(taxonomyLevelKey != null && comptenceVo.getTaxonomyLevelKey() != null && !taxonomyLevelKey.equals(comptenceVo.getTaxonomyLevelKey())) {
			return Response.serverError().status(Status.CONFLICT).build();
		}
		if(taxonomyLevelKey == null) {
			taxonomyLevelKey = comptenceVo.getTaxonomyLevelKey();
		}

		TaxonomyLevel level = taxonomyService.getTaxonomyLevel(new TaxonomyLevelRefImpl(new Long(taxonomyLevelKey)));
		if(level == null) {
			return Response.serverError().status(Status.NOT_FOUND).build();
		}
		Identity identity = securityManager.loadIdentityByKey(comptenceVo.getIdentityKey());
		if(identity == null) {
			return Response.serverError().status(Status.NOT_FOUND).build();
		}
		
		TaxonomyCompetence competence = null;
		List<TaxonomyCompetence> competences = taxonomyService.getTaxonomyLevelCompetences(level, identity);
		for(TaxonomyCompetence c:competences) {
			if(c.getCompetenceType().name().equals(comptenceVo.getTaxonomyCompetenceType())) {
				competence = c;
			}	
		}
		
		if(competence == null) {
			TaxonomyCompetenceTypes competenceType
				= TaxonomyCompetenceTypes.valueOf(comptenceVo.getTaxonomyCompetenceType());
			competence = taxonomyService.addTaxonomyLevelCompetences(level, identity, competenceType, comptenceVo.getExpiration());
		} else {
			competence.setExpiration(comptenceVo.getExpiration());
			competence = taxonomyService.updateTaxonomyLevelCompetence(competence);
		}

		String after = taxonomyService.toAuditXml(competence);
		taxonomyService.auditLog(TaxonomyCompetenceAuditLog.Action.addCompetence, null, after, null, taxonomy, competence, identity, executor);
		
		return Response.ok(new TaxonomyCompetenceVO(competence)).build();
	}
	
	/**
	 * Remove a competence.
	 * 
	 * @response.representation.200.doc The competence was removed sucessfully
	 * @response.representation.401.doc The roles of the authenticated user are not sufficient
	 * @response.representation.404.doc The competence was not found
	 * @param taxonomyKey The taxonomy tree
	 * @param taxonomyLevelKey The taxonomy level
	 * @param competenceKey The competence to remove
	 * @param httpRequest  The HTTP request
	 * @return Nothing
	 */
	@DELETE
	@Path("levels/{taxonomyLevelKey}/competences/{competenceKey}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response removeTaxonomyLevelCompetence(@PathParam("taxonomyLevelKey") Long taxonomyLevelKey,
			@PathParam("competenceKey") Long competenceKey, @Context HttpServletRequest httpRequest) {
		Identity executor = getIdentity(httpRequest);
		TaxonomyCompetence competence = taxonomyService.getTaxonomyCompetence(new TaxonomyCompetenceRefImpl(competenceKey));
		if(competence == null || !competence.getTaxonomyLevel().getKey().equals(taxonomyLevelKey)) {
			return Response.serverError().status(Status.NOT_FOUND).build();
		}
		
		String before = taxonomyService.toAuditXml(competence);
		taxonomyService.removeTaxonomyLevelCompetence(competence);
		taxonomyService.auditLog(TaxonomyCompetenceAuditLog.Action.removeCompetence, before, null, null,
				taxonomy, competence, competence.getIdentity(), executor);
		return Response.ok().build();
	}
	
	/**
	 * Get the configurations for taxonomy levels for the whole taxonomy.
	 * 
	 * @response.representation.200.qname {http://www.example.com}taxonomyLevelTypeVO
	 * @response.representation.200.mediaType application/xml, application/json
	 * @response.representation.200.doc A taxonomy level
	 * @response.representation.200.example {@link org.olat.modules.taxonomy.restapi.Examples#SAMPLE_TAXONOMYLEVELTYPEVO}
	 * @response.representation.401.doc The roles of the authenticated user are not sufficient
	 * @response.representation.404.doc The taxonomy was not found
	 * @param taxonomyKey The taxonomy tree
	 * @param httpRequest  The HTTP request
	 * @return An array of taxonomy levels types
	 */
	@GET
	@Path("types")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getTaxonomyLevelTypes() {
		List<TaxonomyLevelType> types = taxonomyService.getTaxonomyLevelTypes(taxonomy);
		List<TaxonomyLevelTypeVO> typeVOes = new ArrayList<>(types.size());
		for(TaxonomyLevelType type:types) {
			typeVOes.add(new TaxonomyLevelTypeVO(type));
		}
		return Response.ok(typeVOes.toArray(new TaxonomyLevelTypeVO[typeVOes.size()])).build();
	}
	
	/**
	 * Create or Update a taxonomy level's type.
	 * 
	 * @response.representation.200.qname {http://www.example.com}taxonomyLevelTypeVO
	 * @response.representation.200.mediaType application/xml, application/json
	 * @response.representation.200.doc A taxonomy level type
	 * @response.representation.200.example {@link org.olat.modules.taxonomy.restapi.Examples#SAMPLE_TAXONOMYLEVELTYPEVO}
	 * @response.representation.401.doc The roles of the authenticated user are not sufficient
	 * @response.representation.404.doc The taxonomy level type to update was not found
	 * @param taxonomyKey The taxonomy tree
	 * @param httpRequest  The HTTP request
	 * @param typeVo The taxonomy level type to create or update
	 * @return The created/updated taxonomy level type
	 */
	@PUT
	@Path("types")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response putTaxonomyLevelType(TaxonomyLevelTypeVO typeVo) {
		TaxonomyLevelType type;
		if(typeVo.getKey() != null) {
			type = taxonomyService.getTaxonomyLevelType(new TaxonomyLevelTypeRefImpl(typeVo.getKey()));
			if(type == null) {
				return Response.serverError().status(Status.NOT_FOUND).build();
			}
			if(typeVo.getIdentifier() != null) {
				type.setIdentifier(typeVo.getIdentifier());
			}
			if(typeVo.getDisplayName() != null) {
				type.setDisplayName(typeVo.getDisplayName());
			}
			if(typeVo.getDescription() != null) {
				type.setDescription(typeVo.getDescription());
			}
			if(typeVo.getExternalId() != null) {
				type.setExternalId(typeVo.getExternalId());
			}
		} else {
			type = taxonomyService
				.createTaxonomyLevelType(typeVo.getIdentifier(), typeVo.getDisplayName(), typeVo.getDescription(), typeVo.getExternalId(),
						taxonomy);
		}
		
		if(typeVo.getManagedFlags() != null) {
			type.setManagedFlags(TaxonomyLevelTypeManagedFlag.toEnum(typeVo.getManagedFlags()));
		}
		
		if(typeVo.getCssClass() != null) {
			type.setCssClass(typeVo.getCssClass());
		}
		if(typeVo.getVisible() != null) {
			type.setVisible(typeVo.getVisible().booleanValue());
		}
		
		if(typeVo.getDocumentsLibraryEnabled() != null) {
			type.setDocumentsLibraryEnabled(typeVo.getDocumentsLibraryEnabled().booleanValue());
		}
		if(typeVo.getDocumentsLibraryManagerCompetenceEnabled() != null) {
			type.setDocumentsLibraryManageCompetenceEnabled(typeVo.getDocumentsLibraryManagerCompetenceEnabled().booleanValue());
		}
		if(typeVo.getDocumentsLibraryTeachCompetenceReadEnabled() != null) {
			type.setDocumentsLibraryTeachCompetenceReadEnabled(typeVo.getDocumentsLibraryTeachCompetenceReadEnabled().booleanValue());
		}
		if(typeVo.getDocumentsLibraryTeachCompetenceReadParentLevels() != null) {
			type.setDocumentsLibraryTeachCompetenceReadParentLevels(typeVo.getDocumentsLibraryTeachCompetenceReadParentLevels().intValue());
		}
		if(typeVo.getDocumentsLibraryTeachCompetenceWriteEnabled() != null) {
			type.setDocumentsLibraryTeachCompetenceWriteEnabled(typeVo.getDocumentsLibraryTeachCompetenceWriteEnabled().booleanValue());
		}
		if(typeVo.getDocumentsLibraryHaveCompetenceReadEnabled() != null) {
			type.setDocumentsLibraryHaveCompetenceReadEnabled(typeVo.getDocumentsLibraryHaveCompetenceReadEnabled().booleanValue());
		}
		if(typeVo.getDocumentsLibraryTargetCompetenceReadEnabled() != null) {
			type.setDocumentsLibraryTargetCompetenceReadEnabled(typeVo.getDocumentsLibraryTargetCompetenceReadEnabled().booleanValue());
		}

		type = taxonomyService.updateTaxonomyLevelType(type);
		return Response.ok(new TaxonomyLevelTypeVO(type)).build();
	}
	
	/**
	 * Get a taxonomy level's type.
	 * 
	 * @response.representation.200.qname {http://www.example.com}taxonomyLevelTypeVO
	 * @response.representation.200.mediaType application/xml, application/json
	 * @response.representation.200.doc A taxonomy level type
	 * @response.representation.200.example {@link org.olat.modules.taxonomy.restapi.Examples#SAMPLE_TAXONOMYLEVELTYPEVO}
	 * @response.representation.401.doc The roles of the authenticated user are not sufficient
	 * @response.representation.404.doc The taxonomy level type was not found
	 * @param taxonomyKey The taxonomy tree
	 * @param httpRequest  The HTTP request
	 * @param typeKey The primary key of the taxonomy level type to retrieve
	 * @return A taxonomy level type
	 */
	@GET
	@Path("types/{typeKey}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getTaxonomyLevelType(@PathParam("typeKey") Long typeKey) {
		TaxonomyLevelType type = taxonomyService.getTaxonomyLevelType(new TaxonomyLevelTypeRefImpl(typeKey));
		if(type == null) {
			return Response.serverError().status(Status.NOT_FOUND).build();
		}
		return Response.ok(new TaxonomyLevelTypeVO(type)).build();
	}
	
	/**
	 * Get the allowed sub-types of a specified taxonomy level's type.
	 * 
	 * @response.representation.200.qname {http://www.example.com}taxonomyLevelTypeVO
	 * @response.representation.200.mediaType application/xml, application/json
	 * @response.representation.200.doc An array of taxonomy level types
	 * @response.representation.200.example {@link org.olat.modules.taxonomy.restapi.Examples#SAMPLE_TAXONOMYLEVELTYPEVO}
	 * @response.representation.401.doc The roles of the authenticated user are not sufficient
	 * @response.representation.404.doc The taxonomy level type was not found
	 * @param taxonomyKey The taxonomy tree
	 * @param httpRequest  The HTTP request
	 * @param typeKey The primary key of the taxonomy level type
	 * @return An array of taxonomy level types
	 */
	@GET
	@Path("types/{typeKey}/allowedSubTypes")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getAllowedSubTaxonomyLevelTypes(@PathParam("typeKey") Long typeKey) {
		TaxonomyLevelType type = taxonomyService.getTaxonomyLevelType(new TaxonomyLevelTypeRefImpl(typeKey));
		if(type == null) {
			return Response.serverError().status(Status.NOT_FOUND).build();
		}
		Set<TaxonomyLevelTypeToType> typeToTypes = type.getAllowedTaxonomyLevelSubTypes();
		List<TaxonomyLevelTypeVO> subTypeVOes = new ArrayList<>(typeToTypes.size());
		for(TaxonomyLevelTypeToType typeToType:typeToTypes) {
			TaxonomyLevelType subType = typeToType.getAllowedSubTaxonomyLevelType();
			subTypeVOes.add(new TaxonomyLevelTypeVO(subType));
		}
		return Response.ok(subTypeVOes.toArray(new TaxonomyLevelTypeVO[subTypeVOes.size()])).build();
	}
	
	/**
	 * Add a sub-type to a specified taxonomy level's type.
	 * 
	 * @response.representation.200.qname {http://www.example.com}taxonomyLevelTypeVO
	 * @response.representation.200.mediaType application/xml, application/json
	 * @response.representation.200.doc The sub type was added to the allowed sub types
	 * @response.representation.200.example {@link org.olat.modules.taxonomy.restapi.Examples#SAMPLE_TAXONOMYLEVELTYPEVO}
	 * @response.representation.401.doc The roles of the authenticated user are not sufficient
	 * @response.representation.404.doc The taxonomy level type was not found
	 * @param taxonomyKey The taxonomy tree
	 * @param typeKey The type
	 * @param subTypeKey The sub type
	 * @param httpRequest  The HTTP request
	 * @return Nothing
	 */
	@PUT
	@Path("types/{typeKey}/allowedSubTypes/{subTypeKey}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response allowSubTaxonomyLevelType(@PathParam("typeKey") Long typeKey, @PathParam("subTypeKey") Long subTypeKey) {
		TaxonomyLevelType type = taxonomyService.getTaxonomyLevelType(new TaxonomyLevelTypeRefImpl(typeKey));
		TaxonomyLevelType subType = taxonomyService.getTaxonomyLevelType(new TaxonomyLevelTypeRefImpl(subTypeKey));
		if(type == null || subType == null) {
			return Response.serverError().status(Status.NOT_FOUND).build();
		}
		taxonomyService.taxonomyLevelTypeAllowSubType(type, subType);
		return Response.ok().build();
	}
	
	/**
	 * Remove a sub-type to a specified taxonomy level's type.
	 * 
	 * @response.representation.200.doc The sub type was removed sucessfully
	 * @response.representation.401.doc The roles of the authenticated user are not sufficient
	 * @response.representation.404.doc The taxonomy level type was not found
	 * @param taxonomyKey The taxonomy tree
	 * @param typeKey The type
	 * @param subTypeKey The sub type to remove
	 * @param httpRequest  The HTTP request
	 * @return Nothing
	 */
	@DELETE
	@Path("types/{typeKey}/allowedSubTypes/{subTypeKey}")
	public Response disalloweSubTaxonomyLevelType(@PathParam("typeKey") Long typeKey, @PathParam("subTypeKey") Long subTypeKey) {
		TaxonomyLevelType type = taxonomyService.getTaxonomyLevelType(new TaxonomyLevelTypeRefImpl(typeKey));
		TaxonomyLevelType subType = taxonomyService.getTaxonomyLevelType(new TaxonomyLevelTypeRefImpl(subTypeKey));
		if(type == null || subType == null) {
			return Response.serverError().status(Status.NOT_FOUND).build();
		}
		taxonomyService.taxonomyLevelTypeDisallowSubType(type, subType);
		return Response.ok().build();
	}
}
