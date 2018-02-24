package my.rest.messenger.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import my.rest.messenger.models.Message;
import my.rest.messenger.services.MessageService;

/**
 * Root resource (exposed at "messages" path)
 */
@Path("messages")
@Produces(value = { MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
@Consumes(MediaType.APPLICATION_JSON)
public class MessageResource {

	/**
	 * Methods handling HTTP GET, POST, PUT, DELETE requests. The returned object will be sent to
	 * the client as "application/json" media type.
	 *
	 */
	MessageService messageService = new MessageService();

	@GET
	public List<Message> getAllMessages(@QueryParam("year") int year,
										@QueryParam("start") int start,
										@QueryParam("size") int size) {
		if(year > 0){
			return messageService.getAllMessagesForYear(year);
		}
		if(start >= 0 && size > 0){
			return messageService.getAllMessagesPaginated(size, start);
		}
		return messageService.getAllMessages();
	}
	
	/**
	 * This method is to get all messages with params 
	 * by using the Bean Param Annotation.
	 * 
	 *
	@GET
	public List<Message> getAllMessages(@BeanParam MessageFilterBean filterBean) {
		if(filterBean.getYear() > 0){
			return messageService.getAllMessagesForYear(filterBean.getYear());
		}
		if(filterBean.getStart() >= 0 && filterBean.getSize() > 0){
			return messageService.getAllMessagesPaginated(filterBean.getSize(), filterBean.getStart());
		}
		return messageService.getAllMessages();
	}*/

	@POST
	public Response addMessage(Message message, @Context UriInfo uriInfo) {
		Message newmessage = messageService.addMessage(message);
		uriInfo.getAbsolutePathBuilder().path(String.valueOf(newmessage.getId())).build();
		
		return Response.created(uriInfo.getAbsolutePathBuilder()
				.path(String.valueOf(newmessage.getId())).build())
				.entity(newmessage)
				.build();
		//return messageService.addMessage(message);
	}

	@GET
	@Path("/{messageId}")
	public Message getMessage(@PathParam("messageId") long messageId, @Context UriInfo uriInfo) {
		Message message = messageService.getMessage(messageId);
		message.addLink(getUriForSelf(uriInfo, message), "self");
		message.addLink(getUriForProfile(uriInfo, message), "profile");
		message.addLink(getUriForComments(uriInfo, message), "comments");
		return message;
	}

	private String getUriForComments(UriInfo uriInfo, Message message){
		return uriInfo.getBaseUriBuilder()
				.path(MessageResource.class)
				.path(MessageResource.class, "getCommentResource")
				.path(CommentResource.class)
				.resolveTemplate("messageId", message.getId())
				.build()
				.toString();
	}
	
	private String getUriForProfile(UriInfo uriInfo, Message message){
		return uriInfo.getBaseUriBuilder()
				.path(ProfileResource.class)
				.path(message.getAuthor())
				.build()
				.toString();
	}
	
	private String getUriForSelf(UriInfo uriInfo, Message message) {
		return uriInfo.getBaseUriBuilder()
				.path(MessageResource.class)
				.path(Long.toString(message.getId()))
				.build()
				.toString();
	}

	@PUT
	@Path("/{messageId}")
	public Message updateMessage(@PathParam("messageId") long messageId, Message message) {
		message.setId(messageId);
		return messageService.updateMessage(message);
	}

	@DELETE
	@Path("/{messageId}")
	public void deleteMessage(@PathParam("messageId") long messageId) {
		messageService.removeMessage(messageId);
	}
	
	@Path("/{messageId}/comments")
	public CommentResource getCommentResource(){
		return new CommentResource();
	}
}
