package org.telegram.pentagram.data;

import org.telegram.pentagram.utils.JsonUtils;
import org.telegram.pentagram.utils.Preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PentagramStorage extends Preferences{

    PentagramStorage() {
        super("PENTAGRAM_STORAGE_3");
        checkFirstRequest();
    }

    private boolean isFirstRequest() {
        return get("first_request", true);
    }

    private void setFirstRequest(boolean value) {
        set("first_request", value);
    }

    public void setHideReactionsPrivateChats(boolean hide) {
        set("hide_reactions_private_chats", hide);
    }

    public void setHideReactionsGroups(boolean hide) {
        set("hide_reactions_groups", hide);
    }

    public void setHideReactionsChannels(boolean hide) {
        set("hide_reactions_channels", hide);
    }

    public void setHideReactionsBots(boolean hide) {
        set("hide_reactions_bots", hide);
    }

    public boolean isHideReactionsPrivateChats() {
        return get("hide_reactions_private_chats", false);
    }

    public boolean isHideReactionsGroups() {
        return get("hide_reactions_groups", false);
    }

    public boolean isHideReactionsChannels() {
        return get("hide_reactions_channels", false);
    }

    public boolean isHideReactionsBots() {
        return get("hide_reactions_bots", false);
    }

    public void setHideExternalLinks(boolean hide) {
        set("external_links", hide);
    }

    public boolean isHideExternalLinks() {
        return get("external_links", false);
    }

    public void setHideInternalLinks(boolean hide) {
        set("internal_links", hide);
    }

    public boolean isHideInternalLinks() {
        return get("internal_links", false);
    }

    public void setKeywords(List<String> keywords) {
        String json = JsonUtils.listToJsonOrEmpty(keywords);
        set("keywords", json);
    }

    public ArrayList<String> getKeywords() {
        String json = get("keywords", "");
        return JsonUtils.listFromJson(json, new ArrayList<>());
    }

    private void checkFirstRequest() {
        if(get("first_request", true)) {
            String[] initKeywords = new String[]{
                    "follow link",
                    "follow the link",
                    "#ad",
                    "#promo",
                    "subscribe",
                    "promo code",
                    "перейдите",
                    "подпишитесь",
                    "подпишись",
                    "промокод",
                    "перейдіть за посиланням",
                    "підпишіться",
                    "підпишись",
                    "#реклама",
                    "реклама",
                    "#промо",
                    "промо",
                    "ооо",
                    "подробности",
                    "получи",
                    "получить",
                    "бесплатно",
                    "бесплатный",
                    "альфа",
                    "рекламодатель",
                    "подробнее",
                    "оформите",
                    "оформить",
                    "оплатите",
                    "переходите",
                    "переходи",
                    "читайте",
                    "принять участие",
                    "заказать",
                    "регистрируйтесь",
                    "ссылка",
                    "регистрируем",
                    "не упустите",
                    "не пропустите",
                    "не пропустить",
                    "участвуйте",
                    "участвуй",
                    "заявки принимаются сразу",
                    "тебе сюда",
                    "приобрести",
                    "узнай подробнее",
                    "узнай всё подробнее",
                    "по ссылке",
                    "подробнее",
                    "акции",
                    "акция",
                    "промокоду"
            };

            setKeywords(Arrays.asList(initKeywords));

            set("first_request", false);
        }
    }

}
