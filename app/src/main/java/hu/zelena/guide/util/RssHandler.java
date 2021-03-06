package hu.zelena.guide.util;

/**
 * Created by patrik on 2016.11.08..
 */
/**
 Copyright (C) <2017>  <Patrik G. Zelena>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import hu.zelena.guide.modell.RssItem;


public class RssHandler extends DefaultHandler {
    private List<RssItem> rssItemList;
    private RssItem currentItem;
    private boolean parsingTitle;
    private boolean parsingLink;
    private boolean parsingCategory;
    private boolean parsingPubDate;

    public RssHandler() {
        rssItemList = new ArrayList<RssItem>();
    }

    public List<RssItem> getRssItemList() {
        return rssItemList;
    }


    //Kezdő elem
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("item"))
            currentItem = new RssItem();
        else if (qName.equals("title"))
            parsingTitle = true;
        else if (qName.equals("link"))
            parsingLink = true;
        else if (qName.equals("category"))
            parsingCategory = true;
        else if (qName.equals("pubDate"))
            parsingPubDate = true;
    }

    //Záró elem
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("item")) {
            //End of an item so add the currentItem to the list of items.
            rssItemList.add(currentItem);
            currentItem = null;
        } else if (qName.equals("title"))
            parsingTitle = false;
        else if (qName.equals("link"))
            parsingLink = false;
        else if (qName.equals("category"))
            parsingCategory = false;
        else if (qName.equals("pubDate"))
            parsingPubDate = false;
    }

    //Kezdő és záró tag közötti feldolgozás
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currentItem != null) {
            //If parsingTitle is true, then that means we are inside a <title> tag so the text is the title of an item.
            if (parsingTitle)
                currentItem.setTitle(new String(ch, start, length));
                //If parsingLink is true, then that means we are inside a <link> tag so the text is the link of an item.
            else if (parsingLink)
                currentItem.setLink(new String(ch, start, length));
                //If parsingDescription is true, then that means we are inside a <description> tag so the text is the description of an item.
            else if (parsingCategory)
                currentItem.setCategory(new String(ch, start, length));
            else if (parsingPubDate)
                currentItem.setPubDate(new String(ch, start, length));
        }
    }
}
