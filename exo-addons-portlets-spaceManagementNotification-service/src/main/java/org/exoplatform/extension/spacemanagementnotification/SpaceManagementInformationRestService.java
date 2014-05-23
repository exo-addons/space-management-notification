package org.exoplatform.extension.spacemanagementnotification;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.Produces;

import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.social.core.space.SpaceException;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.service.rest.Util;
import org.exoplatform.social.core.manager.IdentityManager;


import java.util.List;
import java.util.ArrayList;

import org.exoplatform.common.http.HTTPStatus;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;


@Path("/spacemanagementinformation")
@Produces("application/json")
public class SpaceManagementInformationRestService implements ResourceContainer {


    private static final Log LOG = ExoLogger.getLogger(SpaceManagementInformationRestService.class);

    private SpaceService spaceService_;

    private IdentityManager identityManager_;

    public SpaceManagementInformationRestService(SpaceService spaceService, IdentityManager identityManager) {
        this.spaceService_=spaceService;
        this.identityManager_=identityManager;
    }

    @GET
    @Path("getRequestsToValidate")
    public Response getRequestsToValidate(@Context UriInfo uriInfo,@Context SecurityContext sc) {
        MediaType mediaType = Util.getMediaType("json");

        String userId = getUserId(sc, uriInfo);
        if (userId == null) {
            return Response.status(HTTPStatus.INTERNAL_ERROR).build();
        }

        List<SpaceResult> spacesUserIsManager = new ArrayList<SpaceResult>();

        try {
            List<Space> userSpaces = spaceService_.getAccessibleSpaces(userId);
            for (Space space : userSpaces) {


                if (spaceService_.isManager(space,userId) && space.getPendingUsers()!=null && space.getPendingUsers().length!=0) {
                    spacesUserIsManager.add(new SpaceResult(space));
                }
            }
        } catch (SpaceException e) {
            LOG.error("Error when getting user spaces: " + e.getMessage(), e);
            return renderJSON(new String("error"));
        }
        return renderJSON(spacesUserIsManager);

    }

    @GET
    @Path("request/confirm/{spaceId}/{requestedUserId}")
    public Response confirm(@PathParam("spaceId") String spaceId,@PathParam("requestedUserId") String requestedUserId, @Context SecurityContext sc, @Context UriInfo uriInfo) {

        try {

            String userId = getUserId(sc, uriInfo);
            if (userId == null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).build();
            }

            Space space = spaceService_.getSpaceById(spaceId);
            if (space==null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).build();
            }

            if (!spaceService_.isManager(space,userId)){
                return Response.status(HTTPStatus.INTERNAL_ERROR).build();
            }

            spaceService_.addMember(space,requestedUserId);

            return renderJSON(new String("Confirmed"));
        } catch (Exception e) {
            LOG.error("Error in validatating request: " + e.getMessage(), e);
            return renderJSON(new String("error"));
        }
    }

    @GET
    @Path("request/deny/{spaceId}/{requestedUserId}")
    public Response deny(@PathParam("spaceId") String spaceId,@PathParam("requestedUserId") String requestedUserId, @Context SecurityContext sc, @Context UriInfo uriInfo) {

        try {

            String userId = getUserId(sc, uriInfo);
            if (userId == null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).build();
            }

            Space space = spaceService_.getSpaceById(spaceId);
            if (space==null) {
                return Response.status(HTTPStatus.INTERNAL_ERROR).build();
            }

            if (!spaceService_.isManager(space,userId)){
                return Response.status(HTTPStatus.INTERNAL_ERROR).build();
            }

            spaceService_.removePendingUser(space,requestedUserId);

            return renderJSON(new String("Confirmed"));
        } catch (Exception e) {
            LOG.error("Error in validatating request: " + e.getMessage(), e);
            return renderJSON(new String("error"));
        }
    }


    private String getUserId(SecurityContext sc, UriInfo uriInfo) {
        try {
            return sc.getUserPrincipal().getName();
        } catch (Exception e) {
            return null;
        }
    }

    private Response renderJSON(Object result) {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);

        return Response.ok(result, MediaType.APPLICATION_JSON).cacheControl(cacheControl).build();
    }

    public class UserResult {
        private String username;
        private String userAvatarUrl;
        private String fullName;

        public UserResult (String username) {
            this.username = username;
            Identity identity = identityManager_.getOrCreateIdentity(OrganizationIdentityProvider.NAME,username, true);
            this.userAvatarUrl = identity.getProfile().getAvatarUrl();
            this.fullName=identity.getProfile().getFullName();
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUserAvatarUrl() {
            return userAvatarUrl;
        }

        public void setUserAvatarUrl(String userAvatarUrl) {
            this.userAvatarUrl = userAvatarUrl;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
    }


    public class SpaceResult {
        private String spaceId;
        private String displayName;
        private String spaceAvatarUrl;
        private String spaceRegistration;
        private int membersNumber;
        private List<UserResult> pendingUsers;
        public SpaceResult (Space space) {
            this.spaceId=space.getId();
            this.displayName=space.getDisplayName();
            this.spaceAvatarUrl = space.getAvatarUrl();
            this.spaceRegistration=space.getRegistration();
            this.membersNumber = space.getMembers().length;



            this.pendingUsers = new ArrayList<UserResult>();
            for (String user : space.getPendingUsers()) {
                this.pendingUsers.add(new UserResult(user));
            }

        }

        public String getSpaceId(){
            return spaceId;
        }
        public String getDisplayName(){
            return displayName;
        }
        public List<UserResult> getPendingUsers(){
            return pendingUsers;
        }

        public String getSpaceAvatarUrl() {
            return spaceAvatarUrl;
        }

        public void setSpaceAvatarUrl(String spaceAvatarUrl) {
            this.spaceAvatarUrl = spaceAvatarUrl;
        }

        public String getSpaceRegistration() {
            return spaceRegistration;
        }

        public void setSpaceRegistration(String spaceRegistration) {
            this.spaceRegistration = spaceRegistration;
        }

        public int getMembersNumber() {
            return membersNumber;
        }

        public void setMembersNumber(int membersNumber) {
            this.membersNumber = membersNumber;
        }
    }
}

