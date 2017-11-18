package com.example.bot.spring;

import com.linecorp.bot.model.action.Action;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

/**
 * This is a Message Factory utilizing factory pattern.
 * This class is responsible for the creation of Message objects and its various types.
 * Including TextMessage, TemplateMessage and ImageMessage.
 */
@Slf4j
public class MessageFactory {
	
	private ArrayList<String> uris = new ArrayList<>();
	private static String IMAGE_NOT_FOUND = "https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png"; // place holder image in case a image is needed
	
	/**
	 * Default constructor of MessageFactory with no image path list provided.
	 * Using this constructor will leave the URIs list empty and assume no image used immediately.
	 */
	public MessageFactory() {
		uris.add(IMAGE_NOT_FOUND);
	}
	
	/**
	 * Constructor of MessageFactory with a list of image paths to be used.
	 * This allows images be used in the Message creation.
	 * Images should be placed in resources/static/ so that it has path "/static/IMAGE_NAME" 
	 * @param pathList A String array of paths of images to be used.
	 */
	public MessageFactory(String[] pathList) {
		if (pathList.length>0)
			for (String path: pathList)
				uris.add(KitchenSinkController.createUri(path));
		else
			uris.add(IMAGE_NOT_FOUND);		
	}
	
	/**
	 * Constructor of MessageFactory with only one image.
	 * This allow only one image to be used. 
	 * Images should be placed in resources/static/ so that it has path "/static/IMAGE_NAME" 
	 * The image will be used for all Message that require the image.
	 * @param path A String of path of the image to be used.
	 */
	public MessageFactory(String path) {
		uris.add(KitchenSinkController.createUri(path));
	}
	
	/**
	 * Factory method for TextMessage.
	 * Creates a TextMessage that looks like a normal text message sent by another account.
	 * @see https://developers.line.me/en/docs/messaging-api/message-types/#text-messages
	 * @param text A String containing the text message.
	 * @return A TextMessage object.
	 */
	public TextMessage createTextMessage(String text) {
		return new TextMessage(text);
	}
	
	/**
	 * Factory metthod for TemplateMessage ie Carousel.
	 * Creates a TemplateMessage that has a number of rows and different contents and buttons.
	 * For each row, the first 2 arguments in contents are title and description respectively.
	 * Then for each button, the first argument is the Action type, the second is the button name,
	 * and the third and fourth are the Action parameters.
	 * The images used are in sequential order as the order of creation
	 * @see https://developers.line.me/en/docs/messaging-api/message-types/#template-messages
	 * @param columns The number of columns shown in this Carousel
	 * @param buttons The number of buttons in each column.
	 * @param contents The contents of each button
	 * @return 
	 */
	public TemplateMessage createCarouselMessage(int columns, int buttons, String[] contents) {
		try {
			ArrayList<CarouselColumn> cc = new ArrayList<>(); // used to store all columns to be form the message
			int c = 0; // used to access each element of contents
			for (int i=0; i<columns; i++){
				String uri = uris.get(i%uris.size()); // use mod to ensure there is always some uris
				String topic = contents[c++];
				String description = contents[c++];
				ArrayList<Action> actions = new ArrayList<>();
				for (int j=0; j<buttons; j++){
					actions.add(createAction(contents[c++], contents[c++], contents[c++]));
				}
				cc.add(new CarouselColumn(uri , topic, description, actions));
			}
			return new TemplateMessage("Carousel message from Fun3111", new CarouselTemplate(cc));
		} catch (ArrayIndexOutOfBoundsException e) {
			log.info(e.toString());
		}
		return null;
	}
	
	/**
	 * Helper method to create an Action object of suitable type.
	 * Type can be "message", "postback", "uri"
	 * @param type Type of Action it is.
	 * @param param1 The first parameter on the creation of said object.
	 * @param param2 The second parameter on the creation of said object.
	 * @return The Action object of suitable class.
	 */
	private Action createAction(String type, String param1, String param2){
		Action a = null;
		switch (type.toLowerCase()) {
			case "message": a = new MessageAction(param1, param2); break;
			case "postback": a = new PostbackAction(param1, param2); break;
			case "uri": a = new URIAction(param1, param2); break;
			default: log.info("Action type not found during carousel creation"); a = new MessageAction(param1, param2); // harmless message
		}
		return a;
	}
	
	/**
	 * Factory method for ImageMessage.
	 * Creates an image message that can be viewed by the user.
	 * This will use the first image path during construction of this factory.
	 * @see https://developers.line.me/en/docs/messaging-api/message-types/#image
	 * @return A ImageMessage containing the image or a placeholder in case no image was found.
	 */
	public ImageMessage createImageMessage() {
		return new ImageMessage(uris.get(0), uris.get(0));
	}
	
	/**
	 * Factory method for any type of Message specified by a String parameter.
	 * This creates a general Message object given the type as parameter.
	 * The types can be "text", "carousel", "image".
	 * @param type A String specifying the type of Message.
	 * @param param Any number of String inputs that suit the need of that type of Message.
	 * @return A Message object of the suitable type.
	 */
	public Message createMessage(String type, Object... param) {
		Message a;
		try {
			switch (type.toLowerCase()) {
				case ("text"): a = createTextMessage((String)param[0]); break;
				case ("carousel"): a = createCarouselMessage((Integer)param[0], (Integer)param[1], (String[])param[2]); break;
				case ("image"): a = createImageMessage(); break;
				default: log.info("Message type not found"); a = createImageMessage(); // return a Image in case the type is not found.
			}
		} catch (Exception e) {
			log.info(e.toString());
			a = createImageMessage(); // return a Image in case of any exceptions.
		}
		return a;
	}
}
