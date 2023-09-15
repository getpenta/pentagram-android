package org.telegram.pentagram.data;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.Log;

import org.telegram.messenger.MessageObject;

import java.util.ArrayList;
import java.util.List;

public class PentagramProvider{

    private static PentagramStorage storage;

    private static boolean hideExternalLinks = false;
    private static boolean hideInternalLinks = false;
    private static List<String> keywords = new ArrayList<>();

    private static boolean hideReactionsPrivateChats = false;
    private static boolean hideReactionsGroups = false;
    private static boolean hideReactionsChannels = false;
    private static boolean hideReactionsBots = false;

    private PentagramProvider() {
    }

    public static void setHideReactionsPrivateChats(boolean hide) {
        getStorage().setHideReactionsPrivateChats(hide);
        hideReactionsPrivateChats = hide;
    }

    public static void setHideReactionsGroups(boolean hide) {
        getStorage().setHideReactionsGroups(hide);
        hideReactionsGroups = hide;
    }

    public static void sethideReactionsChannels(boolean hide) {
        getStorage().setHideReactionsChannels(hide);
        hideReactionsChannels = hide;
    }

    public static void setHideReactionsBots(boolean hide) {
        getStorage().setHideReactionsBots(hide);
        hideReactionsBots = hide;
    }

    public static boolean isHideReactionsPrivateChats() {
        return getStorage().isHideReactionsPrivateChats();
    }

    public static boolean isHideReactionsGroups() {
        return getStorage().isHideReactionsGroups();
    }

    public static boolean isHideReactionsChannels() {
        return getStorage().isHideReactionsChannels();
    }

    public static boolean isHideReactionsBots() {
        return getStorage().isHideReactionsBots();
    }

    public static void setHideExternalLinks(boolean hide) {
        getStorage().setHideExternalLinks(hide);
        hideExternalLinks = hide;
    }

    public static boolean isHideExternalLinks() {
        return getStorage().isHideExternalLinks();
    }

    public static void setHideInternalLinks(boolean hide) {
        getStorage().setHideInternalLinks(hide);
        hideInternalLinks = hide;
    }

    public static boolean isHideInternalLinks() {
        return getStorage().isHideInternalLinks();
    }

    public static void setKeywords(List<String> newKeywords) {
        List<String> lowerCase = new ArrayList<>();
        for(String keyword : newKeywords) {
            lowerCase.add(keyword.toLowerCase());
        }
        getStorage().setKeywords(lowerCase);
        keywords = lowerCase;
    }

    public static void addKeyword(String newKeyword) {
        List<String> newKeywords = getKeywords();
        newKeywords.add(newKeyword);
        setKeywords(newKeywords);
    }

    public static void updateKeyword(String oldKeyword, String newKeyword) {
        List<String> oldKeywords = getKeywords();
        List<String> newKeywords = new ArrayList<>();
        for(String keyword : oldKeywords) {
            if(TextUtils.equals(keyword, oldKeyword)) {
                newKeywords.add(newKeyword);
            } else {
                newKeywords.add(keyword);
            }
        }

        getStorage().setKeywords(newKeywords);
        keywords = newKeywords;
    }

    public static ArrayList<String> getKeywords() {
        return getStorage().getKeywords();
    }

    public static boolean checkNeedHide(MessageObject.GroupedMessages groupedMessages, String chatname, String username) {

        try {
            for(int i = 0; i<groupedMessages.messages.size(); i++) {
                if(checkNeedHide(groupedMessages.messages.get(i), chatname, username)) {
                    return true;
                }
            }
        } catch(Exception e) {
        }

        return false;
    }

    public static boolean checkNeedHide(MessageObject message, String chatname, String username) {

        /*if(1==1){
            return true;
        }*/

        if(message==null || message.isSponsored()) {
            return false;
        }

        //Log.d("develop", "------------------------------------------------------------------------------------");

        String messageBodyStr = "";
        CharSequence messageBody = !TextUtils.isEmpty(message.caption) ? message.caption : message.messageText;

        //Log.d("develop", "SpannableString: messageBody = "+messageBody);
        //Log.d("develop", "SpannableString: caption.class = "+messageBody.getClass().getName());

        boolean hideLinks = hideExternalLinks || hideInternalLinks;

        if(hideLinks && !TextUtils.isEmpty(messageBody)) {
            if(messageBody instanceof SpannableString) {
                SpannableString spannableCaption = (SpannableString) messageBody;
                try {
                    //Log.d("develop", "SpannableString: caption = "+messageBody);
                    URLSpan[] spans = spannableCaption.getSpans(0, spannableCaption.length(), URLSpan.class);
                    for(URLSpan span : spans) {
                        //Log.d("develop", "SpannableString: span = "+span);
                        //Log.d("develop", "SpannableString: span.getURL = "+span.getURL());
                        //Log.d("develop", "SpannableString: span.describeContents = "+span.describeContents());
                        try {
                            String url = span.getURL();
                            if(hideInternalLinks && isInternalLink(url, chatname, username)) {
                                return true;
                            }
                            if(hideExternalLinks && !isInternalLink(url, chatname, username)) {
                                return true;
                            }
                        } catch(Exception ignore) {
                        }
                    }
                } catch(Exception ignore) {
                }
            } else {
                messageBodyStr = messageBody.toString().toLowerCase();
                if(hideInternalLinks && containsInternalLink(messageBodyStr, chatname, username)) {
                    return true;
                }
                if(hideExternalLinks && !containsInternalLink(messageBodyStr, chatname, username)) {
                    return true;
                }
            }
        }

        //Log.d("develop", "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

        if(TextUtils.isEmpty(messageBodyStr)) {
            messageBodyStr = TextUtils.isEmpty(messageBody) ? "" : messageBody.toString().toLowerCase();
        }

        getStorage();
        if(!TextUtils.isEmpty(messageBodyStr)) {
            for(String keyword : keywords) {
                if(messageBodyStr.contains(keyword)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isInternalLink(String url, String chatname, String username) {
        if(url.startsWith("https://t.me")) {
            return true;
        } else if(url.startsWith("@")) {
            boolean isChatname = !TextUtils.isEmpty(chatname) && url.contains(chatname);
            boolean isUsername = !TextUtils.isEmpty(username) && url.contains(username);
            return !isChatname && !isUsername;
        } else {
            return false;
        }
    }

    private static boolean containsInternalLink(String message, String chatname, String username) {
        if(message.contains("https://t.me")) {
            return true;
        } else if(message.contains(" @")) {
            boolean isChatname = !TextUtils.isEmpty(chatname) && message.contains(chatname);
            boolean isUsername = !TextUtils.isEmpty(username) && message.contains(username);
            return !isChatname && !isUsername;
        } else {
            return false;
        }
    }

    private static PentagramStorage getStorage() {
        if(storage==null) {
            storage = new PentagramStorage();
            hideExternalLinks = storage.isHideExternalLinks();
            hideInternalLinks = storage.isHideInternalLinks();
            keywords = storage.getKeywords();
        }
        return storage;
    }

}
