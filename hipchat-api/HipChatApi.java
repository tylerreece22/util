package 

import java.util.ArrayList;

import com.kobie.tr1.util.Settings;

import ch.viascom.hipchat.api.HipChat;
import ch.viascom.hipchat.api.api.RoomsAPI;
import ch.viascom.hipchat.api.exception.APIException;
import ch.viascom.hipchat.api.models.Card;
import ch.viascom.hipchat.api.models.Message;
import ch.viascom.hipchat.api.models.card.CardActivity;
import ch.viascom.hipchat.api.models.card.CardAttribute;
import ch.viascom.hipchat.api.models.card.CardAttributeValue;
import ch.viascom.hipchat.api.models.card.CardFormat;
import ch.viascom.hipchat.api.models.card.CardIcon;
import ch.viascom.hipchat.api.models.card.CardStyle;
import ch.viascom.hipchat.api.models.message.MessageColor;
import ch.viascom.hipchat.api.request.models.AddMember;
import ch.viascom.hipchat.api.request.models.ReplyMessage;
import ch.viascom.hipchat.api.request.models.SendMessage;
import ch.viascom.hipchat.api.request.models.SendNotification;
import ch.viascom.hipchat.api.request.models.ViewRoomHistory;
import ch.viascom.hipchat.api.response.GetCapabilitiesResponse;
import ch.viascom.hipchat.api.response.GetRoomStatisticsResponse;

public class HipChatApi extends HipChat {
	private static final String clientToken = Settings.get("hipchat.token");
	private static final String hipchatRoomId = Settings.get("hipchat.room.id");
	private static final String baseUrl = Settings.get("hipchat.base.url");

	public static void sendRoomNotification(String message) throws APIException {
		HipChat hipChat = new HipChat(clientToken);
		hipChat.setBaseUrl("baseUrl");
		hipChat.roomsAPI().sendRoomNotification(new SendNotification(hipchatRoomId, message, MessageColor.RED, true));
	}

	public static void sendRoomMessage(String message) throws APIException {
		HipChat hipChat = new HipChat(clientToken);
		hipChat.setBaseUrl("baseUrl");
		hipChat.roomsAPI().sendRoomMessage(new SendMessage(hipchatRoomId, message));
	}

	public static void getRoomStatistics() throws APIException {
		HipChat hipChat = new HipChat(clientToken);
		hipChat.setBaseUrl("baseUrl");
		GetRoomStatisticsResponse response = hipChat.roomsAPI().getRoomStatistics(hipchatRoomId);
		System.out.println(response.getMessagesSent());
	}
	
	public static void replyToMessage(String parentMessageId, String replyMessage) throws APIException {
		HipChat hipChat = new HipChat(clientToken);
		hipChat.setBaseUrl("baseUrl");
		hipChat.roomsAPI().replyToMessage(new ReplyMessage(hipchatRoomId, replyMessage, parentMessageId));
	}

	public static void addMember(int userId) throws APIException {
		HipChat hipChat = new HipChat(clientToken);
		hipChat.setBaseUrl("baseUrl");
		hipChat.roomsAPI().addMember(new AddMember(userId, hipchatRoomId, null));
	}
	
	public static ArrayList<Message> viewLast100RoomHistory() throws APIException {
        HipChat hipChat = new HipChat(clientToken);
        hipChat.setBaseUrl("baseUrl");
        ViewRoomHistory viewRoomHistory = new ViewRoomHistory(hipchatRoomId, 0, 100);
        return hipChat.roomsAPI().viewRoomHistory(viewRoomHistory).getItems();
    }

	public static void sendRoomNotificationCard() throws APIException {
		HipChat hipChat = new HipChat(clientToken);
		hipChat.setBaseUrl("baseUrl");
		RoomsAPI roomsAPI = hipChat.roomsAPI();

		SendNotification notification = new SendNotification();
		notification.setRoomId(hipchatRoomId);
		notification.setMessage("testing sendRoomNotificationCard()");
		notification.setColor(MessageColor.RED);
		notification.setNotify(true);

		Card card = new Card();
		card.setTitle("API Card");

		CardIcon icon = new CardIcon();
		icon.setUrl("https://dujrsrsgsd3nh.cloudfront.net/img/emoticons/22438/fire-1366382911.png");
		card.setIcon(icon);
		card.setStyle(CardStyle.APPLICATION);
		card.setUrl("http://github.com");
		card.setId("3985698273957");
		card.setFormat(CardFormat.COMPACT);

		CardAttribute cardAttribute = new CardAttribute();

		cardAttribute.setLabel("Version");

		CardAttributeValue cardAttributeValue = new CardAttributeValue();

		cardAttributeValue.setLabel("1.0");
		CardIcon icon2 = new CardIcon();
		icon2.setUrl("https://dujrsrsgsd3nh.cloudfront.net/img/emoticons/22438/puzzle-1366382992.png");
		cardAttributeValue.setIcon(icon2);

		cardAttribute.setValue(cardAttributeValue);

		card.getAttributes().add(cardAttribute);

		CardActivity cardActivity = new CardActivity();

		cardActivity.setHtml("API test Card from hipchat-api");
		cardActivity.setIcon(icon);

		card.setActivity(cardActivity);

		notification.setCard(card);

		roomsAPI.sendRoomNotification(notification);
	}
}
